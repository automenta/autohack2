package dumb.code.commands.diff;

import dumb.code.Backend;
import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public record DiffCommand(Context context) implements Command {

    @Override
    public void execute(String[] args) {
        Backend backend = context.getBackend();
        MessageHandler messageHandler = context.messageHandler;

        try {
            String diff = backend.diff().get();
            context.setDiff(diff);
            messageHandler.addMessage("system", diff);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}