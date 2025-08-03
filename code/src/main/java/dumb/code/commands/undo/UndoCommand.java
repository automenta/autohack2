package dumb.code.commands.undo;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class UndoCommand implements Command {
    private final MessageHandler messageHandler;

    public UndoCommand(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Undo command is not yet implemented.");
    }

}
