package com.pijul.mcr.tools;

import java.util.Map;

public interface Tool {
    String getName();
    String getDescription();
    String execute(Map<String, Object> args);
}
