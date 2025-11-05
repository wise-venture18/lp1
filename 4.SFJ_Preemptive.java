import java.util.*;

class SJFPreemptive {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Input: Number of processes
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        int[] pid = new int[n]; // Process IDs
        int[] at = new int[n];  // Arrival Times
        int[] bt = new int[n];  // Burst Times
        int[] rt = new int[n];  // Remaining Times
        int[] wt = new int[n];  // Waiting Times
        int[] tat = new int[n]; // Turnaround Times

        // Input: Process details
        for (int i = 0; i < n; i++) {
            System.out.print("Process ID: ");
            pid[i] = sc.nextInt();
            System.out.print("Arrival Time: ");
            at[i] = sc.nextInt();
            System.out.print("Burst Time: ");
            bt[i] = sc.nextInt();
            rt[i] = bt[i]; // Initialize remaining time
            System.out.println();
        }

        int complete = 0, time = 0;
        int minRemaining, shortest = -1;
        boolean found;
        ArrayList<Integer> order = new ArrayList<>();

        // SJF Preemptive Scheduling Loop
        while (complete < n) {
            minRemaining = Integer.MAX_VALUE;
            found = false;

            for (int i = 0; i < n; i++) {
                if (at[i] <= time && rt[i] > 0 && rt[i] < minRemaining) {
                    minRemaining = rt[i];
                    shortest = i;
                    found = true;
                }
            }

            if (!found) {
                time++;
                continue;
            }

            order.add(pid[shortest]); // Record execution order
            rt[shortest]--;
            time++;

            if (rt[shortest] == 0) {
                complete++;
                int finish = time;
                tat[shortest] = finish - at[shortest];
                wt[shortest] = tat[shortest] - bt[shortest];
                if (wt[shortest] < 0) wt[shortest] = 0;
            }
        }

        // Output: Process table
        double avgWT = 0, avgTAT = 0;
        System.out.println("\nPID\tAT\tBT\tWT\tTAT");
        for (int i = 0; i < n; i++) {
            avgWT += wt[i];
            avgTAT += tat[i];
            System.out.println(pid[i] + "\t" + at[i] + "\t" + bt[i] + "\t" + wt[i] + "\t" + tat[i]);
        }

        // Output: Execution order (Gantt-style)
        System.out.println("\nExecution Order:");
        for (int x : order) {
            System.out.print("P" + x + " ");
        }

        // Output: Averages
        System.out.printf("\n\nAverage Waiting Time: %.2f\n", avgWT / n);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTAT / n);
    }
}
