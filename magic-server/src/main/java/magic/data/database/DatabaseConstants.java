package magic.data.database;

import java.io.File;

public class DatabaseConstants {

    public static final File PLAYERS = new File("data/players.txt");
    public static final File DECKS = new File("data/decks.txt");
    public static final File MATCHES = new File("data/matches.txt");
    public static final File TOURNAMENTS = new File("data/tournaments.txt");

    public static final int PLAYERS_ID = 0;
    public static final int PLAYERS_NAME = 1;

    public static final int DECKS_ID = 0;
    public static final int DECKS_FORMAT = 1;
    public static final int DECKS_COLORS = 2;
    public static final int DECKS_ARCHETYPE = 3;

    public static final int MATCHES_ID = 0;

    public static final int TOURNAMENTS_ID = 0;
    public static final int TOURNAMENTS_FORMAT = 1;
    public static final int TOURNAMENTS_CODE = 2;

    private DatabaseConstants() {
        // utils
    }

}
