package org.ferpin.bloques.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.util.HashMap;
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

    public static HashMap<String, String> cleanSentence(String originalText){
        System.out.println("ORIGINAL TEXT: " + originalText);

        Annotation document = new Annotation(originalText);
        corenlp.annotate(document);


        System.out.print("CLEANED TEXT: ");
        CoreMap sentence = document.get(CoreAnnotations.SentencesAnnotation.class).get(0);

        HashMap<String, String> tokens = new HashMap<>();
        for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            String posTag = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

            if(isItWorth(word, posTag)) {
                tokens.put(word, posTag);
                System.out.print(word + " (" + posTag + "), ");
            }
        }
        System.out.println();

        return tokens;
    }

    private static boolean isItWorth(String word, String pos) {
//        if (word.toLowerCase().equals("si")) // exceptions: "si"
//            return true;

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

//    private String clean() {
//        StringBuilder cleanedLines = new StringBuilder();
//        String lines[] = originalText.split("\\r?\\n");
//
//        for (String line: lines) {
//            if (line.isEmpty() || line.charAt(0) == '#')
//                continue;
//
//            if (line.charAt(line.length() - 1) != '.')
//                line = line + ". ";
//
//            line = replaceWord(line,"X", "X "); //TODO: otras letras tambien Y, Z, W
////            line = replaceWord(line,"X", "Xobjetct");
////            line = replaceWord(line,"X", "Xobjetct");
//
//            cleanedLines.append(line);
//        }
//
//
//        return cleanedLines.toString();
//    }
}
