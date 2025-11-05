import java.util.*;

class FirstFit {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Input: Number of memory blocks
        System.out.print("Enter number of memory blocks: ");
        int nb = sc.nextInt();
        int[] block = new int[nb];     // Initial block sizes
        int[] rem = new int[nb];       // Remaining sizes after allocation

        System.out.println("Enter block sizes:");
        for (int i = 0; i < nb; i++) {
            block[i] = sc.nextInt();
            rem[i] = block[i]; // Initially, remaining = full block size
        }

        // Input: Number of processes
        System.out.print("Enter number of processes: ");
        int np = sc.nextInt();
        int[] process = new int[np];   // Process sizes
        int[] alloc = new int[np];     // Allocation index (-1 if not allocated)

        System.out.println("Enter process sizes:");
        for (int i = 0; i < np; i++) {
            process[i] = sc.nextInt();
            alloc[i] = -1; // Default: not allocated
        }

        // First Fit Allocation Logic
        for (int i = 0; i < np; i++) {
            for (int j = 0; j < nb; j++) {
                if (rem[j] >= process[i]) {
                    alloc[i] = j;           // Allocate block j to process i
                    rem[j] -= process[i];   // Reduce remaining size
                    break;                  // Move to next process
                }
            }
        }

        // Output: Allocation Table
        System.out.println("\nProcess\tSize\tBlock Allocated");
        for (int i = 0; i < np; i++) {
            if (alloc[i] != -1)
                System.out.println("P" + (i + 1) + "\t" + process[i] + "\tBlock " + (alloc[i] + 1));
            else
                System.out.println("P" + (i + 1) + "\t" + process[i] + "\tNot Allocated");
        }

        // Output: Fragmentation Report
        System.out.println("\nBlock\tInitial Size\tRemaining\tFragmentation");
        int totalFrag = 0;
        for (int i = 0; i < nb; i++) {
            System.out.println("B" + (i + 1) + "\t" + block[i] + "\t\t" + rem[i] + "\t\t" + rem[i]);
            totalFrag += rem[i];
        }

        System.out.println("\nTotal Fragmented/Unused Memory: " + totalFrag);
    }
}
