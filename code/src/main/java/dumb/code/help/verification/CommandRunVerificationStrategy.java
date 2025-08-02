package dumb.code.help.verification;

import dumb.code.Code;
import dumb.code.project.ProjectTemplate;
import java.util.Arrays;

public class CommandRunVerificationStrategy implements VerificationStrategy {
    @Override
    public boolean verify(Code code, ProjectTemplate.TutorialGoal goal, String[] lastCommand) {
        String expectedCommand = goal.getVerification().get("command");
        if (expectedCommand == null || lastCommand == null || lastCommand.length == 0) {
            return false;
        }

        // This is a simplistic check. It checks if the expected command is a substring
        // of the executed command. E.g. "ls" in "/ls -a"
        String actualCommandStr = String.join(" ", lastCommand);
        return actualCommandStr.contains(expectedCommand);
    }
}
