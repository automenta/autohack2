//package dumb.hack;
//
//import dumb.tools.ToolContextbaseManager;
//import dumb.tools.CommandManager;
//import dumb.tools.ToolContext;
//import dumb.tools.FileManager;
//import dumb.tools.MessageHandler;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class HackIntegrationTest {
//
//    private ToolContext code;
//    private CommandManager commandManager;
//    private MessageHandler messageHandler;
//    private ToolContextbaseManager codebaseManager;
//    private FileManager fileManager;
//    private Path tempDir;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        // Set up a temporary directory for the test
//        tempDir = Files.createTempDirectory("hack-integration-test");
//        System.setProperty("user.dir", tempDir.toString());
//
//        // Mock dependencies
//        messageHandler = mock(MessageHandler.class);
//        when(messageHandler.promptUser(anyString())).thenReturn("yes");
//
//        // Initialize the application context with mocks
//        String[] args = {"--backend=file"}; // Use file backend for simplicity
//        code = new ToolContext(args, null, messageHandler);
//
//        commandManager = code.commandManager;
//        codebaseManager = code.getToolContextbaseManager();
//        fileManager = code.fileManager;
//
//        // Load an empty codebase
//        codebaseManager.loadToolContextbase(tempDir.toString()).join();
//    }
//
//    @Test
//    void testBasicEditingWorkflow() throws IOException {
//        // 1. Create a file
//        Path testFile = tempDir.resolve("TestFile.java");
//        String initialContent = "public class TestFile {\n    public static void main(String[] args) {\n        // Start\n    }\n}\n";
//        Files.writeString(testFile, initialContent);
//
//        // 2. Add it to the context
//        commandManager.processInput("/add " + testFile.toString());
//
//        // 3. Make a change via a natural language prompt
//        // This will be handled by the mock LM in the EditCommand, which should
//        // propose adding a comment.
//        commandManager.processInput("Add a comment here.");
//
//        // 4. Commit the change
//        commandManager.processInput("/commit -m 'Test commit'");
//
//        // 5. Verify the file content
//        String finalContent = Files.readString(testFile);
//        assertTrue(finalContent.contains("// A comment was added"), "The file should contain the new comment.");
//    }
//}
