package dumb.code.commands.test;

import dumb.code.MessageHandler;
import dumb.code.util.IProcessRunner;
import dumb.code.util.ProcessResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestCommandTest {

    private IProcessRunner processRunner;
    private MessageHandler messageHandler;
    private TestCommand testCommand;

    @BeforeEach
    void setUp() {
        processRunner = mock(IProcessRunner.class);
        messageHandler = new MessageHandler();
        testCommand = new TestCommand(processRunner, messageHandler);
    }

    @Test
    void testExecute_testsPass() {
        when(processRunner.run("mvn", "test")).thenReturn(new ProcessResult(0, "BUILD SUCCESS"));

        testCommand.execute(new String[]{});

        java.util.List<String> messages = messageHandler.getMessages();
        assertEquals(2, messages.size());
        assertEquals("system: Running tests...", messages.get(0));
        assertEquals("system: Tests passed!", messages.get(1));
    }

    @Test
    void testExecute_testsFail() {
        when(processRunner.run("mvn", "test")).thenReturn(new ProcessResult(1, "BUILD FAILURE"));

        testCommand.execute(new String[]{});

        java.util.List<String> messages = messageHandler.getMessages();
        assertEquals(2, messages.size());
        assertEquals("system: Running tests...", messages.get(0));
        assertEquals("system: Tests failed:\nBUILD FAILURE", messages.get(1));
    }
}
