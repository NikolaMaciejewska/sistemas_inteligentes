package org.example.agents;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.stream.Collectors;

public class IngredientExtractor {

    private final StanfordCoreNLP pipeline;
    private final List<String> knownIngredients;
    private final int maxDistance;
    private final int minCandidateLength;

    public IngredientExtractor(List<String> knownIngredients, int maxDistance, int minCandidateLength) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse");
        this.pipeline = new StanfordCoreNLP(props);
        this.knownIngredients = knownIngredients.stream()
                .map(this::normalize)
                .collect(Collectors.toList());
        this.maxDistance = maxDistance;
        this.minCandidateLength = minCandidateLength;
    }

    /**
     * Extract noun phrases (NP) and single noun lemmas from text as candidates
     */
    private List<String> extractCandidates(String text) {
        List<String> candidates = new ArrayList<>();
        Annotation doc = new Annotation(text);
        pipeline.annotate(doc);

        // Extract noun phrases (NP)
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            for (Tree subtree : tree) {
                if (subtree.label().value().equals("NP")) {
                    String phrase = subtree.yieldWords().stream()
                            .map(word -> word.word().toLowerCase())
                            .collect(Collectors.joining(" "));
                    candidates.add(phrase);
                }
            }
        }

        // Also extract single noun lemmas to catch single-word ingredients
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                if (pos.startsWith("NN")) {
                    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                    candidates.add(lemma);
                }
            }
        }

        // Normalize and filter out very short candidates (like "a", "an", "in")
        return candidates.stream()
                .map(this::normalize)
                .filter(s -> s.length() >= minCandidateLength)
                .distinct()
                .collect(Collectors.toList());
    }

    // Normalize string (remove special chars, lowercase)
    public String normalize(String s) {
        return s.toLowerCase().replaceAll("[^a-z ]", "").replaceAll("\\s+", " ").trim();
    }

    // Fuzzy match to known ingredients using Levenshtein with maxDistance threshold
    public List<String> matchToKnownIngredients(List<String> candidates) {
        LevenshteinDistance distanceCalc = new LevenshteinDistance();
        List<String> matchedIngredients = new ArrayList<>();
        List<String> matchedCandidates = new ArrayList<>();

        // Sort candidates by length descending to prioritize multi-word ingredients
        candidates.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String candidate : candidates) {
            // Skip if candidate is substring of any previously matched candidate
            boolean isSubCandidate = false;
            for (String matchedCandidate : matchedCandidates) {
                if (matchedCandidate.contains(candidate)) {
                    isSubCandidate = true;
                    break;
                }
            }
            if (isSubCandidate) continue;

            String bestMatch = null;
            int bestScore = Integer.MAX_VALUE;

            for (String known : knownIngredients) {
                int dist = distanceCalc.apply(candidate, known);
                if (dist < bestScore && dist <= maxDistance) {
                    bestMatch = known;
                    bestScore = dist;
                }
            }

            if (bestMatch != null) {
                matchedCandidates.add(candidate);
                matchedIngredients.add(bestMatch);
            }

            System.out.printf("Candidate: %-30s BestMatch: %-30s Score: %d\n", candidate, bestMatch, bestScore);
        }

        // Remove duplicates but keep order
        return matchedIngredients.stream().distinct().collect(Collectors.toList());
    }

    public List<String> extractAndMatch(String rawText) {
        List<String> candidates = extractCandidates(rawText);
        return matchToKnownIngredients(candidates);
    }

    public static void main(String[] args) {
        List<String> knownIngredients = List.of(
                "apple cider vinegar", "onion", "garlic", "baking soda", "olive oil", "tomato", "carrot"
        );
        String input = "Add 2 tbsp of apple cider viniger and chopped onins to the pan.";

        IngredientExtractor processor = new IngredientExtractor(knownIngredients, 2, 3);  // min length 3 chars to avoid "have"
        List<String> matchedIngredients = processor.extractAndMatch(input);

        System.out.println("Matched Ingredients: " + matchedIngredients);
    }
}