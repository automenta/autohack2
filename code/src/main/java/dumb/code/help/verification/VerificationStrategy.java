package dumb.code.help.verification;

import dumb.code.Code;
import dumb.code.project.ProjectTemplate;

public interface VerificationStrategy {
    /**
     * Verifies if the condition for a tutorial step has been met.
     * @param code The main Code object, providing access to file manager, codebase manager, etc.
     * @param goal The tutorial goal to verify.
     * @param lastCommand The last command executed by the user.
     * @return true if the condition is met, false otherwise.
     */
    boolean verify(Code code, ProjectTemplate.TutorialGoal goal, String[] lastCommand);
}
