package dumb.tools.commands.unrecord;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class UnrecordCommand implements Command {
    private final ToolContext code;
    private final MessageHandler messageHandler;

    public UnrecordCommand(ToolContext code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Unrecord command is not yet implemented.");
    }

}
