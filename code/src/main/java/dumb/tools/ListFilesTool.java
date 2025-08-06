package dumb.tools;

import dumb.common.tools.Tool;
import java.io.IOException;
import java.util.Map;

public class ListFilesTool implements Tool {

    private final IFileManager fileManager;

    public ListFilesTool(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public String name() {
        return "list_files";
    }

    @Override
    public String description() {
        return "Lists all files in the root directory. No arguments.";
    }

    @Override
    public String run(Map<String, Object> args) {
        try {
            return String.join("\n", fileManager.listFiles());
        } catch (IOException e) {
            return "Error listing files: " + e.getMessage();
        }
    }
}
