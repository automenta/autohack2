package dumb.code.commands.codebase;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class CodebaseCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public CodebaseCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Codebase command is not yet implemented.");
    }

}
