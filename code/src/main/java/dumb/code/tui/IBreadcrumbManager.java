package dumb.code.tui;

public interface IBreadcrumbManager {
    void setPath(String... elements);
    void push(String element);
    void pop();
}
