package dumb.hack;

import dumb.hack.commands.CodeCommand;
import dumb.hack.commands.McrCommand;
import dumb.hack.tui.HackTUI;
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
            new HackTUI(app).start();
        } else {
            int exitCode = new CommandLine(app).execute(args);
            System.exit(exitCode);
        }
    }
}
