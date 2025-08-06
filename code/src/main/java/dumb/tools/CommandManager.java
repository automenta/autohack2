package dumb.tools;

import dumb.tools.commands.AddCommand;
import dumb.tools.commands.Command;
import dumb.tools.commands.apply.ApplyCommand;
import dumb.tools.commands.channel.ChannelCommand;
import dumb.tools.commands.clear.ClearCommand;
import dumb.tools.commands.codebase.CodebaseCommand;
import dumb.tools.commands.commit.CommitCommand;
import dumb.tools.commands.conflicts.ConflictsCommand;
import dumb.tools.commands.create.CreateCommand;
import dumb.tools.commands.debug.DebugCommand;
import dumb.tools.commands.diff.DiffCommand;
import dumb.tools.commands.doc.DocCommand;
import dumb.tools.commands.drop.DropCommand;
import dumb.tools.commands.edit.EditCommand;
import dumb.tools.commands.exit.ExitCommand;
import dumb.tools.commands.grep.GrepCommand;
import dumb.tools.commands.help.HelpCommand;
import dumb.tools.commands.image.ImageCommand;
import dumb.tools.commands.ls.LsCommand;
import dumb.tools.commands.mv.MvCommand;
import dumb.tools.commands.patch.PatchCommand;
import dumb.tools.commands.query.QueryCommand;
import dumb.tools.commands.record.RecordCommand;
import dumb.tools.commands.refactor.RefactorCommand;
import dumb.tools.commands.rm.RmCommand;
import dumb.tools.commands.run.RunCommand;
import dumb.tools.commands.speech.SpeechCommand;
import dumb.tools.commands.status.StatusCommand;
import dumb.tools.commands.test.TestCommand;
import dumb.tools.commands.undo.UndoCommand;
import dumb.tools.commands.mcr.McrCommand;
import dumb.tools.commands.unrecord.UnrecordCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final MessageHandler messageHandler;
    private final Map<String, Command> commands;

    public CommandManager(ToolContext toolContext) {
        this.messageHandler = toolContext.messageHandler;
        this.commands = new HashMap<>();
        registerCommand("help", new HelpCommand(toolContext));
        registerCommand("exit", new ExitCommand(toolContext));
        registerCommand("add", new AddCommand(toolContext));
        registerCommand("diff", new DiffCommand(toolContext));
        registerCommand("record", new RecordCommand(toolContext));
        registerCommand("commit", new CommitCommand(toolContext)); // Alias for record
        registerCommand("query", new QueryCommand(toolContext));
        registerCommand("mcr", new McrCommand(toolContext.processRunner, toolContext.messageHandler));

        // Newly registered commands
        registerCommand("apply", new ApplyCommand(toolContext));
        registerCommand("channel", new ChannelCommand(toolContext));
        registerCommand("clear", new ClearCommand(toolContext));
        registerCommand("codebase", new CodebaseCommand(toolContext));
        registerCommand("conflicts", new ConflictsCommand(toolContext));
        registerCommand("create", new CreateCommand(toolContext));
        registerCommand("drop", new DropCommand(toolContext));
        registerCommand("edit", new EditCommand(toolContext));
        registerCommand("grep", new GrepCommand(toolContext));
        registerCommand("image", new ImageCommand(toolContext));
        registerCommand("ls", new LsCommand(toolContext));
        registerCommand("mv", new MvCommand(toolContext));
        registerCommand("patch", new PatchCommand(toolContext));
        registerCommand("rm", new RmCommand(toolContext));
        registerCommand("run", new RunCommand(toolContext));
        registerCommand("speech", new SpeechCommand(toolContext));
        registerCommand("status", new StatusCommand(toolContext));
        registerCommand("test", new TestCommand(toolContext));
        registerCommand("undo", new UndoCommand(toolContext));
        registerCommand("unrecord", new UnrecordCommand(toolContext));

        // Stubs for future commands
        registerCommand("refactor", new RefactorCommand(toolContext));
        registerCommand("debug", new DebugCommand(toolContext));
        registerCommand("doc", new DocCommand(toolContext));
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