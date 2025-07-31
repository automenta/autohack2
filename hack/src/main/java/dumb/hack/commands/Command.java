package dumb.hack.commands;

public interface Command {
    void init();

    void execute(String[] args);

    void cleanup();
}
