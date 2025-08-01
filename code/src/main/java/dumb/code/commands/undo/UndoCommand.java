package dumb.code.commands.undo;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class UndoCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public UndoCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Undo command is not yet implemented.");
    }

}
