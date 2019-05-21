package org.ferpin.bloques;

import org.ferpin.bloques.nlp.NLInterpreter;
import org.ferpin.bloques.nlp.PrologTranslator;
import org.ferpin.bloques.prolog.PrologProgram;
import org.ferpin.bloques.util.Files;

import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        String programName = "mundo bloques";
        String author = "Fernanod Pinedo";
        String rulesFilePath = Main.class.getResource("knowledge/mundo-bloques/nuevas_reglas.txt").getPath();
        try {
            NLInterpreter.setNlpPropertiesPath(Main.class.getResource("Custom-StanfordCoreNLP-spanish.properties").getPath());
            String rulesText = Files.readEverythingFromFile(rulesFilePath);

            PrologProgram prologProgram = new PrologProgram(programName, author);
            PrologTranslator translator = new PrologTranslator(prologProgram);
            translator.translateRules(rulesText);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
