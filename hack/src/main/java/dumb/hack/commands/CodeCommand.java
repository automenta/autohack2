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

        LMClient lmClient = new LMClient(model);
        LMManager lmManager = new LMManager(lmClient);
        HelpService helpService = new DefaultHelpService();
        Code code = new Code(backend, null, lmManager, helpService);

        ReasonCommand reasonCommand = new ReasonCommand(code, helpService);
        reasonCommand.execute(new String[]{task});

        System.out.println("Non-interactive task completed.");
        return 0;
    }
}
