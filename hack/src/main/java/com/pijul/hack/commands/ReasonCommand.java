package com.pijul.hack.commands;

import com.example.mcr.core.Session;
import com.pijul.hack.Container;

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

        String task = String.join(" ", args);
        container.getMessageHandler().handleMessage("Reasoning about: " + task);

        Session.SessionOptions options = new Session.SessionOptions();
        options.setStrategy("direct"); // Or any other strategy

        Session session = container.getMcr().createSession(options);
        try {
            String result = session.reason(task, null).toCompletableFuture().get();
            container.getMessageHandler().handleMessage("Result: " + result);
        } catch (InterruptedException | ExecutionException e) {
            container.getMessageHandler().handleMessage("Error during reasoning: " + e.getMessage());
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
