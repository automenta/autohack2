package dumb.hack.commands;

import dev.langchain4j.model.chat.ChatModel;
import dumb.code.*;
import dumb.code.commands.reason.ReasonCommand;
import dumb.code.tools.CodebaseTool;
import dumb.code.tools.FileSystemTool;
import dumb.code.tools.VersionControlTool;
import dumb.hack.App;
import dumb.code.help.DefaultHelpService;
import dumb.code.help.HelpService;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.lm.LMClient;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "code", mixinStandardHelpOptions = true,
        description = "Runs a non-interactive AI pair programming task.")
public class CodeCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"--backend"}, description = "Specify the version control backend (git, pijul, or files).")
    private String backend;

    @CommandLine.Option(names = {"--task"}, required = true, description = "The task to perform.")
    private String task;

    @CommandLine.ParentCommand
    private App app;

    @Override
    public Integer call() {
        ProviderFactory factory = new ProviderFactory(app.getLmOptions());
        ChatModel model;
        try {
            model = factory.create();
        } catch (MissingApiKeyException e) {
            System.err.println(e.getMessage());
            return 1;
        }

        LMClient lmClient = new LMClient(app.getLmOptions().getProvider(), app.getLmOptions().getModel(), app.getLmOptions().getApiKey());
        if (app.getLmOptions().getProvider().equals("mock")) {
            if (task.equals("create a new file called 'test.txt' with the content 'hello world'")) {
                ((dumb.lm.mock.MockChatModel) model).setDefaultResponse("/create test.txt\n/edit test.txt \"hello world\"");
            } else if (task.equals("list the files in the current directory")) {
                ((dumb.lm.mock.MockChatModel) model).setDefaultResponse("/ls");
            } else if (task.equals("run the command 'echo hello'")) {
                ((dumb.lm.mock.MockChatModel) model).setDefaultResponse("/run echo hello");
            } else if (task.equals("run the tests")) {
                ((dumb.lm.mock.MockChatModel) model).setDefaultResponse("/test");
            } else if (task.equals("list the files, run the tests, and then create a file called 'done.txt'")) {
                ((dumb.lm.mock.MockChatModel) model).setDefaultResponse("/ls\n/test\n/create done.txt");
            }
        }
        dumb.code.LMManager lmManager = new dumb.code.LMManager(app.getLmOptions().getProvider(), app.getLmOptions().getModel(), app.getLmOptions().getApiKey());
        dumb.code.tools.FileSystemTool fileSystemTool = new dumb.code.tools.FileSystemTool(System.getProperty("user.dir"));
        dumb.code.tools.VersionControlTool versionControlTool = new dumb.code.tools.VersionControlTool(System.getProperty("user.dir"));
        dumb.code.tools.CodebaseTool codebaseTool = new dumb.code.tools.CodebaseTool(versionControlTool, fileSystemTool);
        codebaseTool.loadCodebase(System.getProperty("user.dir"));
        dumb.code.util.IProcessRunner processRunner = new dumb.code.util.ProcessRunner();
        dumb.code.MessageHandler messageHandler = new dumb.code.MessageHandler();
        dumb.mcr.MCR mcr = new dumb.mcr.MCR(lmClient);
        dumb.code.help.HelpService helpService = new dumb.code.help.DefaultHelpService(mcr, null);
        dumb.code.CommandManager commandManager = new dumb.code.CommandManager(messageHandler, helpService, codebaseTool, versionControlTool, processRunner, lmManager, fileSystemTool);

        dumb.code.commands.reason.ReasonCommand reasonCommand = new dumb.code.commands.reason.ReasonCommand(lmManager, commandManager, null);
        reasonCommand.execute(new String[]{task});

        System.out.println("Non-interactive task completed.");
        return 0;
    }
}
