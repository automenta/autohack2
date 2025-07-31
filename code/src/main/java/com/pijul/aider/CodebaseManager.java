package com.pijul.aider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CodebaseManager {
    private final Backend versioningBackend;
    private String codebasePath;
    private List<String> files = new ArrayList<>();
    private String codebaseContent = ""; // Added for temporary codebase string representation

    public CodebaseManager(Backend versioningBackend) {
        this.versioningBackend = versioningBackend;
    }

    public CompletableFuture<Void> loadCodebase(String path) {
        this.codebasePath = path;
        return versioningBackend.listTrackedFiles().thenApply(files -> {
            this.files = files;
            return null;
        });
    }

    public String getCodebasePath() {
        return codebasePath;
    }

    public List<String> getFiles() {
        return files;
    }

    public String getCodebase() {
        return codebaseContent;
    }

    public void setCodebase(String codebaseContent) {
        this.codebaseContent = codebaseContent;
    }

    public CompletableFuture<String> analyzeCodebase() {
        // Implement code analysis logic here
        return CompletableFuture.completedFuture("Codebase analysis placeholder");
    }

    public CompletableFuture<Void> trackFile(String filePath) {
        Path file = Paths.get(filePath);
        if (Files.exists(file)) {
            return versioningBackend.add(filePath);
        }
        return CompletableFuture.completedFuture(null);
    }
}