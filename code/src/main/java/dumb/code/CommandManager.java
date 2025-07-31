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
import dumb.code.commands.diff.DiffCommand;
import dumb.code.commands.drop.DropCommand;
import dumb.code.commands.edit.EditCommand;
import dumb.code.commands.exit.ExitCommand;
import dumb.code.commands.grep.GrepCommand;
import dumb.code.commands.help.HelpCommand;
import dumb.code.commands.image.ImageCommand;
import dumb.code.commands.ls.LsCommand;
import dumb.code.commands.mv.MvCommand;
import dumb.code.commands.patch.PatchCommand;
import dumb.code.commands.query.QueryCommand;
import dumb.code.commands.record.RecordCommand;
import dumb.code.commands.rm.RmCommand;
import dumb.code.commands.run.RunCommand;
import dumb.code.commands.speech.SpeechCommand;
import dumb.code.commands.status.StatusCommand;
import dumb.code.commands.test.TestCommand;
import dumb.code.commands.undo.UndoCommand;
import dumb.code.commands.unrecord.UnrecordCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final MessageHandler messageHandler;
    private final Map<String, Command> commands;

    public CommandManager(Context context) {
        this.messageHandler = context.messageHandler;
        this.commands = new HashMap<>();
        registerCommand("help", new HelpCommand(context));
        registerCommand("exit", new ExitCommand(context));
        registerCommand("add", new AddCommand(context));
        registerCommand("diff", new DiffCommand(context));
        registerCommand("record", new RecordCommand(context));
        registerCommand("commit", new CommitCommand(context)); // Alias for record
        registerCommand("query", new QueryCommand(context));

        // Newly registered commands
        registerCommand("apply", new ApplyCommand(context));
        registerCommand("channel", new ChannelCommand(context));
        registerCommand("clear", new ClearCommand(context));
        registerCommand("codebase", new CodebaseCommand(context));
        registerCommand("conflicts", new ConflictsCommand(context));
        registerCommand("create", new CreateCommand(context));
        registerCommand("drop", new DropCommand(context));
        registerCommand("edit", new EditCommand(context));
        registerCommand("grep", new GrepCommand(context));
        registerCommand("image", new ImageCommand(context));
        registerCommand("ls", new LsCommand(context));
        registerCommand("mv", new MvCommand(context));
        registerCommand("patch", new PatchCommand(context));
        registerCommand("rm", new RmCommand(context));
        registerCommand("run", new RunCommand(context));
        registerCommand("speech", new SpeechCommand(context));
        registerCommand("status", new StatusCommand(context));
        registerCommand("test", new TestCommand(context));
        registerCommand("undo", new UndoCommand(context));
        registerCommand("unrecord", new UnrecordCommand(context));
    }

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public void processInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String[] parts = input.trim().split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(args);
        } else {
            messageHandler.addMessage("system", "Unknown command: " + commandName);
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