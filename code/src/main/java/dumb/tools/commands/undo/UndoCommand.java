package dumb.tools.commands.undo;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class UndoCommand implements Command {
    private final ToolContext code;
    private final MessageHandler messageHandler;

    public UndoCommand(ToolContext code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Undo command is not yet implemented.");
    }

}
