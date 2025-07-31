package com.pijul.hack.commands;

import com.pijul.hack.Container;
import com.pijul.mcr.ReasoningResult;
import com.pijul.mcr.Session;

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

        Session mcrSession = container.getMcrSession();
        if (mcrSession == null) {
            container.getMessageHandler().handleMessage("MCR Session not initialized.");
            return;
        }

        String task = String.join(" ", args);
        container.getMessageHandler().handleMessage("Reasoning about task: " + task);

        ReasoningResult result = mcrSession.reason(task);

        container.getMessageHandler().handleMessage("\n--- Reasoning History ---");
        for (String step : result.getHistory()) {
            container.getMessageHandler().handleMessage(step);
        }
        container.getMessageHandler().handleMessage("--- End of History ---\n");

        container.getMessageHandler().handleMessage("Final Answer: " + result.getAnswer());
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
