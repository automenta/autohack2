package dumb.tools;

import dumb.common.tools.Tool;
import java.io.IOException;
import java.util.Map;

public class ReadFileTool implements Tool {

    private final IFileManager fileManager;

    public ReadFileTool(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public String name() {
        return "read_file";
    }

    @Override
    public String description() {
        return "Reads the content of a file. Arguments: path (String)";
    }

    @Override
    public String run(Map<String, Object> args) {
        try {
            String path = (String) args.get("path");
            return fileManager.readFile(path);
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }
}
