package dumb.code.commands.debug;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class DebugCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public DebugCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Debug command is not yet implemented.");
    }
}
