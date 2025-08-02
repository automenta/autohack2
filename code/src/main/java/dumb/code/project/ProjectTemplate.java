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

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TutorialGoal> getTutorial() {
        return tutorial;
    }

    public void setTutorial(List<TutorialGoal> tutorial) {
        this.tutorial = tutorial;
    }

    public static class TutorialGoal {
        private String goal;
        private String instruction;
        private Map<String, String> verification;

        public String getGoal() {
            return goal;
        }

        public void setGoal(String goal) {
            this.goal = goal;
        }

        public String getInstruction() {
            return instruction;
        }

        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }

        public Map<String, String> getVerification() {
            return verification;
        }

        public void setVerification(Map<String, String> verification) {
            this.verification = verification;
        }
    }
}
