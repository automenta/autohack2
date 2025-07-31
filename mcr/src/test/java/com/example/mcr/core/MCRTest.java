package com.example.mcr.core;

import com.example.mcr.translation.TranslationStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MCRTest {

    @Test
    void testMCRInitialization() {
        MCR.Config config = new MCR.Config();
        MCR.LlmConfig llmConfig = new MCR.LlmConfig();
        llmConfig.provider = "openai";
        llmConfig.apiKey = "test-key";
        config.llm = llmConfig;

        MCR mcr = new MCR(config);
        assertNotNull(mcr.getLlmClient());
        assertEquals("gpt-3.5-turbo", mcr.llmModel);
    }

    @Test
    void testCreateSession() {
        MCR.Config config = new MCR.Config();
        MCR mcr = new MCR(config);
        Session session = mcr.createSession(new Session.SessionOptions());
        assertNotNull(session);
    }

    @Test
    void testRegisterStrategy() {
        MCR.Config config = new MCR.Config();
        MCR mcr = new MCR(config);

        class MockStrategy implements TranslationStrategy {
            @Override
            public String translate(String naturalLanguage) {
                return "mock_translation";
            }
        }

        mcr.registerStrategy("mock", new MockStrategy());
        assertTrue(mcr.strategyRegistry.containsKey("mock"));
    }
}