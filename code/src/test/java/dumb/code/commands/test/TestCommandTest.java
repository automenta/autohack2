package dumb.code.commands.test;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.util.IProcessRunner;
import dumb.code.util.ProcessResult;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class TestCommandTest {

    @Test
    public void testExecute_success() {
        // Arrange
        IProcessRunner mockProcessRunner = mock(IProcessRunner.class);
        when(mockProcessRunner.run("mvn", "test")).thenReturn(new ProcessResult(0, "BUILD SUCCESS"));

        MessageHandler messageHandler = mock(MessageHandler.class);
        Context context = new Context(new String[]{}, null, messageHandler, null, mockProcessRunner);

        TestCommand testCommand = new TestCommand(context);

        // Act
        testCommand.execute(new String[]{});

        // Assert
        verify(messageHandler).addMessage("system", "Running tests...");
        verify(messageHandler).addMessage("system", "Tests passed!");
    }

    @Test
    public void testExecute_failure() {
        // Arrange
        IProcessRunner mockProcessRunner = mock(IProcessRunner.class);
        when(mockProcessRunner.run("mvn", "test")).thenReturn(new ProcessResult(1, "BUILD FAILURE"));

        MessageHandler messageHandler = mock(MessageHandler.class);
        Context context = new Context(new String[]{}, null, messageHandler, null, mockProcessRunner);

        TestCommand testCommand = new TestCommand(context);

        // Act
        testCommand.execute(new String[]{});

        // Assert
        verify(messageHandler).addMessage("system", "Running tests...");
        verify(messageHandler).addMessage("system", "Tests failed:\nBUILD FAILURE");
    }
}
