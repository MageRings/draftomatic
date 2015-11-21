package magic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/source")
public class SourceResource {

    @GET
    @Path("/")
    @Produces("text/plain")
    public String getSource() {
    	// this endpoint exists to satisfy the AGPL requirement that the server points to the source
    	// if you make modifications to the source you should change this link or push the modifications
    	// to this location
    	return "https://github.com/MageRings/draftomatic";
    }
}
