package dumb.mcr;

import dumb.lm.LMClient;
import dumb.mcr.tools.ToolProvider;

import java.util.Properties;

public class MCR {

    private final LMClient LMClient;

    public MCR(LMClient LMClient) {
        this.LMClient = LMClient;
    }

    public MCR(Properties config) {
        this.LMClient = new LMClient(
                config.getProperty("llm.provider", "openai"),
                config.getProperty("llm.model", "gpt-4o-mini"),
                config.getProperty("llm.apiKey")
        );
    }

    public Session createSession(ToolProvider toolProvider) {
        return new Session(LMClient, toolProvider);
    }

    public Session createSession() {
        return createSession(null);
    }
}
