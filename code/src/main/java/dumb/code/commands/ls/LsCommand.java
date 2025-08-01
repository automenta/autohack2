package dumb.code.commands.ls;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class LsCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public LsCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Ls command is not yet implemented.");
    }

}
