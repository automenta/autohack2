package dumb.lm;

public class LMManager {
    private final ILMClient lmClient;

    public LMManager(String provider, String model, String apiKey) {
        this.lmClient = new LMClient(provider, model, apiKey);
    }

    public LMManager(ILMClient lmClient) {
        this.lmClient = lmClient;
    }

    public ILMClient getLmClient() {
        return lmClient;
    }

    public String getProvider() {
        return lmClient.getProvider();
    }

    public String getModel() {
        return lmClient.getModel();
    }

    public String getApiKey() {
        return lmClient.getApiKey();
    }

    public void shutdown() {
        // Nothing to do
    }

    public String generateResponse(String prompt) {
        if (lmClient == null) {
            return "Error: LLM not initialized.";
        }
        LMResponse response = lmClient.generate(prompt);
        if (response.success()) {
            return response.content();
        } else {
            return "Error: " + response.error();
        }
    }
}