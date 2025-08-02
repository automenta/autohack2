package dumb.code;

import dumb.code.commands.AddCommand;
import dumb.code.commands.Command;
import dumb.code.commands.apply.ApplyCommand;
import dumb.code.commands.channel.ChannelCommand;
import dumb.code.commands.clear.ClearCommand;
import dumb.code.commands.codebase.CodebaseCommand;
import dumb.code.commands.commit.CommitCommand;
import dumb.code.commands.conflicts.ConflictsCommand;
import dumb.code.commands.create.CreateCommand;
import dumb.code.commands.debug.DebugCommand;
import dumb.code.commands.diff.DiffCommand;
import dumb.code.commands.doc.DocCommand;
import dumb.code.commands.drop.DropCommand;
import dumb.code.commands.edit.EditCommand;
import dumb.code.commands.exit.ExitCommand;
import dumb.code.commands.grep.GrepCommand;
import dumb.code.commands.help.HelpCommand;
import dumb.code.commands.image.ImageCommand;
import dumb.code.commands.ls.LsCommand;
import dumb.hack.help.HelpService;
import dumb.code.commands.mv.MvCommand;
import dumb.code.commands.patch.PatchCommand;
import dumb.code.commands.query.QueryCommand;
import dumb.code.commands.record.RecordCommand;
import dumb.code.commands.refactor.RefactorCommand;
import dumb.code.commands.rm.RmCommand;
import dumb.code.commands.run.RunCommand;
import dumb.code.commands.speech.SpeechCommand;
import dumb.code.commands.status.StatusCommand;
import dumb.code.commands.test.TestCommand;
import dumb.code.commands.tutorial.TutorialCommand;
import dumb.code.commands.undo.UndoCommand;
import dumb.code.commands.mcr.McrCommand;
import dumb.code.commands.unrecord.UnrecordCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final MessageHandler messageHandler;
    private final Map<String, Command> commands;
    private final HelpService helpService;

    public CommandManager(Code code, HelpService helpService) {
        this.messageHandler = code.messageHandler;
        this.commands = new HashMap<>();
        this.helpService = helpService;
        registerCommand("help", new HelpCommand(helpService, code.messageHandler));
        registerCommand("exit", new ExitCommand(code));
        registerCommand("add", new AddCommand(code));
        registerCommand("diff", new DiffCommand(code));
        registerCommand("record", new RecordCommand(code));
        registerCommand("commit", new CommitCommand(code)); // Alias for record
        registerCommand("query", new QueryCommand(code));
        registerCommand("mcr", new McrCommand(code.processRunner, code.messageHandler));

        // Newly registered commands
        registerCommand("apply", new ApplyCommand(code));
        registerCommand("channel", new ChannelCommand(code));
        registerCommand("clear", new ClearCommand(code));
        registerCommand("codebase", new CodebaseCommand(code));
        registerCommand("conflicts", new ConflictsCommand(code));
        registerCommand("create", new CreateCommand(code));
        registerCommand("drop", new DropCommand(code));
        registerCommand("edit", new EditCommand(code));
        registerCommand("grep", new GrepCommand(code));
        registerCommand("image", new ImageCommand(code));
        registerCommand("ls", new LsCommand(code));
        registerCommand("mv", new MvCommand(code));
        registerCommand("patch", new PatchCommand(code));
        registerCommand("rm", new RmCommand(code));
        registerCommand("run", new RunCommand(code));
        registerCommand("speech", new SpeechCommand(code));
        registerCommand("status", new StatusCommand(code));
        registerCommand("test", new TestCommand(code));
        registerCommand("tutorial", new TutorialCommand(helpService));
        registerCommand("undo", new UndoCommand(code));
        registerCommand("unrecord", new UnrecordCommand(code));

        // Stubs for future commands
        registerCommand("refactor", new RefactorCommand(code));
        registerCommand("debug", new DebugCommand(code));
        registerCommand("doc", new DocCommand(code));
    }

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public void processInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        input = input.trim();

        if (input.startsWith("/")) {
            String[] parts = input.substring(1).split(" ", 2);
            String commandName = parts[0].toLowerCase();
            String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

            Command command = commands.get(commandName);
            if (command != null) {
                command.execute(args);
            } else {
                messageHandler.addMessage("system", "Unknown command: " + commandName);
            }
        } else {
            // It's a natural language prompt, treat it as a query.
            Command queryCommand = commands.get("query");
            if (queryCommand != null) {
                queryCommand.execute(new String[]{input});
            } else {
                messageHandler.addMessage("system", "Query command not found.");
            }
        }
    }

    public void startListening() {
        for (Command command : commands.values()) {
            command.init();
        }
        System.out.println("All commands initialized and listening.");
    }

    public void stopListening() {
        for (Command command : commands.values()) {
            command.cleanup();
        }
        System.out.println("All commands stopped.");
    }
}