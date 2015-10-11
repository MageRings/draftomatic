package magic.data.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

import magic.data.Deck;
import magic.data.Format;
import magic.data.Player;
import magic.data.tournament.TournamentData;

public final class FileSystemDB implements Database {

    public FileSystemDB() {
        try {
            initDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String DELIMETER = "-----";
    private static final String LINE_END  = "\n";

    private static final AtomicLong PLAYER_ID_COUNTER = readPlayerId();
    private static final AtomicLong DECK_ID_COUNTER   = readDeckId();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static void initDatabase() throws IOException {
        if (Files.notExists(DatabaseConstants.DATA)) {
            Files.createDirectories(DatabaseConstants.DATA);
        }
        if (!Files.isDirectory(DatabaseConstants.DATA)) {
            throw new RuntimeException("Data directory " + DatabaseConstants.DATA + " is a file!");
        }
        if (Files.notExists(DatabaseConstants.PLAYERS)) {
            Files.createFile(DatabaseConstants.PLAYERS);
        }
        if (Files.notExists(DatabaseConstants.DECKS)) {
            Files.createFile(DatabaseConstants.DECKS);
        }
        if (Files.notExists(DatabaseConstants.TOURNAMENTS)) {
            Files.createDirectories(DatabaseConstants.TOURNAMENTS);
        }
        if (!Files.isDirectory(DatabaseConstants.TOURNAMENTS)) {
            throw new RuntimeException("Tournament directory " + DatabaseConstants.TOURNAMENTS + " is a file!");
        }
    }

    /**************************************************
     * BEGIN Table IDs
     ***************************************************/

    public static long nextPlayerId() {
        return PLAYER_ID_COUNTER.incrementAndGet();
    }

    public static long nextDeckId() {
        return DECK_ID_COUNTER.incrementAndGet();
    }

    private static AtomicLong readPlayerId() {
        return readId(DatabaseConstants.PLAYERS, DatabaseConstants.PLAYERS_ID);
    }

    private static AtomicLong readDeckId() {
        return readId(DatabaseConstants.DECKS, DatabaseConstants.DECKS_ID);
    }

    private static AtomicLong readId(Path file, int idIndex) {
        try (Stream<String> lines = lines(file)) {
            return new AtomicLong(lines.map(line -> line.split(DELIMETER))
                    .map(arr -> arr[idIndex])
                    .map(Integer::valueOf)
                    .max(Comparator.naturalOrder())
                    .get());
        } catch (Exception e) {
            return new AtomicLong();
        }
    }

    /**************************************************
     * END Table IDs
     ***************************************************/

    /**************************************************
     * BEGIN Reads
     ***************************************************/

    public static Player readPlayerFromId(long id) throws IOException {
        Optional<String> playerLine = matchLineId(DatabaseConstants.PLAYERS, DatabaseConstants.PLAYERS_ID, id);
        if (playerLine.isPresent()) {
            return parsePlayerFromLine(playerLine.get());
        } else {
            throw new RuntimeException("Could not parse player with id: " + id);
        }
    }

    public static List<Player> readPlayers() throws IOException {
        return lines(DatabaseConstants.PLAYERS)
                .filter(line -> Long.parseLong(line.split(DELIMETER)[DatabaseConstants.PLAYERS_ID]) != 0)
                .map(line -> parsePlayerFromLine(line))
                .collect(Collectors.toList());
    }

    private static Player parsePlayerFromLine(String line) {
        String[] parts = line.split(DELIMETER);
        long id = Long.parseLong(parts[DatabaseConstants.PLAYERS_ID]);
        String name = parts[DatabaseConstants.PLAYERS_NAME];
        return new Player(id, name);
    }

    public static Deck readDeckFromId(long id) throws IOException {
        Optional<String> deckLine = matchLineId(DatabaseConstants.DECKS, DatabaseConstants.DECKS_ID, id);
        if (deckLine.isPresent()) {
            return parseDeckFromLine(deckLine.get());
        } else {
            throw new RuntimeException("Could not parse deck with id: " + id);
        }
    }

    public static List<Deck> readDecks() throws IOException {
        return lines(DatabaseConstants.DECKS)
                .map(line -> parseDeckFromLine(line))
                .collect(Collectors.toList());
    }

    private static Deck parseDeckFromLine(String line) {
        String[] parts = line.split(DELIMETER);
        long id = Long.parseLong(parts[DatabaseConstants.DECKS_ID]);
        String colors = parts[DatabaseConstants.DECKS_COLORS];
        String archetype = parts[DatabaseConstants.DECKS_ARCHETYPE];
        Format format = Format.valueOf(parts[DatabaseConstants.DECKS_FORMAT]);
        return new Deck(id, colors, archetype, format);
    }

    public static TournamentData readTournamentFromId(String id) throws IOException {
        return MAPPER.readValue(new File(DatabaseConstants.TOURNAMENTS.toFile(), id + ".json"), TournamentData.class);
    }

    private static Optional<String> matchLineId(Path file, int matchPart, long id) throws IOException {
        Optional<String> playerLine =
                lines(file).filter(line -> Long.parseLong(line.split(DELIMETER)[matchPart]) == id).findFirst();
        return playerLine;
    }

    private static Stream<String> lines(Path file) throws IOException {
        return Files.lines(file);
    }

    @Override
    public Map<String, TournamentData> loadTournaments() throws IOException {
        Map<String, TournamentData> result = Maps.newHashMap();
        Files.list(DatabaseConstants.TOURNAMENTS).forEach(path -> {
            String id = path.getFileName().toString().replace(".json", "");
            try {
                result.put(id, readTournamentFromId(id));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }

    /**************************************************
     * END Reads
     ***************************************************/

    /**************************************************
     * BEGIN Writes
     ***************************************************/

    public static boolean addPlayer(Player player) {
        return append(DatabaseConstants.PLAYERS, formatPlayer(player));
    }

    private static String formatPlayer(Player player) {
        return player.getId() + DELIMETER +
                player.getName() + LINE_END;
    }

    public static boolean addDeck(Deck deck) {
        return append(DatabaseConstants.DECKS, formatDeck(deck));
    }

    private static String formatDeck(Deck deck) {
        return deck.getId() + DELIMETER +
                deck.getFormat() + DELIMETER +
                deck.getColors() + DELIMETER +
                deck.getArchetype() + LINE_END;
    }

    @Override
    public void writeTournament(TournamentData tournament) throws IOException {
        MAPPER.writerWithDefaultPrettyPrinter()
                .writeValue(new File(DatabaseConstants.TOURNAMENTS.toFile(), tournament.getId() + ".json"), tournament);
    }

    private static boolean append(Path file, String contents) {
        try {
            com.google.common.io.Files.append(contents, file.toFile(), Charsets.UTF_8);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**************************************************
     * END Writes
     ***************************************************/

}
