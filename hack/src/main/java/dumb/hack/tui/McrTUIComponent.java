package dumb.hack.tui;

import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import dev.langchain4j.model.chat.ChatModel;
import dumb.hack.App;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.lm.LMClient;
import dumb.mcr.MCR;
import dumb.mcr.McrTUI;
import dumb.mcr.Session;

public class McrTUIComponent implements TUIComponent {
    @Override
    public String getName() {
        return "MCR";
    }

    @Override
    public Panel createPanel(App app, BreadcrumbManager breadcrumbManager) {
        try {
            // TODO: Pass breadcrumbManager to McrTUI and have it update the breadcrumbs
            ProviderFactory factory = new ProviderFactory(app.getLmOptions());
            ChatModel model = factory.create();
            LMClient lmClient = new LMClient(model);
            String promptsPath = app.getConfigManager().getProperty("prompts.path", null);
            MCR mcr = new MCR(lmClient, promptsPath);
            Session session = mcr.createSession();
            session.assertProlog("is_a(tweety, canary).");
            session.assertProlog("bird(X) :- is_a(X, canary).");
            session.assertProlog("has_wings(X) :- bird(X).");
            session.addRelationship("tweety", "likes", "seeds");

            McrTUI mcrTUI = new McrTUI(session);
            return mcrTUI.createPanel();
        } catch (MissingApiKeyException e) {
            Panel errorPanel = new Panel();
            errorPanel.addComponent(new Label("Error starting MCR TUI: " + e.getMessage()));
            return errorPanel;
        }
    }
}
