package dumb.code.llm;

import dev.langchain4j.service.UserMessage;

public interface Assistant {
    @UserMessage
    String chat(String message);
}
