package com.pijul.hack.commands;

import com.pijul.hack.Container;
import com.pijul.mcr.QueryResult;
import com.pijul.mcr.Session;

public class QueryCommand implements Command {

    private final Container container;

    public QueryCommand(Container container) {
        this.container = container;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            container.getMessageHandler().handleMessage("Usage: /query <your natural language query>");
            return;
        }

        Session mcrSession = container.getMcrSession();
        if (mcrSession == null) {
            container.getMessageHandler().handleMessage("MCR Session not initialized.");
            return;
        }

        String query = String.join(" ", args);
        QueryResult result = mcrSession.nquery(query);

        if (result.isSuccess()) {
            container.getMessageHandler().handleMessage("Query successful.");
            if (result.getBindings().isEmpty()) {
                container.getMessageHandler().handleMessage("No results found.");
            } else {
                result.getBindings().forEach(bindings -> {
                    container.getMessageHandler().handleMessage("Solution: " + bindings.toString());
                });
            }
        } else {
            container.getMessageHandler().handleMessage("Error executing query: " + result.getError());
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
