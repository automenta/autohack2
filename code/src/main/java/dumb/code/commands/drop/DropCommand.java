package dumb.code.commands.drop;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.CodebaseTool;

public class DropCommand implements Command {
    private final CodebaseTool codebaseTool;
    private final MessageHandler messageHandler;

    public DropCommand(CodebaseTool codebaseTool, MessageHandler messageHandler) {
        this.codebaseTool = codebaseTool;
        this.messageHandler = messageHandler;
    }

    @Override
    public void init() {
        // No initialization needed for DropCommand
    }

    @Override
    public void execute(String[] args) {
        for (String file : args) {
            if (codebaseTool.getFiles().contains(file)) {
                codebaseTool.removeFile(file);
                messageHandler.addMessage("system", "Removed " + file + " from the chat.");
            } else {
                messageHandler.addMessage("system", "File " + file + " not found in the chat.");
            }
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for DropCommand
    }
}