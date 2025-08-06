package dumb.tools.commands.doc;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class DocCommand implements Command {
    private final ToolContext toolContext;
    private final MessageHandler messageHandler;

    public DocCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
        this.messageHandler = toolContext.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Doc command is not yet implemented.");
    }
}
