package org.ferpin.bloques.prolog;

public class PredicateClause extends Clause {
    private Predicate predicate; //TODO: add validation with predicate arguments size

    public PredicateClause(Predicate predicate) {
        super(predicate.getName());
        this.predicate = predicate;
    }

    public PredicateClause(Predicate predicate, String... arguments) {
        super(predicate.getName(), arguments);
        this.predicate = predicate;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
