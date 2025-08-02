package dumb.mcr;

import dumb.lm.LMClient;
import dumb.lm.LMResponse;
import dumb.lm.LMUsage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class McrDevelopmentTest {

    private MCR mcr;
    private Session session;
    private LMClient mockLmClient;

    @BeforeEach
    void setUp() {
        mockLmClient = Mockito.mock(LMClient.class);
        mcr = new MCR(mockLmClient);
        session = mcr.createSession();

        session.assertProlog("is_a(tweety, canary).");
        session.assertProlog("bird(X) :- is_a(X, canary).");
        session.assertProlog("can_fly(X) :- bird(X).");
    }

    @Test
    void testReasoningLoopWithMockLLM() {
        String initialGoal = "has_wings(tweety)";
        String concludingGoal = "conclude('Yes, tweety can fly')";

        when(mockLmClient.generate(any(String.class)))
                .thenReturn(new LMResponse(initialGoal, new LMUsage(0, 0, 0), true, null))
                .thenReturn(new LMResponse(concludingGoal, new LMUsage(0, 0, 0), true, null));

        ReasoningResult finalResult = session.reason("Can tweety fly?", 2);

        assertNotNull(finalResult, "Reasoning result should not be null.");
        String expectedAnswer = "Yes, tweety can fly";
        assertEquals(expectedAnswer, finalResult.answer(),
            "The answer should match the argument of the conclude() goal.");
    }
}
