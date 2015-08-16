package magic.resource;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import magic.data.Player;
import magic.data.database.Database;

@Path("/player")
public class PlayerResource {

    @POST
    @Path("/register")
    public void registerPlayer(@QueryParam("player") Player player) {
        Database.addPlayer(player);
    }

    @GET
    @Path("/list")
    public List<Player> listPlayers() throws IOException {
        return Database.readPlayers();
    }

    @GET
    @Path("/list/{id}")
    public Player listPlayer(@PathParam("id") Long id) throws IOException {
        return Database.readPlayerFromId(id);
    }
}
