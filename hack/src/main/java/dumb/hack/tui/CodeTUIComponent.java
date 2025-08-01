package dumb.hack.tui;

import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import dev.langchain4j.model.chat.ChatModel;
import dumb.code.Code;
import dumb.code.CodeUI;
import dumb.hack.App;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.lm.LMClient;

public class CodeTUIComponent implements TUIComponent {
    @Override
    public String getName() {
        return "Code";
    }

    @Override
    public Panel createPanel(App app, BreadcrumbManager breadcrumbManager) {
        try {
            ProviderFactory factory = new ProviderFactory(app.getLmOptions());
            ChatModel model = factory.create();
            LMClient lmClient = new LMClient(model);
            Code code = new Code(null, null, new dumb.code.LMManager(lmClient));
            CodeUI codeUI = new CodeUI(code, breadcrumbManager);
            return codeUI.createPanel();
        } catch (MissingApiKeyException e) {
            Panel errorPanel = new Panel();
            errorPanel.addComponent(new Label("Error starting Code TUI: " + e.getMessage()));
            return errorPanel;
        }
    }
}
