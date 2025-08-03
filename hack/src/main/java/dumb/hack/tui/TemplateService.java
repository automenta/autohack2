package dumb.hack.tui;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TemplateService {

    private static class Manifest {
        String name;
        String description;
    }

    private final String templatesPath;

    public TemplateService(String templatesPath) {
        this.templatesPath = templatesPath;
    }

    public List<Template> getAvailableTemplates() {
        List<Template> templates = new ArrayList<>();
        File templatesDir = new File(templatesPath);

        if (!templatesDir.exists() || !templatesDir.isDirectory()) {
            return templates;
        }

        File[] templateDirs = templatesDir.listFiles(File::isDirectory);
        if (templateDirs == null) {
            return templates;
        }

        for (File templateDir : templateDirs) {
            File manifestFile = new File(templateDir, "manifest.json");
            if (manifestFile.exists()) {
                try (FileReader reader = new FileReader(manifestFile)) {
                    Manifest manifest = new Gson().fromJson(reader, Manifest.class);
                    if (manifest != null && manifest.name != null) {
                        templates.add(new Template(manifest.name, manifest.description, templateDir.getPath()));
                    }
                } catch (IOException e) {
                    // Log or handle error, for now, just skip this template
                    e.printStackTrace();
                }
            }
        }
        return templates;
    }
}
