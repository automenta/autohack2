package dumb.code.commands.image;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class ImageCommand implements Command {
    private final MessageHandler messageHandler;

    public ImageCommand(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Image command is not yet implemented.");
    }

}
