package dumb.code.commands.debug;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class DebugCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public DebugCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Debug command is not yet implemented.");
    }
}
