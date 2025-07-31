package com.example.mcr.ontology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OntologyManagerTest {
    private OntologyManager ontologyManager;

    @BeforeEach
    void setUp() {
        Map<String, Object> ontologyData = new HashMap<>();
        ontologyData.put("types", new HashSet<>(Arrays.asList("bird", "animal")));
        ontologyData.put("relationships", new HashSet<>(Arrays.asList("has_wings")));
        ontologyData.put("constraints", new HashSet<>());
        ontologyData.put("rules", Arrays.asList());
        ontologyData.put("synonyms", new HashMap<>());
        ontologyManager = new OntologyManager(new OntologyManager.Ontology(ontologyData));
    }

    @Test
    void shouldValidateFactWithExistingType() {
        assertDoesNotThrow(() -> ontologyManager.validateFact("bird", List.of("tweety")));
    }

    @Test
    void shouldRejectFactWithMissingType() {
        assertThrows(IllegalArgumentException.class, () -> ontologyManager.validateFact("fish", List.of("nemo")));
    }

    @Test
    void shouldValidateRelationshipWithExistingType() {
        assertDoesNotThrow(() -> ontologyManager.validateFact("has_wings", List.of("tweety", "true")));
    }

    @Test
    void shouldRejectRelationshipWithMissingType() {
        assertThrows(IllegalArgumentException.class, () -> ontologyManager.validateFact("swims", List.of("nemo", "true")));
    }

    @Test
    void shouldValidatePrologClauseAgainstOntology() {
        assertDoesNotThrow(() -> ontologyManager.validatePrologClause("bird(tweety)."));
        assertThrows(IllegalArgumentException.class, () -> ontologyManager.validatePrologClause("fish(nemo)."));
    }

    @Test
    void shouldReloadOntologyAndRevalidate() {
        Map<String, Object> newOntologyData = new HashMap<>();
        newOntologyData.put("types", new HashSet<>(Arrays.asList("mammal")));
        newOntologyData.put("relationships", new HashSet<>(Arrays.asList("eats")));
        newOntologyData.put("constraints", new HashSet<>());
        newOntologyData.put("rules", Arrays.asList());
        newOntologyData.put("synonyms", new HashMap<>());

        ontologyManager = new OntologyManager(new OntologyManager.Ontology(newOntologyData));

        assertThrows(IllegalArgumentException.class, () -> ontologyManager.validateFact("bird", List.of("tweety")));
        assertDoesNotThrow(() -> ontologyManager.validateFact("mammal", List.of("dog")));
    }

    @Test
    void shouldAddAndRetrieveSynonyms() {
        ontologyManager.addSynonym("canary", "bird");
        assertEquals("bird", ontologyManager.resolveSynonym("canary"));
    }
}