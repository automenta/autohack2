package com.pijul.mcr;

import com.pijul.mcr.prolog.Term;
import com.pijul.mcr.prolog.Variable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryResult {
    private final boolean success;
    private final String originalQuery;
    private final List<Map<Variable, Term>> bindings;
    private final String error;

    public QueryResult(boolean success, String originalQuery, List<Map<Variable, Term>> bindings, String error) {
        this.success = success;
        this.originalQuery = originalQuery;
        this.bindings = bindings;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public List<Map<String, String>> getBindings() {
        if (bindings == null) {
            return null;
        }
        return bindings.stream()
                .map(solution -> solution.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey().getName(), e -> e.getValue().toString())))
                .collect(Collectors.toList());
    }

    public String getError() {
        return error;
    }
}
