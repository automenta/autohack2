package dumb.code.commands.record;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.VersionControlTool;

public class RecordCommand implements Command {
    private final VersionControlTool versionControlTool;
    private final MessageHandler messageHandler;

    public RecordCommand(VersionControlTool versionControlTool, MessageHandler messageHandler) {
        this.versionControlTool = versionControlTool;
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        String message = String.join(" ", args);

        if (message.isEmpty()) {
            messageHandler.addMessage("system", "Error: A record message is required.");
            return;
        }

        try {
            versionControlTool.record(message);
            messageHandler.addMessage("system", "Changes recorded.");
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}
