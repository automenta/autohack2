package dumb.code.help.verification;

import dumb.code.Code;
import dumb.code.project.ProjectTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AllFilesInChatVerificationStrategy implements VerificationStrategy {
    @Override
    public boolean verify(Code code, ProjectTemplate.TutorialGoal goal, String[] lastCommand) {
        try {
            List<String> projectFiles;
            try (Stream<Path> walk = Files.walk(Paths.get(code.getFileManager().getRootDir()))) {
                projectFiles = walk.filter(Files::isRegularFile)
                                   .map(path -> Paths.get(code.getFileManager().getRootDir()).relativize(path).toString())
                                   .collect(Collectors.toList());
            }

            List<String> chatFiles = code.getCodebaseManager().getFiles();
            return chatFiles.containsAll(projectFiles);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
