package dumb.code.commands.clear;

import dumb.code.Code;
import dumb.code.CodebaseManager;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public record ClearCommand(Code code) implements Command {

    @Override
    public void init() {
        // No initialization needed for ClearCommand
    }

    @Override
    public void execute(String[] args) {
        try {
            CodebaseManager codebaseManager = code.codebaseManager;
            MessageHandler messageHandler = code.messageHandler;

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