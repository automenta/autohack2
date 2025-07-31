package com.pijul.hack.commands;

import com.pijul.hack.Container;
import com.pijul.aider.llm.LLMChain;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class ReasonCommand implements Command {

    private final Container container;

    public ReasonCommand(Container container) {
        this.container = container;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            container.getMessageHandler().handleMessage("Usage: /reason <your task>");
            return;
        }

        container.getMessageHandler().handleMessage("Reasoning capabilities are not yet implemented.");
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
