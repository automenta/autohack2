package com.pijul.aider.commands.run;

import com.pijul.aider.Container;
import com.pijul.aider.MessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class RunCommandTest {

    @Mock
    private Container container;

    @Mock
    private MessageHandler messageHandler;

    private RunCommand runCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(container.getMessageHandler()).thenReturn(messageHandler);
        runCommand = new RunCommand(container);
    }

    @Test
    public void testRunCommand_withValidCommand_executesCommandAndPrintsOutput() {
        String[] command = {"echo", "hello world"};

        runCommand.execute(command);

        verify(messageHandler, times(1)).addMessage(eq("system"), anyString());
    }

    @Test
    public void testRunCommand_withInvalidCommand_printsErrorMessage() {
        String[] command = {"invalid_command"};

        runCommand.execute(command);

        verify(messageHandler, times(1)).addMessage(eq("system"), anyString());
    }
}
