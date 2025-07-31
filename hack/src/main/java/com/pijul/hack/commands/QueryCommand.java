package com.pijul.hack.commands;

import com.pijul.hack.Container;
import com.pijul.aider.llm.LLMChain;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class QueryCommand implements Command {

    private final Container container;

    public QueryCommand(Container container) {
        this.container = container;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            container.getMessageHandler().handleMessage("Usage: /query <your query>");
            return;
        }

        LLMChain llmChain = container.getLlmChain();
        if (llmChain == null) {
            container.getMessageHandler().handleMessage("LLMChain not initialized.");
            return;
        }

        String query = String.join(" ", args);
        try {
            llmChain.handleQuery(query).get(); // Wait for the future to complete
        } catch (InterruptedException | ExecutionException e) {
            container.getMessageHandler().handleMessage("Error executing query: " + e.getMessage());
        }
    }

    @Override
    public void init() {
        // Nothing to initialize
    }

    @Override
    public void cleanup() {
        // Nothing to clean up
    }
}
