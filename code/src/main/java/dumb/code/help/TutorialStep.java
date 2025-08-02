package dumb.code.help;

import java.util.function.Predicate;

public class TutorialStep {
    private final String instruction;
    private final Predicate<String[]> commandPredicate;

    public TutorialStep(String instruction, Predicate<String[]> commandPredicate) {
        this.instruction = instruction;
        this.commandPredicate = commandPredicate;
    }

    public String getInstruction() {
        return instruction;
    }

    public boolean isComplete(String[] command) {
        return commandPredicate.test(command);
    }
}
