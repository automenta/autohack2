package com.pijul.mcr.core;

import com.pijul.mcr.translation.TranslationStrategy;
import com.pijul.mcr.translation.DirectToProlog;
import com.pijul.mcr.translation.AgenticReasoning;
import com.pijul.mcr.core.Session;
import com.pijul.common.LLMClient;
import com.pijul.common.LLMUsage;
import com.pijul.mcr.translation.JsonToProlog;
import java.util.HashMap;
import java.util.Map;

public class MCR {
    private final Config config;
    private LLMClient llmClient;
    private final String llmModel;
    private final Map<String, TranslationStrategy> strategyRegistry;
    private final LLMUsage totalLlmUsage;

    public MCR(Config config) {
        this.config = config;
        this.llmModel = config.llm != null ? config.llm.model : "gpt-3.5-turbo";
        this.strategyRegistry = new HashMap<>();
        this.strategyRegistry.put("direct", new DirectToProlog());
        this.strategyRegistry.put("json", new JsonToProlog());
        this.strategyRegistry.put("agentic", new AgenticReasoning());
        if (config.strategyRegistry != null) {
            this.strategyRegistry.putAll(config.strategyRegistry);
        }
        this.totalLlmUsage = new LLMUsage();

        if (config.llm != null && config.llm.provider != null) {
            this.llmClient = getLlmClient(config.llm.provider, config.llm.model, config.llm.apiKey);
        } else {
            // Default to OpenAI if no config provided
            try {
                this.llmClient = new LLMClient("openai", "gpt-3.5-turbo", null);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Failed to create default LLM client", e);
            }
        }
    }

    public Session createSession(SessionOptions options) {
        return new Session(this, options);
    }

    public void registerStrategy(String name, TranslationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        this.strategyRegistry.put(name, strategy);
    }

    public LLMUsage getLlmMetrics() {
        return totalLlmUsage;
    }

    private LLMClient getLlmClient(String provider, String model, String apiKey) {
        try {
            return new LLMClient(provider, model, apiKey);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid LLM configuration: " + e.getMessage(), e);
        }
    }

    public static class Config {
        public LlmConfig llm;
        public Map<String, TranslationStrategy> strategyRegistry;
        
        public Config() {}
    }

    public static class LlmConfig {
        public String provider;
        public String apiKey;
        public String model;
        
        public LlmConfig() {}
    }

    public static class SessionOptions {
        public long retryDelay = 500;
        public int maxTranslationAttempts = 2;
        public int maxReasoningSteps = 5;
        public Object ontology;
        public java.util.logging.Logger logger;
        public String translator;
        
        public SessionOptions() {}
    }
}