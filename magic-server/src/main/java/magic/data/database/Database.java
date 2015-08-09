package magic.data.database;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import magic.data.Deck;
import magic.data.Player;
import magic.data.Tournament;

public class Database {

    private static final String DELIMETER = "-----";
    private static final String LINE_END = "\n";

    public static boolean addPlayer(Player player) {
        return appendFile(DatabaseConstants.PLAYERS, formatPlayer(player));
    }

    private static String formatPlayer(Player player) {
        return player.getId() + DELIMETER +
               player.getName() + LINE_END;
    }

    public static boolean addDeck(Deck deck) {
        return appendFile(DatabaseConstants.DECKS, formatDeck(deck));
    }

    private static String formatDeck(Deck deck) {
        return deck.getId() + DELIMETER +
               deck.getFormat() + DELIMETER +
               deck.getColors() + DELIMETER +
               deck.getArchetype() + LINE_END;
    }

    public static boolean addTournament(Tournament tournament) {
        return appendFile(DatabaseConstants.TOURNAMENTS, formatTournament(tournament));
    }

    private static String formatTournament(Tournament tournament) {
        return tournament.getId() + DELIMETER +
               tournament.getFormat() + DELIMETER +
               (tournament.getFormatCode() == Optional.<String>empty() ? "N/A" : tournament.getFormatCode()) + LINE_END;
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

    private Database() {
        // utils
    }

}
