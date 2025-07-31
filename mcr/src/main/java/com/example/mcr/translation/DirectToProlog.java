package com.example.mcr.translation;

import com.pijul.common.LLMClient;
import com.pijul.common.LLMResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DirectToProlog implements TranslationStrategy {
    private LLMClient llmClient;
    private String model;

    public DirectToProlog() {
    }

    public DirectToProlog(LLMClient llmClient, String model) {
        this.llmClient = llmClient;
        this.model = model;
    }

    @Override
    public CompletableFuture<TranslationResult> translate(String input, LLMClient llmClient, String model, List<String> ontologyTerms, String feedback, boolean returnFullResponse) {
        this.llmClient = llmClient;
        this.model = model;
        return CompletableFuture.supplyAsync(() -> {
            if (llmClient == null) {
                throw new IllegalStateException("LLM client not configured for direct translation");
            }

            String prompt = "Translate the following English text to Prolog: \"" + input + "\"";
            if (feedback != null) {
                prompt += "\n\nPrevious error: " + feedback;
            }
            if (ontologyTerms != null && !ontologyTerms.isEmpty()) {
                prompt += "\n\nUse these ontology terms: " + String.join(", ", ontologyTerms);
            }

            LLMResponse response = llmClient.generate(prompt);
            TranslationResult result = new TranslationResult();
            result.setContent(response.getContent());
            // Token usage is not available in the simple model.generate(prompt) response.
            // We will leave the usage empty.
            return result;
        });
    }
}