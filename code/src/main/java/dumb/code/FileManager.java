package dumb.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileManager {
    private final Path root;

    public FileManager() {
        this.root = Paths.get(System.getProperty("user.dir"));
    }

    public FileManager(String root) {
        this.root = Paths.get(root);
    }

    private Path resolve(String filePath) {
        return root.resolve(filePath);
    }

    public String readFile(String filePath) throws IOException {
        Path path = resolve(filePath);
        List<String> lines = Files.readAllLines(path);
        return String.join("\n", lines);
    }

    public void writeFile(String filePath, String content) throws IOException {
        Path path = resolve(filePath);
        Files.write(path, content.getBytes());
    }

    public boolean fileExists(String filePath) {
        Path path = resolve(filePath);
        return Files.exists(path);
    }

    // Add more methods as needed
}