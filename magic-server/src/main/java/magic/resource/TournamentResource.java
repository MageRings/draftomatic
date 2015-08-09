package magic.resource;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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
    public String registerTournament(
            @QueryParam("rounds") Integer rounds,
            @QueryParam("format") Format format,
            @QueryParam("code") String formatCode,
            Collection<Player> players) {
        return jsonifyString(manager.registerTournament(Optional.ofNullable(rounds), Optional.ofNullable(format), Optional.ofNullable(formatCode), players));
    }

    @GET
    @Path("/pairings/{tournamentId}")
    @Produces("application/json")
    public NavigableSet<Pairing> getPairings(
                                             @PathParam("tournamentId") String tournamentId,
                                             @PathParam("round") int round) {
        return manager.getTournament(tournamentId).getPairings(round);
    }

    @PUT
    @Path("/results/{tournamentId}/{round}")
    @Produces("application/json")
    public NavigableSet<Result> registerResults(
                                                @PathParam("tournamentId") String tournamentId,
                                                @PathParam("round") int round,
                                                Collection<Result> results) {
        return manager.getTournament(tournamentId).registerResults(round, results);
    }
}
