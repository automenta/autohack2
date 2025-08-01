package dumb.code.commands.patch;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class PatchCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public PatchCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Patch command is not yet implemented.");
    }

}
