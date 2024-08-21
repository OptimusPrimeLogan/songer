package songer.service.amazon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import songer.resource.amazon.AmazonAuthClient;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class AmazonAuthService {

    @Inject
    @RestClient
    AmazonAuthClient authClient;

    @ConfigProperty(name = "amazon.client.id")
    String clientId;

    @ConfigProperty(name = "amazon.client.secret")
    String clientSecret;

    @ConfigProperty(name = "amazon.redirect.uri")
    String redirectUri;

    private String accessToken;

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String exchangeCodeForToken(String code) throws IOException {
        String grantType = "authorization_code";
        Response response = authClient.exchangeCodeForToken(grantType, code, redirectUri, clientId, clientSecret);
        String responseBody = response.readEntity(String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }


    public Response getUserProfile(String accessToken) {
        if (accessToken == null) {
            throw new IllegalStateException("Access token is not available. Please login first.");
        }

        return authClient.getUserProfile("Bearer " + accessToken);
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
