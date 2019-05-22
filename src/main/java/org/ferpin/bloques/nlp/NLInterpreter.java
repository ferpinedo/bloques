package org.ferpin.bloques.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Properties;

public class NLInterpreter {

    private static String nlpPropertiesPath;
    private static StanfordCoreNLP corenlp;

    private NLInterpreter() {}

    public String getNlpPropertiesPath() {
        return nlpPropertiesPath;
    }

    public static void setNlpPropertiesPath(String nlpPropertiesPath) throws IOException {
        nlpPropertiesPath = nlpPropertiesPath;

        Properties props = new Properties();

        if (nlpPropertiesPath == null)
            props.load(IOUtils.readerFromString("StanfordCoreNLP-spanish.properties"));
        else
            props.load(IOUtils.readerFromString(nlpPropertiesPath));

        corenlp = new StanfordCoreNLP(props);
    }

    public static String analyzeSentence(String originalText){
        System.out.println("ORIGINAL TEXT: " + originalText);


        Annotation document = new Annotation(originalText);
        corenlp.annotate(document);

//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//        for(CoreMap sentence: sentences) {

        System.out.print("Sentence: ");
        CoreMap sentence = document.get(CoreAnnotations.SentencesAnnotation.class).get(0);
        for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

            if(isItWorth(word, pos))
                System.out.print(word + " (" + pos + "), ");
        }
        System.out.println("\n");

        return "";
    }

    public static LinkedList<Token> cleanSentence(String originalText){
        System.out.println("ORIGINAL TEXT: " + originalText);

        Annotation document = new Annotation(originalText);
        corenlp.annotate(document);


        System.out.print("CLEANED TEXT: ");
        CoreMap sentence = document.get(CoreAnnotations.SentencesAnnotation.class).get(0);

        LinkedList<Token> tokens = new LinkedList<>();
        for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            String posTag = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

            if(isItWorth(word, posTag)) {
                tokens.add(new Token(word, posTag));
            }
        }
        System.out.println(tokens);

        return tokens;
    }

    private static boolean isItWorth(String word, String pos) {
        if (word.length() == 1 && !pos.equals("PUNCT")) // exceptions: words of one letter as "a", "y", "u", ...
            return true;

//        && !pos.equals("ADP")
        if (!pos.equals("DET") && !pos.equals("CONJ") && !pos.equals("SCONJ") && !pos.equals("PUNCT"))
            return true;

        return false;
    }

    private String replaceWord(String text, String oldWord, String newWord){
        return text.replaceAll("(?<!\\S)" + oldWord + "(?!\\S)", newWord);
    }

}
