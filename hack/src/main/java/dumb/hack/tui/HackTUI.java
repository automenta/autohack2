package dumb.hack.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dev.langchain4j.model.chat.ChatModel;
import dumb.code.Code;
import dumb.code.CodeUI;
import dumb.code.tui.events.CommandFinishEvent;
import dumb.code.tui.events.CommandOutputEvent;
import dumb.code.tui.events.CommandStartEvent;
import dumb.code.tui.events.PlanGeneratedEvent;
import dumb.code.tui.events.StatusUpdateEvent;
import dumb.code.tui.events.TaskFinishEvent;
import dumb.code.tui.events.UIEvent;
import dumb.hack.App;
import dumb.code.help.DefaultHelpService;
import dumb.code.help.HelpService;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.lm.LMClient;
import dumb.mcr.MCR;
import dumb.mcr.McrTUI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dumb.mcr.Session;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HackTUI {

    private static class Project {
        String name;
        String path;
    }

    private final App app;
    private Panel contentPanel;
    private Label statusBar;

    // Cache for the UI components
    private CodeUI codeUI;
    private McrTUI mcrTUI;
    private Panel codePanel;
    private Panel mcrPanel;

    // State for the Action View
    private List<String> planSteps;
    private List<Label> stepLabels;
    private MultiWindowTextGUI gui;
    private List<Project> projects;
    private Project currentProject;
    private final BlockingQueue<UIEvent> eventQueue = new LinkedBlockingQueue<>();


    public HackTUI(App app) {
        this.app = app;
        loadProjects();
        if (projects != null && !projects.isEmpty()) {
            currentProject = projects.get(0);
        }
    }

    private void loadProjects() {
        try (FileReader reader = new FileReader("projects.json")) {
            Type projectListType = new TypeToken<ArrayList<Project>>(){}.getType();
            projects = new Gson().fromJson(reader, projectListType);
        } catch (IOException e) {
            projects = new ArrayList<>();
            // Handle error, maybe show in status bar
        }
    }

    public void start() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();

            BasicWindow window = new BasicWindow("Hack");
            window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));

            // Root panel with a horizontal layout
            Panel rootPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

            // --- Sidebar Panel (Left) ---
            Panel sidebarPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            ActionListBox projectListBox = new ActionListBox();
            for (Project project : projects) {
                projectListBox.addItem(project.name, () -> switchProject(project));
            }
            sidebarPanel.addComponent(projectListBox);
            rootPanel.addComponent(sidebarPanel.withBorder(Borders.singleLine("Projects")));


            // --- Main Content Panel (Right) ---
            Panel mainContentPanel = new Panel(new LinearLayout(Direction.VERTICAL));

            Panel topPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            topPanel.addComponent(new Button("Code", this::showCodeTUI));
            topPanel.addComponent(new Button("MCR", this::showMcrTUI));
            topPanel.addComponent(new Button("Exit", window::close));
            mainContentPanel.addComponent(topPanel.withBorder(Borders.singleLine()));

            contentPanel = new Panel();
            mainContentPanel.addComponent(contentPanel.withBorder(Borders.singleLine("Content")));

            statusBar = new Label("Ready");
            mainContentPanel.addComponent(statusBar.withBorder(Borders.singleLine()));

            rootPanel.addComponent(mainContentPanel);

            window.setComponent(rootPanel);

            this.gui = new MultiWindowTextGUI(screen);
            startEventProcessor();
            this.gui.addWindowAndWait(window);

        } finally {
            if (screen != null) {
                screen.stopScreen();
            }
        }
    }

    private void startEventProcessor() {
        gui.getGUIThread().invokeLater(() -> {
            while (true) {
                try {
                    UIEvent event = eventQueue.take();
                    // Since we are already on the GUI thread, we can call handleEvent directly.
                    handleEvent(event);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    private void handleEvent(UIEvent event) {
        if (event instanceof StatusUpdateEvent e) {
            statusBar.setText(e.status());
        } else if (event instanceof PlanGeneratedEvent e) {
            this.planSteps = e.plan();
            this.stepLabels = new java.util.ArrayList<>();
            Panel actionPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            for (int i = 0; i < planSteps.size(); i++) {
                Label stepLabel = new Label("[ ] " + (i + 1) + ". " + planSteps.get(i));
                stepLabels.add(stepLabel);
                actionPanel.addComponent(stepLabel);
            }
            contentPanel.removeAllComponents();
            contentPanel.addComponent(actionPanel);
        } else if (event instanceof CommandStartEvent e) {
            if (e.commandIndex() < stepLabels.size()) {
                stepLabels.get(e.commandIndex()).setText("[*] " + (e.commandIndex() + 1) + ". " + planSteps.get(e.commandIndex()));
            }
        } else if (event instanceof CommandOutputEvent e) {
            statusBar.setText("Output: " + e.output());
        } else if (event instanceof CommandFinishEvent e) {
            if (e.commandIndex() < stepLabels.size()) {
                String prefix = e.success() ? "[✓]" : "[✗]";
                stepLabels.get(e.commandIndex()).setText(prefix + " " + (e.commandIndex() + 1) + ". " + planSteps.get(e.commandIndex()));
            }
        } else if (event instanceof TaskFinishEvent e) {
            String message = e.success() ? "Task completed successfully." : "Task failed.";
            statusBar.setText(message);
        }
    }

    private void switchProject(Project project) {
        this.currentProject = project;
        this.codeUI = null; // Invalidate the cache
        this.codePanel = null;
        contentPanel.removeAllComponents();
        statusBar.setText("Switched to project: " + project.name);
        showCodeTUI(); // Reload the Code TUI for the new project
    }

    private void showCodeTUI() {
        contentPanel.removeAllComponents();
        if (codeUI == null) {
            if (currentProject == null) {
                contentPanel.addComponent(new Label("Error: No project selected."));
                return;
            }
            try {
                ProviderFactory factory = new ProviderFactory(app.getLmOptions());
                ChatModel model = factory.create();
                LMClient lmClient = new LMClient(model);
                MCR mcr = new MCR(lmClient);
                HelpService helpService = new DefaultHelpService(mcr);
                dumb.code.IFileManager fileManager = new dumb.code.FileManager(currentProject.path);
                Code code = new Code(null, fileManager, new dumb.code.LMManager(lmClient), helpService, eventQueue);
                codeUI = new CodeUI(code);
                codePanel = codeUI.createPanel();
            } catch (MissingApiKeyException e) {
                contentPanel.addComponent(new Label("Error starting Code TUI: " + e.getMessage()));
                return;
            }
        }
        contentPanel.addComponent(codePanel);
        statusBar.setText("Project: " + currentProject.name + " | Mode: Code");
    }

    private void showMcrTUI() {
        contentPanel.removeAllComponents();
        if (mcrTUI == null) {
            try {
                ProviderFactory factory = new ProviderFactory(app.getLmOptions());
                ChatModel model = factory.create();
                LMClient lmClient = new LMClient(model);
                MCR mcr = new MCR(lmClient);
                Session session = mcr.createSession();
                session.assertProlog("is_a(tweety, canary).");
                session.assertProlog("bird(X) :- is_a(X, canary).");
                session.assertProlog("has_wings(X) :- bird(X).");
                session.addRelationship("tweety", "likes", "seeds");

                mcrTUI = new McrTUI(session);
                mcrPanel = mcrTUI.createPanel();
            } catch (MissingApiKeyException e) {
                contentPanel.addComponent(new Label("Error starting MCR TUI: " + e.getMessage()));
                return;
            }
        }
        contentPanel.addComponent(mcrPanel);
        statusBar.setText("Mode: MCR");
    }
}
