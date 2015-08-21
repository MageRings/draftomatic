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

import magic.data.Match;
import magic.data.Player;
import magic.data.Round;
import magic.data.TournamentStatus;
import magic.tournament.TieBreakers;
import magic.tournament.TournamentManager;
import magic.data.Format;

@Path("tournament")
public class TournamentResource {

    private TournamentManager manager;

    public TournamentResource() {
        this.manager = new TournamentManager();
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

    @PUT
    @Path("/results/{tournamentId}")
    @Produces("application/json")
    public Round registerResults(@PathParam("tournamentId") String tournamentId,
                                                @QueryParam("round") Integer round,
                                                Collection<Match> results) {
        return manager.getTournament(tournamentId).registerResults(Optional.ofNullable(round), results);
    }

    @GET
    @Path("/standings/{tournamentId}")
    @Produces("application/json")
    public NavigableSet<TieBreakers> getStandings(
                                                @PathParam("tournamentId") String tournamentId,
                                                @QueryParam("round") Integer round) {
        return manager.getTournament(tournamentId).getTieBreakers(Optional.ofNullable(round));
    }

    @GET
    @Path("/status/{tournamentId}")
    @Produces("application/json")
    public TournamentStatus getTournamentStatus(@PathParam("tournamentId") String tournamentId) {
        return manager.getTournament(tournamentId).getStatus();
    }

}
