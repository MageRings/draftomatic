package magic.data.database;

import java.io.File;

public class DatabaseConstants {

    public static final File PLAYERS = new File("data/players.txt");
    public static final File DECKS = new File("data/decks.txt");
    public static final File MATCHES = new File("data/matches.txt");
    public static final File TOURNAMENTS = new File("data/tournaments.txt");

    public static final int PLAYERS_ID = 0;
    public static final int PLAYERS_NAME = 1;

    private DatabaseConstants() {
        // utils
    }

}
