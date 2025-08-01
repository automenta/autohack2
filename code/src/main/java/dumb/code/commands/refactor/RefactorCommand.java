package dumb.code.commands.refactor;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class RefactorCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public RefactorCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Refactor command is not yet implemented.");
    }
}
