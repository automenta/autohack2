package dumb.code.commands.query;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import dumb.code.Code;
import dumb.code.LMManager;
import dumb.code.commands.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryCommand implements Command {

    private final Code code;

    public QueryCommand(Code code) {
        this.code = code;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            code.messageHandler.addMessage("system", "Usage: /query <your query>");
            return;
        }

        String prompt = String.join(" ", args);
        String codebase = code.codebaseManager.getCodebaseRepresentation();

        LMManager lmManager = code.lmManager;
        String response = lmManager.generateResponse(codebase + "\n\n---\n\n" + prompt);

        if (response.startsWith("Error:")) {
            code.messageHandler.addMessage("error", response);
            return;
        }

        handleResponse(response);
    }

    private void handleResponse(String response) {
        Pattern codeBlockPattern = Pattern.compile("```(?:java)?\\n(.*?)```", Pattern.DOTALL);
        Matcher matcher = codeBlockPattern.matcher(response);

        if (matcher.find()) {
            String newContent = matcher.group(1).trim();
            // Assuming the first file in the context is the one to be edited.
            // A more robust solution would be to have the LLM specify the file.
            if (code.codebaseManager.getFiles().isEmpty()) {
                code.messageHandler.addMessage("error", "No files in context to edit.");
                return;
            }
            String filePath = code.codebaseManager.getFiles().getFirst();
            applyDiff(filePath, newContent);
        } else {
            code.messageHandler.addMessage("ai", response);
        }
    }

    private void applyDiff(String filePath, String newContent) {
        String oldContent = code.codebaseManager.getFileContent(filePath);
        List<String> oldLines = Arrays.asList(oldContent.split("\n"));
        List<String> newLines = Arrays.asList(newContent.split("\n"));
        Patch<String> patch = DiffUtils.diff(oldLines, newLines);
        List<String> diff = UnifiedDiffUtils.generateUnifiedDiff(filePath, filePath, oldLines, patch, 0);

        code.messageHandler.addMessage("system", "The agent proposes the following changes to " + filePath + ":");
        for (String line : diff) {
            code.messageHandler.addMessage("diff", line);
        }

        // For now, automatically apply the change.
        // A better solution would be to have UI buttons for this.
        try {
            Files.writeString(Path.of(filePath), newContent);
            code.messageHandler.addMessage("system", "Changes applied.");
        } catch (IOException e) {
            code.messageHandler.addMessage("error", "Error writing file: " + e.getMessage());
        }
    }
}
