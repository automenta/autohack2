package dumb.code.commands.clear;

import dumb.code.CodebaseManager;
import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public record ClearCommand(Context context) implements Command {

    @Override
    public void init() {
        // No initialization needed for ClearCommand
    }

    @Override
    public void execute(String[] args) {
        try {
            CodebaseManager codebaseManager = context.codebaseManager;
            MessageHandler messageHandler = context.messageHandler;

            codebaseManager.setCodebase("");
            messageHandler.addMessage("system", "Codebase cleared.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for ClearCommand
    }
}