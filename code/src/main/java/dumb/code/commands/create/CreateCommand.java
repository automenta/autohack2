package dumb.code.commands.create;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateCommand implements Command {
    private final Context context;

    public CreateCommand(Context context) {
        this.context = context;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = context.messageHandler;
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /create <file>");
            return;
        }

        String file = args[0];
        try {
            Path path = Paths.get(file);
            Files.createFile(path);
            messageHandler.addMessage("system", "Created file: " + file);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}