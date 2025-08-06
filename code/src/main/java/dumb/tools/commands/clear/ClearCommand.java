package dumb.tools.commands.clear;

import dumb.tools.ToolContext;
import dumb.tools.Workspace;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public record ClearCommand(ToolContext toolContext) implements Command {

    @Override
    public void init() {
        // No initialization needed for ClearCommand
    }

    @Override
    public void execute(String[] args) {
        try {
            Workspace workspace = toolContext.getWorkspace();
            MessageHandler messageHandler = toolContext.messageHandler;

            workspace.setWorkspace("");
            messageHandler.addMessage("system", "Workspace cleared.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for ClearCommand
    }
}