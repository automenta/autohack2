package dumb.hack;

import dumb.hack.commands.CodeCommand;
import dumb.hack.commands.McrCommand;
import picocli.CommandLine;

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

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
