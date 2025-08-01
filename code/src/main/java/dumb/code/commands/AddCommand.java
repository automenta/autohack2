package dumb.code.commands;

import dumb.code.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class AddCommand implements Command {

    private final Context context;
    private final MessageHandler messageHandler;
    private final CodebaseManager codebaseManager;
    private final Backend backend;
    private final FileSystem fs;

    public AddCommand(Context context) {
        this.context = context;
        this.messageHandler = context.getMessageHandler();
        this.codebaseManager = context.getCodebaseManager();
        this.backend = context.getBackend();
        this.fs = context.getFiles();
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
        codebaseManager.trackFile(filePath.toString()).thenRun(() -> messageHandler.addMessage("system", "Added and staged " + filePath)).exceptionally(e -> {
            e.printStackTrace();
            messageHandler.addMessage("system", "Error adding file: " + filePath);
            return null;
        });
    }

    private void addAllTracked() {
        try {
            Backend backend = context.getBackend();
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
            Backend backend = context.getBackend();
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