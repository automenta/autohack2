package dumb.code.commands.record;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.versioning.Backend;

public class RecordCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public RecordCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        Backend backend = code.getBackend();
        String message = String.join(" ", args);

        if (message.isEmpty()) {
            messageHandler.addMessage("system", "Error: A record message is required.");
            return;
        }

        backend.record(message)
                .thenAccept(v -> messageHandler.addMessage("system", "Changes recorded."))
                .exceptionally(e -> {
                    messageHandler.addMessage("system", "Error: " + e.getMessage());
                    return null;
                });
    }

}
