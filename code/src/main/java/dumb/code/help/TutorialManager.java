package dumb.code.help;

import dumb.code.Code;
import dumb.code.help.verification.VerificationStrategy;
import dumb.code.help.verification.VerificationStrategyFactory;
import dumb.code.project.ProjectTemplate;
import dumb.mcr.MCR;
import dumb.mcr.ReasoningResult;
import dumb.mcr.Session;

public class TutorialManager {

    private final ProjectTemplate template;
    private final Session mcrSession;
    private final Code code;
    private int currentStep;
    private boolean active;

    public TutorialManager(ProjectTemplate template, MCR mcr, Code code) {
        this.template = template;
        this.mcrSession = mcr.createSession();
        this.code = code;
        this.currentStep = -1;
        this.active = false;
    }

    public String start() {
        this.active = true;
        this.currentStep = 0;
        return getCurrentStepInstructions();
    }

    public String checkCommand(String[] command) {
        if (!isActive()) {
            return null;
        }

        ProjectTemplate.TutorialGoal goal = template.getTutorial().get(currentStep);
        VerificationStrategy strategy = VerificationStrategyFactory.getStrategy(goal);

        if (strategy.verify(code, goal, command)) {
            currentStep++;
            if (isActive()) {
                return "Correct! " + getCurrentStepInstructions();
            } else {
                this.active = false;
                return "Congratulations, you have completed the tutorial!";
            }
        } else {
            // The step is not complete, just return null, or maybe the instructions again?
            // Returning the instructions again seems like a good idea.
            return "Not quite. " + getCurrentStepInstructions();
        }
    }

    public boolean isActive() {
        return active && template != null && currentStep < template.getTutorial().size();
    }

    private String getCurrentStepInstructions() {
        if (!isActive()) {
            return null;
        }

        ProjectTemplate.TutorialGoal goal = template.getTutorial().get(currentStep);
        String instruction = goal.getInstruction();

        String prompt = String.format(
            "You are a friendly and helpful AI assistant leading a user through a tutorial. " +
            "The user's current goal is '%s'. " +
            "The basic instruction is: '%s'. " +
            "Please provide a clear, encouraging, and user-friendly message to guide them. " +
            "Keep it concise.",
            goal.getGoal(), instruction);

        ReasoningResult result = mcrSession.reason(prompt);
        return result.answer();
    }
}
