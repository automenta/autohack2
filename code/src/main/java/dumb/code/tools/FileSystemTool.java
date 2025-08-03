package dumb.code.tools;

import dumb.mcr.tools.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A tool for interacting with the file system for a specific project.
 * An instance of this tool is configured for a specific root directory.
 */
public class FileSystemTool {

    private final Path root;

    public FileSystemTool(String rootPath) {
        this.root = Paths.get(rootPath);
    }

    private Path resolve(String filePath) {
        return root.resolve(filePath);
    }

    public String name() {
        return "file_system";
    }

    public String description() {
        return "A tool for interacting with the file system.";
    }

    public String run(Map<String, Object> args) {
        // This tool is not meant to be run directly.
        return "This tool is not meant to be run directly.";
    }

    public String readFile(String filePath) throws IOException {
        Path path = resolve(filePath);
        return String.join("\n", Files.readAllLines(path));
    }

    public void writeFile(String filePath, String content) throws IOException {
        Path path = resolve(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, content.getBytes());
    }

    public boolean fileExists(String filePath) {
        Path path = resolve(filePath);
        return Files.exists(path);
    }

    public List<String> listFiles(String directoryPath) throws IOException {
        Path path = resolve(directoryPath);
        return Files.list(path)
                .map(p -> root.relativize(p).toString())
                .map(p -> Files.isDirectory(resolve(p)) ? p + "/" : p)
                .collect(Collectors.toList());
    }

    public String getRootDir() {
        return root.toString();
    }
}
