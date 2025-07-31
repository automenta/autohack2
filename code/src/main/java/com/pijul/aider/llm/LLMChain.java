package com.pijul.aider.llm;

import com.pijul.aider.Container;
import com.pijul.common.LLMClient;
import com.pijul.common.LLMResponse;

import java.util.concurrent.CompletableFuture;

public class LLMChain {
    private final LLMClient llmClient;
    private final Container container;

    public LLMChain(String provider, String model, String apiKey, Container container) {
        this.llmClient = new LLMClient(provider, model, apiKey);
        this.container = container;
    }

    public CompletableFuture<String> handleQuery(String query, String codebase, String diff, String lastCommandOutput) {
        String prompt = "You are a helpful AI assistant that helps with coding.\n\n" +
                "Here is the current codebase: " + codebase + "\n\n" +
                "Here is the current diff: " + diff + "\n\n" +
                "Here is the output of the last command: " + lastCommandOutput + "\n\n" +
                "Here is the user's query: " + query;

        return CompletableFuture.supplyAsync(() -> {
            LLMResponse response = llmClient.generate(prompt);
            if (response.isSuccess()) {
                String output = response.getContent();
                container.getMessageHandler().addMessage("ai", "Response: " + output);

                // Apply diff if present in response
                if (output.contains("diff")) {
                    // Implement diff application logic here
                    System.out.println("Applying diff from AI response...");
                    // TODO: Implement actual diff application
                }
                return output;
            } else {
                String error = "Error: " + response.getError();
                container.getMessageHandler().addMessage("error", error);
                return error;
            }
        });
    }
}