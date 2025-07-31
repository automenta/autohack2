package dumb.code.commands.speech;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class SpeechCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public SpeechCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Speech command is not yet implemented.");
    }

}
