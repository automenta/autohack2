package dumb.code.versioning;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FileBackend implements Backend {
    private final Map<String, String> files = new HashMap<>();
    private final String rootPath;

    public FileBackend() {
        this.rootPath = ".";
    }

    public FileBackend(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public CompletableFuture<Void> add(String file) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path filePath = Paths.get(rootPath, file);
                if (Files.exists(filePath)) {
                    if (!files.containsKey(file)) {
                        Path backupFilePath = filePath.getParent().resolve(filePath.getFileName().toString() + "." + System.currentTimeMillis() + ".bak");
                        Files.copy(filePath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
                        files.put(file, backupFilePath.toString());
                    }
                } else {
                    throw new RuntimeException("File not found: " + filePath);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> unstage(String file) {
        return CompletableFuture.runAsync(() -> {
            String backupFile = files.get(file);
            if (backupFile != null) {
                try {
                    Files.deleteIfExists(Paths.get(backupFile));
                    files.remove(file);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public CompletableFuture<Void> record(String message) {
        // No-op for file backend
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> apply(String patch) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Create temporary file for patch
                Path tempPatch = Files.createTempFile("patch", ".diff");
                Files.writeString(tempPatch, patch);

                // Apply patch using external tool (e.g., git)
                ProcessBuilder processBuilder = new ProcessBuilder("git", "apply", tempPatch.toString());
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                // Wait for process to complete
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("Failed to apply patch: " + new String(process.getErrorStream().readAllBytes()));
                }

                // Clean up temporary file
                Files.delete(tempPatch);
            } catch (java.io.IOException | InterruptedException e) {
                throw new RuntimeException("Error applying patch", e);
            }
        });
    }

    public CompletableFuture<String> conflicts() {
        return CompletableFuture.completedFuture("[]");
    }

    @Override
    public CompletableFuture<Void> revert(String file) {
        return CompletableFuture.runAsync(() -> {
            String backupFile = files.get(file);
            if (backupFile != null) {
                try {
                    Files.copy(Paths.get(backupFile), Paths.get(rootPath, file), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public CompletableFuture<Void> undo() {
        return CompletableFuture.runAsync(() -> {
            for (String file : files.keySet()) {
                try {
                    revert(file).get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> diff() {
        // Simplified implementation - would need to implement diff logic
        return CompletableFuture.completedFuture("");
    }

    @Override
    public CompletableFuture<Void> clear() {
        return CompletableFuture.runAsync(() -> {
            for (String backupFile : files.values()) {
                try {
                    Files.deleteIfExists(Paths.get(backupFile));
                } catch (Exception e) {
                    // Ignore
                }
            }
            files.clear();
        });
    }

    @Override
    public CompletableFuture<List<String>> listTrackedFiles() {
        return CompletableFuture.completedFuture(new ArrayList<>(files.keySet()));
    }

    @Override
    public CompletableFuture<List<String>> listUntrackedFiles() {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    @Override
    public CompletableFuture<Void> revertAll() {
        return undo();
    }

    @Override
    public CompletableFuture<String> status() {
        return CompletableFuture.completedFuture("not_a_git_repo");
    }

    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.completedFuture(null);
    }
}