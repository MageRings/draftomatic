package magic.resource;

import java.io.IOException;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import magic.data.Player;
import magic.data.database.Database;

@Path("/players")
public class PlayerResource {
	
	private final Database db;
	
	public PlayerResource(Database db) {
		this.db = db;
	}

    @GET
    @Path("/list")
    public Set<Player> listPlayers() throws IOException {
        return this.db.getPlayers();
    }
}
