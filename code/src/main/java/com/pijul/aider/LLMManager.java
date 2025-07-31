package com.pijul.aider;

import com.pijul.common.LLMClient;
import com.pijul.common.LLMResponse;

public class LLMManager {
    private LLMClient llmClient;

    public LLMManager() {
        // For now, we will default to a google gemini model.
        // This should be made configurable in the future.
        this.llmClient = new LLMClient("google", "gemini-1.5-flash-001", null);
    }

    public void initialize() {
        // The LLMClient is initialized in the constructor.
    }

    public void shutdown() {
        // Nothing to do here for now.
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