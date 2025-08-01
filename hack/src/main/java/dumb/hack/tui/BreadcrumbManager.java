package dumb.hack.tui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import dumb.code.tui.IBreadcrumbManager;

public class BreadcrumbManager implements IBreadcrumbManager {
    private final List<String> path = new ArrayList<>();
    private Consumer<String> listener;

    public BreadcrumbManager() {
        path.add("Home");
    }

    public void setListener(Consumer<String> listener) {
        this.listener = listener;
        update();
    }

    public void setPath(String... elements) {
        path.clear();
        path.add("Home");
        path.addAll(Arrays.asList(elements));
        update();
    }

    public void push(String element) {
        path.add(element);
        update();
    }

    public void pop() {
        if (path.size() > 1) {
            path.remove(path.size() - 1);
            update();
        }
    }

    private void update() {
        if (listener != null) {
            String breadcrumbText = String.join(" / ", path);
            listener.accept(breadcrumbText);
        }
    }
}
