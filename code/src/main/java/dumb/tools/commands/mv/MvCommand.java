package dumb.tools.commands.mv;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MvCommand implements Command {
    private final ToolContext toolContext;

    public MvCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = toolContext.messageHandler;

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