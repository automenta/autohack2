package com.pijul.aider.commands.conflicts;

import com.pijul.aider.Backend;
import com.pijul.aider.Container;
import com.pijul.aider.MessageHandler;
import com.pijul.aider.commands.Command;

public class ConflictsCommand implements Command {
    private final Container container;

    public ConflictsCommand(Container container) {
        this.container = container;
    }

    @Override
    public void init() {
    }

    @Override
    public void execute(String[] args) {
        Backend backend = container.getBackend();
        MessageHandler messageHandler = container.getMessageHandler();

        if (backend instanceof com.pijul.aider.PijulBackend) {
            com.pijul.aider.PijulBackend pijulBackend = (com.pijul.aider.PijulBackend) backend;
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

    @Override
    public void cleanup() {
    }
}