package dumb.code.commands.conflicts;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.VersionControlTool;

public class ConflictsCommand implements Command {
    private final VersionControlTool versionControlTool;
    private final MessageHandler messageHandler;

    public ConflictsCommand(VersionControlTool versionControlTool, MessageHandler messageHandler) {
        this.versionControlTool = versionControlTool;
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        try {
            String conflicts = versionControlTool.conflicts();
            // Attempt to parse as JSON array
            conflicts = conflicts.replace("[", "").replace("]", "").replace("\"", "");
            String[] conflictArray = conflicts.split(",");
            if (conflictArray.length > 0 && !conflictArray[0].isEmpty()) {
                StringBuilder conflictMessage = new StringBuilder("Conflicts:\n");
                for (String conflict : conflictArray) {
                    conflictMessage.append("- ").append(conflict.trim()).append("\n");
                }
                messageHandler.addMessage("system", conflictMessage.toString());
            } else {
                messageHandler.addMessage("system", "No conflicts found.");
            }
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error getting conflicts: " + e.getMessage());
        }
    }

}