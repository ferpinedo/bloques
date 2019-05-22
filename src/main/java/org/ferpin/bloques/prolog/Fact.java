package org.ferpin.bloques.prolog;

public class Fact extends PredicateClause {

    public Fact(Predicate predicate) {
        super(predicate);
    }

    public Fact(Predicate predicate, String... arguments) {
        super(predicate, arguments);
    }

    @Override
    public String toString() {
        return super.toString() + '.';
    }
}
