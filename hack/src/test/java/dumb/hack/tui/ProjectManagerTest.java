package dumb.hack.tui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectManagerTest {

    private ProjectManager projectManager;

    @BeforeEach
    void setUp() {
        // We don't want to load projects from a real file in tests.
        // The ProjectManager constructor loads projects, but for our tests,
        // we can just create a new instance and work with projects in memory.
        projectManager = new ProjectManager();
    }

    @Test
    void testRunHealthCheck_Success(@TempDir Path tempDir) throws IOException {
        // Create a valid pom.xml
        String pomContent = "<project>" +
                            "<modelVersion>4.0.0</modelVersion>" +
                            "<groupId>com.example</groupId>" +
                            "<artifactId>test-project</artifactId>" +
                            "<version>1.0.0</version>" +
                            "</project>";
        Files.write(tempDir.resolve("pom.xml"), pomContent.getBytes());

        Project project = new Project();
        project.name = "test-project-success";
        project.path = tempDir.toString();

        List<String> issues = projectManager.runHealthCheck(project);

        assertTrue(issues.isEmpty(), "Health check should pass for a valid project. Issues: " + issues);
    }

    @Test
    void testRunHealthCheck_Failure(@TempDir Path tempDir) throws IOException {
        // Create an invalid pom.xml (e.g., with a syntax error)
        String pomContent = "<project>" +
                            "<modelVersion>4.0.0</modelVersion>" +
                            "<groupId>com.example</groupId>" +
                            "<artifactId>test-project</artifactId>" +
                            "<version>1.0.0</version>" +
                            "<dependencies>" + // unclosed tag
                            "</project>";
        Files.write(tempDir.resolve("pom.xml"), pomContent.getBytes());

        Project project = new Project();
        project.name = "test-project-failure";
        project.path = tempDir.toString();

        List<String> issues = projectManager.runHealthCheck(project);

        assertFalse(issues.isEmpty(), "Health check should fail for an invalid project.");

        boolean hasError = issues.stream().anyMatch(s -> s.contains("[ERROR]"));
        assertTrue(hasError, "The issues list should contain a Maven error message.");
    }

    @Test
    void testRunHealthCheck_NoPom(@TempDir Path tempDir) {
        Project project = new Project();
        project.name = "test-project-no-pom";
        project.path = tempDir.toString();

        List<String> issues = projectManager.runHealthCheck(project);

        assertFalse(issues.isEmpty(), "Health check should fail if pom.xml is missing.");
        assertEquals("pom.xml not found.", issues.get(0));
    }
}
