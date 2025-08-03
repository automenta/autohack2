package dumb.code.commands.ls;

import dumb.code.MessageHandler;
import dumb.code.tools.FileSystemTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LsCommandTest {

    private LsCommand lsCommand;
    private FileSystemTool fileSystemTool;
    private MessageHandler messageHandler;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        messageHandler = mock(MessageHandler.class);
        fileSystemTool = new FileSystemTool(tempDir.toString());
        lsCommand = new LsCommand(fileSystemTool, messageHandler);
    }

    @Test
    void testExecute() throws java.io.IOException {
        Files.createFile(tempDir.resolve("test1.txt"));
        Files.createFile(tempDir.resolve("test2.txt"));

        lsCommand.execute(new String[]{});

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(messageHandler).addMessage(eq("system"), captor.capture());
        String message = captor.getValue();

        assertTrue(message.contains("test1.txt"));
        assertTrue(message.contains("test2.txt"));
    }
}
