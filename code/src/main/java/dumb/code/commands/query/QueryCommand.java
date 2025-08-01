package dumb.code.commands.query;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import dumb.code.Context;
import dumb.code.commands.Command;
import dumb.code.LMManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryCommand implements Command {

    private final Context context;

    public QueryCommand(Context context) {
        this.context = context;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            context.messageHandler.addMessage("system", "Usage: /query <your query>");
            return;
        }

        String prompt = String.join(" ", args);
        String codebase = context.codebaseManager.getCodebaseRepresentation();

        LMManager lmManager = context.LMManager;
        String response = lmManager.generateResponse(codebase + "\n\n---\n\n" + prompt);

        if (response.startsWith("Error:")) {
            context.messageHandler.addMessage("error", response);
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
            if (context.codebaseManager.getFiles().isEmpty()) {
                context.messageHandler.addMessage("error", "No files in context to edit.");
                return;
            }
            String filePath = context.codebaseManager.getFiles().get(0);
            applyDiff(filePath, newContent);
        } else {
            context.messageHandler.addMessage("ai", response);
        }
    }

    private void applyDiff(String filePath, String newContent) {
        try {
            String oldContent = context.codebaseManager.getFileContent(filePath);
            List<String> oldLines = Arrays.asList(oldContent.split("\n"));
            List<String> newLines = Arrays.asList(newContent.split("\n"));
            Patch<String> patch = DiffUtils.diff(oldLines, newLines);
            List<String> diff = UnifiedDiffUtils.generateUnifiedDiff(filePath, filePath, oldLines, patch, 0);

            context.messageHandler.addMessage("system", "The agent proposes the following changes to " + filePath + ":");
            for (String line : diff) {
                context.messageHandler.addMessage("diff", line);
            }

            context.messageHandler.addMessage("system", "Apply this change? (yes/no)");
            String response = context.messageHandler.promptUser("> ");
            if (response != null && response.equalsIgnoreCase("yes")) {
                Files.writeString(Path.of(filePath), newContent);
                context.messageHandler.addMessage("system", "Changes applied.");
            } else {
                context.messageHandler.addMessage("system", "Changes discarded.");
            }
        } catch (IOException e) {
            context.messageHandler.addMessage("error", "Error applying diff: " + e.getMessage());
        }
    }
}
