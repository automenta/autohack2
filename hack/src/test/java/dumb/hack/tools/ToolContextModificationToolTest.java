package dumb.hack.tools;

import dumb.tools.Workspace;
import dumb.tools.FileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CodeModificationToolTest {

    private FileManager fileManager;
    private Workspace workspace;
    private CodeModificationTool tool;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".txt");
        fileManager = new FileManager(tempFile.getParent().toString());
        workspace = Mockito.mock(Workspace.class);
        tool = new CodeModificationTool(fileManager, workspace);

        tempFile = Files.createTempFile("test", ".txt");
        Files.write(tempFile, "initial content".getBytes());

        when(workspace.trackFile(anyString())).thenReturn(CompletableFuture.completedFuture(null));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testModifyFile() throws IOException {
        Map<String, Object> args = new HashMap<>();
        String filePath = tempFile.getFileName().toString();
        String newContent = "new content";
        args.put("FilePath", filePath);
        args.put("NewContent", newContent);

        String result = tool.run(args);

        assertEquals("Successfully modified file: " + filePath, result);
        assertEquals(newContent, new String(Files.readAllBytes(tempFile)));
    }
}
