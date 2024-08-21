package songer.service.spotify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import songer.resource.spotify.SpotifyApiClient;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SpotifyService {

    private final SpotifyApiClient spotifyApiClient;

    @Inject
    private AuthService authService;

    public SpotifyService(@RestClient SpotifyApiClient spotifyApiClient) {
        this.spotifyApiClient = spotifyApiClient;
    }

    public String getUserProfile(String accessToken) {
        if (accessToken == null) {
            throw new IllegalStateException("Access token is not available. Please login first.");
        }

        String authorizationHeader = "Bearer " + accessToken;
        return spotifyApiClient.getUserProfile(authorizationHeader);
    }

    public List<String> getLikedSongs(String accessToken) {
        if (accessToken == null) {
            throw new IllegalStateException("Access token is not available. Please login first.");
        }

        List<String> songList = new ArrayList<>();
        int offset = 0;
        int limit = 50;
        boolean hasMore = true;

        try {
            while (hasMore) {
                String authorizationHeader = "Bearer " + accessToken;

                String jsonResponse = spotifyApiClient.getLikedSongs(authorizationHeader, offset, limit);
                songList.addAll(parseJson(jsonResponse));

                hasMore = songList.size() == (offset + limit);
                offset += limit;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get liked songs", e);
        }

        System.out.println(songList.size());
        songList.forEach(System.out::println);
        return songList;
    }

    private List<String> parseJson(String jsonResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        List<String> songTitles = new ArrayList<>();

        for (JsonNode item : root.path("items")) {
            String songTitle = item.path("track").path("name").asText();
            songTitles.add(songTitle);
        }

        return songTitles;
    }
}
