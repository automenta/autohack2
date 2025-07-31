package com.example.mcr.core;

import com.pijul.common.LLMClient;
import com.pijul.common.LLMResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionTest {

    @Mock
    private LLMClient llmClient;

    private MCR mcr;

    @BeforeEach
    void setUp() {
        MCR.Config config = new MCR.Config();
        MCR.LlmConfig llmConfig = new MCR.LlmConfig();
        llmConfig.provider = "openai";
        llmConfig.apiKey = "test-key";
        config.llm = llmConfig;
        
        mcr = new MCR(config) {
            @Override
            protected LLMClient getLlmClient(MCR.LlmConfig llmConfig) {
                return llmClient;
            }
        };
    }

    @Test
    void testNquery() throws ExecutionException, InterruptedException {
        Session.SessionOptions options = new Session.SessionOptions();
        options.translator = "direct";
        Session session = mcr.createSession(options);

        LLMResponse llmResponse = new LLMResponse();
        llmResponse.setContent("bird(tweety).");
        when(llmClient.generate(anyString())).thenReturn(llmResponse);

        session.assertProlog("canary(tweety).");
        session.assertProlog("bird(X) :- canary(X).");

        Session.QueryResult result = session.nquery("Is tweety a bird?", new Session.QueryOptions()).get();

        assertTrue(result.isSuccess());
    }

    @Test
    void testReason() throws ExecutionException, InterruptedException {
        Session.SessionOptions options = new Session.SessionOptions();
        options.translator = "agentic";
        Session session = mcr.createSession(options);

        // Mock the LLM responses for the agentic reasoning loop
        LLMResponse response1 = new LLMResponse();
        response1.setContent("{\"type\":\"query\",\"content\":\"can_fly(tweety).\"}");
        LLMResponse response2 = new LLMResponse();
        response2.setContent("{\"type\":\"conclude\",\"answer\":\"Yes, Tweety can fly.\",\"explanation\":\"Derived from the knowledge base.\"}");

        when(llmClient.generate(anyString()))
                .thenReturn(response1)
                .thenReturn(response2);

        session.assertProlog("can_fly(X) :- bird(X).");
        session.assertProlog("bird(tweety).");

        Session.QueryResult result = session.reason("Can tweety fly?", new Session.QueryOptions()).get();

        assertTrue(result.isSuccess());
        assertNotNull(result.getExplanation());
        assertFalse(result.getExplanation().isEmpty());
    }
}