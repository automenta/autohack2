package dumb.code.commands.run;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.util.ProcessResult;

public class RunCommand implements Command {
    private final Code code;

    public RunCommand(Code code) {
        this.code = code;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = code.getMessageHandler();
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /run <command>");
            return;
        }

        ProcessResult result = code.processRunner.run(args);
        messageHandler.addMessage("system", "Command finished with exit code " + result.exitCode() + ":\n" + result.output());
    }
}
