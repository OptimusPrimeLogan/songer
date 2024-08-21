package songer.service.amazon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import songer.resource.amazon.AmazonMusicApiClient;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AmazonMusicService {

    @Inject
    @RestClient
    AmazonMusicApiClient amazonMusicApiClient;

    @ConfigProperty(name = "amazon.api.key")
    String apiKey;

    public String getMusicUserProfile(String accessToken) {
        if (accessToken == null) {
            throw new IllegalStateException("Access token is not available. Please login first.");
        }

        return amazonMusicApiClient.getCurrentUser("Bearer " + accessToken, apiKey);
    }


    public List<String> getLikedTracks(String accessToken, String trackIds, Integer limit) throws JsonProcessingException {
        List<String> allTracks = new ArrayList<>();
        String cursor = null;
        Integer remaining = limit;

        do {
            // Make the API call
            String response = amazonMusicApiClient.getLikedTracks("Bearer " + accessToken, apiKey, trackIds, limit, cursor);
            // Parse the response to get the tracks and cursor
            // Assume the response JSON includes a 'tracks' array and 'next' cursor
            // Modify according to your response structure
            // Example parsing code:
            JsonNode jsonResponse = new ObjectMapper().readTree(response);
            JsonNode tracksNode = jsonResponse.get("tracks");
            JsonNode nextCursorNode = jsonResponse.get("next");

            // Extract tracks
            for (JsonNode trackNode : tracksNode) {
                allTracks.add(trackNode.asText());
            }

            // Update cursor and remaining
            cursor = nextCursorNode.asText(null); // Null if there's no next page
            remaining -= tracksNode.size();

        } while (cursor != null && remaining > 0);

        return allTracks;
    }

}
