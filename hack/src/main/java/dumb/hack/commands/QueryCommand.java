package dumb.hack.commands;

import dumb.hack.HackContext;
import dumb.mcr.QueryResult;
import dumb.mcr.Session;

public class QueryCommand implements Command {

    private final HackContext context;

    public QueryCommand(HackContext context) {
        this.context = context;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            context.getMessageHandler().handleMessage("Usage: /query <your natural language query>");
            return;
        }

        Session mcrSession = context.getMcrSession();
        if (mcrSession == null) {
            context.getMessageHandler().handleMessage("MCR Session not initialized.");
            return;
        }

        String query = String.join(" ", args);
        QueryResult result = mcrSession.nquery(query);

        if (result.success()) {
            context.getMessageHandler().handleMessage("Query successful.");
            if (result.getBindings().isEmpty()) {
                context.getMessageHandler().handleMessage("No results found.");
            } else {
                result.getBindings().forEach(bindings -> context.getMessageHandler().handleMessage("Solution: " + bindings.toString()));
            }
        } else {
            context.getMessageHandler().handleMessage("Error executing query: " + result.error());
        }
    }

    @Override
    public void init() {
        // Nothing to initialize
    }

    @Override
    public void cleanup() {
        // Nothing to clean up
    }
}
