package dumb.code.commands;

import dumb.code.tools.CodebaseTool;
import dumb.code.tools.VersionControlTool;
import dumb.code.tools.FileSystemTool;
import dumb.code.MessageHandler;

import java.io.IOException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class AddCommand implements Command {

    private final MessageHandler messageHandler;
    private final CodebaseTool codebaseTool;
    private final VersionControlTool versionControlTool;
    private final FileSystemTool fileSystemTool;

    public AddCommand(MessageHandler messageHandler, CodebaseTool codebaseTool, VersionControlTool versionControlTool, FileSystemTool fileSystemTool) {
        this.messageHandler = messageHandler;
        this.codebaseTool = codebaseTool;
        this.versionControlTool = versionControlTool;
        this.fileSystemTool = fileSystemTool;
    }

    @Override
    public void execute(String[] args) {
        boolean hasDot = Arrays.asList(args).contains(".");
        boolean hasU = Arrays.asList(args).contains("-u");

        if (hasDot) {
            addAllTracked();
        } else if (hasU) {
            addAllUntracked();
        } else {
            addFiles(args);
        }
    }

    private void addFiles(String[] files) {
        try {
            for (String filePattern : files) {
                Path matchingPath = Paths.get(filePattern);
                if (Files.isDirectory(matchingPath)) {
                    Files.list(matchingPath).forEach(this::processFile);
                } else {
                    processFile(matchingPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFile(Path filePath) {
        try {
            codebaseTool.trackFile(filePath.toString());
            messageHandler.addMessage("system", "Added and staged " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            messageHandler.addMessage("system", "Error adding file: " + filePath);
        }
    }

    private void addAllTracked() {
        try {
            List<String> trackedFiles = versionControlTool.listTrackedFiles();
            addFiles(trackedFiles.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAllUntracked() {
        try {
            List<String> untrackedFiles = versionControlTool.listUntrackedFiles();
            addFiles(untrackedFiles.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for AddCommand
    }
}