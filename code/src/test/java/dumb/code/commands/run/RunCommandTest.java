package dumb.code.commands.run;

import dumb.code.Context;
import dumb.code.MessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class RunCommandTest {

    @Mock
    private Context context;

    @Mock
    private MessageHandler messageHandler;

    private RunCommand runCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(context.getMessageHandler()).thenReturn(messageHandler);
        runCommand = new RunCommand(context);
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
