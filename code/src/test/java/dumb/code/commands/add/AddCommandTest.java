package dumb.code.commands.add;

import dumb.code.MessageHandler;
import dumb.code.commands.AddCommand;
import dumb.code.tools.CodebaseTool;
import dumb.code.tools.FileSystemTool;
import dumb.code.tools.VersionControlTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

public class AddCommandTest {

    private MessageHandler messageHandler;
    private CodebaseTool codebaseTool;
    private VersionControlTool versionControlTool;
    private FileSystemTool fileSystemTool;
    private AddCommand addCommand;

    @BeforeEach
    public void setUp() {
        messageHandler = mock(MessageHandler.class);
        codebaseTool = mock(CodebaseTool.class);
        versionControlTool = mock(VersionControlTool.class);
        fileSystemTool = mock(FileSystemTool.class);

        addCommand = new AddCommand(messageHandler, codebaseTool, versionControlTool, fileSystemTool);
    }

    @Test
    public void testAddFile() throws IOException {
        // Create a temporary file
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "hello world");

        String[] args = {tempFile.toString()};
        addCommand.execute(args);

        // Verify that the codebase tool was updated
        verify(codebaseTool, times(1)).trackFile(anyString());

        // Verify that a message was sent
        verify(messageHandler, times(1)).addMessage(eq("system"), anyString());

        // Clean up the temporary file
        Files.delete(tempFile);
    }
}
