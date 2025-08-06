package dumb.tools;

import dumb.tools.versioning.Backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Workspace {
    private final Backend versioningBackend;
    private final Map<String, String> fileContents = new HashMap<>();
    private String workspacePath;
    private List<String> files = new ArrayList<>();

    private final IFileManager fileManager;

    public Workspace(ToolContext code) {
        this.versioningBackend = code.getBackend();
        this.fileManager = code.fileManager;
    }

    public CompletableFuture<Void> loadWorkspace(String path) {
        this.workspacePath = path;
        return versioningBackend.listTrackedFiles().thenAccept(trackedFiles -> {
            this.files = trackedFiles;
            this.fileContents.clear();
            for (String file : trackedFiles) {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(file)));
                    this.fileContents.put(file, content);
                } catch (IOException e) {
                    // Handle exception, maybe log it
                    e.printStackTrace();
                }
            }
        });
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public List<String> getFiles() {
        return files;
    }

    public String getFileContent(String filePath) {
        return fileContents.get(filePath);
    }

    public String getWorkspaceRepresentation() {
        return fileContents.entrySet().stream()
                .map(entry -> "--- " + entry.getKey() + " ---\n" + entry.getValue())
                .collect(Collectors.joining("\n\n"));
    }

    public void setWorkspace(String workspaceContent) {
        // This method is now a no-op, as the workspace is managed by fileContents
    }


    public CompletableFuture<String> analyzeWorkspace() {
        // Implement analysis logic here
        return CompletableFuture.completedFuture("Workspace analysis placeholder");
    }

    public CompletableFuture<Void> trackFile(String filePath) {
        if (fileManager.fileExists(filePath)) {
            return versioningBackend.add(filePath).thenRun(() -> {
                try {
                    String content = fileManager.readFile(filePath);
                    this.fileContents.put(filePath, content);
                    if (!this.files.contains(filePath)) {
                        this.files.add(filePath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return CompletableFuture.completedFuture(null);
    }

    public Backend getVersioningBackend() {
        return versioningBackend;
    }

    public void removeFile(String filePath) {
        this.files.remove(filePath);
        this.fileContents.remove(filePath);
    }
}