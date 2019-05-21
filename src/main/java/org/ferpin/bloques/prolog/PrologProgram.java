package org.ferpin.bloques.prolog;

import java.util.ArrayList;

public class PrologProgram {
    private String name;
    private String author;
    private ArrayList<Predicate> predicates;
    private ArrayList<Fact> facts;
    private ArrayList<Rule> rules;

    public PrologProgram(String name, String author) {
        this.name = name;
        this.author = author;
        this.predicates = new ArrayList<>();
        this.facts = new ArrayList<>();
        this.rules = new ArrayList<>();
    }

    public PrologProgram(String name, String author, ArrayList<Predicate> predicates, ArrayList<Fact> facts, ArrayList<Rule> rules) {
        this.name = name;
        this.author = author;
        this.predicates = predicates;
        this.facts = facts;
        this.rules = rules;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Predicate> getPredicates() {
        return predicates;
    }

    public void setPredicates(ArrayList<Predicate> predicates) {
        this.predicates = predicates;
    }

    public ArrayList<Fact> getFacts() {
        return facts;
    }

    public void setFacts(ArrayList<Fact> facts) {
        this.facts = facts;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rule> rules) {
        this.rules = rules;
    }

    public void addPredicate(Predicate predicate) {
        this.predicates.add(predicate);
    }

    public void addFact(Fact fact) {
        this.facts.add(fact);
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    @Override
    public String toString() {
        StringBuilder program = new StringBuilder("% " + name.toUpperCase() + " program\n");
        program.append("% Written by ").append(author).append("\n\n");

        program.append("\n% Predicates\n");
        for (Predicate predicate: predicates)
            program.append(predicate).append("\n");

        program.append("\n% Facts\n");
        for (PredicateClause fact: facts)
            program.append(fact).append("\n");

        program.append("\n% Rules\n");
        for (Rule rule: rules)
            program.append(rule).append("\n");

        return program.toString();
    }
}
