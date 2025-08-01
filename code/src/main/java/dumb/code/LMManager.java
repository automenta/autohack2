package dumb.code;

import dumb.code.llm.LMClient;
import dumb.lm.ILMClient;
import dumb.lm.LMResponse;

public class LMManager {
    private ILMClient lmClient;

    public LMManager() {
        // Initialize with the real client
        initialize();
    }

    public LMManager(ILMClient lmClient) {
        // Initialize with a mock client for testing
        this.lmClient = lmClient;
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
            this.lmClient = null;
        } else {
            this.lmClient = new LMClient(provider, model, apiKey);
        }
    }

    public void shutdown() {
        // Shutdown LLM - nothing to do for the default client
    }


    public String generateResponse(String prompt) {
        if (lmClient == null) {
            return "Error: LLM not initialized. Please set the OPENAI_API_KEY environment variable.";
        }
        // Generate response from LLM
        LMResponse response = lmClient.generate(prompt);
        if (response.isSuccess()) {
            return response.getContent();
        } else {
            return "Error: " + response.getError();
        }
    }

    // Add more methods as needed
}