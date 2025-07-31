package com.pijul.aider.commands.record;

import com.pijul.aider.Backend;
import com.pijul.aider.Container;
import com.pijul.aider.MessageHandler;
import com.pijul.aider.commands.Command;

public class RecordCommand implements Command {
    private final Container container;
    private final MessageHandler messageHandler;

    public RecordCommand(Container container) {
        this.container = container;
        this.messageHandler = container.getMessageHandler();
    }

    @Override
    public void init() {
    }

    @Override
    public void execute(String[] args) {
        Backend backend = container.getBackend();
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

    @Override
    public void cleanup() {
    }
}
