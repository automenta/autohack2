package dumb.hack.commands;

import dev.langchain4j.model.chat.ChatModel;
import dumb.code.*;
import dumb.code.commands.reason.ReasonCommand;
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
        dumb.mcr.MCR mcr = new dumb.mcr.MCR(lmClient);
        HelpService helpService = new DefaultHelpService(mcr, null);

        ReasonCommand reasonCommand = new ReasonCommand(mcr.createSession(), new CodebaseTool(new VersionControlTool(System.getProperty("user.dir")), new FileSystemTool(System.getProperty("user.dir"))), null, new FileSystemTool(System.getProperty("user.dir")), false);
        reasonCommand.execute(new String[]{task});

        System.out.println("Non-interactive task completed.");
        return 0;
    }
}
