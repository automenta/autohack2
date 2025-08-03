package dumb.hack.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dumb.mcr.Session;
import org.apache.commons.io.FileUtils;

import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Panel sidebarPanel;
    private Button codeButton;
    private Button mcrButton;
    private Button deleteProjectButton;
    private BasicWindow window;


    // Cache for the UI components
    private CodeUI codeUI;
    private McrTUI mcrTUI;
    private Panel codePanel;
    private Panel codeViewPanel;
    private Panel actionPanel;

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
            Type projectListType = new TypeToken<ArrayList<Project>>() {}.getType();
            projects = new Gson().fromJson(reader, projectListType);
            if (projects == null) {
                projects = new ArrayList<>();
            }
        } catch (IOException e) {
            projects = new ArrayList<>();
            // This is not an error, it just means the user hasn't created any projects yet.
        }
    }

    public void start() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();

            window = new BasicWindow("Hack");
            window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));

            // Root panel with a horizontal layout
            Panel rootPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

            // --- Sidebar Panel (Left) ---
            sidebarPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            rootPanel.addComponent(sidebarPanel.withBorder(Borders.singleLine("Projects")));


            // --- Main Content Panel (Right) ---
            Panel mainContentPanel = new Panel(new LinearLayout(Direction.VERTICAL));

            Panel topPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            codeButton = new Button("Code", this::showCodeTUI);
            mcrButton = new Button("MCR", this::showMcrTUI);
            topPanel.addComponent(codeButton);
            topPanel.addComponent(mcrButton);
            topPanel.addComponent(new Button("Exit", window::close));
            mainContentPanel.addComponent(topPanel.withBorder(Borders.singleLine()));

            contentPanel = new Panel();
            mainContentPanel.addComponent(contentPanel.withBorder(Borders.singleLine("Content")));

            statusBar = new Label("Ready");
            mainContentPanel.addComponent(statusBar.withBorder(Borders.singleLine()));

            rootPanel.addComponent(mainContentPanel);

            if (projects.isEmpty()) {
                showWelcomeScreen(sidebarPanel, codeButton, mcrButton);
            } else {
                showProjectScreen(sidebarPanel, codeButton, mcrButton);
            }

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

    private void showWelcomeScreen(Panel sidebar, Button codeButton, Button mcrButton) {
        sidebar.addComponent(new Button("New Project...", this::showNewProjectDialog));
        deleteProjectButton = new Button("Delete Project", this::showDeleteProjectDialog);
        deleteProjectButton.setEnabled(false);
        sidebar.addComponent(deleteProjectButton);

        contentPanel.addComponent(new Label("Welcome to Hack! Create a project to get started."));
        statusBar.setText("No projects loaded.");
        codeButton.setEnabled(false);
        mcrButton.setEnabled(false);
    }

    private void showProjectScreen(Panel sidebar, Button codeButton, Button mcrButton) {
        ActionListBox projectListBox = new ActionListBox();
        for (Project project : projects) {
            projectListBox.addItem(project.name, () -> {
                switchProject(project);
                codeButton.setEnabled(true);
                mcrButton.setEnabled(true);
                deleteProjectButton.setEnabled(true);
            });
        }
        sidebar.addComponent(projectListBox);
        sidebar.addComponent(new Button("New Project...", this::showNewProjectDialog));
        deleteProjectButton = new Button("Delete Project", this::showDeleteProjectDialog);
        sidebar.addComponent(deleteProjectButton);

        // Auto-select first project
        if (currentProject != null) {
            switchProject(currentProject);
            codeButton.setEnabled(true);
            mcrButton.setEnabled(true);
            deleteProjectButton.setEnabled(true);
        } else {
            codeButton.setEnabled(false);
            mcrButton.setEnabled(false);
            deleteProjectButton.setEnabled(false);
        }
    }

    private void showNewProjectDialog() {
        final BasicWindow dialog = new BasicWindow("Create New Project");
        dialog.setHints(Arrays.asList(Window.Hint.CENTERED));

        Panel dialogPanel = new Panel(new GridLayout(2));

        dialogPanel.addComponent(new Label("Project Name:"));
        final TextBox nameBox = new TextBox().addTo(dialogPanel);

        dialogPanel.addComponent(new Label("Template:"));
        ComboBox<Template> templateBox = new ComboBox<>();
        TemplateService templateService = new TemplateService("templates");
        List<Template> templates = templateService.getAvailableTemplates();
        for (Template template : templates) {
            templateBox.addItem(template);
        }
        dialogPanel.addComponent(templateBox);


        dialogPanel.addComponent(new EmptySpace(new TerminalSize(0, 0))); // Spacer

        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttonPanel.addComponent(new Button("Create", () -> {
            String name = nameBox.getText().trim();
            Template selectedTemplate = templateBox.getSelectedItem();

            if (name.isEmpty()) {
                MessageDialog.showMessageDialog(gui, "Input Error", "Project Name cannot be empty.");
                return;
            }

            // Check for duplicate project name
            if (projects.stream().anyMatch(p -> p.name.equalsIgnoreCase(name))) {
                MessageDialog.showMessageDialog(gui, "Input Error", "A project with this name already exists.");
                return;
            }

            if (selectedTemplate == null) {
                MessageDialog.showMessageDialog(gui, "Input Error", "Please select a template.");
                return;
            }

            dialog.close();
            createNewProject(name, selectedTemplate);
        }));
        buttonPanel.addComponent(new Button("Cancel", dialog::close));

        dialogPanel.addComponent(buttonPanel);

        dialog.setComponent(dialogPanel);
        gui.addWindow(dialog);
    }

    private void createNewProject(String name, Template template) {
        Path projectPath = Paths.get(System.getProperty("user.home"), ".autohack", "projects", name);

        // 1. Create project directory from template
        try {
            Files.createDirectories(projectPath);
            FileUtils.copyDirectory(new File(template.getPath()), projectPath.toFile());
        } catch (IOException e) {
            MessageDialog.showMessageDialog(gui, "Error", "Could not create project: " + e.getMessage());
            return;
        }

        // 2. Add project to the list and save to projects.json
        Project newProject = new Project();
        newProject.name = name;
        newProject.path = projectPath.toString();
        projects.add(newProject);
        currentProject = newProject; // Set as current

        try (FileWriter writer = new FileWriter("projects.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(projects, writer);
        } catch (IOException e) {
            MessageDialog.showMessageDialog(gui, "Error", "Could not save projects file: " + e.getMessage());
            projects.remove(newProject); // Rollback
            return;
        }

        // 3. Refresh the UI
        sidebarPanel.removeAllComponents();
        contentPanel.removeAllComponents();
        showProjectScreen(sidebarPanel, codeButton, mcrButton);
        statusBar.setText("Project '" + name + "' created successfully!");
    }

    private void showDeleteProjectDialog() {
        if (currentProject == null) {
            MessageDialog.showMessageDialog(gui, "Error", "No project selected to delete.");
            return;
        }

        final BasicWindow dialog = new BasicWindow("Confirm Deletion");
        dialog.setHints(Arrays.asList(Window.Hint.CENTERED));
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("Are you sure you want to delete the project '" + currentProject.name + "'?"));
        panel.addComponent(new Label("This will permanently delete the directory:"));
        panel.addComponent(new Label(currentProject.path));
        panel.addComponent(new EmptySpace());

        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttonPanel.addComponent(new Button("Yes, Delete", () -> {
            deleteCurrentProject();
            dialog.close();
        }));
        buttonPanel.addComponent(new Button("No, Cancel", dialog::close));
        panel.addComponent(buttonPanel);

        dialog.setComponent(panel);
        gui.addWindow(dialog);
    }

    private void deleteCurrentProject() {
        if (currentProject == null) {
            return; // Should not happen if called from dialog
        }

        Project projectToDelete = currentProject;

        // 1. Delete project directory
        try {
            FileUtils.deleteDirectory(new File(projectToDelete.path));
        } catch (IOException e) {
            MessageDialog.showMessageDialog(gui, "Error", "Could not delete project directory: " + e.getMessage());
            return; // Stop if we can't delete the files
        }

        // 2. Remove project from list
        projects.remove(projectToDelete);

        // 3. Save updated projects.json
        try (FileWriter writer = new FileWriter("projects.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(projects, writer);
        } catch (IOException e) {
            // This is tricky. The directory is gone but the config is not updated.
            // For now, we'll just show an error. A more robust solution might try to restore the directory or retry saving.
            MessageDialog.showMessageDialog(gui, "Fatal Error", "Could not save projects file after deletion: " + e.getMessage() + "\nPlease check your projects.json file.");
            // We continue to update the UI, as the in-memory list is correct.
        }

        // 4. Update UI state
        statusBar.setText("Project '" + projectToDelete.name + "' deleted.");
        currentProject = projects.isEmpty() ? null : projects.get(0); // Select first project or null

        // 5. Refresh the UI
        sidebarPanel.removeAllComponents();
        contentPanel.removeAllComponents();
        if (projects.isEmpty()) {
            showWelcomeScreen(sidebarPanel, codeButton, mcrButton);
        } else {
            showProjectScreen(sidebarPanel, codeButton, mcrButton);
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
            // We now have a persistent actionPanel, so we just clear it and add the new plan.
            if (actionPanel == null) return; // Should not happen if view is visible

            this.planSteps = e.plan();
            this.stepLabels = new java.util.ArrayList<>();

            actionPanel.removeAllComponents(); // Clear the persistent panel

            for (int i = 0; i < planSteps.size(); i++) {
                Label stepLabel = new Label("[ ] " + (i + 1) + ". " + planSteps.get(i));
                stepLabels.add(stepLabel);
                actionPanel.addComponent(stepLabel); // Add to the persistent panel
            }
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
        this.codeViewPanel = null;
        this.actionPanel = null;
        if (deleteProjectButton != null) {
            deleteProjectButton.setEnabled(true);
        }
        contentPanel.removeAllComponents();
        statusBar.setText("Switched to project: " + project.name);
        showCodeTUI(); // Reload the Code TUI for the new project
    }

    private void showCodeTUI() {
        if (codeViewPanel == null) {
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

                codeViewPanel = new Panel(new LinearLayout(Direction.VERTICAL));

                actionPanel = new Panel(new LinearLayout(Direction.VERTICAL));
                actionPanel.addComponent(new Label("Enter a task in the terminal below."));
                codeViewPanel.addComponent(actionPanel.withBorder(Borders.singleLine("Plan")));

                codePanel = codeUI.createPanel(); // This is the terminal
                codeViewPanel.addComponent(codePanel);

            } catch (MissingApiKeyException e) {
                contentPanel.removeAllComponents();
                contentPanel.addComponent(new Label("Error starting Code TUI: " + e.getMessage()));
                return;
            }
        }
        contentPanel.removeAllComponents();
        contentPanel.addComponent(codeViewPanel);
        statusBar.setText("Project: " + currentProject.name + " | Mode: Code");
    }

    private void showMcrTUI() {
        contentPanel.removeAllComponents();
        try {
            ProviderFactory factory = new ProviderFactory(app.getLmOptions());
            ChatModel model = factory.create();
            LMClient lmClient = new LMClient(model);
            MCR mcr = new MCR(lmClient);
            Session session = mcr.createSession();

            // Assert facts about existing projects
            if (projects != null) {
                for (Project project : projects) {
                    // Escape quotes in name just in case
                    String projectName = project.name.replace("\"", "\\\"");
                    session.assertProlog("project(\"" + projectName + "\").");
                }
            }

            // The old hardcoded facts
            session.assertProlog("is_a(tweety, canary).");
            session.assertProlog("bird(X) :- is_a(X, canary).");
            session.assertProlog("has_wings(X) :- bird(X).");
            session.addRelationship("tweety", "likes", "seeds");

            mcrTUI = new McrTUI(session);
            Panel newMcrPanel = mcrTUI.createPanel();
            contentPanel.addComponent(newMcrPanel);

        } catch (MissingApiKeyException e) {
            contentPanel.addComponent(new Label("Error starting MCR TUI: " + e.getMessage()));
            return;
        }
        statusBar.setText("Mode: MCR");
    }
}
