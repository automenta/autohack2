package dumb.code;

import dumb.code.tools.CodebaseTool;
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
import dumb.code.commands.createproject.CreateProjectCommand;
import dumb.code.help.HelpService;
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
import dumb.code.tools.FileSystemTool;
import dumb.code.tools.VersionControlTool;
import dumb.code.util.IProcessRunner;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final MessageHandler messageHandler;
    private final Map<String, Command> commands;
    private final HelpService helpService;

    public CommandManager(MessageHandler messageHandler, HelpService helpService, CodebaseTool codebaseTool, VersionControlTool versionControlTool, IProcessRunner processRunner, LMManager lmManager, FileSystemTool fileSystemTool) {
        this.messageHandler = messageHandler;
        this.commands = new HashMap<>();
        this.helpService = helpService;
        registerCommand("help", new HelpCommand(helpService, messageHandler));
        registerCommand("exit", new ExitCommand());
        registerCommand("add", new AddCommand(messageHandler, codebaseTool, versionControlTool, fileSystemTool));
        registerCommand("diff", new DiffCommand(versionControlTool, messageHandler));
        registerCommand("record", new RecordCommand(versionControlTool, messageHandler));
        registerCommand("commit", new CommitCommand(versionControlTool, messageHandler)); // Alias for record
        registerCommand("query", new QueryCommand(lmManager, codebaseTool, messageHandler));
        registerCommand("mcr", new McrCommand(processRunner, messageHandler));

        // Newly registered commands
        registerCommand("apply", new ApplyCommand(versionControlTool, messageHandler));
        registerCommand("channel", new ChannelCommand(versionControlTool, messageHandler));
        registerCommand("clear", new ClearCommand(codebaseTool, messageHandler));
        registerCommand("codebase", new CodebaseCommand(codebaseTool, messageHandler));
        registerCommand("conflicts", new ConflictsCommand(versionControlTool, messageHandler));
        registerCommand("create", new CreateCommand(fileSystemTool, messageHandler));
        registerCommand("drop", new DropCommand(codebaseTool, messageHandler));
        registerCommand("edit", new EditCommand(lmManager, messageHandler));
        registerCommand("grep", new GrepCommand(messageHandler));
        registerCommand("image", new ImageCommand(messageHandler));
        registerCommand("ls", new LsCommand(fileSystemTool, messageHandler));
        registerCommand("mv", new MvCommand(messageHandler));
        registerCommand("patch", new PatchCommand(messageHandler));
        registerCommand("rm", new RmCommand(messageHandler));
        registerCommand("run", new RunCommand(processRunner, messageHandler));
        registerCommand("speech", new SpeechCommand(messageHandler));
        registerCommand("status", new StatusCommand(versionControlTool, messageHandler));
        registerCommand("test", new TestCommand(processRunner, messageHandler));
        registerCommand("tutorial", new TutorialCommand(helpService, messageHandler));
        registerCommand("new", new CreateProjectCommand(helpService, messageHandler));
        registerCommand("undo", new UndoCommand(messageHandler));
        registerCommand("unrecord", new UnrecordCommand(messageHandler));

        // Stubs for future commands
        registerCommand("refactor", new RefactorCommand(messageHandler));
        registerCommand("debug", new DebugCommand(messageHandler));
        registerCommand("doc", new DocCommand(messageHandler));
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
                String[] fullCommand = new String[args.length + 1];
                fullCommand[0] = commandName;
                System.arraycopy(args, 0, fullCommand, 1, args.length);
                helpService.onCommandExecuted(fullCommand);
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