package dumb.code.commands.debug;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class DebugCommand implements Command {
    private final MessageHandler messageHandler;

    public DebugCommand(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Debug command is not yet implemented.");
    }
}
