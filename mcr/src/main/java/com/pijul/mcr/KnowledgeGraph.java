package com.pijul.mcr;

import com.pijul.mcr.prolog.Clause;
import com.pijul.mcr.prolog.Parser;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeGraph {

    private final List<Clause> clauses = new ArrayList<>();

    public void addClause(String clauseString) {
        clauses.add(Parser.parseClause(clauseString));
    }

    public void removeClause(String clauseString) {
        clauses.removeIf(c -> c.toString().equals(clauseString));
    }

    public List<Clause> getClauses() {
        return clauses;
    }
}
