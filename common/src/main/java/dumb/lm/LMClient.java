package dumb.lm;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dumb.lm.mock.MockChatModel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LMClient implements ILMClient {
    private static final Logger logger = Logger.getLogger(LMClient.class.getName());
    private final ChatModel model;

    public LMClient(ChatModel model) {
        this.model = model;
    }

    public LMClient(String provider, String modelName, String apiKey) {
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
            case "mock":
                this.model = new MockChatModel();
                break;
            default:
                throw new IllegalArgumentException("Unsupported LLM provider: " + provider);
        }
    }

    public LMResponse generate(String prompt) {
        try {
            ChatResponse response = model.chat(UserMessage.from(prompt));
            LMUsage usage = new LMUsage(
                    response.tokenUsage().inputTokenCount(),
                    response.tokenUsage().outputTokenCount(),
                    response.tokenUsage().totalTokenCount()
            );
            return new LMResponse(response.aiMessage().text(), usage, true, null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in LLM generation", e);
            return new LMResponse(null, null, false, "LLM generation failed: " + e.getMessage());
        }
    }

    public ChatModel getChatModel() {
        return model;
    }
}
