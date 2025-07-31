package dumb.code.commands.status;

import dumb.code.Backend;
import dumb.code.Context;
import dumb.code.commands.Command;

import java.util.concurrent.CompletableFuture;

public class StatusCommand implements Command {
    private final Context context;

    public StatusCommand(Context context) {
        this.context = context;
    }

    @Override
    public void execute(String[] args) {
        Backend backend = context.getBackend();
        CompletableFuture<String> statusFuture = backend.status();

        statusFuture.thenAccept(status -> context.messageHandler.addMessage("system", status)).exceptionally(error -> {
            context.messageHandler.addMessage("system", "Error: " + error.getMessage());
            return null;
        });
    }

}