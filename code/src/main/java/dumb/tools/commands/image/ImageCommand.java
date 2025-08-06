package dumb.tools.commands.image;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class ImageCommand implements Command {
    private final ToolContext toolContext;
    private final MessageHandler messageHandler;

    public ImageCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
        this.messageHandler = toolContext.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Image command is not yet implemented.");
    }

}
