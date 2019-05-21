package nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class OwnStanfordCoreNLPTest {
    public static void main(String[] args) throws IOException {
//        BasicConfigurator.configure();

        String paragraph = "Una acción es limpiar, que separa un objeto de cualquier otra entidad" +
                " y relaciona nada encima de él. La acción poner relaciona un objeto arriba de él.";

        Annotation document = new Annotation(paragraph);
        Properties props = new Properties();
        props.load(IOUtils.readerFromString("StanfordCoreNLP-spanish.properties"));
// Or this way of doing it also works
// Properties props = StringUtils.argsToProperties(new String[]{"-props", "StanfordCoreNLP-chinese.properties"});
        StanfordCoreNLP corenlp = new StanfordCoreNLP(props);
        corenlp.annotate(document);


        System.out.println("Core map sentences");
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            System.out.println("Sentence...");
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);


                if(!pos.equals("DET") && !pos.equals("ADP") && !pos.equals("CONJ") && !pos.equals("SCONJ") && !pos.equals("PUNCT"))
                        System.out.println("WORD: " + word + " POS: " + pos);
            }
        }
    }


}
