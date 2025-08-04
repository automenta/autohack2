package dumb.common.tools;

import java.util.Map;

public interface Tool {
    String name();

    String description();

    String run(Map<String, Object> args);
}
