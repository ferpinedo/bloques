package org.ferpin.bloques.prolog;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Program {
    private String name;
    private String author;
    private ArrayList<Predicate> predicates;
    private ArrayList<Fact> facts;
    private ArrayList<Rule> rules;
    private ArrayList<Entity> entities;

    public Program(String name, String author) {
        this.name = name;
        this.author = author;
        this.predicates = new ArrayList<>();
        this.facts = new ArrayList<>();
        this.rules = new ArrayList<>();
        this.entities = new ArrayList<>();
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

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities = entities;
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

    public void addEntity(Entity entity) {
        this.entities.add(entity);
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

    public void consult() throws IOException {
        saveProgram();
        Puppeteer.consult("rules.pl");
    }

    private void saveProgram() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("rules.pl"));
        writer.write(this.toString());

        writer.close();
    }
}
