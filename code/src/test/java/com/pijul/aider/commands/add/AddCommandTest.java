package com.pijul.aider.commands.add;

import com.pijul.aider.Container;
import com.pijul.aider.MessageHandler;
import com.pijul.aider.CodebaseManager;
import com.pijul.aider.commands.AddCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

public class AddCommandTest {

    private Container container;
    private MessageHandler messageHandler;
    private CodebaseManager codebaseManager;
    private AddCommand addCommand;

    @BeforeEach
    public void setUp() {
        container = mock(Container.class);
        messageHandler = mock(MessageHandler.class);
        codebaseManager = new CodebaseManager(null); // Passing null for backend as it's not used in this test

        when(container.getMessageHandler()).thenReturn(messageHandler);
        when(container.getCodebaseManager()).thenReturn(codebaseManager);

        addCommand = new AddCommand(container);
    }

    @Test
    public void testAddFile() throws IOException {
        // Create a temporary file
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "hello world");

        String[] args = {tempFile.toString()};
        addCommand.execute(args);

        // Verify that the codebase manager was updated
        String codebase = codebaseManager.getCodebase();
        assert(codebase.contains("hello world"));
        assert(codebase.contains(tempFile.toString()));

        // Verify that a message was sent
        verify(messageHandler, times(1)).addMessage(eq("system"), anyString());

        // Clean up the temporary file
        Files.delete(tempFile);
    }
}
