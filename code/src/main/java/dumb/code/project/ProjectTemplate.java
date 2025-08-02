package dumb.code.project;

import java.util.List;
import java.util.Map;

public class ProjectTemplate {
    private String name;
    private String description;
    private List<TutorialGoal> tutorial;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<TutorialGoal> getTutorial() {
        return tutorial;
    }

    public static class TutorialGoal {
        private String goal;
        private String instruction;
        private Map<String, String> verification;

        public String getGoal() {
            return goal;
        }

        public String getInstruction() {
            return instruction;
        }

        public Map<String, String> getVerification() {
            return verification;
        }
    }
}
