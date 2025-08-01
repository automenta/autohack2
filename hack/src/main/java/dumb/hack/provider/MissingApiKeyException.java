package dumb.hack.provider;

public class MissingApiKeyException extends RuntimeException {

    public MissingApiKeyException(String provider) {
        super("API key for " + provider + " is not provided. Please set the " + provider.toUpperCase() + "_API_KEY environment variable or use the --api-key option.");
    }
}
