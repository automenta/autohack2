package dumb.hack.tui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            return issues;
        }

        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", "mvn", "clean", "install");
            } else {
                pb = new ProcessBuilder("mvn", "clean", "install");
            }
            pb.directory(new File(project.path));
            pb.redirectErrorStream(true);

            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("[ERROR]")) {
                        issues.add(line);
                    }
                }
            }

            boolean finished = process.waitFor(5, TimeUnit.MINUTES);
            if (!finished) {
                issues.add("Health check timed out after 5 minutes.");
                process.destroy();
            } else if (process.exitValue() != 0) {
                if (issues.isEmpty()) { // If no specific error lines were found, add a generic one.
                    issues.add("Maven build failed with exit code " + process.exitValue());
                }
            }

        } catch (IOException | InterruptedException e) {
            issues.add("Error running health check: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        return issues;
    }

    private void saveProjects() throws IOException {
        try (FileWriter writer = new FileWriter("projects.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(projects, writer);
        }
    }
}
