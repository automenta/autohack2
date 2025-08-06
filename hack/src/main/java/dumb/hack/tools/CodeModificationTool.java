package dumb.hack.tools;

import dumb.tools.Workspace;
import dumb.tools.IFileManager;
import dumb.common.tools.Tool;

import java.util.Map;

public record CodeModificationTool(IFileManager fileManager, Workspace workspace) implements Tool {

    @Override
    public String name() {
        return "modify_file";
    }

    @Override
    public String description() {
        return "Directly modifies a file with new content. Arguments: FilePath (String), NewContent (String). Returns a confirmation message.";
    }

    @Override
    public String run(Map<String, Object> args) {
        if (!args.containsKey("FilePath") || !args.containsKey("NewContent")) {
            return "Error: Missing required arguments 'FilePath' or 'NewContent'.";
        }
        String filePath = (String) args.get("FilePath");
        String newContent = (String) args.get("NewContent");

        try {
            fileManager.writeFile(filePath, newContent);
            workspace.trackFile(filePath).join(); // Ensure the change is tracked
            return "Successfully modified file: " + filePath;
        } catch (java.io.IOException e) {
            return "Error modifying file " + filePath + ": " + e.getMessage();
        }
    }
}
