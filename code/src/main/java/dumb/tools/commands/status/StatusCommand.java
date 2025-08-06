package dumb.tools.commands.status;

import dumb.tools.ToolContext;
import dumb.tools.commands.Command;
import dumb.tools.versioning.Backend;

import java.util.concurrent.CompletableFuture;

public class StatusCommand implements Command {
    private final ToolContext code;

    public StatusCommand(ToolContext code) {
        this.code = code;
    }

    @Override
    public void execute(String[] args) {
        Backend backend = code.getBackend();
        CompletableFuture<String> statusFuture = backend.status();

        statusFuture.thenAccept(status -> code.messageHandler.addMessage("system", status)).exceptionally(error -> {
            code.messageHandler.addMessage("system", "Error: " + error.getMessage());
            return null;
        });
    }

}