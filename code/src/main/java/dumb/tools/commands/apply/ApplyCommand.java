package dumb.tools.commands.apply;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;
import dumb.tools.util.DiffUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public record ApplyCommand(ToolContext toolContext) implements Command {

    @Override
    public void init() {
        // No initialization needed for ApplyCommand
    }

    @Override
    public void execute(String[] args) {
        try {
            MessageHandler messageHandler = toolContext.messageHandler;
            String currentDiff = toolContext.getDiff(); // Assuming Container has getDiff() method

            if (currentDiff == null || currentDiff.isEmpty()) {
                messageHandler.addMessage("system", "No diff to apply.");
                return;
            }

            if (args.length == 0) {
                messageHandler.addMessage("system", "Please provide a file path to apply the patch to.");
                return;
            }

            String filePath = args[0];
            var filePathPath = Paths.get(filePath);

            String fileContent = new String(Files.readAllBytes(filePathPath));

            try {
                String result = DiffUtils.applyPatch(fileContent, currentDiff);
                Files.write(filePathPath, result.getBytes());
                messageHandler.addMessage("system", "Applied patch to " + filePath);
                toolContext.setDiff(null); // Clear the diff after application
            } catch (Exception e) {
                messageHandler.addMessage("system", "Failed to apply patch: " + e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for ApplyCommand
    }
}