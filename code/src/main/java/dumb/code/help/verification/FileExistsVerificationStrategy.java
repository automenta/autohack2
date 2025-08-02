package dumb.code.help.verification;

import dumb.code.Code;
import dumb.code.project.ProjectTemplate;
import java.io.File;

public class FileExistsVerificationStrategy implements VerificationStrategy {
    @Override
    public boolean verify(Code code, ProjectTemplate.TutorialGoal goal, String[] lastCommand) {
        String fileName = goal.getVerification().get("file");
        if (fileName == null) {
            return false;
        }
        return new File(code.getFileManager().getRootDir(), fileName).exists();
    }
}
