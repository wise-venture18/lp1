// Sample Input :
// Enter number of frames: 3
// Enter number of pages: 5
// Enter page reference string:
// 1 2 3 4 5

import java.util.*;

public class Optimal_PageReplacement {
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

        int[] frame = new int[frames]; // Frame array
        Arrays.fill(frame, -1);        // Initialize frames to -1 (empty)

        int pageFaults = 0;

        // Optimal Page Replacement Logic
        for (int i = 0; i < n; i++) {
            int page = pages[i];
            boolean hit = false;

            // Check if page is already in frame
            for (int j = 0; j < frames; j++) {
                if (frame[j] == page) {
                    hit = true;
                    break;
                }
            }

            // If page fault occurs
            if (!hit) {
                int replaceIndex = -1;
                int farthest = -1;

                // Check for free frame
                for (int j = 0; j < frames; j++) {
                    if (frame[j] == -1) {
                        replaceIndex = j;
                        break;
                    }
                }

                // If no free frame, find the page used farthest in future
                if (replaceIndex == -1) {
                    for (int j = 0; j < frames; j++) {
                        int nextUse = Integer.MAX_VALUE;
                        for (int k = i + 1; k < n; k++) {
                            if (frame[j] == pages[k]) {
                                nextUse = k;
                                break;
                            }
                        }
                        if (nextUse > farthest) {
                            farthest = nextUse;
                            replaceIndex = j;
                        }
                    }
                }

                // Replace the selected page
                frame[replaceIndex] = page;
                pageFaults++;
            }

            // Display current frame status
            System.out.print("Frames: ");
            for (int f : frame) {
                System.out.print((f == -1 ? "-" : f) + " ");
            }
            System.out.println();
        }

        // Output total page faults
        System.out.println("\nTotal Page Faults = " + pageFaults);
    }
}
