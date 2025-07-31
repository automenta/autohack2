package com.example.mcr.core;

import com.example.mcr.ontology.OntologyManager;
import com.example.mcr.translation.PrologValidator;
import com.example.mcr.translation.TranslationStrategy;
import com.google.gson.Gson;
import com.pijul.common.LLMClient;
import com.pijul.common.LLMResponse;
import com.pijul.common.LLMUsage;
import it.unibo.tuprolog.core.Struct;
import it.unibo.tuprolog.core.Term;
import it.unibo.tuprolog.core.Var;
import it.unibo.tuprolog.parser.PrologParser;
import it.unibo.tuprolog.solve.SolveInfo;
import it.unibo.tuprolog.solve.Solver;
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory;
import it.unibo.tuprolog.theory.InvalidTheoryException;
import it.unibo.tuprolog.theory.Theory;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Session {
    private final MCR mcr;
    private final SessionOptions options;
    private String sessionId;
    private final LLMClient llmClient;
    private final List<String> program = new ArrayList<>();
    private final Logger logger;
    private OntologyManager ontology;
    private final MCR.LLMUsageMetrics llmUsage = new MCR.LLMUsageMetrics();
    private Solver prologSession = ClassicSolverFactory.get().createSolver();
    private static final Pattern PREDICATE_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9_]*$");
    private final PrologValidator prologValidator = new PrologValidator();

    public Session(MCR mcr, SessionOptions options) {
        this.mcr = mcr;
        this.options = options;
        this.sessionId = options.sessionId != null ? options.sessionId : Long.toString(System.currentTimeMillis(), 36);
        this.logger = options.logger != null ? options.logger : Logger.getLogger(Session.class.getName());
        this.llmClient = mcr.getLlmClient();
        
        if (options.ontology != null) {
            this.ontology = new OntologyManager(new OntologyManager.Ontology(options.ontology));
        } else {
            this.ontology = new OntologyManager(new OntologyManager.Ontology());
        }
        
        if (options.program != null) {
            for (String clause : options.program) {
                try {
                    ontology.validatePrologClause(clause);
                    program.add(clause);
                } catch (Exception e) {
                    logger.warning("Invalid clause in initial program: " + clause + ". Error: " + e.getMessage());
                }
            }
            consultProgram();
        }
    }

    private void consultProgram() {
        try {
            prologSession.setStaticKb(Theory.of(program.stream().map(PrologParser.getWithDefaultOperators()::parseClause).collect(Collectors.toList())));
        } catch (InvalidTheoryException e) {
            logger.severe("Error consulting program: " + e.getMessage());
        }
    }

    private boolean isValidPrologSyntax(String prologString) {
        return prologValidator.isValidPrologClause(prologString);
    }

    private void recordLlmUsage(long startTime, LLMResponse response) {
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        LLMUsage usage = response.getUsage();
        
        if (usage != null) {
            llmUsage.promptTokens += usage.getPromptTokens();
            llmUsage.completionTokens += usage.getCompletionTokens();
            llmUsage.totalTokens += usage.getTotalTokens();
            
            mcr.totalLlmUsage.promptTokens += usage.getPromptTokens();
            mcr.totalLlmUsage.completionTokens += usage.getCompletionTokens();
            mcr.totalLlmUsage.totalTokens += usage.getTotalTokens();
        }
        
        llmUsage.calls++;
        llmUsage.totalLatencyMs += latency;
        mcr.totalLlmUsage.calls++;
        mcr.totalLlmUsage.totalLatencyMs += latency;
    }

    public void reloadOntology(Map<String, Object> newOntology) {
        this.ontology = new OntologyManager(new OntologyManager.Ontology(newOntology));
        List<String> tempProgram = new ArrayList<>(program);
        program.clear();
        for (String clause : tempProgram) {
            assertProlog(clause);
        }
    }

    public void clear() {
        program.clear();
        prologSession = ClassicSolverFactory.get().createSolver();
        if (options.ontology != null) {
            ontology = new OntologyManager(new OntologyManager.Ontology(options.ontology));
        }
        logger.info("Session cleared: " + sessionId);
    }

    public String saveState() {
        Map<String, Object> state = new HashMap<>();
        state.put("program", new ArrayList<>(program));
        state.put("sessionId", sessionId);
        state.put("ontology", ontology.getState());
        return new Gson().toJson(state);
    }

    public void loadState(String state) {
        Gson gson = new Gson();
        Map<String, Object> data = gson.fromJson(state, Map.class);
        this.sessionId = (String) data.get("sessionId");
        this.ontology = new OntologyManager(new OntologyManager.Ontology((Map<String, Object>) data.get("ontology")));
        this.program.clear();
        this.prologSession = ClassicSolverFactory.get().createSolver();
        
        List<String> savedProgram = (List<String>) data.get("program");
        for (String clause : savedProgram) {
            try {
                assertProlog(clause);
            } catch (Exception e) {
                logger.severe("Failed to load clause: " + clause + ". Error: " + e.getMessage());
            }
        }
    }

    public CompletableFuture<QueryResult> nquery(String naturalLanguageQuery, QueryOptions options) {
        logger.info("Natural language query: " + naturalLanguageQuery);
        TranslationStrategy translator = mcr.strategyRegistry.get(this.options.translator);
        if (translator == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Translator not configured or not found in registry"));
        }

        return translator.translate(naturalLanguageQuery, llmClient, mcr.llmModel, new ArrayList<>(ontology.getTerms()), null, false)
                .thenCompose(translationResult -> {
                    String prologQuery = translationResult.getContent();
                    return query(prologQuery, options);
                });
    }

    private List<String> extractPredicates(String query) {
        List<String> predicates = new ArrayList<>();
        String[] tokens = query.split("[,\\s\\(\\)\\.]+");
        for (String token : tokens) {
            if (PREDICATE_PATTERN.matcher(token).matches()) {
                predicates.add(token);
            }
        }
        return predicates;
    }

    public CompletableFuture<QueryResult> query(String prologQuery, QueryOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            QueryResult result = new QueryResult();
            if (!isValidPrologSyntax(prologQuery)) {
                result.setSuccess(false);
                result.setError("Invalid Prolog query syntax");
                return result;
            }

            Struct queryTerm = PrologParser.getWithDefaultOperators().parseStruct(prologQuery);
            List<SolveInfo> solutions = StreamSupport.stream(prologSession.solve(queryTerm).spliterator(), false).collect(Collectors.toList());

            if (solutions.isEmpty() || solutions.get(0).isNo()) {
                result.setSuccess(false);
                result.setError("No solutions found.");
                return result;
            }

            result.setSuccess(true);
            List<Map<String, String>> bindings = new ArrayList<>();
            for (SolveInfo solutionInfo : solutions) {
                if (solutionInfo.isYes()) {
                    Map<String, String> binding = new HashMap<>();
                    for (Map.Entry<Var, Term> entry : solutionInfo.getSolution().getSubstitution().getMap().entrySet()) {
                        binding.put(entry.getKey().getName(), entry.getValue().toString());
                    }
                    bindings.add(binding);
                }
            }
            result.setBindings(bindings);
            return result;
        });
    }

    public AssertionResult assertProlog(String prologClause) {
        AssertionResult result = new AssertionResult();
        if (!isValidPrologSyntax(prologClause)) {
            result.setSuccess(false);
            result.setError("Invalid Prolog clause");
            return result;
        }
        
        program.add(prologClause);
        consultProgram();
        result.setSuccess(true);
        return result;
    }

    public CompletableFuture<QueryResult> reason(String task, QueryOptions options) {
        logger.info("Reasoning about task: " + task);
        TranslationStrategy reasoner = mcr.strategyRegistry.get("agentic");
        if (reasoner == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Agentic reasoning strategy not found"));
        }

        List<String> history = new ArrayList<>();
        return CompletableFuture.supplyAsync(() -> {
            for (int i = 0; i < options.maxReasoningSteps; i++) {
                String feedback = i > 0 ? "Previous steps: " + String.join("\n", history) : null;
                TranslationStrategy.TranslationResult step;
                try {
                    step = reasoner.translate(task, llmClient, mcr.llmModel, new ArrayList<>(ontology.getTerms()), feedback, true).join();
                } catch (Exception e) {
                    return new QueryResult(false, "Reasoning failed at translation step: " + e.getMessage());
                }

                history.add(String.format("Step %d: %s - %s", i + 1, step.getType(), step.getContent() != null ? step.getContent() : step.getAnswer()));

                switch (step.getType()) {
                    case "assert":
                        assertProlog(step.getContent());
                        break;
                    case "query":
                        QueryResult queryResult = query(step.getContent(), options).join();
                        if (!queryResult.isSuccess()) {
                            history.add("Query failed: " + queryResult.getError());
                        } else {
                            history.add("Query succeeded with bindings: " + new Gson().toJson(queryResult.getBindings()));
                        }
                        break;
                    case "conclude":
                        QueryResult finalResult = new QueryResult();
                        finalResult.setSuccess(true);
                        finalResult.setExplanation(history);
                        return finalResult;
                    default:
                        return new QueryResult(false, "Unknown reasoning step type: " + step.getType());
                }
            }
            return new QueryResult(false, "Exceeded max reasoning steps.");
        });
    }

    public AssertionResult assertFact(String fact) {
        if (fact == null || fact.trim().isEmpty()) {
            return new AssertionResult(false, "Fact cannot be null or empty");
        }
        
        if (!fact.trim().endsWith(".")) {
            return new AssertionResult(false, "Invalid fact syntax - must end with '.'");
        }
        
        return assertProlog(fact);
    }

    public AssertionResult addRelationship(String subject, String predicate, String object) {
        if (subject == null || subject.trim().isEmpty() ||
            predicate == null || predicate.trim().isEmpty() ||
            object == null || object.trim().isEmpty()) {
            return new AssertionResult(false, "All relationship components must be non-empty");
        }
        
        String relationship = predicate + "(" + subject + ", " + object + ").";
        return assertProlog(relationship);
    }
    
    public static class SessionOptions {
        public long retryDelay = 500;
        public int maxTranslationAttempts = 2;
        public int maxReasoningSteps = 5;
        public Map<String, Object> ontology;
        public Logger logger;
        public String sessionId;
        public List<String> program;
        public String translator;
    }
    
    public static class QueryOptions {
        public boolean allowSubSymbolicFallback = false;
        public int maxReasoningSteps = 5;
    }

    public static class QueryResult {
        private boolean success;
        private List<Map<String, String>> bindings;
        private String error;
        private List<String> explanation;

        public QueryResult() {}

        public QueryResult(boolean success, String error) {
            this.success = success;
            this.error = error;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public List<Map<String, String>> getBindings() { return bindings; }
        public void setBindings(List<Map<String, String>> bindings) { this.bindings = bindings; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public List<String> getExplanation() { return explanation; }
        public void setExplanation(List<String> explanation) { this.explanation = explanation; }
    }
    
    public static class AssertionResult {
        private boolean success;
        private String symbolicRepresentation;
        private String error;

        public AssertionResult() {}

        public AssertionResult(boolean success, String error) {
            this.success = success;
            this.error = error;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}