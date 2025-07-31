package dumb.code.commands.drop;

import dumb.code.CodebaseManager;
import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class DropCommand implements Command {
    private final Context context;

    public DropCommand(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        // No initialization needed for DropCommand
    }

    @Override
    public void execute(String[] args) {
        CodebaseManager codebaseManager = context.codebaseManager;
        MessageHandler messageHandler = context.messageHandler;

        for (String file : args) {
            if (codebaseManager.getFiles().contains(file)) {
                codebaseManager.removeFile(file);
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