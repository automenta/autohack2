package dumb.code.commands.mv;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MvCommand implements Command {
    private final Code code;

    public MvCommand(Code code) {
        this.code = code;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = code.messageHandler;

        if (args.length < 2) {
            messageHandler.addMessage("system", "Usage: /mv <source> <destination>");
            return;
        }

        String source = args[0];
        String destination = args[1];

        try {
            Path sourcePath = Paths.get(source);
            Path destinationPath = Paths.get(destination);
            Files.move(sourcePath, destinationPath);
            messageHandler.addMessage("system", "Moved " + source + " to " + destination);
        } catch (IOException e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}