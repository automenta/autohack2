package dumb.code.help;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHelp {

    protected List<String> steps = new ArrayList<>();
    protected int currentStep = 0;

    public abstract String getWelcomeMessage();

    public String getCurrentStepMessage() {
        if (currentStep < steps.size()) {
            return steps.get(currentStep);
        }
        return "You have completed the help. Well done! 🎉";
    }

    public boolean isFinished() {
        return currentStep >= steps.size();
    }

    public abstract void processInput(String input);

    protected void nextStep() {
        currentStep++;
    }
}
