package org.ferpin.bloques.nlp;

import org.apache.commons.lang3.StringUtils;
import org.ferpin.bloques.prolog.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// TODO: caja cerrada... add YES-NO Property
public class PrologTranslator {
    private HashMap<Type, ArrayList<Concept>> learnedConcepts;

    private Program prologProgram;

    public PrologTranslator(Program prologProgram) {
        this.prologProgram = prologProgram;
        this.learnedConcepts = new HashMap<>();
        for (Type prologType : Type.values()) {
            learnedConcepts.put(prologType, new ArrayList<>());
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

    public String translateCommand(String line) {
        String commandTranslated = "";
        System.out.println("--------------------------------------------");
        System.out.println("--------------------------------------------");
        System.out.println("COMMAND TRANSLATION BEGAN");

        String preparedLine = detectAndPrepareLines(line).get(0);
        System.out.println(preparedLine);
        if (preparedLine.contains("?")) {
            System.out.println("Its a query");
            commandTranslated = parseQuery(preparedLine);
        } else {
            commandTranslated = parseInstruction(preparedLine);
            System.out.println("Its a statement");
        }

        System.out.println("COMMAND TRANSLATED: \n" + commandTranslated);
        return commandTranslated;
    }

    private String parseInstruction(String line) {
        LinkedList<Token> tokens = NLInterpreter.cleanSentence(line);

        Clause rule = null;
        boolean correctOrder = false;

        ArrayList<Entity> entities = new ArrayList<>();
        ArrayList<String> properties = new ArrayList<>();

        for (Token token: tokens) {
            String word = token.getWord();
            String posTag = token.getPosTag();

            System.out.println(learnedConcepts.get(Type.RULE));
            System.out.println(Arrays.asList(getConceptSynonyms(learnedConcepts.get(Type.RULE).toArray(new Concept[0]))) );
            if (lookFor(word, getConceptSynonyms(learnedConcepts.get(Type.RULE).toArray(new Concept[0])))) {
                Concept concept = learnedConcepts.get(Type.RULE)
                        .stream()
                        .filter(currentConcept -> lookFor(word, getConceptSynonyms(currentConcept))).findAny().orElse(null);
                String name = DictionaryUtils.getFirstSynonym(concept);
                rule= new Clause(name);
                System.out.println("Rule found: " + rule.getName());
                continue;
            }


            if (lookFor(word, getCategoryOptions(learnedConcepts.get(Type.PREDICATE).toArray(new Concept[0])))) { //TODO: identify category on same loop
                properties.add(word);
                Entity toEvaluateExistance = new Entity();
                toEvaluateExistance.setProperties(properties);

                for (Entity entity: prologProgram.getEntities()) {
                    if (entity.equals(toEvaluateExistance)) {
                        System.out.println("equal entity found; " + entity.getIdentifier());
                        entities.add(new Entity(entity.getIdentifier()));
                        properties.clear();
                        break;
                    }
                }
            }

            if (lookForExactWord(word, prologProgram.getEntities().stream().map(Entity::getIdentifier).collect(Collectors.toList()).toArray(new String[0]))) {
                System.out.println(prologProgram.getEntities().stream().map(Entity::getIdentifier).collect(Collectors.toList()));
                System.out.println("Identifier found: " + word);
                entities.add(new Entity(word));
                properties.clear();
                continue;
            }
        }
        System.out.println(entities);

//        if (correctOrder) {
            rule.addArgument(entities.get(0).getIdentifier());
            rule.addArgument(entities.get(1).getIdentifier());
//        } else {
//            rule.addArgument(entities.get(0).getIdentifier());
//            rule.addArgument((entities.get(0).getIdentifier() + "S").toUpperCase());
//        }
        System.out.println("   Result: " + rule + ".");
        System.out.println("\n");

        return rule + ".";
    }

    private String parseQuery(String line) {
        LinkedList<Token> tokens = NLInterpreter.tokenizeSentence(line);

        Predicate predicate = null;
        boolean correctOrder = false;

        ArrayList<Entity> entities = new ArrayList<>();
        ArrayList<String> properties = new ArrayList<>();

        for (Token token: tokens) {
            String word = token.getWord();
            String posTag = token.getPosTag();

            if (lookFor(word, getConceptSynonyms(learnedConcepts.get(Type.PREDICATE).toArray(new Concept[0])))) {
                Concept concept = learnedConcepts.get(Type.PREDICATE)
                        .stream()
                        .filter(currentConcept -> lookFor(word, getConceptSynonyms(currentConcept))).findAny().orElse(null);
                String name = DictionaryUtils.getFirstSynonym(concept);
                predicate= new Predicate(name);
                continue;
            }

            if (lookFor(word, getCategoryOptions(Concept.QUESTIONS))) {
                if (predicate == null) {
                    correctOrder = true;
                }
                continue;
            }

            if (lookFor(word, getCategoryOptions(learnedConcepts.get(Type.PREDICATE).toArray(new Concept[0])))) { //TODO: identify category on same loop
                properties.add(word);
                Entity toEvaluateExistance = new Entity();
                toEvaluateExistance.setProperties(properties);

                for (Entity entity: prologProgram.getEntities()) {
                    if (entity.equals(toEvaluateExistance)) {
                        System.out.println("equal entity found; " + entity.getIdentifier());
                        entities.add(new Entity(entity.getIdentifier()));
                        properties.clear();
                        break;
                    }
                }
            }

            if (lookForExactWord(word, prologProgram.getEntities().stream().map(Entity::getIdentifier).collect(Collectors.toList()).toArray(new String[0]))) {
                System.out.println(prologProgram.getEntities().stream().map(Entity::getIdentifier).collect(Collectors.toList()));
                System.out.println("Identifier found: " + word);
                entities.add(new Entity(word));
                properties.clear();
                continue;
            }
        }
        Fact fact = new Fact(predicate);
        System.out.println(entities);
        if (correctOrder) {
            fact.addArgument((entities.get(0).getIdentifier() + "S").toUpperCase());
            fact.addArgument(entities.get(0).getIdentifier());
        } else {
            fact.addArgument(entities.get(0).getIdentifier());
            fact.addArgument((entities.get(0).getIdentifier() + "S").toUpperCase());
        }
        System.out.println("   Result: " + fact);
        System.out.println("\n");

        return fact.toString();
    }

    private void parseFact(String line) {
        StringBuilder predicateName = new StringBuilder();
        LinkedList<Token> tokens = NLInterpreter.cleanSentence(line);
        System.out.print("TRANSLATING STAGE FACT: ");


        Predicate predicate = null;
        ArrayList<Entity> entities = new ArrayList<>();

        ArrayList<String> properties = new ArrayList<>();

        boolean correctOrder = false;
        for (Token token: tokens) {
//            System.out.println("evaluating token: " + token);
            String word = token.getWord();
            String posTag = token.getPosTag();

            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.NOTHING), Dictionary.SYNONYMS.get(Concept.NO))) {
                return;
//                entities.add(new Entity("_"));
//                continue;
            }

            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.FLOOR))) {
                entities.add(new Entity(DictionaryUtils.getFirstSynonym(Concept.FLOOR)));
                continue;
            }


            if (lookFor(word, getCategoryOptions(learnedConcepts.get(Type.PREDICATE).toArray(new Concept[0])))) { //TODO: identify category on same loop
                properties.add(word);
                Entity toEvaluateExistance = new Entity();
                toEvaluateExistance.setProperties(properties);


                for (Entity entity: prologProgram.getEntities()) {
//                    System.out.println("Comparing " +toEvaluateExistance + " and " + entity);
                    if (entity.equals(toEvaluateExistance)) {
                        System.out.println("equal entity found");
                        entities.add(new Entity(entity.getIdentifier()));
                        properties.clear();
                        break;
                    }
                }
                continue;
            }

            if (lookFor(word, getConceptSynonyms(learnedConcepts.get(Type.PREDICATE).toArray(new Concept[0])))) {
                predicate = prologProgram.getPredicates().stream().
                        filter(predicate1 ->
                                predicate1.getName().equals(DictionaryUtils.getFirstSynonym(findWordMeaning(word))))
                        .findAny()
                        .orElse(null);

                correctOrder = entities.size() == 1;
                System.out.println("Predicate found " + predicate);
            }
        }

        Fact fact = new Fact(predicate);
        System.out.println(entities);
        if (correctOrder) {
            fact.addArgument(entities.get(0).getIdentifier());
            fact.addArgument(entities.get(1).getIdentifier());
        } else {
            fact.addArgument(entities.get(1).getIdentifier());
            fact.addArgument(entities.get(0).getIdentifier());
        }
        System.out.println("   Result: " + fact);
        prologProgram.addFact(fact);

        System.out.println("\n");

    }

    // TODO: convert ArrayList to HashSet to avoid repetitions when adding learningConcepts.add(Type, Concept)

    private void parseEntity(String line) {
        LinkedList<Token> tokens = NLInterpreter.cleanSentence(line);
        System.out.print("TRANSLATING STAGE ENTITY: ");

        Entity entity = new Entity();
        ArrayList<Predicate> predicates = new ArrayList<>();

        for (Token token: tokens) {
            String word = token.getWord();
            String posTag = token.getPosTag();

            if (lookFor(word, getConceptSynonyms(learnedConcepts.get(Type.ENTITY).toArray(new Concept[0])))) {
                entity.setType(DictionaryUtils.getFirstSynonym(findWordMeaning(word)));
                System.out.println("Entity type " + entity.getType());
                continue;
            }

            if (lookFor(word, getCategoryOptions(learnedConcepts.get(Type.PREDICATE).toArray(new Concept[0])))) { //TODO: identify category on same loop
                Predicate predicate = prologProgram.getPredicates().stream().
                        filter(predicate1 ->
                                predicate1.getName().equals(DictionaryUtils.getFirstSynonym(findCategory(word))))
                        .findAny()
                        .orElse(null);
                entity.addProperty(word);
                predicates.add(predicate);

                System.out.println("Entity type " + predicate);
                continue;
            }


            if (lookFor(word,  Dictionary.SYNONYMS.get(Concept.BE), Dictionary.SYNONYMS.get(Concept.ENTITY), Dictionary.CATEGORIES.get(Concept.EXCEPTIONS),
                    getConceptSynonyms(learnedConcepts.get(Type.ENTITY).toArray(new Concept[0])))
                    || posTag.equals("AUX") || posTag.equals("PRON") || posTag.equals("VERB")) {
                continue;
            }

            System.out.print("identifier: " + word + " ");
            entity.setIdentifier(word);
        }


        prologProgram.addFact(new Fact(new Predicate(entity.getType(), 1), entity.getIdentifier()));

        for (int i = 0; i < predicates.size(); i++) {
            Fact fact = new Fact(predicates.get(i), entity.getIdentifier(), entity.getProperties().get(i));
            System.out.println("  Result: " + fact);
            prologProgram.addFact(fact);
        }
        prologProgram.addEntity(entity);

        System.out.println("\n");

    }

    private void identifyEntity(String line) {
//        line = deleteWords(line, Dictionary.ENTITY);
        LinkedList<Token> tokens = NLInterpreter.cleanSentence(line);
        for (Token token: tokens) {
            String word = token.getWord();
            String posTag = token.getPosTag();

            if (!posTag.equals("VERB") && !posTag.equals("ADP")  && !lookFor(word, Dictionary.SYNONYMS.get(Concept.ENTITY))) { //TODO: standardize exceptions (in this case type)
                System.out.println("New entity found: " + word + "\n");
                learnedConcepts.get(Type.ENTITY).add(findWordMeaning(word));
            }
        }
    }

    private void parsePredicate(String line) {
        StringBuilder predicateName = new StringBuilder();

        LinkedList<Token> tokens = NLInterpreter.cleanSentence(line);
        System.out.print("TRANSLATING PREDICATE: ");
        for (Token token: tokens) {
            String word = token.getWord();
            String posTag = token.getPosTag();


            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.BE), Dictionary.SYNONYMS.get(Concept.ENTITY), Dictionary.CATEGORIES.get(Concept.EXCEPTIONS),
                            getConceptSynonyms(learnedConcepts.get(Type.ENTITY).toArray(new Concept[0])))
                            || posTag.equals("AUX") || posTag.equals("PRON")) {
                continue;
            }

            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.FEATURE), Dictionary.SYNONYMS.get(Concept.TYPE)))
                continue;

            System.out.print(" " + word + "(" + posTag + ")");

            Concept wordConcept = findWordMeaning(word);
            learnedConcepts.get(Type.PREDICATE).add(wordConcept);
            predicateName.append(DictionaryUtils.getFirstSynonym(wordConcept));
        }
        Predicate predicate = new Predicate(predicateName.toString(), 2); //TODO: Allow predicates of more atoms
        System.out.println("   RESULT: " + predicate + "\n");
        prologProgram.addPredicate(predicate);
    }

    private void identfyRuleHeader(String line, Rule rule) {
        ArrayList<String> variables = new ArrayList<>();
        LinkedList<Token> tokens = NLInterpreter.cleanSentence(line);
        System.out.print("TRANSLATING RULE HEADER: ");
        for (Token token: tokens) {
            String word = token.getWord();
            String posTag = token.getPosTag();

            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.BE), Dictionary.SYNONYMS.get(Concept.ENTITY), Dictionary.CATEGORIES.get(Concept.EXCEPTIONS),
                    getConceptSynonyms(learnedConcepts.get(Type.ENTITY).toArray(new Concept[0])))
                    || posTag.equals("AUX") || posTag.equals("PRON")) {
                continue;
            }

            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.RULE)) || posTag.equals("ADP"))
                continue;

            if (posTag.equals("VERB")) {
                Concept wordConcept = findWordMeaning(word);
                learnedConcepts.get(Type.PREDICATE).add(wordConcept);
                String name = DictionaryUtils.getFirstSynonym(wordConcept);
                rule.setName(name);

            } else {
                variables.add(word.toUpperCase());
            }

            System.out.print(" " + word + "(" + posTag + ")");
        }
        rule.setArguments(variables);

        learnedConcepts.get(Type.RULE).add(findWordMeaning(rule.getName()));

        System.out.println("   RESULT: " + rule.getName() + "\n");
    }

    private void parseConditionPremise(String line, Rule rule) {
        Clause clause = new Clause();
        PrologKey prologKey = null;
        ArrayList<String> variables = new ArrayList<>();

        LinkedList<Token> tokens = NLInterpreter.cleanSentence(line);
        System.out.print("TRANSLATING RULE CONDITION: ");
        for (Token token: tokens) {
            String word = token.getWord();
            String posTag = token.getPosTag();


            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.NO))) {
                prologKey = PrologKey.NOT;
                continue;
            }

            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.NOTHING))) {
                variables.add(0, "_");
                continue;
            }


            if (lookFor(word, getConceptSynonyms(learnedConcepts.get(Type.PREDICATE).toArray(new Concept[0])))) {  //TODO: also search synonyms of learned words !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                clause.setName(word);
                continue;
            }

            // first filter
            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.BE), Dictionary.SYNONYMS.get(Concept.ENTITY), Dictionary.CATEGORIES.get(Concept.EXCEPTIONS),
                    getConceptSynonyms(learnedConcepts.get(Type.ENTITY).toArray(new Concept[0])))
                    || posTag.equals("AUX") || posTag.equals("PRON")) {
                continue;
            }

            // second filter
            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.RULE)) || posTag.equals("ADP") || posTag.equals("VERB"))
                continue;

//            if (lookForExactWord(word.toUpperCase(), rule.getArguments().toArray(new String[0]))) {
                variables.add(word.toUpperCase());
//            }
            System.out.print(" " + word + "(" + posTag + ")");
        }

        clause.setArguments(variables);

        if (prologKey != null) {
//            if (prologKey == PrologKey.NOT) {
                clause = new Clause("not", clause.toString());
//            }
        }

        System.out.println("   RESULT: " + clause.toString() + "\n");
        rule.addBodyClause(clause);
    }

    private void parseAction(String line, Rule rule) {
        LinkedList<Token> tokens = NLInterpreter.cleanSentence(line);
        System.out.print("TRANSLATING RULE ACTION: ");
        Clause clause = new Clause();
        ArrayList<String> variables = new ArrayList<>();
        for (Token token: tokens) {
            String word = token.getWord();
            String posTag = token.getPosTag();


            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.FLOOR))) {
                variables.add("piso");
                continue;
            }

            // first rules, are more important than states
            if (lookFor(word, getConceptSynonyms(learnedConcepts.get(Type.RULE).toArray(new Concept[0])))) {
                clause.setName(word);
                continue;
            }

            if (lookFor(word, getConceptSynonyms(learnedConcepts.get(Type.PREDICATE).toArray(new Concept[0]))) && clause.getName() == null) {  //TODO: also search synonyms of learned words
                clause.setName(word);
                continue;
            }

            // first filter
            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.BE), Dictionary.SYNONYMS.get(Concept.ENTITY), Dictionary.CATEGORIES.get(Concept.EXCEPTIONS),
                    getConceptSynonyms(learnedConcepts.get(Type.ENTITY).toArray(new Concept[0])))
                    || posTag.equals("AUX") || posTag.equals("PRON")) {
                continue;
            }

            // second filter
            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.RULE)) || posTag.equals("ADP"))
                continue;

//            if (lookForExactWord(word, rule.getArguments().toArray(new String[0]))) {
            variables.add(word.toUpperCase());
//            }
            System.out.print(" " + word + "(" + posTag + ")");
        }
        clause.setArguments(variables);

        System.out.println("   RESULT: " + clause.toString() + "\n");
        rule.addBodyClause(clause);
    }

    private void parsePrimitiveAction(String line, Rule rule) {
        LinkedList<Token> tokens = NLInterpreter.cleanSentence(line);
        System.out.print("TRANSLATING RULE PRIMITIVE  ACTION: ");
        Clause clause = new Clause();
        String innerClauseName = "";
        ArrayList<String> variables = new ArrayList<>();
        PrologKey key = null;
        for (Token token: tokens) {
            String word = token.getWord();
            String posTag = token.getPosTag();


            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.FLOOR))) { // TODO: also learn these words (floor and nothing)
                variables.add(0, "piso");
            }

            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.ATTACH))) {
                key = PrologKey.ATTACH;
                clause.setName("assert");
                continue;
            }

            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.DETACH))) {
                key = PrologKey.DETACH;
                clause.setName("retract");
                continue;
            }

            if (lookFor(word, getConceptSynonyms(learnedConcepts.get(Type.PREDICATE).toArray(new Concept[0])))) {  //TODO: also search synonyms of learned words
                innerClauseName = word;
            }

            // first filter
            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.BE), Dictionary.SYNONYMS.get(Concept.ENTITY), Dictionary.CATEGORIES.get(Concept.EXCEPTIONS),
                    getConceptSynonyms(learnedConcepts.get(Type.ENTITY).toArray(new Concept[0])))
                    || posTag.equals("AUX") || posTag.equals("PRON")) {
                continue;
            }

            // second filter
            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.RULE)) || posTag.equals("ADP"))
                continue;

//            System.out.println(rule.getArguments());
//            System.out.println(word);
            if (lookForExactWord(word.toUpperCase(), rule.getArguments().toArray(new String[0]))) {
                variables.add(word.toUpperCase());
            }
            System.out.print(" " + word + "(" + posTag + ")");
        }

        if (key != null) {
//            if (key == PrologKey.ATTACH || key == PrologKey.DETACH)
                clause.addArgument( (new Clause(innerClauseName, variables.toArray(new String[0]))).toString() );
        }
        System.out.println("   RESULT: " + clause.toString() + "\n");
        rule.addBodyClause(clause);
    }

    private void parseWriteAction(String line, Rule rule) {
        LinkedList<Token> tokens = NLInterpreter.tokenizeSentence(line);
        System.out.print("TRANSLATING RULE WRITE ACTION: ");

        String writeInstruction = "";
        for (Token token: tokens) {
            String word = token.getWord();
            String posTag = token.getPosTag();

            if ((lookFor(word, Dictionary.SYNONYMS.get(Concept.WRITE)))) {
                System.out.println("omitiendo");
                continue; // delete "escribir"
            }

            if (lookFor(word, Dictionary.SYNONYMS.get(Concept.FLOOR))) { // TODO: also learn these words (floor and nothing)
                writeInstruction = writeInstruction + getWriteInstruction(word) + ",";
                continue;
            }

            if (lookForExactWord(word.toUpperCase(), rule.getArguments().toArray(new String[0]))) {
                writeInstruction = writeInstruction + getWriteInstruction(word.toUpperCase()) + ",";
                continue;
            }

            writeInstruction = writeInstruction + getWriteInstruction("' "+ word + " '") + ",";

            System.out.print(" " + word + "(" + posTag + ")");
            System.out.println(writeInstruction);
        }

        writeInstruction = writeInstruction.substring(0, writeInstruction.length() - 1);
        System.out.println("   RESULT: " + writeInstruction);

        rule.addBodyClause(new Clause(writeInstruction));
    }

    String getWriteInstruction(String paramter) {
        return  "write(" + paramter + ")";
    }

    String getWritelnInstruction(String paramter) {
        return "writeln(" + paramter + ")";
    }


    //TODO: parseArithmeticOperation
    //TODO: parseLogicOperation
    private void parseRule(Queue<String> lines) {
        boolean onActionSection = false;
        Rule rule = new Rule();

        while(!lines.isEmpty()) {
            String line = lines.peek();
            if (lookFor(line, Dictionary.SYNONYMS.get(Concept.FEATURE)) ||
                    lookFor(line, Dictionary.SYNONYMS.get(Concept.IF))) {
                break;
            }
            lines.poll();

//            Identify header
            if (lookFor(line, Dictionary.SYNONYMS.get(Concept.RULE))) {
                System.out.println("Header found!");
                identfyRuleHeader(line, rule);
                continue;
            }


            if (!onActionSection) {
                if (lookFor(line, Dictionary.SYNONYMS.get(Concept.THEN))){
                    onActionSection = true;
                    continue;
                }
                parseConditionPremise(line, rule);
            } else {
                if (lookFor(line, Dictionary.SYNONYMS.get(Concept.DETACH), Dictionary.SYNONYMS.get(Concept.ATTACH))) {
                    parsePrimitiveAction(line, rule);
                    continue;
                } else if ((lookFor(line, Dictionary.SYNONYMS.get(Concept.WRITE)))) {
                    parseWriteAction(line, rule);
                } else {
                    parseAction(line, rule);
                }
            }
        }

        System.out.println("RULE TRANSLATED: \n" + rule);

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

            if (lookFor(line, Dictionary.SYNONYMS.get(Concept.FEATURE))) {
                parsePredicate(line);
            } else if (lookFor(line, Dictionary.SYNONYMS.get(Concept.ENTITY))) {
                identifyEntity(line);
            }

            if (lookFor(line, Dictionary.SYNONYMS.get(Concept.IF))) {
                parseRule(lines);
            }
        }

        System.out.println(prologProgram);
    }

    public void translateStage(String stageText) {
        System.out.println("--------------------------------------------");
        System.out.println("--------------------------------------------");
        System.out.println("STAGE TRANSLATION BEGAN");

        ArrayList<String> preparedLines = detectAndPrepareLines(stageText);
        Queue<String> lines = new LinkedList<>(preparedLines);
        while(!lines.isEmpty()) {
            String line = lines.poll();

            System.out.println("LINE: " + line);

            if (lookFor(line, getConceptSynonyms(learnedConcepts.get(Type.ENTITY).toArray(new Concept[0])))) {
                parseEntity(line);
            } else {
                parseFact(line);
            }
        }

        System.out.println("PROGRAM WITH FACTS: \n" + prologProgram);
    }

    

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
    
    private String[] getConceptSynonyms(Concept ...concepts) {
        ArrayList<String> synonyms = new ArrayList<>();
        for (Concept concept: concepts) {
            synonyms.addAll(Arrays.asList(Dictionary.SYNONYMS.get(concept)));
        }
        return synonyms.toArray(new String[0]);
    }

    private Concept findWordMeaning(String word) {
        return Dictionary.SYNONYMS.entrySet()
                .stream()
                .filter(entry -> Arrays.asList(entry.getValue()).contains(word))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()).get(0);
    }

    private String[] getCategoryOptions(Concept ...concepts) {
        ArrayList<String> synonyms = new ArrayList<>();

        for (Concept concept: concepts) {
            if (Dictionary.CATEGORIES.containsKey(concept))
                synonyms.addAll(Arrays.asList(Dictionary.CATEGORIES.get(concept)));
        }
        return synonyms.toArray(new String[0]);
    }

    private Concept findCategory(String word) {
        return Dictionary.CATEGORIES.entrySet()
                .stream()
                .filter(entry -> Arrays.asList(entry.getValue()).contains(word))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()).get(0);
    }



    private boolean contains(String source, String subItem){
        String pattern = "\\b" + subItem;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }

    private boolean containsExactWord(String source, String subItem){
        String pattern = "\\b"+subItem+"\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
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

    private boolean lookForExactWord(String line, String ...wordsToLookFor) {
        for (String word: wordsToLookFor) {
            if (containsExactWord(line, word))
                return true;
        }
        return false;
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
        float average = ((float) similarLetters)/firstWord.length;
        return average > 5.0;
    }

    private String deleteWords(String line, String ...wordsToDelete) {
        String newText = line;
        for (String word: wordsToDelete) {
            newText = newText.replaceAll("(?<!\\S)" + word + "(?!\\S)", "");
        }
        return newText;
    }

}
