package dumb.code.help.verification;

import dumb.code.project.ProjectTemplate;
import java.util.Map;

public class VerificationStrategyFactory {

    public static VerificationStrategy getStrategy(ProjectTemplate.TutorialGoal goal) {
        Map<String, String> verification = goal.getVerification();
        if (verification == null || !verification.containsKey("type")) {
            // Default to a strategy that always fails if no type is specified
            return (code, g, cmd) -> false;
        }

        String type = verification.get("type");
        switch (type) {
            case "file_exists":
                return new FileExistsVerificationStrategy();
            case "git_status_clean":
                return new GitStatusCleanVerificationStrategy();
            case "command_run":
                return new CommandRunVerificationStrategy();
            case "all_files_in_chat":
                return new AllFilesInChatVerificationStrategy();
            default:
                // Log a warning about the unknown type
                System.err.println("Unknown verification type: " + type);
                return (code, g, cmd) -> false;
        }
    }
}
