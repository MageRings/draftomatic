package magic.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.Optional;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.fasterxml.jackson.databind.ObjectMapper;

import magic.data.Match;
import magic.data.Round;
import magic.data.database.Database;
import magic.data.tournament.TournamentData;
import magic.data.tournament.TournamentInput;
import magic.data.tournament.TournamentStatus;
import magic.tournament.TieBreakers;
import magic.tournament.TournamentManager;

@Path("tournament")
public class TournamentResource {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TournamentManager manager;

    public TournamentResource(Database db) {
        this.manager = new TournamentManager(db);
    }

    private String jsonifyString(String arg) {
        return "\"" + arg + "\"";
    }

    @POST
    @Path("/register")
    @Produces("application/json")
    public String registerTournament(@QueryParam("rounds") Integer rounds,
                                     TournamentInput input) {
        return jsonifyString(this.manager.registerTournament(input, Optional.ofNullable(rounds)));
    }

    @PUT
    @Path("/results/{tournamentId}")
    @Produces("application/json")
    public Round registerResults(@PathParam("tournamentId") String tournamentId,
                                 @QueryParam("round") Integer round,
                                 Collection<Match> results) {
        return this.manager.getTournament(tournamentId).registerResults(Optional.ofNullable(round), results);
    }

    @DELETE
    @Path("/round/{tournamentId}")
    @Produces("application/json")
    public Round registerResults(@PathParam("tournamentId") String tournamentId) {
        return this.manager.getTournament(tournamentId).undoLastRound();
    }

    @GET
    @Path("/standings/{tournamentId}")
    @Produces("application/json")
    public NavigableSet<TieBreakers> getStandings(
                                                  @PathParam("tournamentId") String tournamentId,
                                                  @QueryParam("round") Integer round) {
        return this.manager.getTournament(tournamentId).getTieBreakers(Optional.ofNullable(round));
    }

    @GET
    @Path("/status/{tournamentId}")
    @Produces("application/json")
    public TournamentStatus getTournamentStatus(@PathParam("tournamentId") String tournamentId) {
        return this.manager.getTournament(tournamentId).getStatus();
    }

    @GET
    @Path("/export/{tournamentId}")
    @Produces({"application/json"})
    public Response exportTournament(@PathParam("tournamentId") final String tournamentId) {

        final TournamentData data = manager.getTournament(tournamentId).getStatus().getTournamentData();

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException {
                MAPPER.writeValue(output, data);
            }
        };

        return Response.ok(stream, "application/json")
                .header("Content-disposition", "attachment; filename=" + "Tournament_"+ data.getStartTime().toLocalDate().toString() + ".json")
                .build();
    }
}
