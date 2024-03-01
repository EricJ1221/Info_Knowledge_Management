
        
        
        /*String[] wordsToDelete = {"a", "am", "and", "are", "be", "could", "do", "I", "if", "in", "let", "may", "me", "not",
                "on", "or", "so", "that", "the", "them", "they", "will", "with", "would", "you"};

        // Convert all words to lowercase
        for (int i = 0; i < wordsToDelete.length; i++) {
            wordsToDelete[i] = wordsToDelete[i].toLowerCase();
        }

        // Array of file paths
        String[] filepaths = new String[32];
        for (int i = 1; i <= 32; i++) {
            filepaths[i - 1] = "/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/eggs2/" + i + ".txt";
        }

        // Call the readFiles method from ReadFile class to read all files
        ReadFile reader = new ReadFile();
        List<String[]> fileContentsList = reader.readFiles(filepaths);

        // List to store words for each file
        List<List<String>> wordsForFiles = new ArrayList<>();

        // Set to store unique words across all files
        Set<String> uniqueWordsSet = new HashSet<>();

        // Loop through each file's contents and apply logic to delete words and remove punctuation
        for (String[] fileLines : fileContentsList) {
            if (fileLines == null) {
                System.out.println("Error: File content is null");
                continue;
            }

            List<String> wordsForFile = new ArrayList<>(); // List to store words for this file
            for (String line : fileLines) {
                String[] words = line.replaceAll("[^a-zA-Z\\s-]", "").toLowerCase().split("\\s+"); // Remove punctuation and split line into words
                for (String word : words) {
                    if (word.contains("-")) { // Check if word contains a hyphen
                        // Split word further into parts and add them separately to the list
                        wordsForFile.addAll(Arrays.asList(word.split("-")));
                    } else {
                        wordsForFile.add(word); // Add word as is to the list
                    }
                }
            }
            // Remove wordsToDelete from the list
            wordsForFile.removeAll(Arrays.asList(wordsToDelete));
            // Remove duplicates from the list
            uniqueWordsSet.addAll(wordsForFile);
            // Add words to the list of words for each file
            wordsForFiles.add(wordsForFile);
        }

        // Convert the set of unique words to a sorted list
        List<String> uniqueWordsList = new ArrayList<>(uniqueWordsSet);
        Collections.sort(uniqueWordsList);

        // Array of file paths
String[] numericalFilepaths = new String[16];
for (int i = 1; i <= 16; i++) {
    String number = String.format("%02d", i); // Pad the number with leading zeros
    filepaths[i - 1] = "/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/eggs2/" + number + ".txt";
}


        // Initialize the incidence matrix
        int[][] incidenceMatrix = new int[uniqueWordsList.size()][filepaths.length];

        // Populate the incidence matrix
        for (int i = 0; i < uniqueWordsList.size(); i++) {
            String word = uniqueWordsList.get(i);
            for (int j = 0; j < filepaths.length; j++) {
                List<String> wordsForFile = wordsForFiles.get(j);
                if (wordsForFile.contains(word)) {
                    incidenceMatrix[i][j] = 1;
                } else {
                    incidenceMatrix[i][j] = 0;
                }
            }
        }

            // Print the incidence matrix
            System.out.println("________________________________________________Incidence Matrix_______________________________________________");
            System.out.print("  ");
            for (int j = 0; j < filepaths.length; j++) {
                String filepath = filepaths[j];
                if (filepath.endsWith("01.txt")) {
                    System.out.print("_____" + String.format("%-6s ", filepath.substring(filepath.lastIndexOf("/") + 1))); // Adjust the width for "01.txt"
                } else {
                    System.out.print(String.format("%-4s_", filepath.substring(filepath.lastIndexOf("/") + 1)));
                }
            }
            System.out.println();
            for (int i = 0; i < uniqueWordsList.size(); i++) {
                System.out.print(String.format("%-8s", uniqueWordsList.get(i)));
                for (int j = 0; j < filepaths.length; j++) {
                    if (filepaths[j].endsWith("01.txt")) {
                        System.out.print(String.format("%-7d", incidenceMatrix[i][j])); // Adjust the width for "01.txt"
                    } else {
                        System.out.print(String.format("%-7d", incidenceMatrix[i][j]));
                    }
                }
                System.out.println();
            }



            System.out.println();
            System.out.println();
            System.out.println();

        // Initialize the inverted matrix
        String[][] invertedMatrix = new String[uniqueWordsList.size()][2];

        // Populate the inverted matrix
        for (int i = 0; i < uniqueWordsList.size(); i++) {
            String word = uniqueWordsList.get(i);
            StringBuilder fileNames = new StringBuilder();
            for (int j = 0; j < filepaths.length; j++) {
                List<String> wordsForFile = wordsForFiles.get(j);
                if (wordsForFile.contains(word)) {
                    String fileName = filepaths[j].substring(filepaths[j].lastIndexOf("/") + 1);
                    fileName = fileName.substring(0, fileName.lastIndexOf(".")); // Remove the file extension
                    fileNames.append(fileName).append(", ");
                }
            }
            // Remove the last comma and space from the fileNames string
            if (fileNames.length() > 0) {
                fileNames.setLength(fileNames.length() - 2);
            }
            invertedMatrix[i][0] = word;
            invertedMatrix[i][1] = fileNames.toString();
}

       // Print the inverted matrix
        System.out.println("________________________________________________Inverted Matrix_______________________________________________");
        System.out.println("Unique Words                   \tFiles");
        System.out.println("");
        for (int i = 0; i < uniqueWordsList.size(); i++) {
            System.out.println(invertedMatrix[i][0] + "\t\t" + invertedMatrix[i][1]);
        }

        // Create the file for the inverted matrix and write the matrix into it
        try (FileWriter writer = new FileWriter("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/inverted_matrix.txt")) {
            writer.write("________________________________________________Inverted Matrix_______________________________________________\n");
            writer.write("\n");
            writer.write("Unique Words\t\t\t\t\tFiles\n");
            writer.write("\n");
            for (int i = 0; i < uniqueWordsList.size(); i++) {
                writer.write(String.format("%-25s", invertedMatrix[i][0]) + "\t" + invertedMatrix[i][1] + "\n"); // Adjusted formatting
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create the file for the incidence matrix and write the matrix into it
        try (FileWriter writer = new FileWriter("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/incidence_matrix.txt")) {
            writer.write("________________________________________________Incidence Matrix_______________________________________________\n");
            writer.write("\n");
            for (int j = 0; j < filepaths.length; j++) {
                String filepath = filepaths[j];
                if (filepath.endsWith("01.txt")) {
                    writer.write("_____" + String.format("%-6s ", filepath.substring(filepath.lastIndexOf("/") + 1))); // Adjust the width for "01.txt"
                } else {
                    writer.write(String.format("%-4s_", filepath.substring(filepath.lastIndexOf("/") + 1)));
                }
            }
            writer.write("\n");
            writer.write("\n");
            for (int i = 0; i < uniqueWordsList.size(); i++) {
                writer.write(String.format("%-8s", uniqueWordsList.get(i)));
                for (int j = 0; j < filepaths.length; j++) {
                    if (filepaths[j].endsWith("01.txt")) {
                        writer.write(String.format("%-7d", incidenceMatrix[i][j])); // Adjust the width for "01.txt"
                    } else {
                        writer.write(String.format("%-7d", incidenceMatrix[i][j]));
                    }
                }
                writer.write("\n");
            }

            writer.write("\n"); // Add a blank line here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
/* 


// Calling biwordindexer in separate class, need to trouble shoot later, wasn't working correctly. Have to move on to the other problems atm.
/* 
BiWordIndexer biWordIndexer = new BiWordIndexer();
        String[] filePathsBiWord = new String[32];
        for (int i = 1; i <= 32; i++) {
            filePathsBiWord[i - 1] = "/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/eggs2/" + i + ".txt";
        }
        biWordIndexer.buildBiWordIndex(filePathsBiWord);
        biWordIndexer.writeBiWordIndexToFile("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/bi_word_index.txt");
        Set<Integer> matchingDocuments = biWordIndexer.searchBiWordIndex("mouse like");
        if (matchingDocuments.isEmpty()) {
            System.out.println("No documents found for bi-word: mouse like");
        } else {
            System.out.println("Documents containing bi-word \"mouse like\": " + matchingDocuments);
        }
    }
}
*/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;




public class Main {
    public static void main(String[] args) {



// // Create a PermutermIndex object
PermutermIndex permutermIndex = new PermutermIndex();

// Array of file paths
String[] filePathsPermutermWords = new String[32];
for (int i = 1; i <= 32; i++) {
    filePathsPermutermWords[i - 1] = "/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/eggs2/" + i + ".txt";
}

// Read files using ReadFile class
ReadFile readFile = new ReadFile();
List<String[]> fileContentsList = readFile.readFiles(filePathsPermutermWords);

// Insert words into the index and verify document mapping
for (int i = 1; i <= fileContentsList.size(); i++) {
    for (String line : fileContentsList.get(i - 1)) {
        String[] words = line.split("\\s+");
        for (String word : words) {
            // Extract the document name from the file path
            String documentName = filePathsPermutermWords[i - 1].substring(
                filePathsPermutermWords[i - 1].lastIndexOf('/') + 1
            );
            permutermIndex.insertWord(word, word, documentName);  // Pass document name to insertWord
        }
    }
}

// Perform wildcard search on the index
// You can change the query to "in*" if you want to search for words that end with "in"
String query = "*in";

Set<String> matchingWords = permutermIndex.wildcardSearch(query);
System.out.println("Words matching query " + query + " : " + matchingWords);
System.out.println("Matching words size: " + matchingWords.size());
System.out.println("Matching words: " + matchingWords);



Set<String> matchingDocuments = new HashSet<>();
// Retrieve documents for each matching word
for (String word : matchingWords) {
    Set<String> documents = permutermIndex.getDocumentsForWord(word);
    System.out.println("Documents for word " + word + ": " + documents);
    matchingDocuments.addAll(permutermIndex.getDocuments(word));
}
List<String> sortedDocuments = new ArrayList<>(matchingDocuments);

// Sort the list using natural order (least to greatest)
Collections.sort(sortedDocuments);
System.out.println("Documents for query " + query + ": " + sortedDocuments);
   
// Write the permuterm index to a file
String filePath = "/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/Permuterm_index.txt";
permutermIndex.writeIndexToFile(filePath);

// Append the sorted documents to the file
try (FileWriter writer = new FileWriter(filePath, true)) {
    writer.write("\n");
    writer.write("Sorted documents for query " + query + ":\n");
    for (String document : sortedDocuments) {
        writer.write(document + "\n");
    }
} catch (IOException e) {
    e.printStackTrace();
}

    }
    
}