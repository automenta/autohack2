package dumb.code.commands.codebase;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.CodebaseTool;

public class CodebaseCommand implements Command {
    private final CodebaseTool codebaseTool;
    private final MessageHandler messageHandler;

    public CodebaseCommand(CodebaseTool codebaseTool, MessageHandler messageHandler) {
        this.codebaseTool = codebaseTool;
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        String representation = codebaseTool.getCodebaseRepresentation();
        messageHandler.addMessage("system", "Current codebase context:\n" + representation);
    }

}
