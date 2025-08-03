package dumb.code.commands.commit;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.VersionControlTool;

public record CommitCommand(VersionControlTool versionControlTool, MessageHandler messageHandler) implements Command {

    @Override
    public void execute(String[] args) {
        String message = String.join(" ", args);

        if (message.isEmpty()) {
            messageHandler.addMessage("system", "Error: A commit message is required.");
            return;
        }

        try {
            versionControlTool.record(message);
            messageHandler.addMessage("system", "Changes committed.");
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}