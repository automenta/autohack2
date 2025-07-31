package dumb.hack;

import java.util.HashMap;
import java.util.Map;

public class ApiKeyManager {
    private final Map<String, String> apiKeys = new HashMap<>();

    public void setApiKey(String provider, String apiKey) {
        apiKeys.put(provider.toLowerCase(), apiKey);
    }

    public String getApiKey(String provider) {
        return apiKeys.get(provider.toLowerCase());
    }
}
