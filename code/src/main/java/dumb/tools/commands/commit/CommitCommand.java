package dumb.tools.commands.commit;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;
import dumb.tools.versioning.Backend;

public record CommitCommand(ToolContext toolContext) implements Command {

    @Override
    public void execute(String[] args) {
        Backend backend = toolContext.getBackend();
        MessageHandler messageHandler = toolContext.messageHandler;
        String message = String.join(" ", args);

        if (message.isEmpty()) {
            messageHandler.addMessage("system", "Error: A commit message is required.");
            return;
        }

        backend.record(message)
                .thenAccept(v -> messageHandler.addMessage("system", "Changes committed."))
                .exceptionally(e -> {
                    messageHandler.addMessage("system", "Error: " + e.getMessage());
                    return null;
                });
    }

}