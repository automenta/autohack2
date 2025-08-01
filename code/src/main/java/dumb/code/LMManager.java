package dumb.code;

import dumb.lm.LMClient;
import dumb.lm.ILMClient;
import dumb.lm.LMResponse;

public class LMManager {
    private final ILMClient lmClient;

    public LMManager(String provider, String model, String apiKey) {
        this.lmClient = new LMClient(provider, model, apiKey);
    }

    public LMManager(ILMClient lmClient) {
        this.lmClient = lmClient;
    }

    public void shutdown() {
        // Nothing to do
    }

    public String generateResponse(String prompt) {
        if (lmClient == null) {
            return "Error: LLM not initialized.";
        }
        LMResponse response = lmClient.generate(prompt);
        if (response.isSuccess()) {
            return response.getContent();
        } else {
            return "Error: " + response.getError();
        }
    }
}