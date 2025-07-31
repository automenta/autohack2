package com.pijul.mcr;

import com.pijul.common.LLMClient;
import com.pijul.mcr.tools.ToolProvider;

import java.util.Properties;

public class MCR {

    private final LLMClient llmClient;

    public MCR(Properties config) {
        String llmProvider = config.getProperty("llm.provider", "openai");
        String llmApiKey = config.getProperty("llm.apiKey");
        String llmModel = config.getProperty("llm.model", "gpt-4o-mini");
        this.llmClient = new LLMClient(llmProvider, llmModel, llmApiKey);
    }

    public Session createSession(ToolProvider toolProvider) {
        return new Session(llmClient, toolProvider);
    }

    public Session createSession() {
        return createSession(null);
    }
}
