package dumb.tools.commands.record;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;
import dumb.tools.versioning.Backend;

public class RecordCommand implements Command {
    private final ToolContext toolContext;
    private final MessageHandler messageHandler;

    public RecordCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
        this.messageHandler = toolContext.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        Backend backend = toolContext.getBackend();
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
