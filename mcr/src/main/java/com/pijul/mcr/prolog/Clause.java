package com.pijul.mcr.prolog;

import java.util.List;

public class Clause {
    private final Structure head;
    private final List<Term> body;

    public Clause(Structure head, List<Term> body) {
        this.head = head;
        this.body = body;
    }

    public Structure getHead() {
        return head;
    }

    public List<Term> getBody() {
        return body;
    }

    public boolean isFact() {
        return body.isEmpty();
    }
}
