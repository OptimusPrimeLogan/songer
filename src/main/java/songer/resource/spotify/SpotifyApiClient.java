package songer.resource.spotify;

import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "spotify-api")
public interface SpotifyApiClient {

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    String getUserProfile(@HeaderParam("Authorization") String authorizationHeader);

    @GET
    @Path("/me/tracks")
    @Produces(MediaType.APPLICATION_JSON)
    String getLikedSongs(
            @HeaderParam("Authorization") String authorizationHeader,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit
    );
}