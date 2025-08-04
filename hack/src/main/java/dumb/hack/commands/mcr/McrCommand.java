package dumb.hack.commands.mcr;

import dumb.code.commands.Command;
import dumb.code.MessageHandler;
import dumb.mcr.QueryResult;
import dumb.mcr.Session;

public class McrCommand implements Command {

    private final Session session;
    private final MessageHandler messageHandler;

    public McrCommand(Session session, MessageHandler messageHandler) {
        this.session = session;
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            messageHandler.onMessage("Usage: /mcr <query>");
            return;
        }

        String query = String.join(" ", args);
        messageHandler.onMessage("Querying MCR with: '" + query + "'");

        QueryResult result = session.nquery(query);

        if (result.success()) {
            messageHandler.onMessage("MCR query successful.");
            messageHandler.onMessage("Original query: " + result.originalQuery());
            if (result.bindings() != null && !result.bindings().isEmpty()) {
                messageHandler.onMessage("Solutions:");
                result.bindings().forEach(solution -> messageHandler.onMessage("  " + solution));
            } else {
                messageHandler.onMessage("Query was successful, but returned no solutions.");
            }
        } else {
            messageHandler.onMessage("MCR query failed.");
            // Optionally, you could add more error details from the result if available
        }
    }
}
