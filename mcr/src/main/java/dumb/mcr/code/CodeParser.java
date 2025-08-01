package dumb.mcr.code;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CodeParser {

    public List<String> parse(Path path, String content) {
        if (path.toString().endsWith(".java")) {
            return parseJavaFile(path, content);
        } else {
            return parseGenericFile(path, content);
        }
    }

    private List<String> parseJavaFile(Path path, String content) {
        List<String> facts = new ArrayList<>();
        // Normalize path for consistency
        String normalizedPath = path.toString().replace('\\', '/');
        try {
            CompilationUnit cu = StaticJavaParser.parse(content);
            // Use a visitor to extract facts
            new JavaFileVisitor(normalizedPath).visit(cu, facts);
        } catch (Exception e) {
            // If parsing fails, treat it as a generic file
            facts.addAll(parseGenericFile(path, content));
            facts.add("parsing_error('" + escape(normalizedPath) + "', '" + escape(e.getMessage()) + "').");
        }
        return facts;
    }

    private List<String> parseGenericFile(Path path, String content) {
        List<String> facts = new ArrayList<>();
        facts.add("file('" + escape(path.toString()) + "', '" + escape(content) + "').");
        return facts;
    }

    private String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\").replace("'", "\\'");
    }

    private static class JavaFileVisitor extends VoidVisitorAdapter<List<String>> {

        private final String filepath;

        public JavaFileVisitor(String filepath) {
            this.filepath = filepath;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<String> facts) {
            super.visit(n, facts);
            String className = n.getNameAsString();
            facts.add("class('" + escape(className) + "', '" + escape(filepath) + "').");
        }

        @Override
        public void visit(MethodDeclaration n, List<String> facts) {
            super.visit(n, facts);
            String methodName = n.getNameAsString();
            n.findAncestor(ClassOrInterfaceDeclaration.class).ifPresent(p -> {
                String className = p.getNameAsString();
                facts.add("method('" + escape(className) + "', '" + escape(methodName) + "', '" + escape(filepath) + "').");
            });
        }

        private String escape(String text) {
            if (text == null) {
                return "";
            }
            return text.replace("\\", "\\\\").replace("'", "\\'");
        }
    }
}
