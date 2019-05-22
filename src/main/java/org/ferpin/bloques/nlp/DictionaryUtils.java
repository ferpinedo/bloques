package org.ferpin.bloques.nlp;

public class DictionaryUtils {
    public static String getFirstSynonym(Concept concept) {
        return Dictionary.SYNONYMS.get(concept)[0];
    }
}
