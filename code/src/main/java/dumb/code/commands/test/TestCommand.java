package dumb.code.commands.test;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.util.ProcessResult;

public class TestCommand implements Command {
    private final Context context;
    private final MessageHandler messageHandler;

    public TestCommand(Context context) {
        this.context = context;
        this.messageHandler = context.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Running tests...");
        ProcessResult result = context.processRunner.run("mvn", "test");
        if (result.getExitCode() == 0 && result.getOutput().contains("BUILD SUCCESS")) {
            messageHandler.addMessage("system", "Tests passed!");
        } else {
            messageHandler.addMessage("system", "Tests failed:\n" + result.getOutput());
        }
    }
}
