package dumb.code.tools.vcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GitBackend implements Backend {

    private CompletableFuture<String> runCommand(String... command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("Command failed with exit code " + exitCode + ": " + output);
                }
                return output.toString();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> initialize() {
        return runCommand("git", "init").thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> record(String message) {
        return runCommand("git", "commit", "-m", message).thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<String> diff() {
        return runCommand("git", "diff");
    }

    @Override
    public CompletableFuture<Void> revert(String file) {
        return runCommand("git", "checkout", "--", file).thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<Void> revertAll() {
        return runCommand("git", "checkout", ".").thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<List<String>> listTrackedFiles() {
        return runCommand("git", "ls-files").thenApply(output -> new ArrayList<>(List.of(output.split("\n"))));
    }

    @Override
    public CompletableFuture<List<String>> listUntrackedFiles() {
        return runCommand("git", "ls-files", "--others", "--exclude-standard").thenApply(output -> new ArrayList<>(List.of(output.split("\n"))));
    }

    @Override
    public CompletableFuture<Void> add(String file) {
        return runCommand("git", "add", file).thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<String> status() {
        return runCommand("git", "status", "--porcelain");
    }

    @Override
    public CompletableFuture<Void> apply(String patch) {
        return runCommand("git", "apply", "-").thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<Void> clear() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public boolean isClean() {
        return status().join().isEmpty();
    }
}
