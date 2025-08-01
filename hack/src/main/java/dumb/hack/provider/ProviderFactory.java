package dumb.hack.provider;

import dumb.hack.LMOptions;
import dev.langchain4j.model.chat.ChatModel;
import dumb.lm.mock.MockChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.Objects;

public class ProviderFactory {

    private final LMOptions options;

    public ProviderFactory(LMOptions options) {
        this.options = Objects.requireNonNull(options, "LMOptions cannot be null");
    }

    public ChatModel create() {
        String provider = options.getProvider();
        switch (provider.toLowerCase()) {
            case "openai":
                return createOpenAiModel();
            case "mock":
                return new MockChatModel();
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider + ". Please choose 'openai' or 'mock'.");
        }
    }

    private ChatModel createOpenAiModel() {
        String apiKey = options.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new MissingApiKeyException("OpenAI");
        }
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(options.getModel())
                .build();
    }
}
