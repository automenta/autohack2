package dumb.mcr;

import dumb.lm.LMClient;
import dumb.mcr.tools.ToolProvider;

public class MCR {

    private final LMClient lmClient;
    private final String promptsPath;

    public MCR(LMClient lmClient, String promptsPath) {
        this.lmClient = lmClient;
        this.promptsPath = promptsPath;
    }

    public MCR(LMClient lmClient) {
        this(lmClient, null);
    }

    public Session createSession(ToolProvider toolProvider) {
        return new Session(lmClient, toolProvider, promptsPath);
    }

    public Session createSession() {
        return createSession(null);
    }
}
