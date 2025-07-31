package dumb.prolog;

import java.util.List;

public record Clause(Structure head, List<Term> body) {

    public boolean isFact() {
        return body.isEmpty();
    }
}
