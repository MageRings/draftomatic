package magic.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.google.common.collect.Lists;

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
    public List<Player> listPlayers() {
        return Lists.newArrayList();
    }
}
