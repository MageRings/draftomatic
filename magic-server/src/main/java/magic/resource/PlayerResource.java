package magic.resource;

import java.io.IOException;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import magic.data.Player;
import magic.data.database.FileSystemDB;

@Path("/players")
public class PlayerResource {

    @GET
    @Path("/list")
    public Set<Player> listPlayers() throws IOException {
        return FileSystemDB.INSTANCE.getPlayers();
    }
}
