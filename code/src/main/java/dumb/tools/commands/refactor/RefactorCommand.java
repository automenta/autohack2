package dumb.tools.commands.refactor;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class RefactorCommand implements Command {
    private final ToolContext code;
    private final MessageHandler messageHandler;

    public RefactorCommand(ToolContext code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Refactor command is not yet implemented.");
    }
}
