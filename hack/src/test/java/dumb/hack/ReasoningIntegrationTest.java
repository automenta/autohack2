package dumb.hack;

import dumb.code.*;
import dumb.hack.commands.ReasonCommand;
import dumb.hack.tools.CodeModificationTool;
import dumb.mcr.ReasoningResult;
import dumb.mcr.Session;
import dumb.mcr.step.ToolStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ReasoningIntegrationTest {

    private IFileManager fileManager;
    private CodebaseManager codebaseManager;
    private ReasonCommand reasonCommand;
    private Session mcrSession;

    @BeforeEach
    void setUp() throws IOException {
        fileManager = new InMemoryFileManager();
        fileManager.writeFile("test.java", "public class Test {}");

        var code = new Code("file", fileManager, null);
        codebaseManager = code.getCodebaseManager();
        codebaseManager.trackFile("test.java").join();

        mcrSession = Mockito.mock(Session.class);
        MessageHandler messageHandler = Mockito.mock(MessageHandler.class);

        reasonCommand = new ReasonCommand(mcrSession, codebaseManager, messageHandler, fileManager, false);
    }

    @Test
    void testReasonCommandModifiesFile() throws IOException {
        String newContent = "// Hello World\npublic class Test {}";
        var args = new String[]{"Refactor", "the", "Test.java", "file", "to", "add", "a", "comment"};

        // Manually call the tool to simulate MCR's internal execution
        var tool = new CodeModificationTool(fileManager, codebaseManager);
        tool.run(Map.of("FilePath", "test.java", "NewContent", newContent));

        // Mock the reasoning result
        var toolStep = new ToolStep("modify_file", Map.of("FilePath", "test.java", "NewContent", newContent), "Success");
        var reasoningResult = new ReasoningResult("Task completed.", List.of(toolStep));
        when(mcrSession.reason(anyString())).thenReturn(reasoningResult);

        // Execute the command
        reasonCommand.execute(args);

        // Verify the file content
        String actualContent = fileManager.readFile("test.java");
        assertEquals(newContent, actualContent);
    }
}
