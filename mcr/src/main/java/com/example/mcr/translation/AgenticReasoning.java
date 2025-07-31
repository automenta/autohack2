package com.example.mcr.translation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pijul.common.LLMClient;
import com.pijul.common.LLMResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AgenticReasoning implements TranslationStrategy {

    private LLMClient llmClient;
    private String model;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AgenticReasoning() {
    }

    public AgenticReasoning(LLMClient llmClient, String model) {
        this.llmClient = llmClient;
        this.model = model;
    }

    @Override
    public CompletableFuture<TranslationResult> translate(String input, LLMClient llmClient, String model, List<String> ontologyTerms, String feedback, boolean returnFullResponse) {
        this.llmClient = llmClient;
        this.model = model;
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Build ontology hint
                StringBuilder ontologyHint = new StringBuilder();
                if (ontologyTerms != null && !ontologyTerms.isEmpty()) {
                    ontologyHint.append("\n\nAvailable ontology terms: ")
                            .append(String.join(", ", ontologyTerms));
                }

                // Build previous steps hint
                StringBuilder previousStepsHint = new StringBuilder();
                if (feedback != null && !feedback.isEmpty()) {
                    previousStepsHint.append("\n\nFeedback from previous attempt: ")
                            .append(feedback)
                            .append("\n\n");
                }

                // Construct prompt
                String prompt = "You are an expert Prolog reasoner and agent. Your goal is to break down a complex task into discrete, logical steps using Prolog assertions, queries, or by concluding the task.\n" +
                        "You have access to a Prolog knowledge base and can perform actions.\n" +
                        ontologyHint.toString() +
                        previousStepsHint.toString() +
                        "\n\nYour output must be a JSON object with a \"type\" field (\"query\", \"assert\", or \"conclude\") and a \"content\" field (Prolog clause/query string) or an \"answer\" field (natural language conclusion).\n" +
                        "If type is \"conclude\", also include an optional \"explanation\" field (natural language string).\n" +
                        "Ensure all Prolog outputs are syntactically valid and conform to the ontology if applicable.\n" +
                        "Do not include any other text outside the JSON object.\n" +
                        "\nExamples:\n" +
                        "{\"type\":\"assert\",\"content\":\"bird(tweety).\"}\n" +
                        "{\"type\":\"assert\",\"content\":\"flies(X) :- bird(X).\"}\n" +
                        "{\"type\":\"query\",\"content\":\"has_wings(tweety).\"}\n" +
                        "{\"type\":\"conclude\",\"answer\":\"Yes, Tweety can fly\",\"explanation\":\"Derived from bird(tweety) and flies(X) :- bird(X).\"}\n" +
                        "\nGiven the task: \"" + input + "\"\n" +
                        "What is your next logical step?";

                LLMResponse response = llmClient.generate(prompt);
                JsonNode rootNode = objectMapper.readTree(response.getContent());

                TranslationResult result = new TranslationResult();
                result.setType(rootNode.get("type").asText());
                if (rootNode.has("content")) {
                    result.setContent(rootNode.get("content").asText());
                }
                if (rootNode.has("answer")) {
                    result.setAnswer(rootNode.get("answer").asText());
                }
                if (rootNode.has("explanation")) {
                    result.setExplanation(rootNode.get("explanation").asText());
                }

                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}