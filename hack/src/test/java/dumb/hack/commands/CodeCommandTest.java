package dumb.hack.commands;

import dumb.hack.App;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeCommandTest {

    @Test
    public void testCodeCommandRunsSuccessfully() {
        App app = new App();
        CommandLine cmd = new CommandLine(app);

        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int exitCode = cmd.execute("--provider=mock", "--model=mock-model", "--api-key=mock-key", "code", "--task", "list the files in the current directory");
        assertEquals(0, exitCode);
    }
}
