package dumb.tools.commands.create;

import dumb.tools.ToolContext;
import dumb.tools.IFileManager;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

public class CreateCommand implements Command {
    private final ToolContext toolContext;

    public CreateCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = toolContext.messageHandler;
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /create <file>");
            return;
        }

        String file = args[0];
        IFileManager fileManager = toolContext.fileManager;

        try {
            if (fileManager.fileExists(file)) {
                messageHandler.addMessage("system", "Error: File already exists.");
                return;
            }
            fileManager.writeFile(file, ""); // Create an empty file
            messageHandler.addMessage("system", "Created file: " + file);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }
}