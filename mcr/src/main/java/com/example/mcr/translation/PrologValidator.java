package com.example.mcr.translation;


import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

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
            Prolog engine = new Prolog();
            engine.setTheory(new Theory(prologClause));
            return true;
        } catch (InvalidTheoryException e) {
            return false;
        }
    }
}