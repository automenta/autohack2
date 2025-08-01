package dumb.hack.commands;

import dumb.code.CodebaseManager;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.mcr.ReasoningResult;
import dumb.mcr.Session;

public record ReasonCommand(
        Session mcrSession,
        CodebaseManager codebaseManager,
        MessageHandler messageHandler
) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /reason <your task>");
            return;
        }

        if (mcrSession == null) {
            messageHandler.addMessage("system", "MCR Session not initialized.");
            return;
        }

        String task = String.join(" ", args);
        messageHandler.addMessage("system", "Reasoning about task: " + task);

        // Add the codebase context to the MCR session
        String codebase = codebaseManager.getCodebaseRepresentation();
        if (codebase != null && !codebase.isEmpty()) {
            // Escape the codebase string to be a valid Prolog atom
            String escapedCodebase = codebase.replace("\\", "\\\\").replace("'", "\\'");
            mcrSession.assertProlog("codebase('" + escapedCodebase + "').");
        }

        ReasoningResult result = mcrSession.reason(task);

        messageHandler.addMessage("system", "\n--- Reasoning History ---");
        for (String step : result.history()) {
            messageHandler.addMessage("system", step);
        }
        messageHandler.addMessage("system", "--- End of History ---\n");
        messageHandler.addMessage("system", "Final Answer: " + result.answer());
    }
}
