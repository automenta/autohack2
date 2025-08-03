package dumb.code.commands.run;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.util.IProcessRunner;
import dumb.code.util.ProcessResult;

public class RunCommand implements Command {
    private final IProcessRunner processRunner;
    private final MessageHandler messageHandler;

    public RunCommand(IProcessRunner processRunner, MessageHandler messageHandler) {
        this.processRunner = processRunner;
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /run <command>");
            return;
        }

        ProcessResult result = processRunner.run(args);
        messageHandler.addMessage("system", "Command finished with exit code " + result.exitCode() + ":\n" + result.output());
    }
}
