package dumb.code.commands.create;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.FileSystemTool;

public class CreateCommand implements Command {
    private final FileSystemTool fileSystemTool;
    private final MessageHandler messageHandler;

    public CreateCommand(FileSystemTool fileSystemTool, MessageHandler messageHandler) {
        this.fileSystemTool = fileSystemTool;
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /create <file>");
            return;
        }

        String file = args[0];

        try {
            if (fileSystemTool.fileExists(file)) {
                messageHandler.addMessage("system", "Error: File already exists.");
                return;
            }
            fileSystemTool.writeFile(file, ""); // Create an empty file
            messageHandler.addMessage("system", "Created file: " + file);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }
}