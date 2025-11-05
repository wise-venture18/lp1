// Sample Input :
//Enter number of frames: 3
// Enter number of pages: 5
// Enter page reference string:
// 1 2 3 2 1

import java.util.*;

public class LRU_PageReplacement {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Input: Number of frames
        System.out.print("Enter number of frames: ");
        int frames = sc.nextInt();

        // Input: Number of pages
        System.out.print("Enter number of pages: ");
        int n = sc.nextInt();

        int[] pages = new int[n]; // Page reference string
        System.out.println("Enter page reference string:");
        for (int i = 0; i < n; i++) {
            pages[i] = sc.nextInt();
        }

        ArrayList<Integer> frame = new ArrayList<>(frames); // Frame list
        int pageFaults = 0;

        // LRU Page Replacement Logic
        for (int i = 0; i < n; i++) {
            int page = pages[i];

            if (!frame.contains(page)) {
                if (frame.size() == frames) {
                    int lru = Integer.MAX_VALUE;
                    int indexToRemove = -1;

                    // Find least recently used page
                    for (int f = 0; f < frame.size(); f++) {
                        int lastUsed = -1;
                        for (int j = i - 1; j >= 0; j--) {
                            if (pages[j] == frame.get(f)) {
                                lastUsed = j;
                                break;
                            }
                        }
                        if (lastUsed < lru) {
                            lru = lastUsed;
                            indexToRemove = f;
                        }
                    }

                    frame.set(indexToRemove, page); // Replace LRU page
                } else {
                    frame.add(page); // Add page to frame
                }
                pageFaults++;
            }

            // Display current frame status
            System.out.print("Frames: ");
            for (int f = 0; f < frames; f++) {
                if (f < frame.size()) {
                    System.out.print(frame.get(f) + " ");
                } else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }

        // Output total page faults
        System.out.println("\nTotal Page Faults = " + pageFaults);
    }
}
