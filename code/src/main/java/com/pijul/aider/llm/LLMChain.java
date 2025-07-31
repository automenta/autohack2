package com.pijul.aider.llm;

import com.pijul.aider.Container;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LLMChain {

    //    private final Assistant assistant;
//    private final Container container;
//
    public LLMChain(String provider, String model, String apiKey, Container container, List<Object> toolProviders) {
//        this.container = container;
//        LLMClient llmClient = new LLMClient(provider, model, apiKey);
//        var chatModel = llmClient.getChatModel();
//
//        this.assistant = AiServices.builder(Assistant.class)
//                .chatLanguageModel(chatModel)
//                .tools(toolProviders)
//                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
//                .build();
    }

    //
    public CompletableFuture<String> handleQuery(String query) {
        return null;
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                String response = assistant.chat(query);
//                container.getMessageHandler().addMessage("ai", "Response: " + response);
//                return response;
//            } catch (Exception e) {
//                String error = "Error: " + e.getMessage();
//                container.getMessageHandler().addMessage("error", error);
//                return error;
//            }
//        });
    }
}