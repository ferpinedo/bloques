package org.ferpin.bloques.prolog;

import java.util.ArrayList;
import java.util.Arrays;

public class Clause {
    private String name;
    private ArrayList<String> bodyClauses;

    public Clause(String name) {
        this.name = name;
        this.bodyClauses = new ArrayList<>();
    }

    public Clause(String name, String... arguments) {
        this.name = name;
        this.bodyClauses = new ArrayList<>(Arrays.asList(arguments));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getArguments() {
        return bodyClauses;
    }

    public void setArguments(ArrayList<String> arguments) {
        this.bodyClauses = arguments;
    }

    public void addArgument(String argument) {
        this.bodyClauses.add(argument);
    }

    @Override
    public String toString() {
        if (bodyClauses.isEmpty()) {
            return name;
        }
        StringBuilder predicate = new StringBuilder(name + "(");
        for (int i = 0; i < bodyClauses.size() - 1; i++) {
            predicate.append(bodyClauses.get(i)).append(",");
        }
        predicate.append(bodyClauses.get(bodyClauses.size() - 1)).append(")");

        return predicate.toString();
    }
}
