package org.example.agents;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.stream.Collectors;

public class IngredientExtractor {

    private final StanfordCoreNLP pipeline;
    private final List<String> knownIngredients;
    private final int maxDistance;

    public IngredientExtractor(List<String> knownIngredients, int maxDistance) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,parse");
        this.pipeline = new StanfordCoreNLP(props);
        this.knownIngredients = knownIngredients.stream()
                .map(this::normalize)
                .collect(Collectors.toList());
        this.maxDistance = maxDistance;
    }

    // 1. Extract noun phrases (candidates)
    public List<String> extractNounPhrases(String text) {
        List<String> phrases = new ArrayList<>();
        Annotation doc = new Annotation(text);
        pipeline.annotate(doc);

        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            for (Tree subtree : tree) {
                if (subtree.label().value().equals("NP")) {
                    String phrase = subtree.yieldWords().stream()
                            .map(HasWord::word)
                            .collect(Collectors.joining(" "))
                            .toLowerCase();
                    phrases.add(phrase);
                }
            }
        }
        return phrases;
    }

    // 2. Normalize string (remove special chars, lowercasing, etc.)
    public String normalize(String s) {
        return s.toLowerCase().replaceAll("[^a-z ]", "").replaceAll("\\s+", " ").trim();
    }

    // 3. Fuzzy match to known ingredients using Levenshtein
    public List<String> matchToKnownIngredients(List<String> candidates) {
        LevenshteinDistance distanceCalc = new LevenshteinDistance();
        List<String> matched = new ArrayList<>();

        for (String candidate : candidates) {
            String normCandidate = normalize(candidate);
            String bestMatch = null;
            int bestScore = Integer.MAX_VALUE;

            for (String known : knownIngredients) {
                int dist = distanceCalc.apply(normCandidate, known);
                if (dist < bestScore && dist <= maxDistance) {
                    bestMatch = known;
                    bestScore = dist;
                }
            }

            if (bestMatch != null) {
                matched.add(bestMatch);
            }
        }

        return matched;
    }

    // 4. Full pipeline: from raw text → matched ingredients
    public List<String> extractAndMatch(String rawText) {
        List<String> phrases = extractNounPhrases(rawText);
        return matchToKnownIngredients(phrases);
    }

    // Demo usage
    public static void main(String[] args) {
        List<String> knownIngredients = List.of(
                "apple cider vinegar", "onion", "garlic", "baking soda", "olive oil", "tomato", "carrot"
        );
        String input = "Add 2 tbsp of apple cider viniger and chopped onins to the pan.";

        IngredientExtractor processor = new IngredientExtractor(knownIngredients, 2);
        List<String> matchedIngredients = processor.extractAndMatch(input);

        System.out.println("Matched Ingredients: " + matchedIngredients);
    }
}
