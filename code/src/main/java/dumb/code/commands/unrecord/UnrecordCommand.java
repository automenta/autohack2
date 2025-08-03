package dumb.code.commands.unrecord;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class UnrecordCommand implements Command {
    private final MessageHandler messageHandler;

    public UnrecordCommand(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Unrecord command is not yet implemented.");
    }

}
