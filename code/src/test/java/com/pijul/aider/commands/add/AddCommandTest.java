package com.pijul.aider.commands.add;

import com.pijul.aider.*;
import com.pijul.aider.commands.AddCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

public class AddCommandTest {

    private Container container;
    private MessageHandler messageHandler;
    private CodebaseManager codebaseManager;
    private AddCommand addCommand;
    private Backend backend;
    private FileSystem fileSystem;

    @BeforeEach
    public void setUp() {
        container = mock(Container.class);
        messageHandler = mock(MessageHandler.class);
        backend = mock(Backend.class);
        fileSystem = mock(FileSystem.class);
        codebaseManager = new CodebaseManager(backend);

        when(container.getMessageHandler()).thenReturn(messageHandler);
        when(container.getCodebaseManager()).thenReturn(codebaseManager);
        when(container.getBackend()).thenReturn(backend);
        when(container.getFileSystem()).thenReturn(fileSystem);
        when(backend.add(anyString())).thenReturn(CompletableFuture.completedFuture(null));


        addCommand = new AddCommand(container);
    }

    @Test
    public void testAddFile() throws IOException {
        // Create a temporary file
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "hello world");

        String[] args = {tempFile.toString()};
        addCommand.execute(args);

        // Verify that the backend's add method was called
        verify(backend, times(1)).add(tempFile.toString());

        // Verify that the codebase manager was updated
        String codebase = codebaseManager.getCodebaseRepresentation();
        assert (codebase.contains("hello world"));
        assert (codebase.contains(tempFile.toString()));

        // Verify that a message was sent
        verify(messageHandler, times(1)).addMessage(eq("system"), anyString());

        // Clean up the temporary file
        Files.delete(tempFile);
    }
}
