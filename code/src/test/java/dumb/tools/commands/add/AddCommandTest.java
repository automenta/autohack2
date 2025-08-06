package dumb.tools.commands.add;

import dumb.tools.ToolContext;
import dumb.tools.Workspace;
import dumb.tools.FileSystem;
import dumb.tools.MessageHandler;
import dumb.tools.commands.AddCommand;
import dumb.tools.versioning.Backend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

public class AddCommandTest {

    private ToolContext code;
    private MessageHandler messageHandler;
    private Workspace workspace;
    private AddCommand addCommand;
    private Backend backend;
    private FileSystem fileSystem;

    @BeforeEach
    public void setUp() {
        code = mock(ToolContext.class);
        messageHandler = mock(MessageHandler.class);
        backend = mock(Backend.class);
        fileSystem = mock(FileSystem.class);
        workspace = mock(Workspace.class);

        when(code.getMessageHandler()).thenReturn(messageHandler);
        when(code.getWorkspace()).thenReturn(workspace);
        when(code.getBackend()).thenReturn(backend);
        when(code.getFiles()).thenReturn(fileSystem);
        when(backend.add(anyString())).thenReturn(CompletableFuture.completedFuture(null));
        when(workspace.trackFile(anyString())).thenReturn(CompletableFuture.completedFuture(null));


        addCommand = new AddCommand(code);
    }

    @Test
    public void testAddFile() throws IOException {
        // Create a temporary file
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "hello world");

        String[] args = {tempFile.toString()};
        addCommand.execute(args);

        // Verify that the workspace was updated
        verify(workspace, times(1)).trackFile(anyString());

        // Verify that a message was sent
        verify(messageHandler, times(1)).addMessage(eq("system"), anyString());

        // Clean up the temporary file
        Files.delete(tempFile);
    }
}
