package org.ferpin.bloques;

import org.ferpin.bloques.nlp.NLInterpreter;
import org.ferpin.bloques.nlp.PrologTranslator;
import org.ferpin.bloques.prolog.Program;
import org.ferpin.bloques.util.Files;

import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        String programName = "mundo bloques";
        String author = "Fernanod Pinedo";
        String rulesFilePath = Main.class.getResource("knowledge/mundo-bloques/reglas.txt").getPath();
        String stageFilePath = Main.class.getResource("knowledge/mundo-bloques/escenario1.txt").getPath();
        try {
            NLInterpreter.setNlpPropertiesPath(Main.class.getResource("Custom-StanfordCoreNLP-spanish.properties").getPath());
            String rulesText = Files.readEverythingFromFile(rulesFilePath);
            String stageText = Files.readEverythingFromFile(stageFilePath);

            Program prologProgram = new Program(programName, author);
            PrologTranslator translator = new PrologTranslator(prologProgram);
            translator.translateRules(rulesText);
            translator.translateStage(stageText);

            prologProgram.consult();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

