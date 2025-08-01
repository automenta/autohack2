package dumb.hack.tools;

import dumb.code.CodebaseManager;
import dumb.code.FileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CodeModificationToolTest {

    private FileManager fileManager;
    private CodebaseManager codebaseManager;
    private CodeModificationTool tool;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".txt");
        fileManager = new FileManager(tempFile.getParent().toString());
        codebaseManager = Mockito.mock(CodebaseManager.class);
        tool = new CodeModificationTool(fileManager, codebaseManager);

        tempFile = Files.createTempFile("test", ".txt");
        Files.write(tempFile, "initial content".getBytes());

        when(codebaseManager.trackFile(anyString())).thenReturn(CompletableFuture.completedFuture(null));
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

        String expectedPrefix = "diff:" + filePath + ":";
        assertTrue(result.startsWith(expectedPrefix));

        String encodedContent = result.substring(expectedPrefix.length());
        String decodedContent = new String(Base64.getDecoder().decode(encodedContent));
        assertEquals(newContent, decodedContent);

        // The tool should not modify the file directly anymore
        assertEquals("initial content", new String(Files.readAllBytes(tempFile)));
    }
}
