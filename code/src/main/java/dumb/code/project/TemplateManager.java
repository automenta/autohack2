package dumb.code.project;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class TemplateManager {

    private final String templatesDir;
    private final Gson gson;

    public TemplateManager(String templatesDir) {
        this.templatesDir = templatesDir;
        this.gson = new Gson();
    }

    public List<ProjectTemplate> loadTemplates() {
        File dir = new File(templatesDir);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        List<ProjectTemplate> templates = new ArrayList<>();
        for (File templateDir : dir.listFiles()) {
            if (templateDir.isDirectory()) {
                File manifest = new File(templateDir, "manifest.json");
                if (manifest.exists()) {
                    try (FileReader reader = new FileReader(manifest)) {
                        ProjectTemplate template = gson.fromJson(reader, ProjectTemplate.class);
                        template.setTemplateDir(templateDir); // Set the template directory
                        templates.add(template);
                    } catch (IOException e) {
                        // Log or handle error
                        e.printStackTrace();
                    }
                }
            }
        }
        return templates;
    }

    public void createProject(ProjectTemplate template, File targetDir) throws IOException {
        File sourceDir = template.getTemplateDir();
        if (sourceDir == null || !sourceDir.isDirectory()) {
            throw new IOException("Template source directory not found or is not a directory: " + sourceDir);
        }

        Path sourcePath = sourceDir.toPath();
        Path targetPath = targetDir.toPath();

        try (Stream<Path> stream = Files.walk(sourcePath)) {
            for (Path source : (Iterable<Path>) stream::iterator) {
                Path destination = targetPath.resolve(sourcePath.relativize(source));

                // Skip the manifest file itself
                if (source.getFileName().toString().equals("manifest.json")) {
                    continue;
                }

                // For directories, create them. For files, copy them.
                if (Files.isDirectory(source)) {
                    if (!Files.exists(destination)) {
                        Files.createDirectories(destination);
                    }
                } else {
                    // Ensure parent directory of the file exists
                    if (!Files.exists(destination.getParent())) {
                        Files.createDirectories(destination.getParent());
                    }
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
