package com.pijul.aider.commands.query;

import com.pijul.aider.Container;
import com.pijul.aider.commands.Command;
import com.pijul.aider.llm.LLMChain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class QueryCommand implements Command {

    private final Container container;
    private LLMChain llmChain;

    public QueryCommand(Container container) {
        this.container = container;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            container.getMessageHandler().addMessage("system", "Usage: /query <your query>");
            return;
        }

        if (llmChain == null) {
            this.llmChain = new LLMChain("ollama", "llama2", "", container, new ArrayList<>(container.getToolProviders()));
        }

        String query = String.join(" ", args);
        try {
            llmChain.handleQuery(query).get(); // Wait for the future to complete
        } catch (InterruptedException | ExecutionException e) {
            container.getMessageHandler().addMessage("error", "Error executing query: " + e.getMessage());
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
