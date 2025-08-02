package dumb.code.help;

import java.util.ArrayList;
import java.util.List;

public class TutorialManager {

    private final List<TutorialStep> steps;
    private int currentStep;

    public TutorialManager() {
        this.steps = new ArrayList<>();
        this.currentStep = -1;
        initializeSteps();
    }

    private void initializeSteps() {
        steps.add(new TutorialStep("Welcome to the AutoHack tutorial! This tutorial will guide you through creating a new project. To start, create a new file named README.md with the /add command.",
                command -> command.length > 1 && command[0].equals("add") && command[1].equals("README.md")));
        steps.add(new TutorialStep("Great! Now, let's record your changes with the /record command. Give it a message, like \"Initial commit\".",
                command -> command.length > 1 && command[0].equals("record")));
        steps.add(new TutorialStep("Excellent! You've completed the basic workflow. The tutorial is now over.",
                command -> false)); // No command completes this step
    }

    public String start() {
        currentStep = 0;
        return getCurrentStepInstructions();
    }

    public String checkCommand(String[] command) {
        if (!isActive()) {
            return null;
        }

        TutorialStep step = steps.get(currentStep);
        if (step.isComplete(command)) {
            currentStep++;
            if (isActive()) {
                return "Correct! " + getCurrentStepInstructions();
            } else {
                return "Congratulations, you have completed the tutorial!";
            }
        } else {
            return "Not quite. " + getCurrentStepInstructions();
        }
    }

    public boolean isActive() {
        return currentStep >= 0 && currentStep < steps.size();
    }

    private String getCurrentStepInstructions() {
        if (isActive()) {
            return steps.get(currentStep).getInstruction();
        }
        return null;
    }
}
