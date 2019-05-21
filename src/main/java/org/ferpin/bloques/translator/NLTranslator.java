package org.ferpin.bloques.translator;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class NLTranslator {

    private NLTranslator(){}


    private String cleanComments(String lines) {
        String cleanedLines = "";

        return cleanedLines;
    }

    public static String translateToProlog(String originalText) throws IOException {
        System.out.println(originalText);
        Annotation document = new Annotation(originalText);
        Properties props = new Properties();

//        Reader reader = IOUtils.readerFromString(Main.class.getResource("Custom-StanfordCoreNLP-spanish.properties").getPath());
        Reader reader = IOUtils.readerFromString("StanfordCoreNLP-spanish.properties");
        props.load(reader);

        StanfordCoreNLP corenlp = new StanfordCoreNLP(props);
        corenlp.annotate(document);


        System.out.println("Core map sentences");
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            System.out.println("Sentence: ");
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);


                if(!pos.equals("DET") && !pos.equals("ADP") && !pos.equals("CONJ") && !pos.equals("SCONJ") && !pos.equals("PUNCT"))
                    System.out.print(word + " (" + pos + "), ");
            }
            System.out.println("\n");
        }


        return "";
    }


}
