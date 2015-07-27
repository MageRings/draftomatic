package magic.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import magic.data.Deck;

@Path("/deck")
public class DeckResource {

    @POST
    @Path("/register")
    public void registerDeck(Deck deck) {
        System.out.println(deck);
    }
}
