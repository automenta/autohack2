package com.pijul.aider;

import com.pijul.common.LLMClient;
import com.pijul.common.LLMResponse;

public class LLMManager {
    private final LLMClient llmClient;

    public LLMManager() {
        // Default to Ollama provider with llama2 model
        this.llmClient = new LLMClient("ollama", "llama2", null);
    }

    public LLMManager(String provider, String model, String apiKey) {
        this.llmClient = new LLMClient(provider, model, apiKey);
    }

    public void initialize() {
        // Nothing to do here for now
    }

    public void shutdown() {
        // Nothing to do here for now
    }

    public String generateResponse(String prompt) {
        LLMResponse response = llmClient.generate(prompt);
        if (response.isSuccess()) {
            return response.getContent();
        } else {
            return "Error: " + response.getError();
        }
    }
}