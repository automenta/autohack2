package dumb.code.commands.speech;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class SpeechCommand implements Command {
    private final MessageHandler messageHandler;

    public SpeechCommand(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Speech command is not yet implemented.");
    }

}
