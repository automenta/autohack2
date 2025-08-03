package dumb.code;

import dumb.code.agent.AgentOrchestrator;
import dumb.code.tools.FileSystemTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CodeDevelopmentTest {

    private AgentOrchestrator orchestrator;
    private FileSystemTool fileSystemTool;

    @TempDir
    File tempDir;

    @BeforeEach
    public void setUp() {
        // Use a temporary directory for the file manager to avoid cluttering the project root
        fileSystemTool = new FileSystemTool(tempDir.getAbsolutePath());
        orchestrator = new AgentOrchestrator(tempDir.getAbsolutePath(), new LMManager("mock", "mock-model", "mock-key"));
    }

    @Test
    public void testCreateFile() {
        orchestrator.getCommandManager().processInput("/create new_test_file.txt");

        // Verify that the file was created
        assertTrue(fileSystemTool.fileExists("new_test_file.txt"));
    }
}
