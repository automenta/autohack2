package dumb.code.commands.status;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.VersionControlTool;

public class StatusCommand implements Command {
    private final VersionControlTool versionControlTool;
    private final MessageHandler messageHandler;

    public StatusCommand(VersionControlTool versionControlTool, MessageHandler messageHandler) {
        this.versionControlTool = versionControlTool;
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        try {
            String status = versionControlTool.status();
            messageHandler.addMessage("system", status);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}