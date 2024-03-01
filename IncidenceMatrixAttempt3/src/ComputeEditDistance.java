import java.io.FileWriter;
import java.io.IOException;

public class ComputeEditDistance {
    public static int[][] distance(String[] arr1, String[] arr2) {
        int m = arr1.length;
        int n = arr2.length;

        // Initialize the 2D array to store edit distances
        int[][] dp = new int[m + 1][n + 1];

        // Initialize the first row and column
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // Fill in the rest of the matrix
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = arr1[i - 1].equals(arr2[j - 1]) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        // The distance is the value in the bottom right cell
        return dp;
    }

    public static void printMatrix(String[] arr1, String[] arr2, int[][] matrix) {
        int m = arr1.length;
        int n = arr2.length;

        // Print header row
        System.out.print("    ");
        for (String word : arr2) {
            System.out.print(word + " ");
        }
        System.out.println();

        // Print matrix
        for (int i = 0; i <= m; i++) {
            if (i > 0) {
                System.out.print(arr1[i - 1] + " ");
            } else {
                System.out.print("  ");
            }

            for (int j = 0; j <= n; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void writeMatrix(String[] arr1, String[] arr2, int[][] matrix, FileWriter writer) {
        int m = arr1.length;
        int n = arr2.length;
    
        // Print header row
        try {
            writer.write("    ");
            for (String word : arr2) {
                writer.write(word + " ");
            }
            writer.write("\n");
    
            // Print matrix
            for (int i = 0; i <= m; i++) {
                if (i > 0) {
                    writer.write(arr1[i - 1] + " ");
                } else {
                    writer.write("  ");
                }
    
                for (int j = 0; j <= n; j++) {
                    writer.write(matrix[i][j] + " ");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String query = "trie";
        String[] words = {"there", "tree", "try"};
        System.out.println(" ");
        for (String word : words) {
            System.out.println("Distance Matrix for '" + query + "' and '" + word + "':");
            int[][] distanceMatrix = distance(query.split(""), word.split(""));
            printMatrix(query.split(""), word.split(""), distanceMatrix);
            System.out.println(" ");
            // Calculate the edit distance separately
            int editDistance = distanceMatrix[query.length()][word.length()];
            System.out.println("Edit distance = " + editDistance);
            System.out.println();
        }
    
        try (FileWriter writer = new FileWriter("/Users/ericoliver/Desktop/InfoKnowledgeManagementAssignment1/EditDistanceMatrix.txt")) {
            for (String word : words) {
                writer.write("Distance Matrix for '" + query + "' and '" + word + "':\n");
                int[][] distanceMatrix = distance(query.split(""), word.split(""));
                writeMatrix(query.split(""), word.split(""), distanceMatrix, writer);
    
                // Calculate the edit distance separately
                int editDistance = distanceMatrix[query.length()][word.length()];
                writer.write("Edit distance: " + editDistance + "\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
}

