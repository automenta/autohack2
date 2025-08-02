package dumb.code;

import dumb.lm.LMClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import dumb.code.help.HelpService;
import org.mockito.Mock;

public class CodeDevelopmentTest {

    private Code code;
    private CommandManager commandManager;
    private InMemoryFileManager fileManager;

    @Mock
    private HelpService helpService;

    @BeforeEach
    void setUp() {
        fileManager = new InMemoryFileManager();
        LMClient lmClient = new LMClient("mock", "mock-model", "mock-key");
        LMManager lmManager = new LMManager(lmClient);
        helpService = mock(HelpService.class);

        // The "files" backend is most suitable for in-memory testing
        code = new Code("files", fileManager, lmManager, helpService);
        commandManager = code.commandManager;
    }

    @Test
    void testCreateCommand() throws java.io.IOException {
        // Arrange
        String fileName = "new_test_file.txt";
        assertFalse(fileManager.fileExists(fileName), "File should not exist before creation.");

        // Act
        commandManager.processInput("/create " + fileName);

        // Assert
        assertTrue(fileManager.fileExists(fileName), "File should exist after /create command.");
        assertEquals("", fileManager.readFile(fileName), "Newly created file should be empty.");
    }
}
