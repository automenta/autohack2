package dumb.code.commands.image;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class ImageCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public ImageCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Image command is not yet implemented.");
    }

}
