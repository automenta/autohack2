package dumb.code.commands.refactor;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class RefactorCommand implements Command {
    private final MessageHandler messageHandler;

    public RefactorCommand(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Refactor command is not yet implemented.");
    }
}
