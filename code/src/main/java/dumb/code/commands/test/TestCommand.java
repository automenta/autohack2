package dumb.code.commands.test;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.util.ProcessResult;

public class TestCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public TestCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Running tests...");
        ProcessResult result = code.processRunner.run("mvn", "test");
        if (result.exitCode() == 0 && result.output().contains("BUILD SUCCESS")) {
            messageHandler.addMessage("system", "Tests passed!");
        } else {
            messageHandler.addMessage("system", "Tests failed:\n" + result.output());
        }
    }
}
