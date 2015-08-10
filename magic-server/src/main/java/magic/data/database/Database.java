package magic.data.database;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import magic.data.Deck;
import magic.data.Player;
import magic.data.Tournament;

public class Database {

    private static final String DELIMETER = "-----";
    private static final String LINE_END = "\n";

    private static final AtomicLong PLAYER_ID_COUNTER = readPlayerId();
    private static final AtomicLong DECK_ID_COUNTER = readDeckId();
    private static final AtomicLong MATCH_ID_COUNTER = readMatchId();
    private static final AtomicLong TOURNAMENT_ID_COUNTER = readTournamentId();

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

    public static boolean addPlayer(Player player) {
        return appendFile(DatabaseConstants.PLAYERS, formatPlayer(player));
    }

    private static String formatPlayer(Player player) {
        return player.getId() + DELIMETER + player.getName() + LINE_END;
    }

    public static boolean addDeck(Deck deck) {
        return appendFile(DatabaseConstants.DECKS, formatDeck(deck));
    }

    private static String formatDeck(Deck deck) {
        return deck.getId() + DELIMETER + deck.getFormat() + DELIMETER + deck.getColors() + DELIMETER
                + deck.getArchetype() + LINE_END;
    }

    public static boolean addTournament(Tournament tournament) {
        return appendFile(DatabaseConstants.TOURNAMENTS, formatTournament(tournament));
    }

    private static String formatTournament(Tournament tournament) {
        return tournament.getId() + DELIMETER + tournament.getFormat() + DELIMETER
                + (tournament.getFormatCode() == Optional.<String> empty() ? "N/A" : tournament.getFormatCode())
                + LINE_END;
    }

    private static boolean appendFile(File file, String contents) {
        try {
            Files.append(contents, file, Charsets.UTF_8);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
        Stream<String> lines = null;
        try {
            lines = java.nio.file.Files.lines(file.toPath());
            Optional<Integer> max = lines.map(line -> line.split(DELIMETER))
                                        .map(arr -> arr[idIndex])
                                        .map(id -> Integer.valueOf(id))
                                        .max(Comparator.naturalOrder());
            if (max == Optional.<Integer>empty()) {
                return new AtomicLong();
            } else {
                return new AtomicLong(max.get());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error parsing data ids in file " + file.getAbsolutePath(), e);
        } finally {
            if (lines != null) {
                lines.close();
            }
        }
    }

    private Database() {
        // utils
    }

}
