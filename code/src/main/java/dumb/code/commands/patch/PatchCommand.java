package dumb.code.commands.patch;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class PatchCommand implements Command {
    private final MessageHandler messageHandler;

    public PatchCommand(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Patch command is not yet implemented.");
    }

}
