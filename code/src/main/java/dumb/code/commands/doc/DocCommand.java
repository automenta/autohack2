package dumb.code.commands.doc;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class DocCommand implements Command {
    private final MessageHandler messageHandler;

    public DocCommand(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Doc command is not yet implemented.");
    }
}
