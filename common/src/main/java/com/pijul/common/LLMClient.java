package com.pijul.common;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LLMClient {
    private static final Logger logger = Logger.getLogger(LLMClient.class.getName());
    private final ChatModel model;

    public LLMClient(String provider, String modelName, String apiKey) {
        switch (provider.toLowerCase()) {
            case "ollama":
                this.model = OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName(modelName)
                        .build();
                break;
            case "openai":
                this.model = OpenAiChatModel.builder()
                        .apiKey(apiKey)
                        .modelName(modelName)
                        .build();
                break;
            case "google":
                this.model = GoogleAiGeminiChatModel.builder()
                        .modelName(modelName)
                        .build();
                break;
            default:
                throw new IllegalArgumentException("Unsupported LLM provider: " + provider);
        }
    }

    public LLMResponse generate(String prompt) {
        LLMResponse llmResponse = new LLMResponse();
        try {
            ChatResponse response = model.chat(UserMessage.from(prompt));
            llmResponse.setContent(response.aiMessage().text());
            LLMUsage usage = new LLMUsage();
            usage.setPromptTokens(response.tokenUsage().inputTokenCount());
            usage.setCompletionTokens(response.tokenUsage().outputTokenCount());
            usage.setTotalTokens(response.tokenUsage().totalTokenCount());
            llmResponse.setUsage(usage);
            llmResponse.setSuccess(true);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in LLM generation", e);
            llmResponse.setSuccess(false);
            llmResponse.setError("LLM generation failed: " + e.getMessage());
        }
        return llmResponse;
    }

    public ChatModel getChatModel() {
        return model;
    }
}
