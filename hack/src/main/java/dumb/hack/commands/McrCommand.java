package dumb.hack.commands;

import dumb.mcr.MCR;
import dumb.mcr.QueryResult;
import dumb.mcr.Session;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "mcr", mixinStandardHelpOptions = true,
        description = "Interacts with the Model Context Reasoner.")
public class McrCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The natural language query to send to the MCR.")
    private String query;

    @Override
    public Integer call() {
        String provider = System.getProperty("llm.provider", "openai");
        String apiKey = System.getenv(provider.toUpperCase() + "_API_KEY");
        String model = "gpt-4o-mini";

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Warning: No API key found for provider '" + provider + "'. MCR may not function as expected.");
            provider = "mock";
        }

        MCR mcr = new MCR(provider, model, apiKey);
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
            if (result.getBindings() != null && !result.getBindings().isEmpty()) {
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
