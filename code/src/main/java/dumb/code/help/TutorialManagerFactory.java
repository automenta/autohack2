package dumb.code.help;

import dumb.code.MessageHandler;
import dumb.code.project.ProjectTemplate;

public interface TutorialManagerFactory {
    TutorialManager create(ProjectTemplate template, MessageHandler messageHandler);
}
