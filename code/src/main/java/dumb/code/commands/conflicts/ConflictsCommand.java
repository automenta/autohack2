package dumb.code.commands.conflicts;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.versioning.Backend;
import dumb.code.versioning.PijulBackend;

public class ConflictsCommand implements Command {
    private final Code code;

    public ConflictsCommand(Code code) {
        this.code = code;
    }

    @Override
    public void execute(String[] args) {
        Backend backend = code.getBackend();
        MessageHandler messageHandler = code.messageHandler;

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