package dumb.hack.commands.prolog;

import dumb.code.commands.Command;
import dumb.code.MessageHandler;
import dumb.mcr.QueryResult;
import dumb.mcr.Session;

public class PrologCommand implements Command {

    private final Session session;
    private final MessageHandler messageHandler;

    public PrologCommand(Session session, MessageHandler messageHandler) {
        this.session = session;
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            messageHandler.onMessage("Usage: /prolog <query>");
            return;
        }

        String query = String.join(" ", args);
        messageHandler.onMessage("Querying MCR with Prolog: '" + query + "'");

        QueryResult result = session.query(query);

        if (result.success()) {
            messageHandler.onMessage("Prolog query successful.");
            if (result.bindings() != null && !result.bindings().isEmpty()) {
                messageHandler.onMessage("Solutions:");
                result.bindings().forEach(solution -> messageHandler.onMessage("  " + solution));
            } else {
                messageHandler.onMessage("Query was successful, but returned no solutions.");
            }
        } else {
            messageHandler.onMessage("Prolog query failed.");
        }
    }
}
