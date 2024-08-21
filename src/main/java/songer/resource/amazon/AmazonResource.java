package songer.resource.amazon;

import songer.service.amazon.AmazonAuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import songer.service.amazon.AmazonMusicService;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Path("/amazon")
public class AmazonResource {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonResource.class);

    @Inject
    AmazonAuthService amazonAuthService;

    @Inject
    AmazonMusicService amazonMusicService;

    @GET
    @Path("/login")
    public Response login() {
        String state = amazonAuthService.generateRandomString(16);
        //String scope = "profile music::profile music::favorites::read"; // Correct scopes separated by spaces
        String scope = "profile"; // Correct scopes separated by spaces


        String authUrl = "https://www.amazon.com/ap/oa?" +
                "client_id=" + amazonAuthService.getClientId() + "&" +
                "scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) + "&" +
                "response_type=code&" +
                "redirect_uri=" + URLEncoder.encode(amazonAuthService.getRedirectUri(), StandardCharsets.UTF_8) + "&" +
                "state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);

        return Response.seeOther(URI.create(authUrl)).build();
    }

    @GET
    @Path("/callback")
    public Response callback(@QueryParam("code") String code, @QueryParam("state") String state) {
        if (code != null && !code.isEmpty()) {
            try {
                String token = amazonAuthService.exchangeCodeForToken(code);
                if (token != null) {
                    amazonAuthService.setAccessToken(token);
                    return Response.ok("Token obtained and saved: " + token).build();
                } else {
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                }
            } catch (IOException e) {
                LOG.error("Error exchanging code for token", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("/profile")
    public Response getProfile() {
        try {
            String profile = amazonMusicService.getMusicUserProfile(amazonAuthService.getAccessToken());
            return Response.ok(profile).build();
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logger
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to get user profile").build();
        }
    }

    @GET
    @Path("/user-profile")
    public Response getUserProfile() {
        try {
            return amazonAuthService.getUserProfile(amazonAuthService.getAccessToken());
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logger
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to get user profile").build();
        }
    }

    @GET
    @Path("/liked-songs")
    public Response getLikedSongs() {
        try {
            List<String> songs = amazonMusicService.getLikedTracks(amazonAuthService.getAccessToken(), null, null);
            return Response.ok(songs).build(); // Quarkus will convert the list to JSON
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logger
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to get liked songs").build();
        }
    }


}
