package dumb.hack;

import picocli.CommandLine;

public class LMOptions {

    @CommandLine.Option(names = "--provider",
            description = "The LM provider to use. Defaults to 'openai'. Can be set via the 'lm.provider' system property.",
            defaultValue = "${sys:lm.provider:-openai}")
    private String provider;

    @CommandLine.Option(names = "--model",
            description = "The model to use. Defaults to 'gpt-4o-mini'.",
            defaultValue = "gpt-4o-mini")
    private String model;

    @CommandLine.Option(names = "--api-key",
            description = "The API key for the LM provider. Can be set via the '<PROVIDER>_API_KEY' environment variable (e.g., OPENAI_API_KEY).",
            arity = "1")
    private String apiKey;

    public String getProvider() {
        return provider;
    }

    public String getModel() {
        return model;
    }

    public String getApiKey() {
        if (apiKey != null) {
            return apiKey;
        }
        // Fallback to environment variable if --api-key is not provided
        String envVarName = provider.toUpperCase() + "_API_KEY";
        return System.getenv(envVarName);
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
