// Sample Input :
//Enter number of frames: 2
// Enter number of pages: 5
// Enter page reference string:
// 1 2 3 2 1

import java.util.*;

public class FIFO_PageReplacement {
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

        int pointer = 0;               // FIFO pointer
        int pageFaults = 0;

        // Page replacement logic
        for (int page : pages) {
            boolean found = false;

            // Check if page is already in frame
            for (int f : frame) {
                if (f == page) {
                    found = true;
                    break;
                }
            }

            // If not found, replace using FIFO
            if (!found) {
                frame[pointer] = page;
                pointer = (pointer + 1) % frames;
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
