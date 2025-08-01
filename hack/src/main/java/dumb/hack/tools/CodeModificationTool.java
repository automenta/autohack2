package dumb.hack.tools;

import dumb.code.CodebaseManager;
import dumb.code.FileManager;
import dumb.mcr.tools.Tool;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public class CodeModificationTool implements Tool {

    private final FileManager fileManager;
    private final CodebaseManager codebaseManager;

    public CodeModificationTool(FileManager fileManager, CodebaseManager codebaseManager) {
        this.fileManager = fileManager;
        this.codebaseManager = codebaseManager;
    }

    @Override
    public String name() {
        return "modify_file";
    }

    @Override
    public String description() {
        return "Proposes a modification to a file. Arguments: FilePath (String), NewContent (String). Returns a special string that represents the proposed change.";
    }

    @Override
    public String run(Map<String, Object> args) {
        if (!args.containsKey("FilePath") || !args.containsKey("NewContent")) {
            return "Error: Missing required arguments 'FilePath' or 'NewContent'.";
        }
        String filePath = (String) args.get("FilePath");
        String newContent = (String) args.get("NewContent");

        // Instead of writing the file, return a special string with the proposed change.
        // The format is "diff:filepath:base64_encoded_new_content"
        String encodedContent = Base64.getEncoder().encodeToString(newContent.getBytes());
        return "diff:" + filePath + ":" + encodedContent;
    }
}
