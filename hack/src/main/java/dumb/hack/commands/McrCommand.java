package dumb.hack.commands;

import dev.langchain4j.model.chat.ChatModel;
import dumb.hack.App;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.hack.tools.CodeToolProvider;
import dumb.lm.LMClient;
import dumb.mcr.*;
import dumb.tools.ToolContext;
import dumb.tools.Workspace;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "mcr", mixinStandardHelpOptions = true,
        description = "Interacts with the Model Context Reasoner.")
public class McrCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The natural language query to send to the MCR.", arity = "0..1")
    private String query;

    @CommandLine.Option(names = {"--server"}, description = "Run the MCR in server mode.")
    private boolean serverMode;

    @CommandLine.ParentCommand
    private App app;

    @Override
    public Integer call() throws IOException {
        if (query == null && !serverMode) {
            System.err.println("Error: A query is required unless in --server mode.");
            return 1;
        }

        ProviderFactory factory = new ProviderFactory(app.getLmOptions());
        ChatModel model;
        try {
            model = factory.create();
        } catch (MissingApiKeyException e) {
            System.err.println(e.getMessage());
            return 1;
        }

        LMClient lmClient = new LMClient(model);
        ToolContext toolContext = new ToolContext(null, null, new dumb.tools.LMManager(lmClient));
        Workspace workspace = toolContext.getWorkspace();
        workspace.loadWorkspace(".");

        MCR mcr = new MCR(lmClient);
        CodeToolProvider toolProvider = new CodeToolProvider(toolContext.fileManager, workspace);
        Session session = mcr.createSession(toolProvider);

        for (String file : workspace.getFiles()) {
            session.assertProlog("file(\"" + file + "\").");
        }

        if (serverMode) {
            new McrServer(session).start();
        } else {
            System.out.println("Querying MCR with: '" + query + "'");
            QueryResult result = session.nquery(query);

            if (result.success()) {
                System.out.println("Success!");
                System.out.println("Original Query: " + result.originalQuery());
                if (result.bindings() != null && !result.bindings().isEmpty()) {
                    System.out.println("Solutions:");
                    result.getBindings().forEach(solution -> System.out.println("  " + solution));
                } else {
                    System.out.println("Query was successful, but returned no solutions.");
                }
            } else {
                System.err.println("MCR query failed.");
            }
        }

        return 0;
    }
}
