package dumb.tools.commands.run;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;
import dumb.tools.util.ProcessResult;

public class RunCommand implements Command {
    private final ToolContext code;

    public RunCommand(ToolContext code) {
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
        messageHandler.addMessage("system", "Command finished with exit code " + result.exitToolContext() + ":\n" + result.output());
    }
}
