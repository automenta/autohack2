package com.pijul.mcr;

import com.google.gson.Gson;
import com.pijul.common.LLMClient;
import com.pijul.mcr.prolog.Parser;
import com.pijul.mcr.prolog.Solver;
import com.pijul.mcr.prolog.Term;
import com.pijul.mcr.prolog.Variable;
import com.pijul.mcr.tools.Tool;
import com.pijul.mcr.tools.ToolProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private void resetSolver() {
        this.solver = new Solver(knowledgeGraph.getClauses(), toolProvider);
    }

    private void assertToolFacts() {
        if (toolProvider != null) {
            for (Tool tool : toolProvider.getTools().values()) {
                knowledgeGraph.addClause("tool(" + tool.getName() + ", '" + tool.getDescription() + "').");
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
        StringBuilder prompt = new StringBuilder();
        prompt.append("Translate the following natural language question into a Prolog query.\n");
        prompt.append("You can use the following types and relationships defined in the ontology:\n");
        prompt.append("Types: ").append(ontology.getTypes()).append("\n");
        prompt.append("Relationships: ").append(ontology.getRelationships()).append("\n\n");
        prompt.append("Example 1:\n");
        prompt.append("Question: Is tweety a bird?\n");
        prompt.append("Prolog: bird(tweety).\n\n");
        prompt.append("Example 2:\n");
        prompt.append("Question: What does tweety like?\n");
        prompt.append("Prolog: likes(tweety, X).\n\n");
        prompt.append("Now, translate this question:\n");
        prompt.append("Question: ").append(naturalLanguageQuery).append("\n");
        prompt.append("Prolog: ");
        return prompt.toString();
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
                QueryResult result = query(prologGoal);
                history.add("Goal result: " + new Gson().toJson(result.getBindings()));

                if (Parser.parseTerm(prologGoal) instanceof com.pijul.mcr.prolog.Structure && ((com.pijul.mcr.prolog.Structure)Parser.parseTerm(prologGoal)).getFunctor().getName().equals("conclude")) {
                    // This is a simplification. A real implementation would extract the answer from the bindings.
                    return new ReasoningResult("Task concluded.", history);
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
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a reasoning agent. Your goal is to solve a task by issuing a series of Prolog goals.\n");
        prompt.append("You can use the `use_tool/4` predicate to call tools.\n");
        prompt.append("To conclude the task, issue a `conclude/1` goal with your final answer.\n\n");

        prompt.append("Here is the history of the reasoning process so far:\n");
        for (String item : history) {
            prompt.append(item).append("\n");
        }
        prompt.append("\nBased on the history, what is the next Prolog goal you should issue? Provide only the Prolog goal.\n");
        return prompt.toString();
    }
}
