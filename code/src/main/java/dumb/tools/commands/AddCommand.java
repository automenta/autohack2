package dumb.tools.commands;

import dumb.tools.ToolContext;
import dumb.tools.Workspace;
import dumb.tools.FileSystem;
import dumb.tools.MessageHandler;
import dumb.tools.versioning.Backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class AddCommand implements Command {

    private final ToolContext toolContext;
    private final MessageHandler messageHandler;
    private final Workspace workspace;
    private final Backend backend;
    private final FileSystem fs;

    public AddCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
        this.messageHandler = toolContext.getMessageHandler();
        this.workspace = toolContext.getWorkspace();
        this.backend = toolContext.getBackend();
        this.fs = toolContext.getFiles();
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
        workspace.trackFile(filePath.toString()).thenRun(() -> messageHandler.addMessage("system", "Added and staged " + filePath)).exceptionally(e -> {
            e.printStackTrace();
            messageHandler.addMessage("system", "Error adding file: " + filePath);
            return null;
        });
    }

    private void addAllTracked() {
        try {
            Backend backend = toolContext.getBackend();
            backend.listTrackedFiles().thenAccept(trackedFiles -> addFiles(trackedFiles.toArray(new String[0]))).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAllUntracked() {
        try {
            Backend backend = toolContext.getBackend();
            backend.listUntrackedFiles().thenAccept(untrackedFiles -> addFiles(untrackedFiles.toArray(new String[0]))).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for AddCommand
    }
}