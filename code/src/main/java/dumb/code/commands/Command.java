package dumb.code.commands;

public interface Command {
    default void init() {
    }

    void execute(String[] args);

    default void cleanup() {
    }
}