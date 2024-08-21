package songer.service.spotify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import songer.resource.spotify.SpotifyAuthClient;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    @Inject
    @RestClient
    private SpotifyAuthClient authClient;

    @ConfigProperty(name = "spotify.client.id")
    String clientId;

    @ConfigProperty(name = "spotify.client.secret")
    String clientSecret;

    @ConfigProperty(name = "spotify.redirect.uri")
    String redirectUri;

    private String accessToken;

    public Response performLogin() {
        String state = generateRandomString(16);
        String scope = "user-read-private user-read-email user-library-read";
        String encodedScope = URLEncoder.encode(scope, StandardCharsets.UTF_8);

        String authUrl = "https://accounts.spotify.com/authorize?" +
                "response_type=code&" +
                "client_id=" + clientId + "&" +
                "scope=" + encodedScope + "&" +
                "redirect_uri=" + redirectUri + "&" +
                "state=" + state;

        return Response.seeOther(URI.create(authUrl)).build();
    }

    public String exchangeCodeForToken(String code) throws IOException {
        // Define the parameters
        String grantType = "authorization_code";

        // Call the REST client
        String response = authClient.exchangeCodeForToken(grantType, code, redirectUri, clientId, clientSecret);

        // Parse the JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);
        return jsonNode.get("access_token").asText();
    }

    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
