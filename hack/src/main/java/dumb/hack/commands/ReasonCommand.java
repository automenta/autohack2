package dumb.hack.commands;

import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import dumb.code.CodebaseManager;
import dumb.code.FileManager;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.mcr.ReasoningResult;
import dumb.mcr.Session;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public record ReasonCommand(
        Session mcrSession,
        CodebaseManager codebaseManager,
        MessageHandler messageHandler,
        FileManager fileManager // Added FileManager
) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /reason <your task>");
            return;
        }

        if (mcrSession == null) {
            messageHandler.addMessage("system", "MCR Session not initialized.");
            return;
        }

        String task = String.join(" ", args);
        messageHandler.addMessage("system", "Reasoning about task: " + task);

        // --- New logic using CodeParser ---
        dumb.mcr.code.CodeParser codeParser = new dumb.mcr.code.CodeParser();
        java.util.List<String> files = codebaseManager.getFiles();
        for (String file : files) {
            String content = codebaseManager.getFileContent(file);
            if (content != null) {
                java.util.List<String> facts = codeParser.parse(java.nio.file.Paths.get(file), content);
                for (String fact : facts) {
                    mcrSession.assertProlog(fact);
                }
            }
        }
        // --- End of new logic ---

        ReasoningResult result = mcrSession.reason(task);

        messageHandler.addMessage("system", "\n--- Reasoning History ---");
        for (String step : result.history()) {
            messageHandler.addMessage("system", "  - " + step);
        }
        messageHandler.addMessage("system", "--- End of History ---\n");

        String answer = result.answer();
        if (answer != null && answer.startsWith("diff:")) {
            handleDiff(answer);
        } else {
            messageHandler.addMessage("system", "Final Answer: " + answer);
        }
    }

    private void handleDiff(String diffResponse) {
        String[] parts = diffResponse.split(":", 3);
        if (parts.length != 3) {
            messageHandler.addMessage("system", "Invalid diff response from agent.");
            return;
        }

        String filePath = parts[1];
        String encodedContent = parts[2];
        String newContent = new String(Base64.getDecoder().decode(encodedContent));

        try {
            String oldContent = fileManager.readFile(filePath);

            List<String> oldLines = Arrays.asList(oldContent.split("\n"));
            List<String> newLines = Arrays.asList(newContent.split("\n"));
            Patch<String> patch = com.github.difflib.DiffUtils.diff(oldLines, newLines);
            List<String> diff = UnifiedDiffUtils.generateUnifiedDiff(filePath, filePath, oldLines, patch, 0);

            messageHandler.addMessage("system", "The agent proposes the following changes to " + filePath + ":");
            for (String line : diff) {
                messageHandler.addMessage("diff", line);
            }

            messageHandler.addMessage("system", "Apply this change? (yes/no)");
            String response = messageHandler.promptUser("> ");
            if (response != null && response.equalsIgnoreCase("yes")) {
                fileManager.writeFile(filePath, newContent);
                codebaseManager.trackFile(filePath).join();
                messageHandler.addMessage("system", "Changes applied.");
            } else {
                messageHandler.addMessage("system", "Changes discarded.");
            }

        } catch (IOException e) {
            messageHandler.addMessage("system", "Error handling diff: " + e.getMessage());
        }
    }
}
