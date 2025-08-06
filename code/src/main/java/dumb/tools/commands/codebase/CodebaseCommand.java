package dumb.tools.commands.codebase;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class CodebaseCommand implements Command {
    private final ToolContext toolContext;
    private final MessageHandler messageHandler;

    public CodebaseCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
        this.messageHandler = toolContext.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Codebase command is not yet implemented.");
    }

}
