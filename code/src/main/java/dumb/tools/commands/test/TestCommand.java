package dumb.tools.commands.test;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;
import dumb.tools.util.ProcessResult;

public class TestCommand implements Command {
    private final ToolContext code;
    private final MessageHandler messageHandler;

    public TestCommand(ToolContext code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Running tests...");
        ProcessResult result = code.processRunner.run("mvn", "test");
        if (result.exitToolContext() == 0 && result.output().contains("BUILD SUCCESS")) {
            messageHandler.addMessage("system", "Tests passed!");
        } else {
            messageHandler.addMessage("system", "Tests failed:\n" + result.output());
        }
    }
}
