package dumb.code.commands.unrecord;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class UnrecordCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public UnrecordCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Unrecord command is not yet implemented.");
    }

}
