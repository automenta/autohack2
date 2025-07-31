package dumb.hack.commands;

import dumb.hack.HackContext;
import dumb.mcr.ReasoningResult;
import dumb.mcr.Session;

public record ReasonCommand(HackContext context) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            context.getMessageHandler().handleMessage("Usage: /reason <your task>");
            return;
        }

        Session mcrSession = context.getMcrSession();
        if (mcrSession == null) {
            context.getMessageHandler().handleMessage("MCR Session not initialized.");
            return;
        }

        String task = String.join(" ", args);
        context.getMessageHandler().handleMessage("Reasoning about task: " + task);

        ReasoningResult result = mcrSession.reason(task);

        context.getMessageHandler().handleMessage("\n--- Reasoning History ---");
        for (String step : result.history()) {
            context.getMessageHandler().handleMessage(step);
        }
        context.getMessageHandler().handleMessage("--- End of History ---\n");

        context.getMessageHandler().handleMessage("Final Answer: " + result.answer());
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
