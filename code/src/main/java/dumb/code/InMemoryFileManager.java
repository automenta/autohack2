package dumb.code;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryFileManager implements IFileManager {

    private final Map<String, String> files = new HashMap<>();
    private final String rootDir;

    public InMemoryFileManager() {
        this.rootDir = "/";
    }

    public InMemoryFileManager(String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public String readFile(String filePath) throws IOException {
        if (!files.containsKey(filePath)) {
            throw new IOException("File not found: " + filePath);
        }
        return files.get(filePath);
    }

    @Override
    public void writeFile(String filePath, String content) throws IOException {
        System.out.println("InMemoryFileManager: Writing to " + filePath + " with content: " + content);
        files.put(filePath, content);
    }

    @Override
    public boolean fileExists(String filePath) {
        return files.containsKey(filePath);
    }

    @Override
    public String getRootDir() {
        return rootDir;
    }
}
