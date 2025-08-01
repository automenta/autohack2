package dumb.lm.mock;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;

import java.util.HashMap;
import java.util.Map;

public class MockChatModel implements ChatModel {

    private final Map<String, String> cannedResponses = new HashMap<>();
    private String defaultResponse = "This is a mock response from the mock LLM.";

    public void addCannedResponse(String promptContains, String response) {
        cannedResponses.put(promptContains, response);
    }

    public void setDefaultResponse(String defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    public void clearCannedResponses() {
        cannedResponses.clear();
    }

    @Override
    public ChatResponse chat(ChatRequest chatRequest) {
        if (chatRequest.messages().isEmpty()) {
            return ChatResponse.builder()
                    .aiMessage(new AiMessage(defaultResponse))
                    .tokenUsage(new TokenUsage(0, 0, 0))
                    .build();
        }

        dev.langchain4j.data.message.ChatMessage lastMessage = chatRequest.messages().getLast();
        if (!(lastMessage instanceof dev.langchain4j.data.message.UserMessage)) {
            // Not a user message, return default
            return ChatResponse.builder()
                    .aiMessage(new AiMessage(defaultResponse))
                    .tokenUsage(new TokenUsage(0, 0, 0))
                    .build();
        }

        String userMessage = ((dev.langchain4j.data.message.UserMessage) lastMessage).singleText();

        for (Map.Entry<String, String> entry : cannedResponses.entrySet()) {
            if (userMessage.contains(entry.getKey())) {
                return ChatResponse.builder()
                        .aiMessage(new AiMessage(entry.getValue()))
                        .tokenUsage(new TokenUsage(0, 0, 0))
                        .build();
            }
        }

        return ChatResponse.builder()
                .aiMessage(new AiMessage(defaultResponse))
                .tokenUsage(new TokenUsage(0, 0, 0))
                .build();
    }
}
