package dumb.hack.help;

import dumb.code.MessageHandler;
import dumb.code.help.TutorialManager;
import dumb.code.help.TutorialManagerFactory;
import dumb.code.project.ProjectTemplate;
import dumb.mcr.MCR;

public class TutorialManagerFactoryImpl implements TutorialManagerFactory {

    private final MCR mcr;

    public TutorialManagerFactoryImpl(MCR mcr) {
        this.mcr = mcr;
    }

    @Override
    public TutorialManager create(ProjectTemplate template, MessageHandler messageHandler) {
        return new TutorialManagerImpl(template, mcr, messageHandler);
    }
}
