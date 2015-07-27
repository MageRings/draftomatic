package magic.resource;

import java.util.List;
import java.util.NavigableSet;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.google.common.base.Optional;

import magic.data.Format;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;
import magic.swiss.SwissManager;

@Path("tournament")
public class TournamentResource {

    private SwissManager manager;

    public TournamentResource() {
        this.manager = new SwissManager();
    }

    private String jsonifyString(String arg) {
        return "\"" + arg + "\"";
    }

    @POST
    @Path("/register")
    @Produces("application/json")
    public String registerTournament(@QueryParam("format") Format format,
                                     @QueryParam("code") Optional<String> formatCode,
                                     List<Player> players) {
        return jsonifyString(manager.registerTournament(format, formatCode, players));
    }

    @POST
    @Path("/pairings/{tournamentId}/{round}")
    @Produces("application/json")
    public NavigableSet<Pairing> getPairings(
                                             @PathParam("tournamentId") String tournamentId,
                                             @PathParam("round") int round,
                                             List<Player> players) {
        return manager.getTournament(tournamentId).getPairings(round);
    }

    @PUT
    @Path("/pairings/{tournamentId}/{round}")
    @Produces("application/json")
    public List<Result> registerResults(
                                        @PathParam("tournamentId") String tournamentId,
                                        @PathParam("round") int round,
                                        List<Result> results) {
        manager.getTournament(tournamentId).registerResults(round, results);
        return results;
    }
}
