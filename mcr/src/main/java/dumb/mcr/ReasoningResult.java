package dumb.mcr;

import java.util.List;

public record ReasoningResult(String answer, List<String> history) {
}
