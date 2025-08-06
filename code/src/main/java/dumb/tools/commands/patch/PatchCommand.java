package dumb.tools.commands.patch;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class PatchCommand implements Command {
    private final ToolContext toolContext;
    private final MessageHandler messageHandler;

    public PatchCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
        this.messageHandler = toolContext.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Patch command is not yet implemented.");
    }

}
