package com.pijul.hack;

//import com.example.mcr.core.Session;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Workspace {
    //private final Map<Path, Session> projects = new HashMap<>();
    private final List<Path> projects = new ArrayList<>();
    private Path activeProject;

    public void addProject(Path projectPath) {
        if (!projects.contains(projectPath)) {
            projects.add(projectPath);
        }
        if (activeProject == null) {
            activeProject = projectPath;
        }
    }

    public void removeProject(Path projectPath) {
        projects.remove(projectPath);
        if (activeProject != null && activeProject.equals(projectPath)) {
            activeProject = projects.stream().findFirst().orElse(null);
        }
    }

    public List<Path> getProjects() {
        return new ArrayList<>(projects);
    }

    // public Session getSession(Path projectPath) {
    //     return projects.get(projectPath);
    // }

    public Path getActiveProject() {
        return activeProject;
    }

    public void setActiveProject(Path activeProject) {
        this.activeProject = activeProject;
    }
}
