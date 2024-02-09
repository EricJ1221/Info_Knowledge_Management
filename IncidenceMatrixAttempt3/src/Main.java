import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        String[] wordsToDelete = {"a", "am", "and", "are", "be", "could", "do", "I", "if", "in", "let", "may", "me", "not",
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
                System.out.print("_______"+String.format("%-6s ", filepath.substring(filepath.lastIndexOf("/") + 1))); // Adjust the width for "01.txt"
            } else {
                System.out.print(String.format("%-4s__", filepath.substring(filepath.lastIndexOf("/") + 1)));
            }
            }
            System.out.println();
            for (int i = 0; i < uniqueWordsList.size(); i++) {
            System.out.print(String.format("%-11s", uniqueWordsList.get(i)));
            for (int j = 0; j < filepaths.length; j++) {
                if (filepaths[j].endsWith("01.txt")) {
                    System.out.print(String.format("%-8d", incidenceMatrix[i][j])); // Adjust the width for "01.txt"
                } else {
                    System.out.print(String.format("%-8d", incidenceMatrix[i][j]));
                }
            }
            System.out.println();
            }
    }
}

