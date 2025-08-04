package dumb.hack.agent;

import dumb.code.CommandManager;
import dumb.lm.LMManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentOrchestratorTest {

    @TempDir
    File tempDir;

    @Test
    public void testAgentOrchestratorInitialization() {
        LMManager lmManager = new LMManager("mock", "mock-model", "mock-key");
        AgentOrchestrator orchestrator = new AgentOrchestrator(tempDir.getAbsolutePath(), lmManager);

        assertNotNull(orchestrator.getCommandManager());
        assertNotNull(orchestrator.getTools());

        List<Object> tools = orchestrator.getTools();
        assertFalse(tools.isEmpty());

        // Check if the essential tools are registered
        assertTrue(tools.stream().anyMatch(t -> t.getClass().getSimpleName().equals("FileSystemTool")));
        assertTrue(tools.stream().anyMatch(t -> t.getClass().getSimpleName().equals("CodebaseTool")));
        assertTrue(tools.stream().anyMatch(t -> t.getClass().getSimpleName().equals("VersionControlTool")));
    }
}
