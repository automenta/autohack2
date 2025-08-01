package dumb.code;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dumb.code.llm.LMClient;
import dumb.lm.ILMClient;
import dumb.lm.LMResponse;

public class LMManager {
    private ILMClient lmClient;

    public LMManager() {
        initialize();
    }

    public LMManager(ILMClient lmClient) {
        this.lmClient = lmClient;
    }

    public void initialize() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        String provider = System.getenv("LLM_PROVIDER");
        String model = System.getenv("LLM_MODEL");

        if (provider == null || provider.isEmpty()) {
            provider = "mock"; // Default to mock if not specified
        }

        if (model == null || model.isEmpty()) {
            model = "gpt-4o-mini";
        }

        ChatLanguageModel chatModel;
        switch (provider.toLowerCase()) {
            case "openai":
                if (apiKey == null || apiKey.isEmpty()) {
                    System.err.println("WARNING: OPENAI_API_KEY environment variable not set.");
                    chatModel = createMockChatModel();
                } else {
                    chatModel = OpenAiChatModel.builder()
                            .apiKey(apiKey)
                            .modelName(model)
                            .build();
                }
                break;
            case "ollama":
                chatModel = OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName(model)
                        .build();
                break;
            default:
                chatModel = createMockChatModel();
                break;
        }
        this.lmClient = new LMClient(chatModel);
    }

    private ChatLanguageModel createMockChatModel() {
        return new ChatLanguageModel() {
            @Override
            public String generate(String message) {
                return "```java\n// A comment was added\npublic class TestFile {\n    public static void main(String[] args) {\n        // Start\n    }\n}\n```";
            }
        };
    }

    public String generateResponse(String prompt) {
        if (lmClient == null) {
            return "Error: LLM not initialized.";
        }
        LMResponse response = lmClient.generate(prompt);
        if (response.isSuccess()) {
            return response.getContent();
        } else {
            return "Error: " + response.getError();
        }
    }
}
