package dumb.hack.commands;

import dumb.code.CodebaseManager;
import dumb.code.MessageHandler;
import dumb.code.versioning.Backend;
import dumb.mcr.Session;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

public class ReasonCommandTest {

    @Test
    public void testExecute() {
        // Arrange
        CodebaseManager codebaseManager = mock(CodebaseManager.class);
        Backend backend = mock(Backend.class);
        when(codebaseManager.getVersioningBackend()).thenReturn(backend);
        when(backend.status()).thenReturn(CompletableFuture.completedFuture("M. newfile.txt"));

        Session mcrSession = mock(Session.class);
        MessageHandler messageHandler = mock(MessageHandler.class);

        ReasonCommand reasonCommand = new ReasonCommand(mcrSession, codebaseManager, messageHandler, null);

        // Act
        reasonCommand.execute(new String[]{"test"});

        // Assert
        verify(mcrSession).assertProlog("git_status(\"M. newfile.txt\").");
    }
}
