package dumb.code.commands.create;

import dumb.code.Code;
import dumb.code.IFileManager;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class CreateCommand implements Command {
    private final Code code;

    public CreateCommand(Code code) {
        this.code = code;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = code.messageHandler;
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /create <file>");
            return;
        }

        String file = args[0];
        IFileManager fileManager = code.fileManager;

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