package dumb.hack;

import dumb.hack.commands.CodeCommand;
import dumb.hack.commands.McrCommand;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.tui.TUI;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        name = "hack",
        mixinStandardHelpOptions = true,
        version = "hack 1.0",
        description = "AI-powered software engineering tool.",
        subcommands = {
                CodeCommand.class,
                McrCommand.class,
                CommandLine.HelpCommand.class
        })
public class App {

    @CommandLine.Mixin
    private final LMOptions lmOptions = new LMOptions();

    public LMOptions getLmOptions() {
        return lmOptions;
    }

    public static void main(String[] args) throws IOException {
        App app = new App();
        if (args.length == 0) {
            try {
                HackController controller = new HackController(app);
                new TUI(controller).start();
            } catch (MissingApiKeyException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        } else {
            int exitCode = new CommandLine(app).execute(args);
            System.exit(exitCode);
        }
    }
}
