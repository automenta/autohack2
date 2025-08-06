//package dumb.tools.commands.run;
//
//import dumb.tools.ToolContext;
//import dumb.tools.MessageHandler;
//import dumb.tools.util.IProcessRunner;
//import dumb.tools.util.ProcessResult;
//import org.junit.jupiter.api.Test;
//
//import static org.mockito.Mockito.*;
//
//public class RunCommandTest {
//
//    @Test
//    public void testRunCommand_withValidCommand_executesCommandAndPrintsOutput() {
//        // Arrange
//        IProcessRunner mockProcessRunner = mock(IProcessRunner.class);
//        when(mockProcessRunner.run(new String[]{"echo", "hello", "world"})).thenReturn(new ProcessResult(0, "hello world\n"));
//
//        MessageHandler messageHandler = mock(MessageHandler.class);
//        ToolContext code = new ToolContext(new String[]{}, null, messageHandler, null, mockProcessRunner);
//
//        RunCommand runCommand = new RunCommand(code);
//
//        // Act
//        runCommand.execute(new String[]{"echo", "hello", "world"});
//
//        // Assert
//        verify(messageHandler).addMessage("system", "Command finished with exit code 0:\nhello world\n");
//    }
//
//    @Test
//    public void testRunCommand_withInvalidCommand_printsErrorMessage() {
//        // Arrange
//        IProcessRunner mockProcessRunner = mock(IProcessRunner.class);
//        when(mockProcessRunner.run(new String[]{"invalid_command"})).thenReturn(new ProcessResult(-1, "Error message"));
//
//        MessageHandler messageHandler = mock(MessageHandler.class);
//        ToolContext code = new ToolContext(new String[]{}, null, messageHandler, null, mockProcessRunner);
//
//        RunCommand runCommand = new RunCommand(code);
//
//        // Act
//        runCommand.execute(new String[]{"invalid_command"});
//
//        // Assert
//        verify(messageHandler).addMessage("system", "Command finished with exit code -1:\nError message");
//    }
//}
