package dumb.code.help;

import dumb.code.MessageHandler;
import dumb.code.project.ProjectTemplate;
import dumb.mcr.MCR;
import dumb.mcr.QueryResult;
import dumb.mcr.ReasoningResult;
import dumb.mcr.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TutorialManagerTest {

    @Mock
    private MCR mcr;

    @Mock
    private Session mcrSession;

    @Mock
    private MessageHandler messageHandler;

    private TutorialManager tutorialManager;

    @BeforeEach
    void setUp() {
        // Configure mocks
        when(mcr.createSession()).thenReturn(mcrSession);
        // Default mock for reason() to avoid NPE in start() method
        when(mcrSession.reason(anyString())).thenReturn(new ReasoningResult("Default instruction.", Collections.emptyList()));

        // Create a test template
        ProjectTemplate.TutorialGoal goal = new ProjectTemplate.TutorialGoal();
        goal.setGoal("test_goal");
        goal.setInstruction("test_instruction");
        goal.setVerification(Map.of("type", "prolog", "query", "test_query."));

        ProjectTemplate template = new ProjectTemplate();
        template.setName("Test Template");
        template.setDescription("A template for testing.");
        template.setTutorial(List.of(goal));

        // Initialize the manager
        tutorialManager = new TutorialManager(template, mcr, messageHandler);
    }

    @Test
    void testTutorialStepSuccess() {
        // Arrange
        tutorialManager.start();
        when(mcrSession.query("test_query.")).thenReturn(new QueryResult(true, "test_query.", Collections.emptyList(), null));

        // Act
        String result = tutorialManager.checkCommand(new String[]{"/any_command"});

        // Assert
        assertTrue(result.startsWith("Congratulations"), "The tutorial should end with a congratulations message.");
        assertFalse(tutorialManager.isActive(), "Tutorial should be complete after the only step is done.");
    }

    @Test
    void testTutorialStepFailure() {
        // Arrange
        tutorialManager.start();
        when(mcrSession.query("test_query.")).thenReturn(new QueryResult(false, "test_query.", Collections.emptyList(), "failed"));
        when(mcrSession.reason(anyString())).thenReturn(new ReasoningResult("Instruction again.", Collections.emptyList()));

        // Act
        String result = tutorialManager.checkCommand(new String[]{"/wrong_command"});

        // Assert
        assertTrue(result.startsWith("Not quite."));
        assertTrue(tutorialManager.isActive(), "Tutorial should still be active after a failure.");
    }

    @Test
    void testProactiveHintTriggered() {
        // Arrange
        tutorialManager.start();
        when(mcrSession.query("test_query.")).thenReturn(new QueryResult(false, "test_query.", Collections.emptyList(), "failed"));
        when(mcrSession.reason(contains("STUCK"))).thenReturn(new ReasoningResult("This is a proactive hint.", Collections.emptyList()));
        // The default anyString() mock from setUp will handle the non-stuck cases.

        // Act & Assert
        // First failure
        String result1 = tutorialManager.checkCommand(new String[]{"/wrong_command"});
        assertTrue(result1.startsWith("Not quite."));

        // Second failure, should trigger hint
        String result2 = tutorialManager.checkCommand(new String[]{"/another_wrong_command"});
        assertTrue(result2.startsWith("Looks like you're stuck."));

        // Verify mock interactions
        verify(mcrSession, times(1)).reason(contains("STUCK"));
        verify(mcrSession, times(3)).reason(anyString()); // 1 for start, 1 for first failure, 1 for hint

        assertTrue(tutorialManager.isActive(), "Tutorial should still be active after a hint.");
    }
}
