import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SoundexIndex {
    private static final Map<Character, Character> encodingMap = new HashMap<>() {{
        put('a', '0');
        put('e', '0');
        put('i', '0');
        put('o', '0');
        put('u', '0');
        put('w', '0');
        put('y', '0');
        put('b', '1');
        put('f', '1');
        put('p', '1');
        put('v', '1');
        put('c', '2');
        put('g', '2');
        put('j', '2');
        put('k', '2');
        put('q', '2');
        put('s', '2');
        put('x', '2');
        put('z', '2');
        put('d', '3');
        put('t', '3');
        put('l', '4');
        put('m', '5');
        put('n', '5');
        put('r', '6');
    }};
    
    private static final Set<String> stopWords = new HashSet<>(Arrays.asList(
        "a", "am", "and", "are", "be", "could", "do", "i", "if", "in", "let", "may", "me", "not",
        "on", "or", "so", "that", "the", "them", "they", "will", "with", "would", "you"
    ));

    public static String encode(String word) {
        word = word.toLowerCase();
        StringBuilder result = new StringBuilder();
        result.append(Character.toUpperCase(word.charAt(0)));
    
        for (int i = 1; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            char encodedChar = encodingMap.getOrDefault(currentChar, '0');
            //if (result.length() == 1 || encodedChar != result.charAt(result.length()-1)) {
                result.append(encodedChar);
            //}
        }
        
        System.out.println("Initial encoded string: " + result.toString());
        //keeps first occurence of non zero digit and removes consecutive non-zero duplicates
        String cleaned = result.toString().replaceAll("([1-6])\\1+", "$1");

        //cleaned = cleaned.replaceAll("([1-6])\\1", "$1");

        //System.out.println("Cleaned encoded string after removing consecutive non-zero duplicates: " + cleaned);
    
        //removes all zeros
        cleaned = cleaned.replaceAll("0", "");
        //system.out.println("Cleaned encoded string after removing zeros: " + cleaned);
    
        //padding zeros added
        cleaned = cleaned + "0".repeat(Math.max(0, 4 - cleaned.length()));
        //System.out.println("Final encoded string with padding: " + cleaned);
        
        return cleaned;
    }
    

    public static Set<String> findSimilarEncodedWords(String query, Map<String, Set<String>> encodedWordsIndex) {
        String encodedQuery = encode(query);
        System.out.println("Encoded query: " + encodedQuery); // Print encoded query word
        Set<String> similarEncodedWords = encodedWordsIndex.getOrDefault(encodedQuery, Collections.emptySet());
    
        return similarEncodedWords;
    }
    

    public void writeIndexToFile(String filePath, Map<String, Set<String>> encodedWordsIndex, String query, Set<String> originalWords) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("________________________________________________Soundex Index_______________________________________________\n");
            writer.write("\n");
            writer.write("Soundex code\t\t\t\t\tVocabulary Terms\n");
            writer.write("\n");
    
            List<String> words = new ArrayList<>(encodedWordsIndex.keySet());
            Collections.sort(words);
            for (String word : words) {
                StringJoiner rotationsJoiner = new StringJoiner(" ");
                for (String rotation : encodedWordsIndex.get(word)) {
                    rotationsJoiner.add(rotation);
                }
                String rotationsString = rotationsJoiner.toString();
    
                String paddedWord = String.format("%2s", word);
                String paddedRotations = String.format("%-2s", rotationsString);
                writer.write(paddedWord + "\t\t\t\t" + paddedRotations + "\n");
            }
            writer.write(" ");
            writer.write("_____________________________Query for " + query + "__________________________\n");
            
            // Calculate the Soundex encoding for the query
            String encodedQuery = encode(query);
            writer.write("Encoded query: " + encodedQuery + "\n");
            writer.write("Original words for '" + query + "': " + originalWords + "\n");
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static String[] tokenizeAndRemoveStopWordsAndClean(String input, String documentName) {
        List<String> tokensList = new ArrayList<>();
        for (String word : input.toLowerCase().split("\\s+")) {
            String cleanedWord = word.replaceAll("[\\p{Punct}&&[^-]]", "").replaceAll("-", " ");
            String[] subtokens = cleanedWord.split("\\s+"); // Split the word into subtokens
            for (String subtoken : subtokens) {
                if (!stopWords.contains(subtoken)) {
                    String phoneticCode = SoundexIndex.encode(subtoken);
                    tokensList.add(subtoken);
                    System.out.println(" ");
                    System.out.println("Document: " + documentName);
                    System.out.println("Token: " + subtoken);
                    System.out.println("Phonetic Code: " + phoneticCode);
                    System.out.println("______________________Tokens________________________________");
                    System.out.println(Arrays.toString(tokensList.toArray()));
                }
            }
        }

        String[] tokens = new String[tokensList.size()];
        tokensList.toArray(tokens);
        return tokens;
    }
    
/// ___________MAIN______________________________MAIN______________________MAIN__________________________________________________________
/// ___________MAIN______________________________MAIN______________________MAIN__________________________________________________________
/// ___________MAIN______________________________MAIN______________________MAIN__________________________________________________________
    public static void main(String[] args) {
       /*System.out.println("_____________________________Test Statements__________________________"); 
        System.out.println(SoundexIndex.encode("Stephen")); // Output: S315
        System.out.println(SoundexIndex.encode("Steven"));  // Output: S315
        System.out.println(SoundexIndex.encode("Anne")); // Output: A500
        System.out.println(SoundexIndex.encode("Ann")); // Output: A500
        System.out.println("_____________________________________________________________________");
*/
        String[] filePathsSoundexTerms = new String[32];
        for (int i = 1; i <= 32; i++) { 
            filePathsSoundexTerms[i - 1] = "/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/eggs2/" + i + ".txt";
        }

        // Create a map to store the index of encoded words
        Map<String, Set<String>> encodedWordsIndex = new HashMap<>();

        for (String filePath : filePathsSoundexTerms) {
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

            try {
                String documentContent = readFile(filePath);
                String[] tokens = tokenizeAndRemoveStopWordsAndClean(documentContent, fileName);
                
                for (String token : tokens) {
                    String encodedWord = SoundexIndex.encode(token);
                    // If the encoded word is not already in the index, create a new set for it
                    encodedWordsIndex.putIfAbsent(encodedWord, new HashSet<>());
                    // Add the original word to the set for the encoded word
                    encodedWordsIndex.get(encodedWord).add(token);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Print the index of encoded words
        for (Map.Entry<String, Set<String>> entry : encodedWordsIndex.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        String  query = "trie";

        System.out.println("_____________________________________________________________________");
        System.out.println("_____________________________Query for "+  query +"__________________________");
        Set<String> originalWords = findSimilarEncodedWords(query, encodedWordsIndex);
        System.out.println("Original words for '" + query + "': " + originalWords);
        

        

    // Create an instance of SoundexIndex
    SoundexIndex soundexIndex = new SoundexIndex();

    // Calculate the Soundex encoding for the query
    String encodedQuery = encode(query);
    originalWords = findSimilarEncodedWords(query, encodedWordsIndex);

    // Your existing code...

    // Write the index to a file
    String filePath = "/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/SoundexIndex.txt";
    soundexIndex.writeIndexToFile(filePath, encodedWordsIndex, query, originalWords);
}
    }

