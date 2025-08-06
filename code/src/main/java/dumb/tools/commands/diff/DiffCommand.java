package dumb.tools.commands.diff;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;
import dumb.tools.versioning.Backend;

public record DiffCommand(ToolContext toolContext) implements Command {

    @Override
    public void execute(String[] args) {
        Backend backend = toolContext.getBackend();
        MessageHandler messageHandler = toolContext.messageHandler;

        try {
            String diff = backend.diff().get();
            toolContext.setDiff(diff);
            messageHandler.addMessage("system", diff);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}