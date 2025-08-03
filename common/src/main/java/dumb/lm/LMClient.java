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
    private final String provider;
    private final String modelName;
    private final String apiKey;

    public LMClient(String provider, String modelName, String apiKey) {
        this.provider = provider;
        this.modelName = modelName;
        this.apiKey = apiKey;
        this.model = createModel(provider, modelName, apiKey);
    }

    public void configureModel(String prompt, String response) {
        if (model instanceof dumb.lm.mock.MockChatModel) {
            ((dumb.lm.mock.MockChatModel) model).addCannedResponse(prompt, response);
        }
    }

    private static ChatModel createModel(String provider, String modelName, String apiKey) {
        switch (provider.toLowerCase()) {
            case "ollama":
                return OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName(modelName)
                        .build();
            case "openai":
                return OpenAiChatModel.builder()
                        .apiKey(apiKey)
                        .modelName(modelName)
                        .build();
            case "google":
                return GoogleAiGeminiChatModel.builder()
                        .modelName(modelName)
                        .build();
            case "mock":
                return new MockChatModel();
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

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getModel() {
        return modelName;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }
}
