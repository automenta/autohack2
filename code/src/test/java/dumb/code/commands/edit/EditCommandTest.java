package dumb.code.commands.edit;

import dumb.code.Context;
import dumb.code.LMManager;
import dumb.code.MessageHandler;
import dumb.lm.ILMClient;
import dumb.lm.LMResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EditCommandTest {

    @TempDir
    Path tempDir;

    @Test
    public void testExecute() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "Hello, world!".getBytes());

        ILMClient mockLmClient = mock(ILMClient.class);
        LMResponse lmResponse = new LMResponse();
        lmResponse.setSuccess(true);
        lmResponse.setContent("Hello, universe!");
        when(mockLmClient.generate("Edit the following file based on the prompt:\n\nHello, world!\n\nPrompt: change world to universe")).thenReturn(lmResponse);

        LMManager lmManager = new LMManager(mockLmClient);

        // This is a bit of a hack, since the Context class is not very test-friendly.
        // In a real project, I would refactor the Context class to make it easier to inject dependencies.
        Context context = new Context(new String[]{}, null, mock(MessageHandler.class), lmManager);


        EditCommand editCommand = new EditCommand(context);

        // Act
        editCommand.execute(new String[]{testFile.toString(), "change", "world", "to", "universe"});

        // Assert
        String fileContent = new String(Files.readAllBytes(testFile));
        assertEquals("Hello, universe!", fileContent);
    }
}
