package org.ferpin.bloques.prolog;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Entity {
    String type;
    String identifier;
    ArrayList<String> properties;

    public Entity() {
        this.properties = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Entity(String identifier) {
        this.identifier = identifier;
        this.properties = new ArrayList<>();
    }

    public Entity(String identifier, ArrayList<String> properties) {
        this.identifier = identifier;
        this.properties = properties;
    }

    public void addProperty(String property) {
        properties.add(property);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ArrayList<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    public boolean equals(Entity o) {
        if (this == o) return true;

        int equalProperties = 0;

        for (String property: properties) {
            for (String oProperty: o.getProperties()) {
//                System.out.println("Comparing: " + property + " & " + oProperty);
                if (oProperty.equals(property)) {
                    equalProperties++;
                }
            }
        }
//        System.out.println(equalProperties + "==" + properties.size());

        return equalProperties == properties.size();
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    @Override
    public String toString() {
        return "Entity{" +
                "identifier='" + identifier + '\'' +
                ", properties=" + properties +
                '}';
    }
}
