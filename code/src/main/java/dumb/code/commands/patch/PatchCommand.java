package dumb.code.commands.patch;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class PatchCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public PatchCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Patch command is not yet implemented.");
    }

}
