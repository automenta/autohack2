package dumb.hack.commands;

import dumb.tools.Workspace;
import dumb.tools.IFileManager;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;
import dumb.hack.util.CodeParser;
import dumb.mcr.ReasoningResult;
import dumb.mcr.Session;
import dumb.mcr.step.PrologStep;
import dumb.mcr.step.StepResult;
import dumb.mcr.step.ToolStep;

import java.util.stream.Collectors;

public record ReasonCommand(
        Session mcrSession,
        Workspace workspace,
        MessageHandler messageHandler,
        IFileManager fileManager,
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

        // Automatically proceed with the task

        messageHandler.addMessage("system", "Reasoning...");
        addCodebaseContext();

        ReasoningResult result = mcrSession.reason(task);

        messageHandler.addMessage("system", "\n--- Reasoning History ---");
        for (StepResult step : result.history()) {
            displayStep(step);
        }
        messageHandler.addMessage("system", "--- End of History ---\n");

        String answer = result.answer();
        messageHandler.addMessage("system", "Final Answer: " + answer);
    }

    private void displayStep(StepResult step) {
        switch (step) {
            case PrologStep prologStep -> {
                String solutions = "No solutions found.";
                if (prologStep.result() != null && prologStep.result().getBindings() != null && !prologStep.result().getBindings().isEmpty()) {
                    solutions = prologStep.result().getBindings().stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));
                }
                messageHandler.addMessage("system", "🤖 Thought: " + prologStep.goal() + " -> " + solutions);
            }
            case ToolStep toolStep -> {
                messageHandler.addMessage("system", "🛠️ Tool Call: " + toolStep.toolName());
                messageHandler.addMessage("system", "   Args: " + toolStep.args());
                messageHandler.addMessage("system", "   Result: " + toolStep.result());
            }
            default -> messageHandler.addMessage("system", "Unknown step type: " + step.getClass().getName());
        }
    }


    private void addCodebaseContext() {
        CodeParser codeParser = new CodeParser();
        java.util.List<String> files = workspace.getFiles();
        for (String file : files) {
            String content = workspace.getFileContent(file);
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

        try {
            String status = workspace.getVersioningBackend().status().get();
            if (status != null && !status.isBlank()) {
                mcrSession.assertProlog("git_status(\"" + status + "\").");
            }
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            messageHandler.addMessage("system", "Error getting git status: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
