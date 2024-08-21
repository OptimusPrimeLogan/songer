package songer.resource.spotify;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey="spotify-auth-api")
@Path("/api")
public interface SpotifyAuthClient {

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    String exchangeCodeForToken(
            @FormParam("grant_type") String grantType,
            @FormParam("code") String code,
            @FormParam("redirect_uri") String redirectUri,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret
    );
}
