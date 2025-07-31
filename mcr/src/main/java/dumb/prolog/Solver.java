package dumb.prolog;

import dumb.mcr.tools.Tool;
import dumb.mcr.tools.ToolProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Solver(List<Clause> knowledgeBase, ToolProvider toolProvider) {

    public List<Map<Variable, Term>> solve(Term query) {
        List<Map<Variable, Term>> solutions = new ArrayList<>();
        solve(query, new HashMap<>(), solutions);
        return solutions;
    }

    private void solve(Term query, Map<Variable, Term> substitution, List<Map<Variable, Term>> solutions) {
        if (query instanceof Structure && ((Structure) query).getFunctor().getName().equals("use_tool")) {
            executeTool((Structure) query, substitution, solutions);
            return;
        }

        for (Clause clause : knowledgeBase) {
            Map<Variable, Term> newSubst = Unification.unify(query, clause.head(), new HashMap<>(substitution));
            if (newSubst != null) {
                if (clause.isFact()) {
                    solutions.add(newSubst);
                } else {
                    solveBody(clause.body(), newSubst, solutions);
                }
            }
        }
    }

    private void solveBody(List<Term> body, Map<Variable, Term> substitution, List<Map<Variable, Term>> solutions) {
        if (body.isEmpty()) {
            solutions.add(substitution);
            return;
        }

        Term firstGoal = body.get(0);
        List<Term> remainingGoals = body.subList(1, body.size());

        solve(firstGoal, substitution, (newSolutions) -> {
            for (Map<Variable, Term> newSubst : newSolutions) {
                solveBody(remainingGoals, newSubst, solutions);
            }
        });
    }

    private void solve(Term query, Map<Variable, Term> substitution, SolutionCallback callback) {
        List<Map<Variable, Term>> solutions = new ArrayList<>();
        // This is a simplified version, a real implementation would be more complex
        solve(query, substitution, solutions);
        callback.onSolutions(solutions);
    }

    private void executeTool(Structure toolQuery, Map<Variable, Term> substitution, List<Map<Variable, Term>> solutions) {
        if (toolProvider == null) {
            return;
        }
        // Assuming use_tool(ToolName, MethodName, Args, Result)
        String toolName = ((Atom) toolQuery.getArgs().get(0)).getName();
        String methodName = ((Atom) toolQuery.getArgs().get(1)).getName();
        // This is a simplified representation of args. A real implementation would need to handle lists of pairs.
        Map<String, Object> args = new HashMap<>();
        Term resultVariable = toolQuery.getArgs().get(3);

        Tool tool = toolProvider.getTools().get(toolName);
        if (tool != null) {
            // This is a simplification. A real implementation would need to match the method and args.
            String result = tool.run(args);
            Map<Variable, Term> newSubst = Unification.unify(resultVariable, new Atom(result), substitution);
            if (newSubst != null) {
                solutions.add(newSubst);
            }
        }
    }


    // Helper interface for callback
    private interface SolutionCallback {
        void onSolutions(List<Map<Variable, Term>> solutions);
    }
}
