package dumb.code.commands.run;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.help.HelpService;
import dumb.code.util.IProcessRunner;
import dumb.code.util.ProcessResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RunCommandTest {

    private IProcessRunner processRunner;
    private MessageHandler messageHandler;
    private RunCommand runCommand;
    private Code code;

    @BeforeEach
    void setUp() {
        processRunner = mock(IProcessRunner.class);
        HelpService helpService = mock(HelpService.class);
        code = new Code(null, null, null, helpService);
        messageHandler = code.messageHandler;
        code.processRunner = processRunner;
        runCommand = new RunCommand(code);
    }

    @Test
    void testExecute() {
        when(processRunner.run("echo", "hello")).thenReturn(new ProcessResult(0, "hello\n"));

        runCommand.execute(new String[]{"echo", "hello"});

        java.util.List<String> messages = messageHandler.getMessages();
        assertEquals(1, messages.size());
        assertEquals("system: Command finished with exit code 0:\nhello\n", messages.get(0));
    }
}
