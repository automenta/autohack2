package dumb.hack.tui;

public class Template {
    private final String name;
    private final String description;
    private final String path;

    public Template(String name, String description, String path) {
        this.name = name;
        this.description = description;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return name;
    }
}
