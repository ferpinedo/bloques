package org.ferpin.bloques.prolog;

import java.util.ArrayList;

public class Rule extends Clause {
    private ArrayList<Clause> bodyClauses;

    public Rule(String name) {
        super(name);
    }

    public Rule(String name, String... variables) {
        super(name, variables);
    }

    @Override
    public String toString() {
        StringBuilder rule = new StringBuilder(super.toString() + ":-");
        for (int i = 0; i < bodyClauses.size() - 1; i++) {
            rule.append(bodyClauses.get(i)).append(",\n");
        }
        rule.append(bodyClauses.get(bodyClauses.size() - 1)).append(".\n");

        return rule.toString();
    }
}
