package dumb.code.tools.vcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PijulBackend implements Backend {

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
        return runCommand("pijul", "init").thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> record(String message) {
        return runCommand("pijul", "record", "-m", message).thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<String> diff() {
        return runCommand("pijul", "diff");
    }

    @Override
    public CompletableFuture<Void> revert(String file) {
        // Pijul doesn't have a direct equivalent to `git checkout -- <file>`
        // This is a placeholder
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> revertAll() {
        return runCommand("pijul", "reset").thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<List<String>> listTrackedFiles() {
        return runCommand("pijul", "ls-files").thenApply(output -> new ArrayList<>(List.of(output.split("\n"))));
    }

    @Override
    public CompletableFuture<List<String>> listUntrackedFiles() {
        // Pijul doesn't have a direct equivalent to `git ls-files --others`
        // This is a placeholder
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    @Override
    public CompletableFuture<Void> add(String file) {
        return runCommand("pijul", "add", file).thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<String> status() {
        return runCommand("pijul", "status");
    }

    @Override
    public CompletableFuture<Void> apply(String patch) {
        return runCommand("pijul", "apply", "-").thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<Void> clear() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public boolean isClean() {
        return status().join().contains("Nothing to record");
    }

    public CompletableFuture<String> channel(String subcommand, String name) {
        return runCommand("pijul", "channel", subcommand, name);
    }

    public CompletableFuture<String> conflicts() {
        return runCommand("pijul", "conflicts");
    }
}
