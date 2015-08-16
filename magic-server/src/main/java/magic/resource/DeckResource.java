package magic.resource;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import magic.data.Deck;
import magic.data.database.Database;

@Path("/deck")
public class DeckResource {

    @POST
    @Path("/register")
    public void registerDeck(Deck deck) {
        Database.addDeck(deck);
    }

    @GET
    @Path("/list")
    public List<Deck> listDecks() throws IOException {
        return Database.readDecks();
    }

    @GET
    @Path("/list/{id}")
    public Deck listDeck(@PathParam("id") Long id) throws IOException {
        return Database.readDeckFromId(id);
    }
}
