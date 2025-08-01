package dumb.code.commands.grep;

import dumb.code.Context;
import dumb.code.MessageHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GrepCommandTest {

    @TempDir
    Path tempDir;

    @Test
    public void testExecute() throws IOException {
        // Arrange
        Path file1 = tempDir.resolve("file1.txt");
        Files.write(file1, "hello world".getBytes());

        Path file2 = tempDir.resolve("file2.txt");
        Files.write(file2, "hello there".getBytes());

        Path subDir = tempDir.resolve("subdir");
        Files.createDirectory(subDir);
        Path file3 = subDir.resolve("file3.txt");
        Files.write(file3, "another world".getBytes());

        MessageHandler messageHandler = mock(MessageHandler.class);
        Context context = new Context(new String[]{}, null, messageHandler, null, null);

        GrepCommand grepCommand = new GrepCommand(context);

        // Act
        grepCommand.execute(new String[]{"world", tempDir.toString()});

        // Assert
        String expectedOutput = file1.toString() + "\n" + file3.toString() + "\n";
        String[] expectedLines = expectedOutput.split("\n");
        Arrays.sort(expectedLines);
        String sortedExpectedOutput = String.join("\n", expectedLines);

        verify(messageHandler).addMessage(eq("system"), argThat(argument -> {
            String[] actualLines = argument.toString().split("\n");
            Arrays.sort(actualLines);
            String sortedActualOutput = String.join("\n", actualLines);
            return sortedExpectedOutput.equals(sortedActualOutput);
        }));
    }
}
