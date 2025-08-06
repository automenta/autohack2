package dumb.tools.commands.drop;

import dumb.tools.ToolContext;
import dumb.tools.Workspace;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class DropCommand implements Command {
    private final ToolContext toolContext;

    public DropCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
    }

    @Override
    public void init() {
        // No initialization needed for DropCommand
    }

    @Override
    public void execute(String[] args) {
        Workspace workspace = toolContext.getWorkspace();
        MessageHandler messageHandler = toolContext.messageHandler;

        for (String file : args) {
            if (workspace.getFiles().contains(file)) {
                workspace.removeFile(file);
                messageHandler.addMessage("system", "Removed " + file + " from the chat.");
            } else {
                messageHandler.addMessage("system", "File " + file + " not found in the chat.");
            }
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for DropCommand
    }
}