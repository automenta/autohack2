package dumb.hack;

import dumb.code.Code;
import dumb.code.CodebaseManager;
import dumb.code.CommandManager;
import dumb.code.FileManager;
import dumb.code.MessageHandler;
import dumb.code.InMemoryFileManager;
import dumb.hack.commands.ReasonCommand;
import dumb.hack.tools.CodeToolProvider;
import dumb.lm.LMClient;
import dumb.lm.mock.MockChatModel;
import dumb.mcr.MCR;
import dumb.mcr.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ReasoningIntegrationTest {

    private MessageHandler messageHandler;
    private dumb.code.IFileManager fileManager;
    private CodebaseManager codebaseManager;
    private ReasonCommand reasonCommand;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        // Use an in-memory file manager for the test
        fileManager = new dumb.code.InMemoryFileManager();
        fileManager.writeFile("test.java", "public class Test {}");

        // Mock LMClient to return a specific reasoning step
        MockChatModel mockChatModel = new MockChatModel();
        LMClient lmClient = new LMClient(mockChatModel);

        // This is the key part: we mock the LLM's response to the reasoning prompt
        String mockPrologGoal = "use_tool(modify_file, [prop(FilePath, 'test.java'), prop(NewContent, '// Hello World\npublic class Test {}')], Result).";
        mockChatModel.setDefaultResponse(mockPrologGoal);

        // Set up the necessary components
        Code code = new Code("file", fileManager, new dumb.code.LMManager(lmClient));
        messageHandler = code.getMessageHandler();
        codebaseManager = code.getCodebaseManager();
        codebaseManager.trackFile("test.java").join(); // Track the file

        CodeToolProvider toolProvider = new CodeToolProvider(fileManager, codebaseManager);
        MCR mcr = new MCR(lmClient);
        Session mcrSession = mcr.createSession(toolProvider);

        reasonCommand = new ReasonCommand(mcrSession, codebaseManager, messageHandler, fileManager, false); // non-interactive
    }

    @Test
    void testReasonCommandModifiesFile() throws IOException {
        // Execute the command
        String[] args = {"Refactor", "the", "Test.java", "file", "to", "add", "a", "comment"};
        reasonCommand.execute(args);

        // Verify the file content
        String newContent = fileManager.readFile("test.java");
        assertEquals("// Hello World\npublic class Test {}", newContent);
    }
}
