package dumb.code.commands.undo;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class UndoCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public UndoCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Undo command is not yet implemented.");
    }

}
