package dumb.code;

import dumb.lm.ILMClient;
import dumb.lm.LMClient;
import dumb.lm.LMResponse;

public record LMManager(ILMClient lmClient) {
    public LMManager(String provider, String model, String apiKey) {
        this(new LMClient(provider, model, apiKey));
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