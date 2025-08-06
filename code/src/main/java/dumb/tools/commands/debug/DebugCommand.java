package dumb.tools.commands.debug;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class DebugCommand implements Command {
    private final ToolContext toolContext;
    private final MessageHandler messageHandler;

    public DebugCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
        this.messageHandler = toolContext.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Debug command is not yet implemented.");
    }
}
