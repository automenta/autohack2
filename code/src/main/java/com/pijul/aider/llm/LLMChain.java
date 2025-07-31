package com.pijul.aider.llm;

import com.pijul.aider.CodebaseManager;
import com.pijul.aider.Container;
import com.pijul.aider.DiffUtils;
import com.pijul.aider.FileManager;
import com.pijul.common.LLMClient;
import com.pijul.common.LLMResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LLMChain {
    private final LLMClient llmClient;
    private final Container container;

    public LLMChain(String provider, String model, String apiKey, Container container) {
        this.llmClient = new LLMClient(provider, model, apiKey);
        this.container = container;
    }

    public CompletableFuture<String> handleQuery(String query, String codebase, String diff, String lastCommandOutput) {
        String prompt = "You are a helpful AI assistant that helps with coding.\n\n" +
                "Here is the current codebase: " + codebase + "\n\n" +
                "Here is the current diff: " + diff + "\n\n" +
                "Here is the output of the last command: " + lastCommandOutput + "\n\n" +
                "Here is the user's query: " + query;

        return CompletableFuture.supplyAsync(() -> {
            LLMResponse response = llmClient.generate(prompt);
            if (response.isSuccess()) {
                String output = response.getContent();
                container.getMessageHandler().addMessage("ai", "Response: " + output);

                // Apply diff if present in response
                if (output.contains("```diff")) {
                    applyDiffFromResponse(output);
                }
                return output;
            } else {
                String error = "Error: " + response.getError();
                container.getMessageHandler().addMessage("error", error);
                return error;
            }
        });
    }

    private void applyDiffFromResponse(String response) {
        try {
            String diffContent = extractDiffContent(response);
            if (diffContent.isEmpty()) {
                container.getMessageHandler().addMessage("error", "Could not extract diff content from response.");
                return;
            }

            String fileName = extractFileNameFromDiff(diffContent);
            if (fileName == null) {
                container.getMessageHandler().addMessage("error", "Could not extract filename from diff.");
                return;
            }

            CodebaseManager codebaseManager = container.getCodebaseManager();
            FileManager fileManager = container.getFileManager();
            Path filePath = Paths.get(codebaseManager.getCodebasePath(), fileName);

            String originalContent = fileManager.readFile(filePath.toString());
            String patchedContent = DiffUtils.applyPatch(originalContent, diffContent);

            fileManager.writeFile(filePath.toString(), patchedContent);

            container.getMessageHandler().addMessage("system", "Applied patch to " + fileName);
        } catch (Exception e) {
            container.getMessageHandler().addMessage("error", "Failed to apply diff: " + e.getMessage());
        }
    }

    private String extractDiffContent(String response) {
        Pattern pattern = Pattern.compile("```diff\\n(.*?)\\n```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private String extractFileNameFromDiff(String diff) {
        Pattern pattern = Pattern.compile("--- a/(.*?)\\n");
        Matcher matcher = pattern.matcher(diff);
        if (matcher.find()) {
            return matcher.group(1);
        }
        // Fallback for --- /dev/null or similar
        pattern = Pattern.compile("\\+\\+\\+ b/(.*?)\\n");
        matcher = pattern.matcher(diff);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}