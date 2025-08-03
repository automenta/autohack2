package dumb.code.commands.clear;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.CodebaseTool;

public record ClearCommand(CodebaseTool codebaseTool, MessageHandler messageHandler) implements Command {

    @Override
    public void init() {
        // No initialization needed for ClearCommand
    }

    @Override
    public void execute(String[] args) {
        try {
            codebaseTool.clear();
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