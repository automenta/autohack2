package dumb.mcr;

import dumb.lm.LMClient;
import dumb.mcr.tools.ToolProvider;

public class MCR {

    private final LMClient lmClient;

    public MCR(LMClient lmClient) {
        this.lmClient = lmClient;
    }

    public MCR(String provider, String model, String apiKey) {
        this.lmClient = new LMClient(provider, model, apiKey);
    }

    public Session createSession(ToolProvider toolProvider) {
        return new Session(lmClient, toolProvider);
    }

    public Session createSession() {
        return createSession(null);
    }
}
