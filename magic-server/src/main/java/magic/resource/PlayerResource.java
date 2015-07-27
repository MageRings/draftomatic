package magic.resource;


import com.google.common.collect.ImmutableList;

import magic.data.Player;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/player")
public class PlayerResource {

    @POST
    @Path("/register")
    public void registerPlayer(String name) {
        System.out.println(name);
    }

    @GET
    @Path("/list")
    public List<Player> listPlayers() {
        return ImmutableList.of(new Player("A. Square"));
    }
}
