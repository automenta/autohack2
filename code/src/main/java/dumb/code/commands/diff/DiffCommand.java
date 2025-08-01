package dumb.code.commands.diff;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.versioning.Backend;

public record DiffCommand(Code code) implements Command {

    @Override
    public void execute(String[] args) {
        Backend backend = code.getBackend();
        MessageHandler messageHandler = code.messageHandler;

        try {
            String diff = backend.diff().get();
            code.setDiff(diff);
            messageHandler.addMessage("system", diff);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}