package dumb.code.commands.ls;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class LsCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public LsCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Ls command is not yet implemented.");
    }

}
