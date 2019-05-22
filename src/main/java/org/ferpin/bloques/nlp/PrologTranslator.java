package org.ferpin.bloques.nlp;

import com.sun.org.apache.bcel.internal.generic.D2I;
import org.apache.commons.lang3.StringUtils;
import org.ferpin.bloques.prolog.Clause;
import org.ferpin.bloques.prolog.Predicate;
import org.ferpin.bloques.prolog.PrologProgram;
import org.ferpin.bloques.prolog.Rule;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrologTranslator {
    private HashMap<Type, ArrayList<String>> learnedWords;

    private PrologProgram prologProgram;

    public PrologTranslator(PrologProgram prologProgram) {
        this.prologProgram = prologProgram;
        this.learnedWords = new HashMap<>();
        for (Type type: Type.values()) {
            learnedWords.put(type, new ArrayList<String>());
        }
    }

    private ArrayList<String> detectAndPrepareLines(String originalText) {
        String lines[] = originalText.split("\\r?\\n");
        ArrayList<String> preparedLines = new ArrayList<>();

        for (String line: lines) {
            if (line.isEmpty() || line.charAt(0) == '#')
                continue;

            if (line.charAt(line.length() - 1) == '.')
                line = line.substring(0, line.length() - 1);

            line = line.toLowerCase();
            line = StringUtils.stripAccents(line);
            line = line.replaceAll("ñ", "n");

            System.out.println("Adding line: " + line);
            preparedLines.add(line);
        }
        return preparedLines;
    }

    private void identifyEntity(String line) {
//        line = deleteWords(line, Dictionary.ENTITY);
        HashMap<String, String> tokens = NLInterpreter.cleanSentence(line);
        for (Map.Entry<String, String> token: tokens.entrySet()) {
            String word = token.getKey();
            String posTag = token.getValue();
            if (!posTag.equals("VERB") && !lookFor(word, Dictionary.Synonyms.ENTITY)) {
                System.out.println("New entity found: " + word + "\n");
                learnedWords.get(Type.ENTITY).add(word);
            }
        }
    }

    private void translateState(String line) {
        HashMap<String, String> tokens = NLInterpreter.cleanSentence(line);
        System.out.print("TRANSLATING STATE: ");
        StringBuilder predicateName = new StringBuilder();
        for (Map.Entry<String, String> token: tokens.entrySet()) {
            String word = token.getKey();
            String posTag = token.getValue();

            if (lookFor(word,  Dictionary.Synonyms.BE, Dictionary.Synonyms.ENTITY, Dictionary.EXCEPTIONS,
                            learnedWords.get(Type.ENTITY).toArray(new String[0]))
                            || posTag.equals("AUX") || posTag.equals("PRON")) {
                continue;
            }

            if (lookFor(word, Dictionary.Synonyms.FEATURE))
                continue;

            System.out.print(" " + word + "(" + posTag + ")");

            predicateName.append(word);
            learnedWords.get(Type.STATE).add(word);
        }
        Predicate predicate = new Predicate(predicateName.toString(), 2); //TODO: Allow predicates of more atoms
        System.out.println("   RESULT: " + predicate + "\n");
        prologProgram.addPredicate(predicate);
    }

    private void identfyRuleHeader(String line, Rule rule) {
        HashMap<String, String> tokens = NLInterpreter.cleanSentence(line);
        System.out.print("TRANSLATING RULE HEADER: ");
        ArrayList<String> variables = new ArrayList<>();
        for (Map.Entry<String, String> token: tokens.entrySet()) {
            String word = token.getKey();
            String posTag = token.getValue();

            if (lookFor(word, Dictionary.Synonyms.BE, Dictionary.Synonyms.ENTITY, Dictionary.EXCEPTIONS,
                    learnedWords.get(Type.ENTITY).toArray(new String[0]))
                    || posTag.equals("AUX") || posTag.equals("PRON")) {
                continue;
            }

            if (lookFor(word, Dictionary.Synonyms.RULE)|| posTag.equals("ADP"))
                continue;

            if (posTag.equals("VERB")) {
                rule.setName(word);
            } else {
                variables.add(word);
            }

            System.out.print(" " + word + "(" + posTag + ")");
        }
        rule.setArguments(variables);

        System.out.println("   RESULT: " + rule.getName() + "\n");
    }

    private void translateConditionPremise(String line, Rule rule) {
        HashMap<String, String> tokens = NLInterpreter.cleanSentence(line);

        System.out.print("Translating condition : ");
        StringBuilder predicateName = new StringBuilder();
        for (Map.Entry<String, String> token: tokens.entrySet()) {
            String word = token.getKey();
            String posTag = token.getValue();

            if (lookFor(word, Dictionary.Synonyms.FEATURE, Dictionary.Synonyms.BE,
                    Dictionary.Synonyms.ENTITY, Dictionary.EXCEPTIONS,
                    learnedWords.get(Type.ENTITY).toArray(new String[0])) || posTag.equals("AUX") ) {
                continue;
            }
            System.out.print(" " + word + "(" + posTag + ")");

            predicateName.append(word);
            learnedWords.get(Type.STATE).add(word);
        }
        Predicate predicate = new Predicate(predicateName.toString(), 2); //TODO: Allow predicates of more atoms
        prologProgram.addPredicate(predicate);
    }

    private void translateAction(String line, Rule rule) {
        HashMap<String, String> tokens = NLInterpreter.cleanSentence(line);
        System.out.print("TRANSLATING RULE ACTION: ");
//        Clause clause = new Clause();
        Clause clause = null;
        String clauseName = "";
        ArrayList<String> variables = new ArrayList<>();
        PrologKey key = null;
        for (Map.Entry<String, String> token: tokens.entrySet()) {
            String word = token.getKey();
            String posTag = token.getValue();

            if (lookFor(word, Dictionary.Synonyms.ATTACH)) {
                key = PrologKey.ATTACH;
                continue;
            }

            if (lookFor(word, Dictionary.Synonyms.DETACH)) {
                key = PrologKey.DETACH;
                continue;
            }

            if (lookFor(word, learnedWords.get(Type.STATE).toArray(new String[0]))) {  //TODO: also search synonyms of learned words
                clauseName = word;
            }

            // first filter
            if (lookFor(word, Dictionary.Synonyms.BE, Dictionary.Synonyms.ENTITY, Dictionary.EXCEPTIONS,
                    learnedWords.get(Type.ENTITY).toArray(new String[0]))
                    || posTag.equals("AUX") || posTag.equals("PRON")) {
                continue;
            }

            // second filter
            if (lookFor(word, Dictionary.Synonyms.RULE)|| posTag.equals("ADP"))
                continue;

            if (lookForExactWord(word, rule.getArguments().toArray(new String[0]))) {
                variables.add(word);
            }
            System.out.print(" " + word + "(" + posTag + ")");
        }

        if (key != null) {
            if (key == PrologKey.ATTACH) {
                clause = new Clause("assert", (new Clause(clauseName, variables.toArray(new String[0]))).toString() );
            }
            if (key == PrologKey.DETACH) {
                clause = new Clause("retract", (new Clause(clauseName, variables.toArray(new String[0]))).toString() );
            }
        }
        System.out.println("   RESULT: " + clause.toString() + "\n");
        rule.addBodyClause(clause);
    }


    //TODO: translateArithmeticOperation

    private void translateRule(Queue<String> lines) {
        boolean onActionSection = false;
        Rule rule = new Rule();

        while(!lines.isEmpty()) {
            String line = lines.peek();
            if (lookFor(line, Dictionary.Synonyms.FEATURE) ||
                    lookFor(line, Dictionary.Synonyms.IF)) {
                break;
            }
            lines.poll();

//            Identify header
            if (lookFor(line, Dictionary.Synonyms.RULE)) {
                System.out.println("Header found!");
                identfyRuleHeader(line, rule);
                continue;
            }


            if (!onActionSection) {
                if (lookFor(line, Dictionary.Synonyms.THEN)){
                    onActionSection = true;
                    continue;
                }
                translateConditionPremise(line, rule);
            } else {
                translateAction(line, rule);
            }
        }

        System.out.println("RULE TRANSLATED: " + rule);

        prologProgram.addRule(rule);
    }

    public void translateRules(String rulesText) {
        System.out.println("--------------------------------------------");
        System.out.println("--------------------------------------------");
        System.out.println("RULES TRANSLATION BEGAN");

        ArrayList<String> preparedLines = detectAndPrepareLines(rulesText);
        Queue<String> lines = new LinkedList<>(preparedLines);
        while(!lines.isEmpty()) {
            String line = lines.poll();

            if (lookFor(line, Dictionary.Synonyms.FEATURE)) {
                translateState(line);
            } else if (lookFor(line, Dictionary.Synonyms.ENTITY)) {
                identifyEntity(line);
            }

            if (lookFor(line, Dictionary.Synonyms.IF)) {
                translateRule(lines);
            }
        }
    }


//    private void sectionText() {
//        statesSection = new LinkedList<>();
//        conditionsSection = new LinkedList<>();
//
//        boolean onConditionsSection = false;
//        boolean onStatesSection = false;
//
//        for (String line: preparedLines) {
//            if (onStatesSection) {
//                statesSection.add(line);
//                onConditionsSection = false;
//            }
//            if (onConditionsSection) {
//                conditionsSection.add(line);
//                onStatesSection = false;
//            }
//
//            onStatesSection = lookFor(line, "caracteristicas", "características", "estados") > -1;
//            onConditionsSection = lookFor(line, "reglas", "condiciones", "premisas") > -1;
//        }
//    }


//    private int lookFor(String line, String ...wordsToLookFor) {
//        int firstFound = -1;
//        for (String word: wordsToLookFor) {
//            if (line.contains(word)) {
//                int foundPosition = line.indexOf(word);
//                firstFound = foundPosition < firstFound ? foundPosition : firstFound;
//            }
//        }
//        return firstFound;
//    }

    private boolean lookFor(String line, String ...wordsToLookFor) {
        for (String word: wordsToLookFor) {
            if (contains(line, word))
                return true;
        }
        return false;
    }

    private boolean lookFor(String line, String[] ...wordsToLookFor) {
        for (String[] words: wordsToLookFor) {
            for (String word: words) {
                if (contains(line, word))
                    return true;
            }
        }
        return false;
    }



    private boolean contains(String source, String subItem){
        String pattern = "\\b" + subItem;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }

    private boolean lookForExactword(String line, String ...wordsToLookFor) {
        for (String word: wordsToLookFor) {
            if (containsExactWord(line, word))
                return true;
        }
        return false;
    }

    private boolean lookForExactWord(String line, String[] ...wordsToLookFor) {
        for (String[] words: wordsToLookFor) {
            for (String word: words) {
                if (containsExactWord(line, word))
                    return true;
            }
        }
        return false;
    }

    private boolean containsExactWord(String source, String subItem){
        String pattern = "\\b"+subItem+"\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }

    private boolean lookLike(String word, String otherWord) {
        char[] firstWord = word.toCharArray();
        char[] secondWord = otherWord.toCharArray();

        int similarLetters = 0;
        for (int i = 0; i < firstWord.length; i++) {
            if (firstWord[i] == secondWord[i]) {
                similarLetters++;
            }
        }
        double average = similarLetters/firstWord.length;
        return average > 5.0;
    }

    private String deleteWords(String line, String ...wordsToDelete) {
        String newText = line;
        for (String word: wordsToDelete) {
//            newText = line.replaceAll("(?<!\\S)" + word + "(?!\\S)", "");
            newText = newText.replaceAll("(?<!\\S)" + word + "(?!\\S)", "");
        }
        return newText;
    }

}
