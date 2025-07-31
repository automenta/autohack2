package com.pijul.mcr;

import com.pijul.common.LLMClient;
import com.pijul.mcr.prolog.Parser;
import com.pijul.mcr.prolog.Solver;
import com.pijul.mcr.prolog.Term;
import com.pijul.mcr.prolog.Variable;
import com.pijul.mcr.tools.Tool;
import com.pijul.mcr.tools.ToolProvider;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Session {

    private final LLMClient llmClient;
    private final KnowledgeGraph knowledgeGraph;
    private final Ontology ontology;
    private final ToolProvider toolProvider;
    private Solver solver;

    public Session(LLMClient llmClient, ToolProvider toolProvider) {
        this.llmClient = llmClient;
        this.knowledgeGraph = new KnowledgeGraph();
        this.ontology = new Ontology();
        this.toolProvider = toolProvider;
        resetSolver();
        assertToolFacts();
    }

    private String loadPrompt(String name) {
        String resourcePath = "/prompts/" + name;
        try (InputStream inputStream = Session.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                return scanner.useDelimiter("\\A").next();
            }
        } catch (Exception e) {
            // Consider a more robust error handling strategy
            throw new RuntimeException("Failed to load prompt: " + name, e);
        }
    }

    private void resetSolver() {
        this.solver = new Solver(knowledgeGraph.getClauses(), toolProvider);
    }

    private void assertToolFacts() {
        if (toolProvider != null) {
            for (Tool tool : toolProvider.getTools().values()) {
                String descriptionAtom = tool.getDescription().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
                knowledgeGraph.addClause("tool(" + tool.getName() + ", " + descriptionAtom + ").");
            }
            resetSolver();
        }
    }

    public Result assertProlog(String clause) {
        knowledgeGraph.addClause(clause);
        resetSolver();
        return Result.success("Clause asserted successfully.");
    }

    public Result retractProlog(String clause) {
        knowledgeGraph.removeClause(clause);
        resetSolver();
        return Result.success("Clause retracted successfully.");
    }

    public Result addFact(String entity, String type) {
        if (!ontology.hasType(type)) {
            return Result.failure("Unknown type: " + type);
        }
        String fact = type + "(" + entity + ").";
        knowledgeGraph.addClause(fact);
        resetSolver();
        return Result.success("Fact added successfully.");
    }

    public Result addRelationship(String subject, String relation, String object) {
        if (!ontology.hasRelationship(relation)) {
            return Result.failure("Unknown relationship: " + relation);
        }
        String relationship = relation + "(" + subject + ", " + object + ").";
        knowledgeGraph.addClause(relationship);
        resetSolver();
        return Result.success("Relationship added successfully.");
    }

    public Result addRule(String rule) {
        // Basic validation: check if it contains ':-'
        if (!rule.contains(":-")) {
            return Result.failure("Invalid rule format. Expected 'head :- body.'");
        }
        knowledgeGraph.addClause(rule);
        resetSolver();
        return Result.success("Rule added successfully.");
    }

    public Ontology getOntology() {
        return ontology;
    }

    public KnowledgeGraph getKnowledgeGraph() {
        return knowledgeGraph;
    }

    public ToolProvider getToolProvider() {
        return toolProvider;
    }

    public QueryResult query(String prologQuery) {
        try {
            Term queryTerm = Parser.parseTerm(prologQuery);
            List<Map<Variable, Term>> solutions = solver.solve(queryTerm);
            return new QueryResult(!solutions.isEmpty(), prologQuery, solutions, null);
        } catch (Exception e) {
            return new QueryResult(false, prologQuery, null, e.getMessage());
        }
    }

    public QueryResult nquery(String naturalLanguageQuery) {
        String prompt = buildNQueryPrompt(naturalLanguageQuery);
        com.pijul.common.LLMResponse response = llmClient.generate(prompt);

        if (!response.isSuccess()) {
            return new QueryResult(false, naturalLanguageQuery, null, "LLM query translation failed: " + response.getError());
        }

        String prologQuery = response.getContent().trim();
        // Optional: Add some basic validation for the generated prolog query
        if (!prologQuery.endsWith(".")) {
            prologQuery += ".";
        }

        return query(prologQuery);
    }

    private String buildNQueryPrompt(String naturalLanguageQuery) {
        String promptTemplate = loadPrompt("nquery.prompt");
        return promptTemplate
                .replace("{{ontology_types}}", ontology.getTypes().toString())
                .replace("{{ontology_relationships}}", ontology.getRelationships().toString())
                .replace("{{natural_language_query}}", naturalLanguageQuery);
    }

    public ReasoningResult reason(String taskDescription, int maxSteps) {
        List<String> history = new ArrayList<>();
        history.add("Task: " + taskDescription);

        for (int i = 0; i < maxSteps; i++) {
            String prompt = buildReasoningPrompt(history);
            com.pijul.common.LLMResponse response = llmClient.generate(prompt);

            if (!response.isSuccess()) {
                return new ReasoningResult("LLM reasoning failed: " + response.getError(), history);
            }

            String prologGoal = response.getContent().trim();
            history.add("LLM goal: " + prologGoal);

            try {
                Term parsedGoal = Parser.parseTerm(prologGoal);
                QueryResult result = query(prologGoal);
                history.add("Goal result: " + result.getBindings());

                if (parsedGoal instanceof com.pijul.mcr.prolog.Structure) {
                    com.pijul.mcr.prolog.Structure goalStructure = (com.pijul.mcr.prolog.Structure) parsedGoal;
                    if (goalStructure.getFunctor().getName().equals("conclude")) {
                        if (result.isSuccess() && result.getBindings() != null && !result.getBindings().isEmpty()) {
                            // Extract the answer from the first solution
                            Map<Variable, Term> firstSolution = result.getBindings().get(0);
                            Term answerTerm = firstSolution.values().stream().findFirst().orElse(null);
                            String answer = (answerTerm != null) ? answerTerm.toString() : "Concluded without a specific answer.";
                            return new ReasoningResult(answer, history);
                        } else {
                            return new ReasoningResult("Concluded, but no specific answer found.", history);
                        }
                    }
                }
            } catch (Exception e) {
                history.add("Error executing goal: " + e.getMessage());
            }
        }

        return new ReasoningResult("Max steps reached without conclusion.", history);
    }

    public ReasoningResult reason(String taskDescription) {
        return reason(taskDescription, 10); // Default to 10 steps
    }

    private String buildReasoningPrompt(List<String> history) {
        String promptTemplate = loadPrompt("reason.prompt");
        String historyString = history.stream().collect(Collectors.joining("\n"));
        return promptTemplate
                .replace("{{ontology_types}}", ontology.getTypes().toString())
                .replace("{{ontology_relationships}}", ontology.getRelationships().toString())
                .replace("{{history}}", historyString);
    }
}
