package dumb.code.commands.apply;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.VersionControlTool;
import dumb.code.util.DiffUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public record ApplyCommand(VersionControlTool versionControlTool, MessageHandler messageHandler) implements Command {

    @Override
    public void init() {
        // No initialization needed for ApplyCommand
    }

    @Override
    public void execute(String[] args) {
        try {
            String currentDiff = versionControlTool.diff();

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