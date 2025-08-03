package dumb.code.help;

import dumb.code.MessageHandler;
import dumb.code.project.ProjectTemplate;
import dumb.mcr.MCR;
import dumb.mcr.ReasoningResult;
import dumb.mcr.Session;

import java.util.HashMap;
import java.util.Map;

public class TutorialManager {

    private final ProjectTemplate template;
    private final Session mcrSession;
    private final MessageHandler messageHandler;
    private int currentStep;
    private boolean active;
    private final Map<Integer, Integer> stepFailureCounts;
    private static final int PROACTIVE_HINT_THRESHOLD = 2;

    public TutorialManager(ProjectTemplate template, MCR mcr, MessageHandler messageHandler) {
        this.template = template;
        this.mcrSession = mcr.createSession();
        this.messageHandler = messageHandler;
        this.currentStep = -1;
        this.active = false;
        this.stepFailureCounts = new HashMap<>();
    }

    public String start() {
        this.active = true;
        this.currentStep = 0;
        return getCurrentStepInstructions();
    }

    public void stop() {
        this.active = false;
    }

    public String checkCommand(String[] command) {
        if (!isActive()) {
            return null;
        }

        // 1. Get the current goal and verification info
        ProjectTemplate.TutorialGoal goal = template.getTutorial().get(currentStep);
        Map<String, String> verification = goal.getVerification();
        String verificationType = verification.get("type");

        if (!"prolog".equals(verificationType)) {
            // Fallback or error for non-prolog verification for now
            return "Error: This tutorial step is not configured for MCR verification.";
        }

        // 2. Assert current context into MCR session
        assertContextIntoMcr(command);

        // 3. Execute the prolog query
        String prologQuery = verification.get("query");
        boolean success = mcrSession.query(prologQuery).success();

        // 4. Handle result
        if (success) {
            stepFailureCounts.remove(currentStep); // Reset failure count on success
            currentStep++;
            if (isActive()) {
                return "Correct! " + getCurrentStepInstructions();
            } else {
                this.active = false;
                return "Congratulations, you have completed the tutorial!";
            }
        } else {
            int failures = stepFailureCounts.getOrDefault(currentStep, 0) + 1;
            stepFailureCounts.put(currentStep, failures);

            if (failures >= PROACTIVE_HINT_THRESHOLD) {
                return getProactiveHint(goal, prologQuery);
            } else {
                return "Not quite. " + getCurrentStepInstructions();
            }
        }
    }

    private String getProactiveHint(ProjectTemplate.TutorialGoal goal, String verificationQuery) {
        String prompt = String.format(
            "You are a friendly and helpful AI assistant. A user is STUCK on a tutorial step. " +
            "Their goal is '%s'. The instruction is '%s'. They have failed multiple times. " +
            "The technical condition for success is this Prolog query: '%s'. " +
            "Please provide a specific, actionable HINT to help them satisfy the condition. " +
            "For example, if the query is about a file existing, suggest the command to create it. " +
            "Be encouraging and don't just give the answer away.",
            goal.getGoal(), goal.getInstruction(), verificationQuery
        );

        ReasoningResult result = mcrSession.reason(prompt);
        return "Looks like you're stuck. Here's a hint: " + result.answer();
    }

    private void assertContextIntoMcr(String[] command) {
        mcrSession.clear(); // Clear facts from previous turn

        // Assert last command
        String fullCommand = String.join(" ", command);
        mcrSession.assertProlog(String.format("last_command('%s').", fullCommand));
        mcrSession.assertProlog(String.format("last_command_startsWith(X) :- atom_concat(X, _, '%s').", fullCommand));


        // This part needs to be refactored as we don't have access to the codebase manager anymore
        // For now, we will just assert an empty workspace
        mcrSession.assertProlog("workspace_file('').");
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
