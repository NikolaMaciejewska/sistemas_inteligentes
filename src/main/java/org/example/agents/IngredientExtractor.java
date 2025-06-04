package org.example.agents;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.File;
import java.io.IOException;
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
     * Extract noun phrases and noun lemmas from input text
     */
    private List<String> extractCandidates(String text) {
        List<String> candidates = new ArrayList<>();
        Annotation doc = new Annotation(text);
        pipeline.annotate(doc);

        // Extract noun phrases
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

        // Add single noun lemmas
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                if (pos.startsWith("NN")) {
                    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                    candidates.add(lemma);
                }
            }
        }

        return candidates.stream()
                .map(this::normalize)
                .filter(s -> s.length() >= minCandidateLength)
                .distinct()
                .collect(Collectors.toList());
    }

    public String normalize(String s) {
        return s.toLowerCase().replaceAll("[^a-z ]", "").replaceAll("\\s+", " ").trim();
    }

    public List<String> matchToKnownIngredients(List<String> candidates) {
        LevenshteinDistance distanceCalc = new LevenshteinDistance();
        List<String> matchedIngredients = new ArrayList<>();
        List<String> matchedCandidates = new ArrayList<>();

        // Sort long phrases first
        candidates.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String candidate : candidates) {
            boolean isSubCandidate = matchedCandidates.stream().anyMatch(m -> m.contains(candidate));
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

        return matchedIngredients.stream().distinct().collect(Collectors.toList());
    }

    public List<String> extractAndMatch(String rawText) {
        List<String> candidates = extractCandidates(rawText);
        return matchToKnownIngredients(candidates);
    }

    /**
     * Extract and match ingredients from an image
     */
    public List<String> extractAndMatchFromImage(File imageFile) throws IOException {
        ImageObjectExtractor imageExtractor = new ImageObjectExtractor();
        List<String> labels = imageExtractor.extractLabels(imageFile);

        // Normalize and filter
        List<String> candidates = labels.stream()
                .map(this::normalize)
                .filter(s -> s.length() >= minCandidateLength)
                .collect(Collectors.toList());

        return matchToKnownIngredients(candidates);
    }
}