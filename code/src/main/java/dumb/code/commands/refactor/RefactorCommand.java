package dumb.code.commands.refactor;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class RefactorCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public RefactorCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Refactor command is not yet implemented.");
    }
}
