package dumb.hack;

import dev.langchain4j.model.chat.ChatModel;
import dumb.code.Code;
import dumb.code.LMManager;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.lm.LMClient;
import dumb.hack.tui.TUIModel;
import dumb.mcr.MCR;
import dumb.mcr.QueryResult;
import dumb.mcr.Result;
import dumb.mcr.Session;

import java.util.ArrayList;
import java.util.List;

public class HackController {

    private final App app;
    private final LMManager lmManager;
    private final Code code;
    private final MCR mcr;
    private final Session session;
    private final TUIModel model;

    public HackController(App app) throws MissingApiKeyException {
        this.app = app;
        this.model = new TUIModel();
        ProviderFactory factory = new ProviderFactory(app.getLmOptions());
        ChatModel model = factory.create();
        LMClient lmClient = new LMClient(model);
        this.lmManager = new LMManager(lmClient);
        this.code = new Code(null, null, this.lmManager);
        this.mcr = new MCR(lmClient);
        this.session = this.mcr.createSession();

        // Listen for messages from the code module and update the model
        this.code.getMessageHandler().setListener(this.model::setCodeOutput);
    }

    public Code getCode() {
        return code;
    }

    public Session getSession() {
        return session;
    }

    public TUIModel getModel() {
        return model;
    }

    public void processCodeInput(String input) {
        code.commandManager.processInput(input);
    }

    public void executeMcrQuery(String query) {
        String trimmedQuery = query.trim();
        List<String> results = new ArrayList<>();

        if (trimmedQuery.startsWith("assertProlog(")) {
            String clause = trimmedQuery.substring("assertProlog(".length(), trimmedQuery.length() - 1);
            Result result = session.assertProlog(clause);
            results.add(result.message());
        } else if (trimmedQuery.startsWith("retractProlog(")) {
            String clause = trimmedQuery.substring("retractProlog(".length(), trimmedQuery.length() - 1);
            Result result = session.retractProlog(clause);
            results.add(result.message());
        } else {
            QueryResult result = session.nquery(query);
            results.add("Original Query: " + result.originalQuery());
            if (result.success()) {
                results.add("Success!");
                if (result.bindings() != null && !result.bindings().isEmpty()) {
                    results.add("Solutions:");
                    result.getBindings().forEach(solution -> results.add("  " + solution));
                } else {
                    results.add("Query was successful, but returned no solutions.");
                }
            } else {
                results.add("MCR query failed.");
            }
        }
        model.setMcrResults(results);
        updateKnowledgeBaseModel();
    }

    private void updateKnowledgeBaseModel() {
        List<String> kb = new ArrayList<>();
        for (dumb.prolog.Clause clause : session.getKnowledgeGraph().getClauses()) {
            kb.add(clause.toString());
        }
        model.setKnowledgeBase(kb);
    }
}
