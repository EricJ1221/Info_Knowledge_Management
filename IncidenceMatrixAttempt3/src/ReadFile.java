import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadFile {
    public List<String[]> readFiles(String[] filepaths) {
        List<String[]> fileContentsList = new ArrayList<>();

        for (String filepath : filepaths) {
            String[] lines = readFile(filepath);
            fileContentsList.add(lines);
        }

        return fileContentsList;
    }

    private String[] readFile(String filePath) {
        String[] lines = null; // Initialize lines to null
        BufferedReader reader = null;
        try {
            // Open the file and count the number of lines
            reader = new BufferedReader(new FileReader(filePath));
            List<String> linesList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                linesList.add(line);
            }
            lines = linesList.toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        } finally {
            // Close the reader in the finally block
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
        return lines;
    }
}
