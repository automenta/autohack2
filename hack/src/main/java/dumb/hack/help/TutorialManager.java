package dumb.hack.help;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the interactive tutorial.
 */
public class TutorialManager {

    private final List<String> steps;
    private int currentStep;

    public TutorialManager() {
        this.steps = new ArrayList<>();
        this.currentStep = -1;
        initializeSteps();
    }

    private void initializeSteps() {
        steps.add("Welcome to the AutoHack tutorial!");
        steps.add("This tutorial will guide you through creating a new project.");
        steps.add("Step 1: Create a new file with the /create command. For example: /create README.md");
        steps.add("Step 2: Add some content to the file. For example: /edit README.md");
        steps.add("Step 3: Save your changes with the /record command. For example: /record \"Initial commit\"");
        steps.add("Congratulations, you have completed the tutorial!");
    }

    /**
     * Starts the tutorial.
     *
     * @return The first step of the tutorial.
     */
    public String start() {
        currentStep = 0;
        return steps.get(currentStep);
    }

    /**
     * Proceeds to the next step of the tutorial.
     *
     * @return The next step of the tutorial, or null if the tutorial is complete.
     */
    public String next() {
        if (currentStep < steps.size() - 1) {
            currentStep++;
            return steps.get(currentStep);
        } else {
            return null;
        }
    }

    /**
     * Checks if the tutorial is currently active.
     *
     * @return True if the tutorial is active, false otherwise.
     */
    public boolean isActive() {
        return currentStep != -1 && currentStep < steps.size();
    }
}
