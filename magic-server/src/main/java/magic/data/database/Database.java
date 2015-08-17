package magic.data.database;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import magic.data.Deck;
import magic.data.Player;
import magic.data.Tournament;
import magic.data.Format;

public class Database {

    private Database() {
        // utils
    }

    private static final String DELIMETER = "-----";
    private static final String LINE_END = "\n";
    private static final String NO_FORMAT_CODE = "N/A";

    private static final AtomicLong PLAYER_ID_COUNTER = readPlayerId();
    private static final AtomicLong DECK_ID_COUNTER = readDeckId();
    private static final AtomicLong MATCH_ID_COUNTER = readMatchId();
    private static final AtomicLong TOURNAMENT_ID_COUNTER = readTournamentId();

    /**************************************************
    * BEGIN Table IDs
    ***************************************************/

    public static long nextPlayerId() {
        return PLAYER_ID_COUNTER.incrementAndGet();
    }

    public static long nextDeckId() {
        return DECK_ID_COUNTER.incrementAndGet();
    }

    public static long nextMatchId() {
        return MATCH_ID_COUNTER.incrementAndGet();
    }

    public static long nextTournamentId() {
        return TOURNAMENT_ID_COUNTER.incrementAndGet();
    }

    private static AtomicLong readPlayerId() {
        return readId(DatabaseConstants.PLAYERS, DatabaseConstants.PLAYERS_ID);
    }

    private static AtomicLong readDeckId() {
        return readId(DatabaseConstants.DECKS, DatabaseConstants.DECKS_ID);
    }

    private static AtomicLong readMatchId() {
        return readId(DatabaseConstants.MATCHES, DatabaseConstants.MATCHES_ID);
    }

    private static AtomicLong readTournamentId() {
        return readId(DatabaseConstants.TOURNAMENTS, DatabaseConstants.TOURNAMENTS_ID);
    }

    private static AtomicLong readId(File file, int idIndex) {
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

    public static Tournament readTournamentFromId(long id) throws IOException {
        Optional<String> tournamentLine = matchLineId(DatabaseConstants.TOURNAMENTS, DatabaseConstants.TOURNAMENTS_ID, id);
        if (tournamentLine.isPresent()) {
            return parseTournamentFromLine(tournamentLine.get());
        } else {
            throw new RuntimeException("Could not parse tournament with id: " + id);
        }
    }

    public static List<Tournament> readTournaments() throws IOException {
        return lines(DatabaseConstants.TOURNAMENTS)
                .map(line -> parseTournamentFromLine(line))
                .collect(Collectors.toList());
    }

    private static Tournament parseTournamentFromLine(String line) {
        String[] parts = line.split(DELIMETER);
        long id = Long.parseLong(parts[DatabaseConstants.TOURNAMENTS_ID]);
        Format format = Format.valueOf(parts[DatabaseConstants.TOURNAMENTS_FORMAT]);
        String code = parts[DatabaseConstants.TOURNAMENTS_CODE];
        Optional<String> formatCode = code.equals(NO_FORMAT_CODE) ? Optional.empty() : Optional.of(code);
        return new Tournament(id, format, formatCode);
    }

    private static Optional<String> matchLineId(File file, int matchPart, long id) throws IOException {
        Optional<String> playerLine = lines(file).filter(line -> Long.parseLong(line.split(DELIMETER)[matchPart]) == id).findFirst();
        return playerLine;
    }

    private static Stream<String> lines(File file) throws IOException {
        return java.nio.file.Files.lines(file.toPath());
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

    public static boolean addTournament(Tournament tournament) {
        return append(DatabaseConstants.TOURNAMENTS, formatTournament(tournament));
    }

    private static String formatTournament(Tournament tournament) {
        return tournament.getId() + DELIMETER +
               tournament.getFormat() + DELIMETER +
               (tournament.getFormatCode() == Optional.<String> empty() ? NO_FORMAT_CODE : tournament.getFormatCode()) + LINE_END;
    }

    private static boolean append(File file, String contents) {
        try {
            Files.append(contents, file, Charsets.UTF_8);
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
