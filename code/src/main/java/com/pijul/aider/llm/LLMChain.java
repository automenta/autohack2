package com.pijul.aider.llm;

import com.pijul.aider.Container;
import com.pijul.common.LLMClient;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.Assistant;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LLMChain {
    private final Assistant assistant;
    private final Container container;

    public LLMChain(String provider, String model, String apiKey, Container container, List<Object> toolProviders) {
        this.container = container;
        LLMClient llmClient = new LLMClient(provider, model, apiKey);
        ChatLanguageModel chatModel = llmClient.getChatModel();

        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .tools(toolProviders)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    public CompletableFuture<String> handleQuery(String query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String response = assistant.chat(query);
                container.getMessageHandler().addMessage("ai", "Response: " + response);
                return response;
            } catch (Exception e) {
                String error = "Error: " + e.getMessage();
                container.getMessageHandler().addMessage("error", error);
                return error;
            }
        });
    }
}