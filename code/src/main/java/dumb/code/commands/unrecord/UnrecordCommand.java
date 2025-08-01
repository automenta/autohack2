package dumb.code.commands.unrecord;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class UnrecordCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public UnrecordCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Unrecord command is not yet implemented.");
    }

}
