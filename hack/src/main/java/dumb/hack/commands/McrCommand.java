package dumb.hack.commands;

import dev.langchain4j.model.chat.ChatModel;
import dumb.hack.App;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.lm.LMClient;
import dumb.mcr.*;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "mcr", mixinStandardHelpOptions = true,
        description = "Interacts with the Model Context Reasoner.")
public class McrCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The natural language query to send to the MCR.", arity = "0..1")
    private String query;

    @CommandLine.Option(names = {"--tui"}, description = "Run the MCR in interactive TUI mode.")
    private boolean tuiMode;

    @CommandLine.Option(names = {"--server"}, description = "Run the MCR in server mode.")
    private boolean serverMode;

    @CommandLine.ParentCommand
    private App app;

    @Override
    public Integer call() throws IOException {
        if (tuiMode && serverMode) {
            System.err.println("Error: --tui and --server options cannot be used together.");
            return 1;
        }

        if (query == null && !tuiMode && !serverMode) {
            System.err.println("Error: A query is required unless in --tui or --server mode.");
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

        LMClient lmClient = new LMClient(app.getLmOptions().getProvider(), app.getLmOptions().getModel(), app.getLmOptions().getApiKey());
        MCR mcr = new MCR(lmClient);
        Session session = mcr.createSession();

        // For this example, we'll assert some basic knowledge.
        // In a real application, this would be more sophisticated.
        session.assertProlog("is_a(tweety, canary).");
        session.assertProlog("bird(X) :- is_a(X, canary).");
        session.assertProlog("has_wings(X) :- bird(X).");
        session.addRelationship("tweety", "likes", "seeds");

        if (tuiMode) {
            McrTUI mcrTUI = new McrTUI(session);
            com.googlecode.lanterna.screen.TerminalScreen screen = new com.googlecode.lanterna.terminal.DefaultTerminalFactory().createScreen();
            screen.startScreen();
            com.googlecode.lanterna.gui2.BasicWindow window = new com.googlecode.lanterna.gui2.BasicWindow("MCR TUI");
            window.setComponent(mcrTUI.createPanel());
            new com.googlecode.lanterna.gui2.MultiWindowTextGUI(screen).addWindowAndWait(window);
        } else if (serverMode) {
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
