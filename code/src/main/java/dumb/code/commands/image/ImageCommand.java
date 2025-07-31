package dumb.code.commands.image;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class ImageCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public ImageCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Image command is not yet implemented.");
    }

}
