package dumb.code.commands.diff;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.VersionControlTool;

public record DiffCommand(VersionControlTool versionControlTool, MessageHandler messageHandler) implements Command {

    @Override
    public void execute(String[] args) {
        try {
            String diff = versionControlTool.diff();
            // The diff is now returned, and the caller is responsible for handling it.
            // For now, just print it to the message handler.
            messageHandler.addMessage("system", diff);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}