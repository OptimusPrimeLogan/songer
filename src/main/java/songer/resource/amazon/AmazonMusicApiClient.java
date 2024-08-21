package songer.resource.amazon;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "amazon-music-api")
public interface AmazonMusicApiClient {

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    String getCurrentUser(
            @HeaderParam("Authorization") String authorizationHeader,
            @HeaderParam("x-api-key") String apiKeyHeader);

    @GET
    @Path("/me/tracks")
    @Produces(MediaType.APPLICATION_JSON)
    String getLikedTracks(
            @HeaderParam("Authorization") String authorizationHeader,
            @HeaderParam("x-api-key") String apiKeyHeader,
            @QueryParam("trackIds") String trackIds,
            @QueryParam("limit") Integer limit,
            @QueryParam("cursor") String cursor);
}
