package com.pijul.aider;

import com.pijul.common.LLMClient;
import com.pijul.common.LLMResponse;

public class LLMManager {
    private LLMClient llmClient;

    public LLMManager() {
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
            this.llmClient = null;
        } else {
            this.llmClient = new LLMClient(provider, model, apiKey);
        }
    }

    public void shutdown() {
        // Shutdown LLM - nothing to do for the default client
    }



    public String generateResponse(String prompt) {
        if (llmClient == null) {
            return "Error: LLM not initialized. Please set the OPENAI_API_KEY environment variable.";
        }
        // Generate response from LLM
        LLMResponse response = llmClient.generate(prompt);
        if (response.isSuccess()) {
            return response.getContent();
        } else {
            return "Error: " + response.getError();
        }
    }

    // Add more methods as needed
}