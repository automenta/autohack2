package dumb.tools.commands.speech;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class SpeechCommand implements Command {
    private final ToolContext code;
    private final MessageHandler messageHandler;

    public SpeechCommand(ToolContext code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Speech command is not yet implemented.");
    }

}
