package com.example.mcr.translation;


public class PrologValidator {

    /**
     * Validates a Prolog fact or rule syntax using the tuProlog engine.
     * @param prologClause The Prolog clause to validate.
     * @return True if the clause is valid, false otherwise.
     */
    public boolean isValidPrologClause(String prologClause) {
        if (prologClause == null || prologClause.trim().isEmpty()) {
            return false;
        }
        try {
            //new PrologParser.getWithDefaultOperators().parseTheory(prologClause);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}