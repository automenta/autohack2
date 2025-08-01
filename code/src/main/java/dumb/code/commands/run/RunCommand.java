package dumb.code.commands.run;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.util.ProcessResult;

public class RunCommand implements Command {
    private final Context context;

    public RunCommand(Context context) {
        this.context = context;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = context.getMessageHandler();
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /run <command>");
            return;
        }

        ProcessResult result = context.processRunner.run(args);
        messageHandler.addMessage("system", "Command finished with exit code " + result.getExitCode() + ":\n" + result.getOutput());
    }
}
