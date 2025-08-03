package dumb.code.tools.vcs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FileBackend implements Backend {
    private final String rootDir;

    public FileBackend(String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> record(String message) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<String> diff() {
        return CompletableFuture.completedFuture("");
    }

    @Override
    public CompletableFuture<Void> revert(String file) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> revertAll() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<String>> listTrackedFiles() {
        try {
            List<String> files = new ArrayList<>();
            Files.walk(Paths.get(rootDir)).forEach(path -> {
                if (Files.isRegularFile(path)) {
                    files.add(path.toString());
                }
            });
            return CompletableFuture.completedFuture(files);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public CompletableFuture<List<String>> listUntrackedFiles() {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    @Override
    public CompletableFuture<Void> add(String file) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<String> status() {
        return CompletableFuture.completedFuture("File backend does not support status.");
    }

    @Override
    public CompletableFuture<Void> apply(String patch) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> clear() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public boolean isClean() {
        return true;
    }
}
