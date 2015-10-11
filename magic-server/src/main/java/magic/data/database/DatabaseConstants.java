package magic.data.database;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseConstants {

    public static final Path DATA        = Paths.get("data/");
    public static final Path PLAYERS     = Paths.get("data/players.txt");
    public static final Path DECKS       = Paths.get("data/decks.txt");
    // tournaments each get their own file in this directory
    public static final Path TOURNAMENTS = Paths.get("data/tournaments/");

    public static final int PLAYERS_ID   = 0;
    public static final int PLAYERS_NAME = 1;

    public static final int DECKS_ID        = 0;
    public static final int DECKS_FORMAT    = 1;
    public static final int DECKS_COLORS    = 2;
    public static final int DECKS_ARCHETYPE = 3;

    public static final int MATCHES_ID = 0;

    private DatabaseConstants() {
        // utils
    }

}
