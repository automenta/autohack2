package dumb.tools.commands.ls;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class LsCommand implements Command {
    private final ToolContext toolContext;
    private final MessageHandler messageHandler;

    public LsCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
        this.messageHandler = toolContext.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Ls command is not yet implemented.");
    }

}
