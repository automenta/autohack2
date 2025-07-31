package dumb.code.commands.record;

import dumb.code.Backend;
import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class RecordCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public RecordCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        Backend backend = context.getBackend();
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
