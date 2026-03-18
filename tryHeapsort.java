//Mosa Nkuna
//4446478
//Consulted:ChatGBT For the reading of the file

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class tryHeapsort{
    private String[] heap;
    private int size;

    // Constructor
    public tryHeapsort(int capacity) {
        heap = new String[capacity];
        size = 0;
    }

    private void swap(String[] arr, int i, int j) {
        String temp = arr[i];
        arr[i]      = arr[j];
        arr[j]      = temp;
    }

    
    private void heapify(String[] arr, int index, int heapSize) {
        int largest = index;
        int left    = 2 * index + 1;
        int right   = 2 * index + 2;

        if (left  < heapSize && arr[left].compareTo(arr[largest])  > 0) largest = left;
        if (right < heapSize && arr[right].compareTo(arr[largest]) > 0) largest = right;

        if (largest != index) {
            swap(arr, index, largest);
            heapify(arr, largest, heapSize);
        }
    }

    
    private void swim(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[index].compareTo(heap[parent]) > 0) {
                swap(heap, index, parent);
                index = parent;
            } else {
                break;
            }
        }

    //  (1) BUILD HEAP BOTTOM-UP - O(n)
    //  Floyd's algorithm: start at last parent, sift everything down
    public void buildUp(String[] array) {
        for (int i = 0; i < array.length; i++) {
            heap[i] = array[i];
        }
        size = array.length;

        for (int i = (size / 2) - 1; i >= 0; i--) {
            heapify(heap, i, size);
        }
    }

    // ================================================================
    //  (2) BUILD HEAP TOP-DOWN - O(n log n)
    //  Insert one word at a time, sift up after each insert
    // ================================================================
    public void insert(String value) {
        if (size >= heap.length) {
            throw new IllegalStateException("Heap is full");
        }
        heap[size] = value;
        swim(size);
        size++;
    }

    // ================================================================
    //  (3) HEAP SORT - shared by both build methods
    //  Works on a COPY so the original heap is not destroyed
    // ================================================================
    public String[] sort() {
        // Copy heap so we don't destroy the original
        String[] tempHeap = new String[size];
        for (int i = 0; i < size; i++) {
            tempHeap[i] = heap[i];
        }

        // Repeatedly swap root (largest) with last, shrink, re-heapify
        for (int i = tempHeap.length - 1; i > 0; i--) {
            swap(tempHeap, 0, i);
            heapify(tempHeap, 0, i);
        }

        // Result is alphabetical order (smallest to largest)
        return tempHeap;
    }

    // ================================================================
    //  READ + CLEAN WORDS from raw Ulysses text
    //  Strips punctuation, lowercases, splits on whitespace
    // ================================================================
    private static String[] readWordsFromFile(String filename) {
        List<String> wordList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                for (String token : line.trim().split("\\s+")) {
                    String cleaned = token.replaceAll("[^a-zA-Z]", "").toLowerCase();
                    if (!cleaned.isEmpty()) {
                        wordList.add(cleaned);
                    }
                }
            }
            System.out.println("Successfully read " + wordList.size() +
                " words from " + filename);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return wordList.toArray(new String[0]);
    }

    // ================================================================
    //  TIMING UTILITY
    //  Pass true for bottom-up, false for top-down
    // ================================================================
    private static long timeSort(String[] words, boolean bottomUp) {
        tryHeapsort h = new tryHeapsort(words.length);
        long start = System.nanoTime();
        if (bottomUp) {
            h.buildUp(words);
        } else {
            for (String w : words) h.insert(w);
        }
        h.sort();
        return System.nanoTime() - start;
    }

    // ================================================================
    //  MAIN
    // ================================================================
    public static void main(String[] args) {

        // ── PART A: Small test (20 words) ────────────────────────────
        System.out.println("=== SMALL TEST (20 words) ===");

        String[] small = {
            "zebra",  "mango",  "apple",  "heap",   "data",
            "sort",   "java",   "queue",  "node",   "tree",
            "binary", "insert", "delete", "index",  "array",
            "stack",  "list",   "graph",  "search", "path"
        };

        tryHeapsort h1 = new tryHeapsort(small.length);
        h1.buildUp(small.clone());
        String[] sortedBU = h1.sort();

        tryHeapsort h2 = new tryHeapsort(small.length);
        for (String w : small) h2.insert(w);
        String[] sortedTD = h2.sort();

        System.out.println("Bottom-up sorted:");
        for (int i = 0; i < sortedBU.length; i++) {
            System.out.println("  " + (i + 1) + ": " + sortedBU[i]);
        }

        System.out.println("\nTop-down sorted:");
        for (int i = 0; i < sortedTD.length; i++) {
            System.out.println("  " + (i + 1) + ": " + sortedTD[i]);
        }

        boolean smallMatch = true;
        for (int i = 0; i < sortedBU.length; i++) {
            if (!sortedBU[i].equals(sortedTD[i])) { smallMatch = false; break; }
        }
        System.out.println("\nSmall test - both methods match: " + smallMatch);

        // ── PART B: Full Ulysses run ──────────────────────────────────
        System.out.println("\n=== FULL ULYSSES RUN ===");

        String filename = "ulysses.text";
        String[] words = readWordsFromFile(filename);

        if (words.length == 0) {
            System.err.println("No words found. Check the filename path.");
            return;
        }
        System.out.println("Total words loaded: " + words.length);

        // Warm-up run - stops JIT skewing first timing
        timeSort(words.clone(), true);
        timeSort(words.clone(), false);

        // ── Timed runs ────────────────────────────────────────────────
        long startTime = System.nanoTime();
        tryHeapsort heapBU = new tryHeapsort(words.length);
        heapBU.buildUp(words.clone());
        String[] sortedBottomUp = heapBU.sort();
        long timeBottomUp = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        tryHeapsort heapTD = new tryHeapsort(words.length);
        for (String word : words) heapTD.insert(word);
        String[] sortedTopDown = heapTD.sort();
        long timeTopDown = System.nanoTime() - startTime;

        // ── Timing results ────────────────────────────────────────────
        System.out.println("\n=== TIMING RESULTS ===");
        System.out.printf("Bottom-up build + sort: %.3f ms%n", timeBottomUp / 1_000_000.0);
        System.out.printf("Top-down  build + sort: %.3f ms%n", timeTopDown  / 1_000_000.0);

        if (timeBottomUp < timeTopDown) {
            System.out.printf("Bottom-up is faster by %.2fx%n",
                (double) timeTopDown / timeBottomUp);
        } else {
            System.out.printf("Top-down is faster by %.2fx%n",
                (double) timeBottomUp / timeTopDown);
        }

        // ── First 20 sorted words ─────────────────────────────────────
        System.out.println("\n=== FIRST 20 SORTED WORDS (bottom-up) ===");
        for (int i = 0; i < Math.min(20, sortedBottomUp.length); i++) {
            System.out.println("  " + (i + 1) + ": " + sortedBottomUp[i]);
        }

        // ── Verify both methods match ─────────────────────────────────
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
}
