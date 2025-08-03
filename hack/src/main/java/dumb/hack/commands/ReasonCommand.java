package dumb.hack.commands;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.CodebaseTool;
import dumb.hack.util.CodeParser;
import dumb.mcr.ReasoningResult;
import dumb.mcr.Session;
import dumb.mcr.step.PrologStep;
import dumb.mcr.step.StepResult;
import dumb.mcr.step.ToolStep;

import java.util.stream.Collectors;

public record ReasonCommand(
        Session mcrSession,
        CodebaseTool codebaseTool,
        MessageHandler messageHandler,
        FileSystemTool fileSystemTool,
        boolean interactive
) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: /reason <your task>");
            return;
        }

        if (mcrSession == null) {
            System.out.println("MCR Session not initialized.");
            return;
        }

        String task = String.join(" ", args);
        System.out.println("Task: " + task);

        // Automatically proceed with the task

        System.out.println("Reasoning...");
        addCodebaseContext();

        ReasoningResult result = mcrSession.reason(task);

        System.out.println("\n--- Reasoning History ---");
        for (StepResult step : result.history()) {
            displayStep(step);
        }
        System.out.println("--- End of History ---\n");

        String answer = result.answer();
        System.out.println("Final Answer: " + answer);
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
                System.out.println("🤖 Thought: " + prologStep.goal() + " -> " + solutions);
            }
            case ToolStep toolStep -> {
                System.out.println("🛠️ Tool Call: " + toolStep.toolName());
                System.out.println("   Args: " + toolStep.args());
                System.out.println("   Result: " + toolStep.result());
            }
            default -> System.out.println("Unknown step type: " + step.getClass().getName());
        }
    }


    private void addCodebaseContext() {
        CodeParser codeParser = new CodeParser();
        java.util.List<String> files = codebaseTool.getFiles();
        for (String file : files) {
            String content = codebaseTool.getFileContent(file);
            if (content != null) {
                java.nio.file.Path filePath = java.nio.file.Paths.get(fileSystemTool.getRootDir(), file);
                java.util.List<String> facts = codeParser.parse(filePath, content);
                for (String fact : facts) {
                    if (fact != null && !fact.isBlank() && fact.matches("^[a-z_]+\\(.*\\)\\.$")) {
                        mcrSession.assertProlog(fact);
                    }
                }
            }
        }

        try {
            String status = codebaseTool.versionControlTool().status().get();
            if (status != null && !status.isBlank()) {
                mcrSession.assertProlog("git_status(\"" + status + "\").");
            }
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            System.out.println("Error getting git status: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
