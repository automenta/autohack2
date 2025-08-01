package dumb.code.commands.codebase;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class CodebaseCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public CodebaseCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Codebase command is not yet implemented.");
    }

}
