package dumb.code.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dumb.lm.ILMClient;
import dumb.lm.LMResponse;

public class LMClient implements ILMClient {

    private final ChatLanguageModel chatModel;

    public LMClient(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public LMResponse generate(String prompt) {
        try {
            String response = chatModel.generate(prompt);
            return LMResponse.success(response);
        } catch (Exception e) {
            return LMResponse.error(e.getMessage());
        }
    }
}
