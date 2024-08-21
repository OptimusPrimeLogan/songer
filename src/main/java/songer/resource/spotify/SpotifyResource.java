package songer.resource.spotify;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import songer.service.spotify.AuthService;
import songer.service.spotify.SpotifyService;

import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;

@RequestScoped
@Path("/spotify")
public class SpotifyResource {

    @Inject
    SpotifyService spotifyService;

    @Inject
    AuthService authService;

    private static String accessToken;

    @GET
    @Path("/login")
    public Response login() {
        return authService.performLogin();
    }

    @GET
    @Path("/callback")
    public Response callback(@QueryParam("code") String code, @QueryParam("state") String state) {
        if (code == null || state == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("State mismatch").build();
        }

        try {
            accessToken = authService.exchangeCodeForToken(code);
            if (accessToken != null) {
                return Response.ok("Token obtained and saved: " + accessToken).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/profile")
    public Response getProfile() {
        try {
            String profile = spotifyService.getUserProfile(accessToken);
            return Response.ok(profile).build();
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
            List<String> songs = spotifyService.getLikedSongs(accessToken);
            return Response.ok(songs).build(); // Quarkus will convert the list to JSON
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logger
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to get liked songs").build();
        }
    }

}
