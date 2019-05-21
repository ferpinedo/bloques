package org.ferpin.bloques;

import org.ferpin.bloques.translator.NLTranslator;
import org.ferpin.bloques.util.Files;

import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        try {
            String originalText = Files.readEverythingFromFile(Main.class.getResource("rules.txt").getPath());
            NLTranslator.translateToProlog(originalText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
