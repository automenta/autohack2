package dumb.code.commands.doc;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class DocCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public DocCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Doc command is not yet implemented.");
    }
}
