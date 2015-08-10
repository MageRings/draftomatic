package magic.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import magic.data.Deck;
import magic.data.database.Database;

@Path("/deck")
public class DeckResource {

    @POST
    @Path("/register")
    public void registerDeck(Deck deck) {
        Database.addDeck(deck);
    }
}
