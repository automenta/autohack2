package com.pijul.common.mock;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;

public class MockChatModel implements ChatModel {

    @Override
    public ChatResponse chat(ChatRequest chatRequest) {
        return ChatResponse.builder()
                .aiMessage(new AiMessage("This is a mock response from the mock LLM."))
                .tokenUsage(new TokenUsage(0, 0, 0))
                .build();
    }
}
