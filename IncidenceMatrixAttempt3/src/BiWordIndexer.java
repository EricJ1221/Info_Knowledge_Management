import java.io.*;
import java.util.*;

public class BiWordIndexer {
    private Map<String, Set<Integer>> biWordsByFile = new HashMap<>();
    private String[] wordsToDelete = {"a", "am", "and", "are", "be", "could", "do", "I", "if", "in", "let", "may", "me", "not",
                                       "on", "or", "so", "that", "the", "them", "they", "will", "with", "would", "you"};

    public void buildBiWordIndex(String[] filePathsBiWord) {
        for (int docNumber = 1; docNumber <= filePathsBiWord.length; docNumber++) {
            String filePath = filePathsBiWord[docNumber - 1];
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                StringBuilder fileContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line.replaceAll("[^a-zA-Z\\s]+", " ")).append(" ");
                }
                String[] words = fileContent.toString().toLowerCase().split("\\s+");
                List<String> wordsList = new ArrayList<>(Arrays.asList(words));
                wordsList.removeAll(Arrays.asList(wordsToDelete));
                for (int i = 0; i < wordsList.size() - 1; i++) {
                    String biWord = wordsList.get(i) + " " + wordsList.get(i + 1);
                    if (!biWordsByFile.containsKey(biWord)) {
                        biWordsByFile.put(biWord, new HashSet<>());
                    }
                    biWordsByFile.get(biWord).add(docNumber);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeBiWordIndexToFile(String filePath) {
        List<String> biWordsList = new ArrayList<>();
        for (String biWord : biWordsByFile.keySet()) {
            StringBuilder sb = new StringBuilder(biWord);
            for (int docNumber = 1; docNumber <= 32; docNumber++) {
                sb.append("\t");
                if (biWordsByFile.get(biWord).contains(docNumber)) {
                    sb.append(docNumber); // Replace "X" with the document number
                } else {
                    sb.append(" ");
                }
            }
            biWordsList.add(sb.toString());
        }
        Collections.sort(biWordsList);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("________________________________________________Bi-Word Index_______________________________________________\n");
            writer.write("\n");
            writer.write("Unique Bi-Words\t\t\t\t\tFiles\n");
            writer.write("\n");
            for (String biWord : biWordsList) {
                String[] parts = biWord.split("\t");
                String paddedBiWord = String.format("%-50s", parts[0]);
                String paddedDocNumbers = String.format("%-10s", parts[1].trim().replaceAll("\\s+", ", "));
                writer.write(paddedBiWord + "\t\t\t\t" + paddedDocNumbers + "\n"); // Adjusted formatting
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<Integer> searchBiWordIndex(String queryBiWord) {
        Set<Integer> matchingDocuments = new HashSet<>();
        for (String biWord : biWordsByFile.keySet()) {
            if (biWord.equals(queryBiWord)) {
                matchingDocuments.addAll(biWordsByFile.get(biWord));
            }
        }
        return matchingDocuments;
    }
}


/* 
// Code that was used in main to finish assignment 2 problem 1,2
// Words to delete
List<String> wordsToDelete = Arrays.asList("a", "am", "and", "are", "be", "could", "do", "I", "if", "in", "let", "may", "me", "not",
"on", "or", "so", "that", "the", "them", "they", "will", "with", "would", "you");

// Convert all words to lowercase
wordsToDelete.replaceAll(String::toLowerCase);

// Array of file paths
String[] filePathsBiWord = new String[32];
for (int i = 1; i <= 32; i++) {
filePathsBiWord[i - 1] = "/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/eggs2/" + i + ".txt";
}

// List to store bi-words for each file
Map<String, Set<Integer>> biWordsByFile = new HashMap<>(); // Map of bi-words and the documents in which they occur

// Loop through each file's contents and apply logic to delete words and remove punctuation
for (int docNumber = 1; docNumber <= filePathsBiWord.length; docNumber++) {
String filePath = filePathsBiWord[docNumber - 1];
try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
// Create StringBuilder to store the content of each file
StringBuilder fileContent = new StringBuilder();

// Read the content of each file
String line;
while ((line = reader.readLine()) != null) {
    fileContent.append(line.replaceAll("[^a-zA-Z\\s]+", " ")).append(" "); // Append each line to the content StringBuilder, remove punctuation, replace line breaks with spaces
}

String[] words = fileContent.toString().toLowerCase().split("\\s+"); // Remove punctuation and split content into words

System.out.println("Words for document " + docNumber + " before processing:");
for (String word : words) {
    System.out.print(word + " ");
}
System.out.println(" ");
System.out.println(" ");

// Convert words to List and remove wordsToDelete
List<String> wordsList = new ArrayList<>(Arrays.asList(words));
wordsList.removeAll(wordsToDelete);

for (int i = 0; i < wordsList.size() - 1; i++) {
    // Generate bi-words from consecutive words in the file content
    String biWord = wordsList.get(i) + " " + wordsList.get(i + 1);
    if (!biWordsByFile.containsKey(biWord)) {
        biWordsByFile.put(biWord, new HashSet<>());
    }
    biWordsByFile.get(biWord).add(docNumber); // Add the document number to the set of documents in which the bi-word occurs
}
} catch (IOException e) {
e.printStackTrace();
}

System.out.println("Bi-words for document " + docNumber + " after processing:");
System.out.println(biWordsByFile);
System.out.println("");
}

// Convert the map of bi-words and the documents in which they occur to a list of strings for sorting
List<String> biWordsList = new ArrayList<>();
for (String biWord : biWordsByFile.keySet()) {
StringBuilder sb = new StringBuilder(biWord);
for (int docNumber = 1; docNumber <= filePathsBiWord.length; docNumber++) {
sb.append("\t");
if (biWordsByFile.get(biWord).contains(docNumber)) {
    sb.append(docNumber); // Replace "X" with the document number
} else {
    sb.append(" ");
}
}
biWordsList.add(sb.toString());
}

// Sort the list of strings
Collections.sort(biWordsList);

// Print the bi-word index
System.out.println("________________________________________________Bi-word Index_______________________________________________");
System.out.println("Unique Bi-Words                \tFiles");
System.out.println("");
for (String biWord : biWordsList) {
    String[] parts = biWord.split("\t");
    String biword = parts[0];
    Set<Integer> documents = biWordsByFile.get(biword);
    StringJoiner documentNumbers = new StringJoiner(", ");
    if (documents != null) {
        for (int docNumber = 1; docNumber <= filePathsBiWord.length; docNumber++) {
            if (documents.contains(docNumber)) {
                documentNumbers.add(Integer.toString(docNumber));
            }
        }
    }
    System.out.println(biword + "\t\t" + documentNumbers);
}


// Create the file for the bi-word index and write the index into it
try (FileWriter writer = new FileWriter("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/bi_word_index.txt")) {
    writer.write("________________________________________________Bi-Word Index_______________________________________________\n");
    writer.write("\n");
    writer.write("Unique Bi-Words\t\t\t\t\tFiles\n");
    writer.write("\n");
    for (String biWord : biWordsList) {
        String[] parts = biWord.split("\t");
        String biword = parts[0];
        Set<Integer> documents = biWordsByFile.get(biword);
        StringJoiner documentNumbers = new StringJoiner(", ");
        if (documents != null) {
            for (int docNumber = 1; docNumber <= filePathsBiWord.length; docNumber++) {
                if (documents.contains(docNumber)) {
                    documentNumbers.add(Integer.toString(docNumber));
                }
            }
        }
        String paddedBiWord = String.format("%-50s", biword);
        String paddedDocNumbers = String.format("%-10s", documentNumbers.toString());
        writer.write(paddedBiWord + "\t\t\t\t" + paddedDocNumbers + "\n"); // Adjusted formatting
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Query the bi-word index for "moust like"
String queryBiWord = "mouse like";
Set<Integer> matchingDocuments = new HashSet<>();
for (String biWord : biWordsByFile.keySet()) {
    if (biWord.equals(queryBiWord)) {
        matchingDocuments.addAll(biWordsByFile.get(biWord));
    }
}

// Output the matching documents
if (matchingDocuments.isEmpty()) {
    System.out.println("No documents found for bi-word: " + queryBiWord);
} else {
    System.out.println("Documents containing bi-word \"" + queryBiWord + "\": " + matchingDocuments);
}
// Output the matching documents to the same file
try (FileWriter writer = new FileWriter("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/bi_word_index.txt", true)) {
    if (matchingDocuments.isEmpty()) {
        writer.write("No documents found for bi-word: " + queryBiWord + "\n");
    } else {
        writer.write("Documents containing bi-word \"" + queryBiWord + "\": " + matchingDocuments + "\n");
    }
} catch (IOException e) {
    e.printStackTrace();
}
}
} */