package dumb.hack.commands;

import dumb.hack.App;
import dumb.hack.LMOptions;
import dumb.hack.provider.ProviderFactory;
import dumb.hack.provider.MissingApiKeyException;
import dumb.lm.LMClient;
import dumb.mcr.MCR;
import dumb.mcr.QueryResult;
import dumb.mcr.Session;
import dev.langchain4j.model.chat.ChatModel;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "mcr", mixinStandardHelpOptions = true,
        description = "Interacts with the Model Context Reasoner.")
public class McrCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The natural language query to send to the MCR.")
    private String query;

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
        MCR mcr = new MCR(lmClient);
        Session session = mcr.createSession();

        // For this example, we'll assert some basic knowledge.
        // In a real application, this would be more sophisticated.
        session.assertProlog("is_a(tweety, canary).");
        session.assertProlog("bird(X) :- is_a(X, canary).");
        session.assertProlog("has_wings(X) :- bird(X).");
        session.addRelationship("tweety", "likes", "seeds");

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

        return 0;
    }
}
