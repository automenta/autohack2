package dumb.lm;

public interface ILMClient {
    LMResponse generate(String prompt);
}
