package dumb.hack.tui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProjectManager {
    private List<Project> projects;

    public ProjectManager() {
        loadProjects();
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

    public List<Project> getProjects() {
        return projects;
    }

    public Project createNewProject(String name, Template template) throws IOException {
        Path projectPath = Paths.get(System.getProperty("user.home"), ".autohack", "projects", name);

        // 1. Create project directory from template
        Files.createDirectories(projectPath);
        FileUtils.copyDirectory(new File(template.getPath()), projectPath.toFile());

        // 2. Add project to the list and save to projects.json
        Project newProject = new Project();
        newProject.name = name;
        newProject.path = projectPath.toString();
        projects.add(newProject);

        saveProjects();
        return newProject;
    }

    public Project importProject(String path) throws IOException {
        File projectDir = new File(path);
        String name = projectDir.getName();

        // 1. Add project to the list and save to projects.json
        Project newProject = new Project();
        newProject.name = name;
        newProject.path = projectDir.getAbsolutePath();
        projects.add(newProject);

        saveProjects();
        return newProject;
    }

    public void renameProject(Project project, String newName) throws IOException {
        project.name = newName;
        saveProjects();
    }

    public void deleteProject(Project project) throws IOException {
        FileUtils.deleteDirectory(new File(project.path));
        projects.remove(project);
        saveProjects();
    }

    public void archiveProject(Project project) throws IOException {
        Path archivedDir = Paths.get(System.getProperty("user.home"), ".autohack", "archived");
        Files.createDirectories(archivedDir);
        Path sourcePath = new File(project.path).toPath();
        Path destPath = archivedDir.resolve(sourcePath.getFileName());
        Files.move(sourcePath, destPath);
        projects.remove(project);
        saveProjects();
    }

    public List<String> runHealthCheck(Project project) {
        List<String> issues = new ArrayList<>();
        File pom = new File(project.path, "pom.xml");
        if (!pom.exists()) {
            issues.add("pom.xml not found.");
        }
        return issues;
    }

    private void saveProjects() throws IOException {
        try (FileWriter writer = new FileWriter("projects.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(projects, writer);
        }
    }
}
