package com.example.mcr.translation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pijul.common.LLMClient;
import com.pijul.common.LLMResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JsonToProlog implements TranslationStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private LLMClient llmClient;
    private String model;
    private double temperature;

    public JsonToProlog() {
    }

    public JsonToProlog(LLMClient llmClient, String model, double temperature) {
        this.llmClient = llmClient;
        this.model = model;
        this.temperature = temperature;
    }

    @Override
    public CompletableFuture<TranslationResult> translate(String input, LLMClient llmClient, String model, List<String> ontologyTerms, String feedback, boolean returnFullResponse) {
        this.llmClient = llmClient;
        this.model = model;
        return CompletableFuture.supplyAsync(() -> {
            try {
                StringBuilder ontologyHint = new StringBuilder();
                if (ontologyTerms != null && !ontologyTerms.isEmpty()) {
                    ontologyHint.append("\n\nAvailable ontology terms: ")
                            .append(String.join(", ", ontologyTerms));
                }

                StringBuilder feedbackHint = new StringBuilder();
                if (feedback != null && !feedback.isEmpty()) {
                    feedbackHint.append("\n\nFeedback from previous attempt: ")
                            .append(feedback)
                            .append("\n\n");
                }

                String prompt = "Translate the following into JSON representation, then convert to Prolog.\n" +
                        ontologyHint.toString() +
                        feedbackHint.toString() +
                        "\nOutput ONLY valid JSON with:\n" +
                        "- \"type\" (\"fact\", \"rule\", or \"query\")\n" +
                        "- \"head\" with \"predicate\" and \"args\" array\n" +
                        "- \"body\" array (for rules only) with elements having \"predicate\" and \"args\"\n\n" +
                        "Examples:\n" +
                        "{\"type\":\"fact\",\"head\":{\"predicate\":\"bird\",\"args\":[\"tweety\"]}}\n" +
                        "{\"type\":\"rule\",\"head\":{\"predicate\":\"has_wings\",\"args\":[\"X\"]},\"body\":[{\"predicate\":\"bird\",\"args\":[\"X\"]}]}\n" +
                        "{\"type\":\"query\",\"head\":{\"predicate\":\"can_migrate\",\"args\":[\"tweety\"]}}\n" +
                        "\nInput: " + input + "\n" +
                        "Output:";

                LLMResponse response = llmClient.generate(prompt);

                JsonNode rootNode = objectMapper.readTree(response.getContent());
                String type = rootNode.get("type").asText();
                JsonNode headNode = rootNode.get("head");

                String prolog = convertJsonToProlog(type, headNode, rootNode.get("body"));

                TranslationResult result = new TranslationResult();
                result.setType(type);
                result.setContent(prolog);

                return result;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON processing error", e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<String> parseArgs(JsonNode argsNode) {
        List<String> args = new ArrayList<>();
        if (argsNode != null && argsNode.isArray()) {
            for (JsonNode arg : argsNode) {
                args.add(arg.asText());
            }
        }
        return args;
    }

    private String convertJsonToProlog(String type, JsonNode head, JsonNode body) {
        String predicate = head.get("predicate").asText();
        List<String> args = parseArgs(head.get("args"));
        
        StringBuilder prolog = new StringBuilder();
        prolog.append(predicate);
        prolog.append("(");
        prolog.append(String.join(", ", args));
        prolog.append(")");
        
        if (type.equals("rule") && body != null && body.isArray()) {
            prolog.append(" :- ");
            List<String> bodyClauses = new ArrayList<>();
            for (JsonNode clause : body) {
                String clausePredicate = clause.get("predicate").asText();
                List<String> clauseArgs = parseArgs(clause.get("args"));
                bodyClauses.add(clausePredicate + "(" + String.join(", ", clauseArgs) + ")");
            }
            prolog.append(String.join(", ", bodyClauses));
        }
        
        prolog.append(".");
        return prolog.toString();
    }
}