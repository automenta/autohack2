package dumb.prolog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Parser {

    private static final Pattern ATOM_PATTERN = Pattern.compile("[a-z][a-zA-Z0-9_]*");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("[A-Z_][a-zA-Z0-9_]*");

    public static Clause parseClause(String text) {
        text = text.trim();
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }

        String[] parts = text.split(":-");
        Structure head = (Structure) parseTerm(parts[0].trim());
        List<Term> body = new ArrayList<>();
        if (parts.length > 1) {
            String[] bodyParts = parts[1].trim().split(",");
            for (String part : bodyParts) {
                body.add(parseTerm(part.trim()));
            }
        }
        return new Clause(head, body);
    }

    public static Term parseTerm(String text) {
        text = text.trim();
        if (text.endsWith(")")) {
            int openParen = text.indexOf('(');
            String functorName = text.substring(0, openParen);
            Atom functor = new Atom(functorName);
            String argsStr = text.substring(openParen + 1, text.length() - 1);
            List<Term> args = new ArrayList<>();
            // This is a very simple arg parser, doesn't handle nested structures well
            for (String argStr : argsStr.split(",")) {
                args.add(parseTerm(argStr.trim()));
            }
            return new Structure(functor, args);
        } else if (VARIABLE_PATTERN.matcher(text).matches()) {
            return new Variable(text);
        } else if (ATOM_PATTERN.matcher(text).matches()) {
            return new Atom(text);
        } else {
            throw new IllegalArgumentException("Cannot parse term: " + text);
        }
    }
}
