package dumb.code.commands.commit;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.versioning.Backend;

public record CommitCommand(Code code) implements Command {

    @Override
    public void execute(String[] args) {
        Backend backend = code.getBackend();
        MessageHandler messageHandler = code.messageHandler;
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