package com.pijul.hack.tools;

import com.pijul.mcr.tools.Tool;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class FileSystemTool implements Tool {

    @Override
    public String getName() {
        return "fileSystem";
    }

    @Override
    public String getDescription() {
        return "A tool for reading and writing files.";
    }

    @Override
    public String execute(Map<String, Object> args) {
        String methodName = (String) args.get("method");
        if ("readFile".equals(methodName)) {
            return readFile((String) args.get("path"));
        } else if ("writeFile".equals(methodName)) {
            return writeFile((String) args.get("path"), (String) args.get("content"));
        } else if ("listFiles".equals(methodName)) {
            return listFiles((String) args.get("path"));
        } else {
            return "Error: Unknown method " + methodName;
        }
    }

    private String readFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    private String writeFile(String path, String content) {
        try {
            Files.write(Paths.get(path), content.getBytes());
            return "File written successfully.";
        } catch (IOException e) {
            return "Error writing file: " + e.getMessage();
        }
    }

    private String listFiles(String path) {
        try {
            return Files.list(Paths.get(path))
                    .map(p -> p.getFileName().toString())
                    .reduce("", (a, b) -> a + b + "\n");
        } catch (IOException e) {
            return "Error listing files: " + e.getMessage();
        }
    }
}
