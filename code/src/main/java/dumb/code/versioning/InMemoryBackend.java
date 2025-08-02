package dumb.code.versioning;

import dumb.code.IFileManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class InMemoryBackend implements Backend {

    private final IFileManager fileManager;
    private final Set<String> trackedFiles = new HashSet<>();

    public InMemoryBackend(IFileManager fileManager) {
        this.fileManager = fileManager;
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
        return CompletableFuture.completedFuture(new ArrayList<>(trackedFiles));
    }

    @Override
    public CompletableFuture<List<String>> listUntrackedFiles() {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    @Override
    public CompletableFuture<Void> add(String file) {
        return CompletableFuture.runAsync(() -> {
            if (fileManager.fileExists(file)) {
                trackedFiles.add(file);
            } else {
                throw new RuntimeException("File not found: " + file);
            }
        });
    }

    @Override
    public CompletableFuture<String> status() {
        return CompletableFuture.completedFuture("");
    }

    @Override
    public CompletableFuture<Void> apply(String patch) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> clear() {
        return CompletableFuture.runAsync(trackedFiles::clear);
    }

    @Override
    public boolean isClean() {
        return true;
    }
}
