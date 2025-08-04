package dumb.hack.commands.mcr;

import dumb.code.MessageHandler;
import dumb.mcr.QueryResult;
import dumb.mcr.Session;
import dumb.prolog.Atom;
import dumb.prolog.Variable;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class McrCommandTest {

    @Test
    void testExecute() {
        // Arrange
        Session session = mock(Session.class);
        MessageHandler messageHandler = mock(MessageHandler.class);
        McrCommand mcrCommand = new McrCommand(session, messageHandler);

        String query = "what does tweety like?";
        String[] args = query.split(" ");

        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.success()).thenReturn(true);
        when(queryResult.originalQuery()).thenReturn(query);

        // The raw bindings() method on the QueryResult record returns List<Map<Variable, Term>>
        Variable varX = new Variable("X");
        Atom termSeeds = new Atom("seeds");
        Map<dumb.prolog.Variable, dumb.prolog.Term> solution = Collections.singletonMap(varX, termSeeds);
        when(queryResult.bindings()).thenReturn(Collections.singletonList(solution));

        when(session.nquery(anyString())).thenReturn(queryResult);

        // Act
        mcrCommand.execute(args);

        // Assert
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).nquery(queryCaptor.capture());
        assertEquals(query, queryCaptor.getValue());

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageHandler, times(5)).onMessage(messageCaptor.capture());

        List<String> capturedMessages = messageCaptor.getAllValues();
        assertEquals("Querying MCR with: '" + query + "'", capturedMessages.get(0));
        assertEquals("MCR query successful.", capturedMessages.get(1));
        assertEquals("Original query: " + query, capturedMessages.get(2));
        assertEquals("Solutions:", capturedMessages.get(3));
        // The toString() of the map is what's printed
        assertEquals("  {X=seeds}", capturedMessages.get(4));
    }
}
