package dumb.code.commands.test;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class TestCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public TestCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Test command is not yet implemented.");
    }

}
