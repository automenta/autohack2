package dumb.hack.commands;

import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import dumb.code.CodebaseManager;
import dumb.code.IFileManager;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.hack.util.CodeParser;
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
        IFileManager fileManager, // Added FileManager
        boolean interactive
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
        messageHandler.addMessage("system", "Task: " + task);

        if (interactive) {
            messageHandler.addMessage("system", "Proceed with this task? (yes/no)");
            String response = messageHandler.promptUser("> ");
            if (response == null || !response.equalsIgnoreCase("yes")) {
                messageHandler.addMessage("system", "Task cancelled.");
                return;
            }
        }

        messageHandler.addMessage("system", "Reasoning...");
        // Add codebase context to the MCR session
        addCodebaseContext();

        ReasoningResult result = mcrSession.reason(task);

        messageHandler.addMessage("system", "\n--- Reasoning History ---");
        for (String step : result.history()) {
            messageHandler.addMessage("system", "  - " + step);
        }
        messageHandler.addMessage("system", "--- End of History ---\n");

        String answer = result.answer();
        messageHandler.addMessage("system", "Final Answer: " + answer);
    }

    private void addCodebaseContext() {
        // Add file information
        CodeParser codeParser = new CodeParser();
        java.util.List<String> files = codebaseManager.getFiles();
        for (String file : files) {
            String content = codebaseManager.getFileContent(file);
            if (content != null) {
                java.nio.file.Path filePath = java.nio.file.Paths.get(fileManager.getRootDir(), file);
                java.util.List<String> facts = codeParser.parse(filePath, content);
                for (String fact : facts) {
                    if (fact != null && !fact.isBlank() && fact.matches("^[a-z_]+\\(.*\\)\\.$")) {
                        mcrSession.assertProlog(fact);
                    }
                }
            }
        }

        // Add git status information
        try {
            String status = codebaseManager.getVersioningBackend().status().get();
            mcrSession.assertProlog("git_status(\"" + status + "\").");
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            messageHandler.addMessage("system", "Error getting git status: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
