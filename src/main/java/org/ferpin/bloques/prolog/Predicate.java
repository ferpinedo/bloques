package org.ferpin.bloques.prolog;

public class Predicate {
    private String name;
    private int size;

    @Override
    public String toString() {
        return ":-dynamic " + name + "/" + size + ".";
    }

    public Predicate() {
    }

    public Predicate(String name) {
        this.name = name;
    }

    public Predicate(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
