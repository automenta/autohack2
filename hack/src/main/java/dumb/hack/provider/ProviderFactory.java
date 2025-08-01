package dumb.hack.provider;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dumb.hack.LMOptions;
import dumb.lm.mock.MockChatModel;

import java.util.Objects;

public record ProviderFactory(LMOptions options) {

    public ProviderFactory(LMOptions options) {
        this.options = Objects.requireNonNull(options, "LMOptions cannot be null");
    }

    public ChatModel create() {
        String provider = options.getProvider();
        return switch (provider.toLowerCase()) {
            case "openai" -> createOpenAiModel();
            case "mock" -> new MockChatModel();
            default ->
                    throw new IllegalArgumentException("Unsupported provider: " + provider + ". Please choose 'openai' or 'mock'.");
        };
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
