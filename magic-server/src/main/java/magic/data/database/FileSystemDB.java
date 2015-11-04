package magic.data.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import jersey.repackaged.com.google.common.collect.Sets;
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

    private static final AtomicLong DECK_ID_COUNTER   = readDeckId();

    private final ObjectMapper mapper = new ObjectMapper();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private void initDatabase() throws IOException {
        if (Files.notExists(DatabaseConstants.DATA)) {
            Files.createDirectories(DatabaseConstants.DATA);
        }
        if (!Files.isDirectory(DatabaseConstants.DATA)) {
            throw new RuntimeException("Data directory " + DatabaseConstants.DATA + " is a file!");
        }
        if (Files.notExists(DatabaseConstants.PLAYERS)) {
            mapper.writeValue(DatabaseConstants.PLAYERS.toFile(), ImmutableList.of());
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

    public static long nextDeckId() {
        return DECK_ID_COUNTER.incrementAndGet();
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

    public TournamentData readTournamentFromId(String id) throws IOException {
        return mapper.readValue(new File(DatabaseConstants.TOURNAMENTS.toFile(), id + ".json"), TournamentData.class);
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
        mapper.writerWithDefaultPrettyPrinter()
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

    @Override
    public Set<Player> getPlayers() throws IOException {
        return mapper.readValue(DatabaseConstants.PLAYERS.toFile(), new TypeReference<Set<Player>>(){});
    }

    @Override
    public Set<Player> registerPlayers(List<String> playerNames) throws IOException {
        lock.writeLock().lock();;
        try {
            Set<Player> currentPlayers = getPlayers();
            Set<Player> addedPlayers = DbUtils.registerPlayers(playerNames, currentPlayers);
            mapper.writeValue(DatabaseConstants.PLAYERS.toFile(), Sets.union(addedPlayers, currentPlayers));
            return addedPlayers;
        } finally {
            lock.writeLock().unlock();
        }
    }

}
