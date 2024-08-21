package songer.resource.amazon;

import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "amazon-auth-api")
public interface AmazonAuthClient {

    @POST
    @Path("/auth/o2/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response exchangeCodeForToken(
            @FormParam("grant_type") String grantType,
            @FormParam("code") String code,
            @FormParam("redirect_uri") String redirectUri,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret);

    @GET
    @Path("/user/profile")
    Response getUserProfile(
            @HeaderParam("Authorization") String accessToken
    );
}
