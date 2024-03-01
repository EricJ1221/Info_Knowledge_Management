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

    public void insertWord(String word, String originalWord, Set<String> documents) {
    word = word.replaceAll("[-,.!?]+", " ");
    word = word.toLowerCase();

    String[] words = word.split("\\s+");
    for (String w : words) {
        if (stopWords.contains(w)) {
            continue; // Skip inserting stop words
        }

        List<String> rotations = generateRotations(w);

        for (String rotation : rotations) {
            index.putIfAbsent(rotation, new HashSet<>());
            index.get(rotation).add(w);

            permutermToDocuments.putIfAbsent(rotation, new HashSet<>());
            permutermToDocuments.get(rotation).addAll(documents);  // Add all documents associated with originalWord
        }

        groupedRotations.put(w, rotations);
    }
}


    public Set<String> getDocuments(String permuterm) {
        return permutermToDocuments.getOrDefault(permuterm, Collections.emptySet());
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
            matchingDocuments.addAll(documents);
        }
    
        return matchingDocuments;
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
            writer.write("________________________________________________Permuterm Index_______________________________________________\n");
            writer.write("\n");
            writer.write("Unique Permuterms\t\t\t\t\tRotations\n");
            writer.write("\n");

            List<String> words = new ArrayList<>(groupedRotations.keySet());
            Collections.sort(words);
            for (String word : words) {
                StringJoiner rotationsJoiner = new StringJoiner(" ");
                for (String rotation : groupedRotations.get(word)) {
                    rotationsJoiner.add(rotation);
                }
                String rotationsString = rotationsJoiner.toString();

                String paddedWord = String.format("%15s", word);
                String paddedRotations = String.format("%-10s", rotationsString);
                writer.write(paddedWord + "\t\t\t\t" + paddedRotations + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}