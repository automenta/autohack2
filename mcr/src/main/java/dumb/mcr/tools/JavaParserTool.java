package dumb.mcr.tools;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import dumb.common.tools.Tool;
import dumb.mcr.Session;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public class JavaParserTool implements Tool {

    private final Session session;

    public JavaParserTool(Session session) {
        this.session = session;
    }

    @Override
    public String name() {
        return "java_parser";
    }

    @Override
    public String description() {
        return "A tool for parsing Java files and asserting facts about them into the MCR knowledge base.";
    }

    @Override
    public String run(Map<String, Object> args) {
        String filePath = (String) args.get("file_path");
        if (filePath == null) {
            return "Error: file_path not specified.";
        }

        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(filePath));
            for (ClassOrInterfaceDeclaration clas : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                String className = clas.getNameAsString();
                session.assertProlog(String.format("class('%s').", className));
                for (MethodDeclaration method : clas.getMethods()) {
                    String methodName = method.getNameAsString();
                    session.assertProlog(String.format("method('%s', '%s').", className, methodName));
                }
            }
            return "Successfully parsed " + filePath;
        } catch (FileNotFoundException e) {
            return "Error: File not found: " + filePath;
        }
    }
}
