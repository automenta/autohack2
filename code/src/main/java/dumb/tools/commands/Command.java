package dumb.tools.commands;

public interface Command {
    default void init() {
    }

    void execute(String[] args);

    default void cleanup() {
    }
}