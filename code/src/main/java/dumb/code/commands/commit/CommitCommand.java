package dumb.code.commands.commit;

import dumb.code.Backend;
import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public record CommitCommand(Context context) implements Command {

    @Override
    public void execute(String[] args) {
        Backend backend = context.getBackend();
        MessageHandler messageHandler = context.messageHandler;
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