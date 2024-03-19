import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TotalOccurenceMatrix {

    private static String[] wordsToDelete = {"a", "am", "and", "are", "be", "could", "do", "I", "if", "in", "let", "may", "me", "not",
            "on", "or", "so", "that", "the", "them", "they", "will", "with", "would", "you"};

    private static String[] filepaths = new String[32];
    //private static List<List<String>> wordsForFiles = new ArrayList<>();
    private static Set<String> uniqueWordsSet = new HashSet<>();
    private static List<String> uniqueWordsList = new ArrayList<>();
    private static int[][] wordOccurrences;
    private static List<TermFrequency> nonZeroTermFrequencies = new ArrayList<>(); // List to store non-zero term frequencies

    // Method to calculate Term Frequency (TF) for a term in a document
    public static double calculateTermFrequency(int termIndex, int documentIndex) {
        int termFrequency = wordOccurrences[termIndex][documentIndex];
        int totalTermsInDocument = 0;
    
        // Calculate total terms in the document
        for (int i = 0; i < uniqueWordsList.size(); i++) {
            totalTermsInDocument += wordOccurrences[i][documentIndex];
        }
    
        // Print statement to check term frequency and total terms in document
        System.out.println(" ");  
        System.out.println("Term frequency for term " + uniqueWordsList.get(termIndex) + " in document " + (documentIndex + 1) + ": " + termFrequency);
        System.out.println("Total terms in document " + (documentIndex + 1) + ": " + totalTermsInDocument);
    
        // Calculate Term Frequency
        double tf = 0.0;
        if (totalTermsInDocument != 0) {
            tf = (double) termFrequency / totalTermsInDocument;
    
            // Store the non-zero term frequency
            if (termFrequency != 0) {
                String term = uniqueWordsList.get(termIndex);
                nonZeroTermFrequencies.add(new TermFrequency(term, documentIndex, tf));
            }
        }
    
        return tf;
    }
    

    private static double[] calculateIDF() {
        double[] idfValues = new double[uniqueWordsList.size()];
        for (int i = 0; i < uniqueWordsList.size(); i++) {
            String term = uniqueWordsList.get(i);
            int documentFrequency = getDocumentFrequency(term);
            double idf = documentFrequency == 0 ? 0 : 1.0 / documentFrequency;
            idfValues[i] = idf;
    
            // Print statement to check IDF calculation
            //System.out.println("IDF for term '" + term + "': " + idf);
            //System.err.println("AHHHHHHHHH");
        }
        return idfValues;
    }
    

    public static double calculateTFIDF(int termIndex, int documentIndex, double[] idfValues) {
        // Calculate TF for the term in the document
        double tf = calculateTermFrequency(termIndex, documentIndex);
    
        // Get the IDF value for the term
        double idf = idfValues[termIndex];
    
        // Calculate TF-IDF
        double tfidf = tf * idf;
    
        return tfidf;
    }
    
    private static int getDocumentFrequency(String term) {
        int frequency = 0;
        int termIndex = uniqueWordsList.indexOf(term);
        if (termIndex != -1) {
            for (int j = 0; j < filepaths.length; j++) {
                frequency += wordOccurrences[termIndex][j] > 0 ? 1 : 0;
            }
        }
        return frequency;
    }

    private static void processFiles() {
        ReadFile reader = new ReadFile();
        List<String[]> fileContentsList = reader.readFiles(filepaths);
        List<List<String>> wordsForFiles = new ArrayList<>(); // Declaration of wordsForFiles

        for (String[] fileLines : fileContentsList) {
            if (fileLines == null) {
                System.out.println("Error: File content is null");
                continue;
            }

            List<String> wordsForFile = new ArrayList<>(); // Declaration of wordsForFile
            for (String line : fileLines) {
                // Split the line into words
                String[] words = line.toLowerCase().split("\\s+");
                for (String word : words) {
                    // Remove punctuation including hyphens
                    word = word.replaceAll("[^a-zA-Z\\s-]", "");
                    // Check if the word is not a stop word and is not empty
                    if (!Arrays.asList(wordsToDelete).contains(word) && !word.isEmpty()) {
                        // Split hyphenated words and add individual words to the list
                        String[] subWords = word.split("-");
                        for (String subWord : subWords) {
                            if (!subWord.isEmpty() && !Arrays.asList(wordsToDelete).contains(subWord)) {
                                wordsForFile.add(subWord);
                            }
                        }
                    }
                }
            }

            // Add the filtered words to the set of unique words
            uniqueWordsSet.addAll(wordsForFile);
            wordsForFiles.add(wordsForFile);
        }

        // Add unique words to the list and sort
        uniqueWordsList.addAll(uniqueWordsSet);
        Collections.sort(uniqueWordsList);

        // Populate the word occurrences matrix
        wordOccurrences = new int[uniqueWordsList.size()][filepaths.length];

        for (int i = 0; i < filepaths.length; i++) {
            for (int j = 0; j < wordsForFiles.get(i).size(); j++) {
                String word = wordsForFiles.get(i).get(j);
                int index = uniqueWordsList.indexOf(word);
                if (index != -1) {
                    wordOccurrences[index][i]++;
                }
            }
        }
        
    }

    private static void writeTotalTermsPerDocument() {
        try (FileWriter writer = new FileWriter("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/TotalTermsPerDocument.txt")) {
            writer.write("________________________________________________Total Terms Per Document_______________________________________________\n");
            writer.write("\n");
            writer.write("Document,d\tN(d)\n");
            for (int j = 0; j < filepaths.length; j++) {
                int totalTerms = 0;
                String fileName = filepaths[j].substring(filepaths[j].lastIndexOf("/") + 1);
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
                for (int i = 0; i < uniqueWordsList.size(); i++) {
                    totalTerms += wordOccurrences[i][j];
                }
                String output = String.format("%-10s\t%d\n", fileName + ".txt", totalTerms);
                writer.write(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeDocumentFrequency() {
        try (FileWriter termFrequencyWriter = new FileWriter("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/DocumentFrequency.txt")) {
            termFrequencyWriter.write("________________________________________________Document Frequency_______________________________________________\n");
            termFrequencyWriter.write("\n");
            termFrequencyWriter.write("Term,t\tDF(t)\n");
            for (String term : uniqueWordsList) {
                int frequency = 0;
                for (int j = 0; j < filepaths.length; j++) {
                    frequency += wordOccurrences[uniqueWordsList.indexOf(term)][j];
                }
                String output = String.format("%s\t%d\n", term, frequency);
                termFrequencyWriter.write(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeInvertedMatrix() {
        try (FileWriter invertedMatrixWriter = new FileWriter("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/InvertedMatrix.txt")) {
            invertedMatrixWriter.write("________________________________________________Inverted Matrix_______________________________________________\n");
            invertedMatrixWriter.write("\n");
            invertedMatrixWriter.write("Term,t\tPosting List <DocID, TF>\n");

            for (int i = 0; i < uniqueWordsList.size(); i++) {
                String term = uniqueWordsList.get(i);
                StringBuilder postingsBuilder = new StringBuilder();

                for (int j = 0; j < filepaths.length; j++) {
                    int frequency = wordOccurrences[i][j];
                    if (frequency > 0) {
                        String fileName = filepaths[j].substring(filepaths[j].lastIndexOf("/") + 1);
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));
                        postingsBuilder.append(", <").append(j + 1).append(",").append(frequency).append(">");
                    }
                }
                String postings = postingsBuilder.toString().replaceFirst(", ", "");
                invertedMatrixWriter.write(term + ": " + postings + "\n");
                System.out.println(term + ": " + postings);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Posting> queryInvertedIndex(String query) {
        List<Posting> result = new ArrayList<>();
        String[] queryTerms = query.split("\\s+");

        for (String term : queryTerms) {
            int termIndex = uniqueWordsList.indexOf(term);
            if (termIndex != -1) {
                for (int j = 0; j < filepaths.length; j++) {
                    int frequency = wordOccurrences[termIndex][j];
                    if (frequency > 0) {
                        String fileName = filepaths[j].substring(filepaths[j].lastIndexOf("/") + 1);
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));
                        Posting posting = new Posting(fileName, frequency);
                        result.add(posting);
                    }
                }
            }
        }

        return result;
    }

    
    
    
    

    public static void writeTop5ResultsToFile(List<TFIDFResult> results, String outputFile) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("Doc ID\tTF-IDF Sum\n");
            for (TFIDFResult result : results) {
                writer.write(result.getDocumentId() + "\t" + result.getTFIDFSum() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    

    
    static class Posting {
        private String documentId;
        private int frequency;

        public Posting(String documentId, int frequency) {
            this.documentId = documentId;
            this.frequency = frequency;
        }

        public String getDocumentId() {
            return documentId;
        }

        public int getFrequency() {
            return frequency;
        }
    }

    static class TermFrequency {
        private String term;
        private int documentIndex;
        private double frequency;

        public TermFrequency(String term, int documentIndex, double frequency) {
            this.term = term;
            this.documentIndex = documentIndex;
            this.frequency = frequency;
        }

        public String getTerm() {
            return term;
        }

        public int getDocumentIndex() {
            return documentIndex;
        }

        public double getFrequency() {
            return frequency;
        }
    }

    static class TFIDFResult implements Comparable<TFIDFResult> {
        private int documentId;
        private double tfidf;
        private double tfidfSum; // Add a field to store the sum of TF-IDF scores
    
        public TFIDFResult(int documentId, double tfidf) {
            this.documentId = documentId;
            this.tfidf = tfidf;
        }
    
        public int getDocumentId() {
            return documentId;
        }
    
        public double getTFIDF() {
            return tfidf;
        }
    
        public double getTFIDFSum() {
            System.out.println("TF-IDF Sum for document " + documentId + ": " + tfidfSum);
            return tfidfSum;
        }
    
        public void addTFIDF(double tfidf) {
            tfidfSum += tfidf; // Add each TF-IDF score to the sum
        }
    
        @Override
        public int compareTo(TFIDFResult other) {
            return Double.compare(this.tfidfSum, other.tfidfSum); // Compare by sum of TF-IDF scores
        }
    }
    
             
    public static void main(String[] args) {
        // Initialize file paths
        for (int i = 1; i <= 32; i++) {
            filepaths[i - 1] = "/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/eggs2/" + i + ".txt";
        }
    
        // Read files and process words
        processFiles();
    
        // Write the output of the Total Term Occurrence per document to a file
        writeTotalTermsPerDocument();
    
        // Write the output of the Document Frequency to a file
        writeDocumentFrequency();
    
        // Write the output of the Inverted Matrix to a file and also print it to console
        writeInvertedMatrix();
    
        // Calculate IDF
        double[] idfValues = calculateIDF();
    
        // Print IDF values
        System.out.println("IDF Values:");
        for (int i = 0; i < idfValues.length; i++) {
            System.out.println("IDF for term '" + uniqueWordsList.get(i) + "': " + idfValues[i]);
        }
    
        // Example query
        String query = "eat";
        List<Posting> queryResult = queryInvertedIndex(query);
        System.out.println(" ");
        System.out.println("Query result for term '" + query + "':");
        for (Posting posting : queryResult) {
            System.out.println(posting.getDocumentId() + ": " + posting.getFrequency());
        }
        System.out.println(" ");
    
        // Calculate IDF for the query term
        int termIndex = uniqueWordsList.indexOf(query);
        if (termIndex != -1) { // Check if the query term exists in the uniqueWordsList
            double idf = idfValues[termIndex];
            System.out.println("IDF for query term '" + query + "': " + idf);
    
            // Create a map to store TF-IDF results
            Map<Integer, Double> tfidfResults = new HashMap<>();
    
            // Calculate TF-IDF for the query term in documents that contain the term
            for (int documentIndex = 0; documentIndex < filepaths.length; documentIndex++) {
                int termFrequency = wordOccurrences[termIndex][documentIndex];
                if (termFrequency > 0) { // Check if the document contains the queried term
                    double tfidf = calculateTFIDF(termIndex, documentIndex, idfValues);
                    System.out.println("Calculating TF-IDF for term '" + query + "' in document " + (documentIndex + 1) + ":");
                    System.out.println("Term frequency in document: " + termFrequency);
                    System.out.println("IDF for term '" + query + "': " + idfValues[termIndex]);
                    System.out.println("TF-IDF for term '" + query + "' in document " + (documentIndex + 1) + ": " + tfidf);
                    tfidfResults.put(documentIndex + 1, tfidf);
                }
            }
    
            // Print TF-IDF results before sorting
            System.out.println("TF-IDF results before sorting:");
            for (Map.Entry<Integer, Double> entry : tfidfResults.entrySet()) {
                int documentId = entry.getKey();
                double tfidf = entry.getValue();
                System.out.println("Document ID: " + documentId + ", TF-IDF: " + tfidf);
            }
            System.out.println(" ");
    
            // Sort TF-IDF results in descending order
            List<Map.Entry<Integer, Double>> sortedResults = new ArrayList<>(tfidfResults.entrySet());
            sortedResults.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));
    
            // Print sorted TF-IDF results
            System.out.println("Sorting TF-IDF results in descending order:");
            for (Map.Entry<Integer, Double> entry : sortedResults) {
                int documentId = entry.getKey();
                double tfidf = entry.getValue();
                System.out.println("Document ID: " + documentId + ", TF-IDF: " + tfidf);
            }
            System.out.println(" ");
    
            // Output the top 5 documents along with their TF-IDF scores
            System.out.println("Top 5 documents for query '" + query + "':");
            for (int i = 0; i < Math.min(5, sortedResults.size()); i++) {
                Map.Entry<Integer, Double> entry = sortedResults.get(i);
                int documentId = entry.getKey();
                double tfidf = entry.getValue();
                System.out.println("Document ID: " + documentId + ", TF-IDF: " + tfidf);
            }
    
            // Write TF-IDF results to a file
            try (FileWriter TFIDFwriter = new FileWriter("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/SingleWordQuery.txt")) {
                TFIDFwriter.write("______________________________TFIDF Results from Single Word Query____________________________\n");
                TFIDFwriter.write("\n");
                TFIDFwriter.write("DocID\tTF-IDF(Q, docID)\n");
                int count = 0;
                for (Map.Entry<Integer, Double> entry : sortedResults) {
                    if (count >= 5) break; // Write only the top 5 scores
                    int documentId = entry.getKey();
                    double tfidf = entry.getValue();
                    TFIDFwriter.write(documentId + "\t" + tfidf + "\n");
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Query term '" + query + "' not found in the processed documents.");
        }
    
        // Example phrase query
        String phraseQuery = "dark green house";
        // Split the query into individual terms
        String[] queryTerms = phraseQuery.split("\\s+");
    
        // Create a map to store the sum of TF-IDF scores for each document
        Map<Integer, Double> documentScores = new HashMap<>();
    
        // Calculate TF-IDF scores for each term in each document
        for (String term : queryTerms) {
            termIndex = uniqueWordsList.indexOf(term);
            if (termIndex != -1) { // Check if the query term exists in the uniqueWordsList
                // Calculate TF-IDF for the query term in documents that contain the term
                for (int documentIndex = 0; documentIndex < filepaths.length; documentIndex++) {
                    int termFrequency = wordOccurrences[termIndex][documentIndex];
                    if (termFrequency > 0) { // Check if the document contains the queried term
                        // Calculate TF-IDF
                        double tfidf = calculateTFIDF(termIndex, documentIndex, idfValues);
                        // Add TF-IDF to the sum for the corresponding document
                        documentScores.put(documentIndex, documentScores.getOrDefault(documentIndex, 0.0) + tfidf);
                    }
                }
            } else {
                System.out.println("Query term '" + term + "' not found in the processed documents.");
            }
        }

        // Sort TF-IDF results in descending order
        List<Map.Entry<Integer, Double>> sortedDocuments = new ArrayList<>(documentScores.entrySet());
        sortedDocuments.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));
    
        // Output the top 5 documents along with their TF-IDF scores
        System.out.println("Top 5 documents for query '" + phraseQuery + "':");
        for (int i = 0; i < Math.min(5, sortedDocuments.size()); i++) {
            Map.Entry<Integer, Double> entry = sortedDocuments.get(i);
            int documentIndex = entry.getKey();
            double tfidfSum = entry.getValue();
            System.out.println("Document ID: " + (documentIndex + 1) + ", TF-IDF Sum: " + tfidfSum);
        }

        // Write TF-IDF results to a file
        try (FileWriter TFIDFwriter = new FileWriter("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/PhraseQuery.txt")) {
            TFIDFwriter.write("_______________________________TFIDF Results from Phrase Query________________________\n");
            TFIDFwriter.write("\n");
            TFIDFwriter.write("DocID\tTF-IDF(Q, docID)\n");
            int count = 0;
            for (Map.Entry<Integer, Double> entry : sortedDocuments) {
                if (count >= 5) break; // Write only the top 5 scores
                int documentId = entry.getKey();
                double tfidf = entry.getValue();
                TFIDFwriter.write(documentId + "\t" + tfidf + "\n");
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
