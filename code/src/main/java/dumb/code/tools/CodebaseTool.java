package dumb.code.tools;

import dumb.mcr.tools.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CodebaseTool {

    private final VersionControlTool versionControlTool;
    private final FileSystemTool fileSystemTool;
    private final Map<String, String> fileContents = new HashMap<>();
    private String codebasePath;
    private List<String> files = new ArrayList<>();

    public CodebaseTool(VersionControlTool versionControlTool, FileSystemTool fileSystemTool) {
        this.versionControlTool = versionControlTool;
        this.fileSystemTool = fileSystemTool;
        this.codebasePath = fileSystemTool.getRootDir();
        loadCodebase(this.codebasePath);
    }

    private void loadCodebase(String path) {
        this.files = versionControlTool.listTrackedFiles();
        this.fileContents.clear();
        for (String file : this.files) {
            try {
                String content = this.fileSystemTool.readFile(file);
                this.fileContents.put(file, content);
            } catch (IOException e) {
                // Handle exception, maybe log it
                e.printStackTrace();
            }
        }
    }

    public String name() {
        return "codebase";
    }

    public String description() {
        return "A tool for interacting with the codebase.";
    }

    public String run(Map<String, Object> args) {
        // This tool is not meant to be run directly.
        return "This tool is not meant to be run directly.";
    }

    public List<String> getFiles() {
        return files;
    }

    public String getFileContent(String filePath) {
        return fileContents.get(filePath);
    }

    public String getCodebaseRepresentation() {
        return fileContents.entrySet().stream()
                .map(entry -> "--- " + entry.getKey() + " ---\n" + entry.getValue())
                .collect(Collectors.joining("\n\n"));
    }

    public void trackFile(String filePath) {
        if (fileSystemTool.fileExists(filePath)) {
            versionControlTool.add(filePath);
            try {
                String content = fileSystemTool.readFile(filePath);
                this.fileContents.put(filePath, content);
                if (!this.files.contains(filePath)) {
                    this.files.add(filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFile(String filePath) {
        this.files.remove(filePath);
        this.fileContents.remove(filePath);
    }

    public void clear() {
        this.files.clear();
        this.fileContents.clear();
    }
}
