package dumb.tools;

import dumb.common.tools.Tool;
import java.io.IOException;
import java.util.Map;

public class WriteFileTool implements Tool {

    private final IFileManager fileManager;

    public WriteFileTool(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public String name() {
        return "write_file";
    }

    @Override
    public String description() {
        return "Writes content to a file. Arguments: path (String), content (String)";
    }

    @Override
    public String run(Map<String, Object> args) {
        try {
            String path = (String) args.get("path");
            String content = (String) args.get("content");
            fileManager.writeFile(path, content);
            return "Successfully wrote to file: " + path;
        } catch (IOException e) {
            return "Error writing file: " + e.getMessage();
        }
    }
}
