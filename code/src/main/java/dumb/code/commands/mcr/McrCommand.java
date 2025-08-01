package dumb.code.commands.mcr;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dumb.code.commands.Command;
import dumb.code.util.IProcessRunner;
import dumb.code.MessageHandler;
import dumb.code.util.ProcessResult;

import java.util.Map;

public class McrCommand implements Command {

    private final IProcessRunner processRunner;
    private final MessageHandler messageHandler;
    private final Gson gson;

    public McrCommand(IProcessRunner processRunner, MessageHandler messageHandler) {
        this.processRunner = processRunner;
        this.messageHandler = messageHandler;
        this.gson = new Gson();
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            messageHandler.onMessage("Usage: /mcr <query>");
            return;
        }

        String query = String.join(" ", args);
        messageHandler.onMessage("Querying MCR with: '" + query + "'");

        // The path to the hack jar will need to be configured
        // For now, we assume it's in the target directory
        ProcessResult result = processRunner.runWithInput(query, "java", "-jar", "../hack/target/hack-1.0-SNAPSHOT.jar", "mcr", "--server");

        if (result.exitCode() != 0) {
            messageHandler.onMessage("MCR process failed with exit code " + result.exitCode());
            messageHandler.onMessage("Output:\n" + result.output());
            return;
        }

        try {
            Map<String, Object> jsonResult = gson.fromJson(result.output(), Map.class);
            if ((boolean) jsonResult.get("success")) {
                messageHandler.onMessage("MCR query successful.");
                messageHandler.onMessage("Original query: " + jsonResult.get("originalQuery"));
                Object bindings = jsonResult.get("bindings");
                if (bindings != null) {
                    messageHandler.onMessage("Solutions:");
                    messageHandler.onMessage(bindings.toString());
                } else {
                    messageHandler.onMessage("Query was successful, but returned no solutions.");
                }
            } else {
                messageHandler.onMessage("MCR query failed.");
            }
        } catch (JsonSyntaxException e) {
            messageHandler.onMessage("Failed to parse MCR output as JSON.");
            messageHandler.onMessage("Raw output:\n" + result.output());
        }
    }
}
