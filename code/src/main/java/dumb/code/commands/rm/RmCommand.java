package dumb.code.commands.rm;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RmCommand implements Command {
    private final Code code;

    public RmCommand(Code code) {
        this.code = code;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = code.messageHandler;

        if (args.length < 1) {
            messageHandler.addMessage("system", "Usage: /rm <file1> [file2...]");
            return;
        }

        try {
            for (String filePath : args) {
                Path path = Paths.get(filePath);
                Files.delete(path);
                messageHandler.addMessage("system", "Removed " + filePath);
            }
        } catch (IOException e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}