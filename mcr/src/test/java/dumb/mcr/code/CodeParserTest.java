package dumb.mcr.code;

import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CodeParserTest {

    @Test
    void testParseJavaFile() {
        CodeParser parser = new CodeParser();
        String javaContent = "package com.example; class MyClass { void myMethod() {} }";
        List<String> facts = parser.parse(Paths.get("com/example/MyClass.java"), javaContent);

        assertTrue(facts.contains("class('MyClass', 'com/example/MyClass.java')."));
        assertTrue(facts.contains("method('MyClass', 'myMethod', 'com/example/MyClass.java')."));
    }

    @Test
    void testParseGenericFile() {
        CodeParser parser = new CodeParser();
        String textContent = "This is a generic file.";
        List<String> facts = parser.parse(Paths.get("file.txt"), textContent);

        assertEquals(1, facts.size());
        assertEquals("file('file.txt', 'This is a generic file.').", facts.get(0));
    }

    @Test
    void testParseInvalidJavaFile() {
        CodeParser parser = new CodeParser();
        String invalidJavaContent = "class MyClass {";
        List<String> facts = parser.parse(Paths.get("MyClass.java"), invalidJavaContent);

        assertTrue(facts.contains("file('MyClass.java', 'class MyClass {')."));
        assertTrue(facts.stream().anyMatch(s -> s.startsWith("parsing_error('MyClass.java'")));
    }
}
