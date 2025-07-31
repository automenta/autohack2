package dumb.code.commands.query;

import dumb.code.Context;
import dumb.code.commands.Command;
import dumb.code.lm.LMChain;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class QueryCommand implements Command {

    private final Context context;
    private LMChain LMChain;

    public QueryCommand(Context context) {
        this.context = context;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            context.messageHandler.addMessage("system", "Usage: /query <your query>");
            return;
        }

        if (LMChain == null) {
            this.LMChain = new LMChain("ollama", "llama2", "", context, new ArrayList<>(context.toolProviders));
        }

        String query = String.join(" ", args);
        try {
            LMChain.handleQuery(query).get(); // Wait for the future to complete
        } catch (InterruptedException | ExecutionException e) {
            context.messageHandler.addMessage("error", "Error executing query: " + e.getMessage());
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
