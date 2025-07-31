package com.pijul.aider.commands.run;

import com.pijul.aider.Container;
import com.pijul.aider.MessageHandler;
import com.pijul.aider.commands.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RunCommandTest {

    @Mock
    private Container container;

    @Mock
    private MessageHandler messageHandler;

    @InjectMocks
    private RunCommand runCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(container.getMessageHandler()).thenReturn(messageHandler);
    }

    @Test
    public void testRunCommand_withValidCommand_executesCommandAndPrintsOutput() throws IOException, InterruptedException {
        List<String> command = Arrays.asList("echo", "hello world");

        runCommand.run(command);

        verify(messageHandler, times(1)).handle(eq("system"), anyString());
    }

    @Test
    public void testRunCommand_withInvalidCommand_printsErrorMessage() throws IOException, InterruptedException {
        List<String> command = Arrays.asList("invalid_command");

        runCommand.run(command);

        verify(messageHandler, times(1)).handle(eq("system"), anyString());
    }
}
