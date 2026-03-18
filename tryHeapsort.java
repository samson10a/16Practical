//Mosa Nkuna
//4446478
//Consulted:ChatGBT For the reading of the file

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class tryHeapsort {
    public static void main(String[] args) {
        String filename = "ulysses.txt";
        
        
        String[] words = readWordsFromFile(filename);
        if (words.length == 0) {
            System.err.println("No words found in file. Exiting.");
            return;
        }
        
        System.out.println("Total words to sort: " + words.length);
        
    
        String[] wordsForBottomUp = words.clone();
        String[] wordsForTopDown = words.clone();
        
        //Bottom-up timing
        long startTime = System.nanoTime();
        
        Heap heapBottomUp = new Heap(wordsForBottomUp.length);
        heapBottomUp.buildUp(wordsForBottomUp);
        String[] sortedBottomUp = heapBottomUp.sort();
        
        long endTime = System.nanoTime();
        long timeBottomUp = endTime - startTime;
        
        //Topdown timing
        startTime = System.nanoTime();
        
        Heap heapTopDown = new Heap(wordsForTopDown.length);
        for (String word : wordsForTopDown) {
            heapTopDown.insert(word);
        }
        String[] sortedTopDown = heapTopDown.sort();
        
        endTime = System.nanoTime();
        long timeTopDown = endTime - startTime;
        
        // Display results
        System.out.println("\n=== TIMING RESULTS ===");
        System.out.printf("Bottom-up build + sort: %.3f ms%n", timeBottomUp / 1_000_000.0);
        System.out.printf("Top-down build + sort:   %.3f ms%n", timeTopDown / 1_000_000.0);
        
        // Verify first few words are sorted
        System.out.println("\n=== VERIFICATION (first 20 sorted words) ===");
        System.out.println("Bottom-up sort result:");
        for (int i = 0; i < Math.min(20, sortedBottomUp.length); i++) {
            System.out.println((i+1) + ": " + sortedBottomUp[i]);
        }
        
        // Check if sorts match
        boolean match = true;
        for (int i = 0; i < sortedBottomUp.length; i++) {
            if (!sortedBottomUp[i].equals(sortedTopDown[i])) {
                match = false;
                System.out.println("Mismatch at index " + i + 
                    ": " + sortedBottomUp[i] + " vs " + sortedTopDown[i]);
                break;
            }
        }
        System.out.println("\nBoth sorts produced " + 
            (match ? "IDENTICAL" : "DIFFERENT") + " results.");
    }
    
    private static String[] readWordsFromFile(String filename) {
        List<String> wordList = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    wordList.add(line);
                }
            }
            System.out.println("Successfully read " + wordList.size() + " words from " + filename);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        
        return wordList.toArray(new String[0]);
    }
}