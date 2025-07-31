package com.pijul.common;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.vertexai.VertexAiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client for interacting with various LLM providers.
 * Provides a unified interface for generating responses from different LLM providers.
 */
public class LLMClient {
    private static final Logger logger = Logger.getLogger(LLMClient.class.getName());
    private final ChatModel model;

    /**
     * Constructs an LLMClient for the specified provider and configuration.
     *
     * @param provider The LLM provider (e.g., "ollama", "google", "anthropic", "openai")
     * @param modelName The model name to use
     * @param apiKey The API key for providers that require authentication
     * @throws IllegalArgumentException for unsupported providers or missing required parameters
     */
    public LLMClient(String provider, String modelName, String apiKey) {
        switch (provider.toLowerCase()) {
            case "ollama":
                this.model = OllamaChatModel.builder()
                    .modelName(modelName)
                    .baseUrl("http://localhost:11434")
                    .build();
                break;

            case "google":
                this.model = VertexAiChatModel.builder()
                    .project("mcr-llm")
                    .location("us-central1")
                    .modelName(modelName)
                    .build();
                break;

            case "anthropic":
                if (apiKey == null) {
                    throw new IllegalArgumentException("Anthropic provider requires an apiKey");
                }
                this.model = AnthropicChatModel.builder()
                    .modelName(modelName)
                    .apiKey(apiKey)
                    .build();
                break;

            case "openai":
                if (apiKey == null) {
                    throw new IllegalArgumentException("OpenAI provider requires an apiKey");
                }
                this.model = OpenAiChatModel.builder()
                    .modelName(modelName)
                    .apiKey(apiKey)
                    .build();
                break;

            default:
                throw new IllegalArgumentException("Unsupported LLM provider: " + provider);
        }
    }

    /**
     * Generates a response for the given prompt text.
     *
     * @param prompt The input prompt text
     * @return The generated response as a string
     */
    public LLMResponse generate(String prompt) {
        LLMResponse response = new LLMResponse();
        try {
            String rawResponse = model.chat(prompt);
            
            // The token usage is not available in the simple model.generate(prompt) response.
            // LangChain4j's StreamingChatLanguageModel provides more detailed responses with token counts.
            // For now, we will leave the usage empty.
            LLMUsage usage = new LLMUsage();
            
            response.setContent(rawResponse);
            response.setUsage(usage);
            response.setSuccess(true);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in LLM generation", e);
            response.setSuccess(false);
            response.setError("LLM generation failed: " + e.getMessage());
        }
        return response;
    }
}
