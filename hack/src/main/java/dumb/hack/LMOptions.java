package dumb.hack;

import picocli.CommandLine;

import java.util.Properties;

public class LMOptions {

    @CommandLine.Option(names = "--provider",
            description = "The LM provider to use. Defaults to 'openai'. Can be set via the 'lm.provider' system property.",
            defaultValue = "${sys:lm.provider:-openai}")
    private String provider = "openai";

    @CommandLine.Option(names = "--model",
            description = "The model to use. Defaults to 'gpt-4o-mini'.")
    private String model = "gpt-4o-mini";

    @CommandLine.Option(names = "--api-key",
            description = "The API key for the LM provider. Can be set via the '<PROVIDER>_API_KEY' environment variable (e.g., OPENAI_API_KEY).",
            arity = "1")
    private String apiKey;

    public LMOptions() {
        // Default constructor for picocli
    }

    public LMOptions(Properties properties) {
        this.provider = properties.getProperty("lm.provider", this.provider);
        this.model = properties.getProperty("lm.model", this.model);
        this.apiKey = properties.getProperty("lm.apiKey", this.apiKey);
    }

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
}
