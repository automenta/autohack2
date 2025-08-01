package dumb.code.commands.doc;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class DocCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public DocCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Doc command is not yet implemented.");
    }
}
