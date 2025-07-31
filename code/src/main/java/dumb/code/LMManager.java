package dumb.code;

import dumb.lm.LMClient;
import dumb.lm.LMResponse;

public class LMManager {
    private LMClient LMClient;

    public LMManager() {
        // Constructor
        initialize();
    }

    public void initialize() {
        // Initialize LLM
        String apiKey = System.getenv("OPENAI_API_KEY");
        String provider = System.getenv("LLM_PROVIDER");
        String model = System.getenv("LLM_MODEL");

        if (provider == null || provider.isEmpty()) {
            provider = "openai";
        }

        if (model == null || model.isEmpty()) {
            model = "gpt-4o-mini";
        }


        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("WARNING: OPENAI_API_KEY environment variable not set. LLM functionality will be disabled.");
            this.LMClient = null;
        } else {
            this.LMClient = new LMClient(provider, model, apiKey);
        }
    }

    public void shutdown() {
        // Shutdown LLM - nothing to do for the default client
    }


    public String generateResponse(String prompt) {
        if (LMClient == null) {
            return "Error: LLM not initialized. Please set the OPENAI_API_KEY environment variable.";
        }
        // Generate response from LLM
        LMResponse response = LMClient.generate(prompt);
        if (response.isSuccess()) {
            return response.getContent();
        } else {
            return "Error: " + response.getError();
        }
    }

    // Add more methods as needed
}