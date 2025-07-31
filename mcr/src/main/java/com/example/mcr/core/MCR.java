package com.example.mcr.core;

import com.example.mcr.translation.AgenticReasoning;
import com.example.mcr.translation.DirectToProlog;
import com.example.mcr.translation.JsonToProlog;
import com.example.mcr.translation.TranslationStrategy;
import com.pijul.common.LLMClient;

import java.util.HashMap;
import java.util.Map;

public class MCR {
    final String llmModel;
    final Map<String, TranslationStrategy> strategyRegistry;
    final LLMUsageMetrics totalLlmUsage;
    private final Config config;
    private LLMClient llmClient;

    public MCR(Config config) {
        this.config = config;

        if (config.llm != null && config.llm.provider != null) {
            this.llmClient = getLlmClient(config.llm);
        }

        this.llmModel = (config.llm != null && config.llm.model != null) ? config.llm.model : "gpt-3.5-turbo";
        this.strategyRegistry = new HashMap<>();
        this.strategyRegistry.put("direct", new DirectToProlog(this.llmClient, this.llmModel));
        this.strategyRegistry.put("json", new JsonToProlog(this.llmClient, this.llmModel, 0.7));
        this.strategyRegistry.put("agentic", new AgenticReasoning(this.llmClient, this.llmModel));
        if (config.strategyRegistry != null) {
            this.strategyRegistry.putAll(config.strategyRegistry);
        }
        this.totalLlmUsage = new LLMUsageMetrics();
    }

    public Session createSession(Session.SessionOptions options) {
        return new Session(this, options);
    }

    public LLMClient getLlmClient() {
        return this.llmClient;
    }

    public void registerStrategy(String name, TranslationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        this.strategyRegistry.put(name, strategy);
    }

    public LLMUsageMetrics getLlmMetrics() {
        return new LLMUsageMetrics(totalLlmUsage);
    }

    // Assuming getLlmClient is implemented elsewhere
    protected LLMClient getLlmClient(LlmConfig llmConfig) {
        if (llmConfig == null) {
            throw new IllegalArgumentException("LLM config cannot be null");
        }

        try {
            return new LLMClient(
                    llmConfig.provider,
                    llmConfig.model,
                    llmConfig.apiKey
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid LLM configuration: " + e.getMessage(), e);
        }
    }

    public static class Config {
        public LlmConfig llm;
        public Map<String, TranslationStrategy> strategyRegistry;

        public Config() {
        }
    }

    public static class LlmConfig {
        public String provider;
        public String apiKey;
        public String model;

        public LlmConfig() {
        }
    }

    public static class LLMUsageMetrics {
        public long promptTokens;
        public long completionTokens;
        public long totalTokens;
        public int calls;
        public long totalLatencyMs;

        public LLMUsageMetrics() {
        }

        public LLMUsageMetrics(LLMUsageMetrics source) {
            this.promptTokens = source.promptTokens;
            this.completionTokens = source.completionTokens;
            this.totalTokens = source.totalTokens;
            this.calls = source.calls;
            this.totalLatencyMs = source.totalLatencyMs;
        }
    }
}