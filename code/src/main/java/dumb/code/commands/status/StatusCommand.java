package dumb.code.commands.status;

import dumb.code.Code;
import dumb.code.commands.Command;
import dumb.code.versioning.Backend;

import java.util.concurrent.CompletableFuture;

public class StatusCommand implements Command {
    private final Code code;

    public StatusCommand(Code code) {
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