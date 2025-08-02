package dumb.code.help.verification;

import dumb.code.Code;
import dumb.code.project.ProjectTemplate;
import java.util.concurrent.ExecutionException;

public class GitStatusCleanVerificationStrategy implements VerificationStrategy {
    @Override
    public boolean verify(Code code, ProjectTemplate.TutorialGoal goal, String[] lastCommand) {
        try {
            String status = code.getCodebaseManager().getVersioningBackend().status().get();
            // Assuming an empty status means clean. This might need adjustment
            // depending on the actual output of the versioning backend.
            return status == null || status.isBlank();
        } catch (InterruptedException | ExecutionException e) {
            // Log the error
            e.printStackTrace();
            return false;
        }
    }
}
