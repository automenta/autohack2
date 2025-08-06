package dumb.tools.commands.conflicts;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;
import dumb.tools.versioning.Backend;
import dumb.tools.versioning.PijulBackend;

public class ConflictsCommand implements Command {
    private final ToolContext toolContext;

    public ConflictsCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
    }

    @Override
    public void execute(String[] args) {
        Backend backend = toolContext.getBackend();
        MessageHandler messageHandler = toolContext.messageHandler;

        if (backend instanceof PijulBackend pijulBackend) {
            try {
                String conflicts = pijulBackend.conflicts().get();
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
        } else {
            messageHandler.addMessage("system", "This backend does not support conflicts.");
        }
    }

}