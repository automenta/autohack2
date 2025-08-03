package dumb.lm;

public interface ILMClient {
    LMResponse generate(String prompt);
    String getProvider();
    String getModel();
    String getApiKey();
}
