package dumb.code.commands.mcr;

import dumb.code.MessageHandler;
import dumb.code.util.IProcessRunner;
import dumb.code.util.ProcessResult;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class McrCommandTest {

    @Test
    void testExecute() {
        // Arrange
        IProcessRunner processRunner = mock(IProcessRunner.class);
        MessageHandler messageHandler = mock(MessageHandler.class);
        McrCommand mcrCommand = new McrCommand(processRunner, messageHandler);

        String query = "what is the meaning of life?";
        String[] args = query.split(" ");
        String expectedJson = "{\"success\":true,\"originalQuery\":\"what is the meaning of life?\",\"bindings\":[{\"X\":\"42\"}]}";
        ProcessResult processResult = new ProcessResult(0, expectedJson);

        when(processRunner.runWithInput(anyString(), any(String[].class))).thenReturn(processResult);

        // Act
        mcrCommand.execute(args);

        // Assert
        ArgumentCaptor<String> processInputCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String[]> processCommandCaptor = ArgumentCaptor.forClass(String[].class);
        verify(processRunner).runWithInput(processInputCaptor.capture(), processCommandCaptor.capture());

        assertEquals(query, processInputCaptor.getValue());

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageHandler, times(5)).onMessage(messageCaptor.capture());

        assertEquals("Querying MCR with: '" + query + "'", messageCaptor.getAllValues().get(0));
        assertEquals("MCR query successful.", messageCaptor.getAllValues().get(1));
        assertEquals("Original query: what is the meaning of life?", messageCaptor.getAllValues().get(2));
        assertEquals("Solutions:", messageCaptor.getAllValues().get(3));
        assertEquals("[{X=42}]", messageCaptor.getAllValues().get(4));
    }
}
