package com.pijul.aider.commands;

import com.pijul.aider.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class AddCommand implements Command {
    private final Container container;

    public AddCommand(Container container) {
        this.container = container;
    }

    @Override
    public void init() {
        // No initialization needed for AddCommand
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
            Backend backend = container.getBackend();
            MessageHandler messageHandler = container.getMessageHandler();
            FileSystem fs = container.getFileSystem();
            CodebaseManager codebaseManager = container.getCodebaseManager();

            for (String filePattern : files) {
                Path matchingPath = Paths.get(filePattern);
                if (Files.isDirectory(matchingPath)) {
                    Files.list(matchingPath).forEach(path -> processFile(path, backend, messageHandler, fs, codebaseManager));
                } else {
                    processFile(matchingPath, backend, messageHandler, fs, codebaseManager);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFile(Path filePath, Backend backend, MessageHandler messageHandler, FileSystem fs, CodebaseManager codebaseManager) {
        codebaseManager.trackFile(filePath.toString()).thenRun(() -> {
            messageHandler.addMessage("system", "Added and staged " + filePath);
        }).exceptionally(e -> {
            e.printStackTrace();
            messageHandler.addMessage("system", "Error adding file: " + filePath);
            return null;
        });
    }

    private void addAllTracked() {
        try {
            Backend backend = container.getBackend();
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
            Backend backend = container.getBackend();
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