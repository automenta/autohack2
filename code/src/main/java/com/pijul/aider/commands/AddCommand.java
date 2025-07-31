package com.pijul.aider.commands;

import com.pijul.aider.PijulAider;
import com.pijul.aider.Backend;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class AddCommand implements Command {
    private final PijulAider aider;

    public AddCommand(PijulAider aider) {
        this.aider = aider;
    }

    @Override
    public void execute(String[] args) {
        boolean hasDot = Arrays.stream(args).anyMatch(arg -> arg.equals("."));
        boolean hasU = Arrays.stream(args).anyMatch(arg -> arg.equals("-u"));

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
            Backend backend = aider.getBackendManager().getBackend();

            for (String filePattern : files) {
                Path matchingPath = Paths.get(filePattern);
                if (Files.isDirectory(matchingPath)) {
                    Files.list(matchingPath).forEach(path -> processFile(path, backend));
                } else {
                    processFile(matchingPath, backend);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFile(Path filePath, Backend backend) {
        try {
            backend.add(filePath.toString());
            byte[] contentBytes = Files.readAllBytes(filePath);
            String content = new String(contentBytes, StandardCharsets.UTF_8);
            String currentCodebase = aider.getCodebaseManager().getCodebase();
            currentCodebase += "--- " + filePath.toString() + " ---\n" + content + "\n\n";
            aider.getCodebaseManager().setCodebase(currentCodebase);
            aider.getUiManager().displayMessage("Added and staged " + filePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addAllTracked() {
        try {
            Backend backend = aider.getBackendManager().getBackend();
            backend.listTrackedFiles().thenAccept(trackedFiles -> {
                addFiles(trackedFiles.toArray(new String[0]));
            }).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAllUntracked() {
        try {
            Backend backend = aider.getBackendManager().getBackend();
            backend.listUntrackedFiles().thenAccept(untrackedFiles -> {
                addFiles(untrackedFiles.toArray(new String[0]));
            }).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
