package dumb.mcr;

import dumb.lm.LMClient;
import dumb.lm.LMResponse;
import dumb.mcr.tools.Tool;
import dumb.mcr.tools.ToolProvider;
import dumb.prolog.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Session {

    private final LMClient LMClient;
    private final KnowledgeGraph knowledgeGraph;
    private final Ontology ontology;
    private final ToolProvider toolProvider;
    private Solver solver;

    public Session(LMClient LMClient, ToolProvider toolProvider) {
        this.LMClient = LMClient;
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
                String descriptionAtom = tool.description().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
                knowledgeGraph.addClause("tool(" + tool.name() + ", " + descriptionAtom + ").");
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
        LMResponse response = LMClient.generate(prompt);

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
            LMResponse response = LMClient.generate(prompt);

            if (!response.isSuccess()) {
                return new ReasoningResult("LLM reasoning failed: " + response.getError(), history);
            }

            String prologGoal = response.getContent().trim();
            history.add("LLM goal: " + prologGoal);

            try {
                Term parsedGoal = Parser.parseTerm(prologGoal);
                QueryResult result = query(prologGoal);
                history.add("Goal result: " + result.getBindings());

                if (parsedGoal instanceof Structure goalStructure) {
                    if (goalStructure.getFunctor().getName().equals("conclude")) {
                        return new ReasoningResult(
                                result.success() && result.getBindings() != null && !result.getBindings().isEmpty() ? firstAnswer(result) : "Concluded, but no specific answer found.",
                                history);
                    }
                }
            } catch (Exception e) {
                history.add("Error executing goal: " + e.getMessage());
            }
        }

        return new ReasoningResult("Max steps reached without conclusion.", history);
    }

    /** Extract the answer from the first solution */
    private String firstAnswer(QueryResult result) {
        var firstSolution = result.getBindings().getFirst();
        return firstSolution.values().stream().findFirst().orElse("Concluded without a specific answer.");
    }

    public ReasoningResult reason(String taskDescription) {
        return reason(taskDescription, 10); // Default to 10 steps
    }

    private String buildReasoningPrompt(List<String> history) {
        String promptTemplate = loadPrompt("reason.prompt");
        String historyString = String.join("\n", history);
        return promptTemplate
                .replace("{{ontology_types}}", ontology.getTypes().toString())
                .replace("{{ontology_relationships}}", ontology.getRelationships().toString())
                .replace("{{history}}", historyString);
    }
}
