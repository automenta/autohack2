package dumb.code.project;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
}
