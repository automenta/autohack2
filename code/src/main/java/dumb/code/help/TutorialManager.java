package dumb.code.help;

public interface TutorialManager {
    String start();
    void stop();
    String checkCommand(String[] command);
    boolean isActive();
}
