package com.pijul.aider;

import com.pijul.aider.commands.AddCommand;
import com.pijul.aider.commands.Command;
import com.pijul.aider.commands.apply.ApplyCommand;
import com.pijul.aider.commands.channel.ChannelCommand;
import com.pijul.aider.commands.clear.ClearCommand;
import com.pijul.aider.commands.codebase.CodebaseCommand;
import com.pijul.aider.commands.commit.CommitCommand;
import com.pijul.aider.commands.conflicts.ConflictsCommand;
import com.pijul.aider.commands.create.CreateCommand;
import com.pijul.aider.commands.diff.DiffCommand;
import com.pijul.aider.commands.drop.DropCommand;
import com.pijul.aider.commands.edit.EditCommand;
import com.pijul.aider.commands.exit.ExitCommand;
import com.pijul.aider.commands.grep.GrepCommand;
import com.pijul.aider.commands.help.HelpCommand;
import com.pijul.aider.commands.image.ImageCommand;
import com.pijul.aider.commands.ls.LsCommand;
import com.pijul.aider.commands.mv.MvCommand;
import com.pijul.aider.commands.patch.PatchCommand;
import com.pijul.aider.commands.query.QueryCommand;
import com.pijul.aider.commands.record.RecordCommand;
import com.pijul.aider.commands.rm.RmCommand;
import com.pijul.aider.commands.run.RunCommand;
import com.pijul.aider.commands.speech.SpeechCommand;
import com.pijul.aider.commands.status.StatusCommand;
import com.pijul.aider.commands.test.TestCommand;
import com.pijul.aider.commands.undo.UndoCommand;
import com.pijul.aider.commands.unrecord.UnrecordCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final MessageHandler messageHandler;
    private final Map<String, Command> commands;

    public CommandManager(Container container) {
        this.messageHandler = container.getMessageHandler();
        this.commands = new HashMap<>();
        registerCommand("help", new HelpCommand(container));
        registerCommand("exit", new ExitCommand(container));
        registerCommand("add", new AddCommand(container));
        registerCommand("diff", new DiffCommand(container));
        registerCommand("record", new RecordCommand(container));
        registerCommand("commit", new CommitCommand(container)); // Alias for record
        registerCommand("query", new QueryCommand(container));

        // Newly registered commands
        registerCommand("apply", new ApplyCommand(container));
        registerCommand("channel", new ChannelCommand(container));
        registerCommand("clear", new ClearCommand(container));
        registerCommand("codebase", new CodebaseCommand(container));
        registerCommand("conflicts", new ConflictsCommand(container));
        registerCommand("create", new CreateCommand(container));
        registerCommand("drop", new DropCommand(container));
        registerCommand("edit", new EditCommand(container));
        registerCommand("grep", new GrepCommand(container));
        registerCommand("image", new ImageCommand(container));
        registerCommand("ls", new LsCommand(container));
        registerCommand("mv", new MvCommand(container));
        registerCommand("patch", new PatchCommand(container));
        registerCommand("rm", new RmCommand(container));
        registerCommand("run", new RunCommand(container));
        registerCommand("speech", new SpeechCommand(container));
        registerCommand("status", new StatusCommand(container));
        registerCommand("test", new TestCommand(container));
        registerCommand("undo", new UndoCommand(container));
        registerCommand("unrecord", new UnrecordCommand(container));
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