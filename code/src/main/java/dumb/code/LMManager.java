package dumb.code;

import dumb.lm.ILMClient;
import dumb.lm.LMClient;
import dumb.lm.LMResponse;
import dumb.mcr.MCR;
import dumb.mcr.Session;

public class LMManager {
    private final ILMClient lmClient;
    private final MCR mcr;
    private final Session session;

    public LMManager(String provider, String model, String apiKey) {
        this.lmClient = new LMClient(provider, model, apiKey);
        this.mcr = new MCR((LMClient) this.lmClient);
        this.session = mcr.createSession();
    }

    public Session getSession() {
        return session;
    }

    public ILMClient getLmClient() {
        return lmClient;
    }

    public String getProvider() {
        return lmClient.getProvider();
    }

    public String getModel() {
        return lmClient.getModel();
    }

    public String getApiKey() {
        return lmClient.getApiKey();
    }

    public void shutdown() {
        // Nothing to do
    }

    public String generateResponse(String prompt) {
        if (lmClient == null) {
            return "Error: LLM not initialized.";
        }
        LMResponse response = lmClient.generate(prompt);
        if (response.success()) {
            return response.content();
        } else {
            return "Error: " + response.error();
        }
    }
}