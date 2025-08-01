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
        if (query instanceof Structure structure && structure.getFunctor().getName().equals("use_tool") && structure.getArgs().size() == 3) {
            executeTool(structure, substitution, solutions);
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
            return; // No tool provider, cannot execute.
        }

        // use_tool(ToolName, [prop(K,V), ...], ResultVar)
        Term toolNameTerm = toolQuery.getArgs().get(0);
        Term argsListTerm = toolQuery.getArgs().get(1);
        Term resultVariable = toolQuery.getArgs().get(2);

        if (!(toolNameTerm instanceof Atom toolNameAtom)) {
            // Tool name must be an atom.
            return;
        }
        String toolName = toolNameAtom.getName();

        Map<String, Object> args = parseArgs(argsListTerm);
        if (args == null) {
            // Argument parsing failed.
            return;
        }

        Tool tool = toolProvider.getTools().get(toolName);
        if (tool != null) {
            String result = tool.run(args);
            // The result from a tool is always a string, so we create an Atom.
            // In a more advanced system, tools might return structured terms.
            Map<Variable, Term> newSubst = Unification.unify(resultVariable, new Atom(result), substitution);
            if (newSubst != null) {
                // The tool call was successful and the result was unified.
                solutions.add(newSubst);
            }
        }
    }

    private Map<String, Object> parseArgs(Term argsListTerm) {
        Map<String, Object> args = new HashMap<>();
        Term current = argsListTerm;

        while (current instanceof Structure listNode && listNode.getFunctor().getName().equals(".") && listNode.getArgs().size() == 2) {
            Term head = listNode.getArgs().get(0);
            if (head instanceof Structure prop && prop.getFunctor().getName().equals("prop") && prop.getArgs().size() == 2) {
                Term keyTerm = prop.getArgs().get(0);
                Term valueTerm = prop.getArgs().get(1);

                if (keyTerm instanceof Atom keyAtom) {
                    // For now, we'll convert values to their string representation.
                    // A more sophisticated implementation might handle different types.
                    args.put(keyAtom.getName(), valueTerm.toString());
                } else {
                    return null; // Key must be an atom
                }
            } else {
                return null; // List element must be a prop/2 structure
            }
            current = listNode.getArgs().get(1);
        }

        if (current instanceof Atom atom && atom.getName().equals("[]")) {
            return args; // End of list
        }

        return null; // Malformed list
    }


    // Helper interface for callback
    private interface SolutionCallback {
        void onSolutions(List<Map<Variable, Term>> solutions);
    }
}
