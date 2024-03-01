import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PermutermIndex {
    Map<String, Set<String>> index = new HashMap<>();
    Map<String, Set<String>> permutermToDocuments = new HashMap<>();
    private Map<String, List<String>> groupedRotations = new HashMap<>();

    String[] wordsToDelete = {"a", "am", "and", "are", "be", "could", "do", "i", "if", "in", "let", "may", "me", "not",
            "on", "or", "so", "that", "the", "them", "they", "will", "with", "would", "you"};
    Set<String> stopWords = new HashSet<>(Arrays.asList(wordsToDelete));

    public void insertWord(String word, String originalWord, String documentId) {
        word = word.replaceAll("[-,.!?]+", " "); // Remove punctuation
        word = word.toLowerCase(); // Convert to lowercase
    
        String[] words = word.split("\\s+"); // Split the word by spaces
        for (String w : words) {
            if (stopWords.contains(w)) {
                continue; // Skip inserting stop words
            }
    
            List<String> rotations = generateRotations(w); // Generate rotations for the word
    
            for (String rotation : rotations) {
                index.putIfAbsent(rotation, new HashSet<>());
                index.get(rotation).add(w);
    
                // Associate each word with the document ID directly
                permutermToDocuments.putIfAbsent(w, new HashSet<>());
                permutermToDocuments.get(w).add(documentId);
            }
    
            groupedRotations.put(w, rotations);
        }
    }


    public Set<String> getDocuments(String word) {
        return permutermToDocuments.getOrDefault(word, Collections.emptySet());
    }

    private List<String> generateRotations(String word) {
        List<String> rotations = new ArrayList<>();
        for (int i = 0; i <= word.length(); i++) {
            String permuterm = word.substring(i) + "$" + word.substring(0, i);
            rotations.add(permuterm);
        }
        return rotations;
    }

    public Set<String> wildcardSearch(String query) {
        if (query.endsWith("*")) {
            String pattern = query.substring(0, query.length() - 1) + "$.*";
            return index.keySet().stream().filter(rotation -> rotation.matches(pattern))
                    .flatMap(rotation -> index.get(rotation).stream())
                    .collect(Collectors.toSet());
        } else if (query.startsWith("*")) {
            String pattern = ".*" + query.substring(1) + "$";
            return index.keySet().stream().filter(rotation -> rotation.matches(pattern))
                    .flatMap(rotation -> index.get(rotation).stream())
                    .collect(Collectors.toSet());
        } else {
            String pattern = query + "$";
            return index.keySet().stream().filter(rotation -> rotation.equals(pattern))
                    .flatMap(rotation -> index.get(rotation).stream())
                    .collect(Collectors.toSet());
        }
    }

    public static Set<String> findDocumentsMatchingQuery(String query, PermutermIndex permutermIndex) {
        Set<String> matchingDocuments = new HashSet<>();
        Set<String> permuterms = permutermIndex.wildcardSearch(query);
    
        for (String permuterm : permuterms) {
            Set<String> documents = permutermIndex.getDocuments(permuterm);
            System.out.println("Documents for " + permuterm + ": " + documents);
            matchingDocuments.addAll(documents);
            System.out.println("Matching documents: " + matchingDocuments);
        }
    
        return matchingDocuments;
    }
    
    // Method to get documents associated with a word
    public Set<String> getDocumentsForWord(String word) {
    Set<String> documents = permutermToDocuments.getOrDefault(word, Collections.emptySet());
    return documents;
    }



    public void printIndex() {
        List<String> words = new ArrayList<>(groupedRotations.keySet());
        Collections.sort(words);
        System.out.println(" ");
        System.out.println(" Permuterms and rotations ");
        System.out.println("______________________________________________________");
        for (String word : words) {
            System.out.print(word + ": ");
            for (String rotation : groupedRotations.get(word)) {
                System.out.print(rotation + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void writeIndexToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("________________________________________________Vocabulary Terms_______________________________________________\n");
            writer.write("\n");
            writer.write("Vocabulary Terms\t\t\t\t\tWords\n");
            writer.write("\n");
    
            List<String> permuterms = new ArrayList<>(index.keySet());
            Collections.sort(permuterms);
            for (String permuterm : permuterms) {
                Set<String> words = index.get(permuterm);
    
                // Write each permuterm and its associated words on a new line
                for (String word : words) {
                    String paddedPermuterm = String.format("%15s", permuterm);
                    String paddedWord = String.format("%-10s", word);
                    writer.write(paddedPermuterm + "\t\t\t\t" + paddedWord + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}