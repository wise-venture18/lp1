import java.util.*;

class PriorityNonPreemptive {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        int[] pid = new int[n];  // Process IDs
        int[] at = new int[n];   // Arrival Times
        int[] bt = new int[n];   // Burst Times
        int[] pr = new int[n];   // Priorities
        int[] wt = new int[n];   // Waiting Times
        int[] tat = new int[n];  // Turnaround Times
        boolean[] completed = new boolean[n]; // Completion flags

        // Input process details
        for (int i = 0; i < n; i++) {
            System.out.print("Process ID: ");
            pid[i] = sc.nextInt();
            System.out.print("Arrival Time: ");
            at[i] = sc.nextInt();
            System.out.print("Burst Time: ");
            bt[i] = sc.nextInt();
            System.out.print("Priority (lower = higher priority): ");
            pr[i] = sc.nextInt();
            System.out.println();
        }

        int complete = 0, time = 0;
        ArrayList<Integer> order = new ArrayList<>();

        // Priority Scheduling (Non-Preemptive)
        while (complete < n) {
            int idx = -1;
            int highestPriority = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!completed[i] && at[i] <= time && pr[i] < highestPriority) {
                    highestPriority = pr[i];
                    idx = i;
                }
            }

            if (idx == -1) { // No process has arrived yet
                time++;
                continue;
            }

            // Execute selected process fully (non-preemptive)
            order.add(pid[idx]);
            time += bt[idx];
            tat[idx] = time - at[idx];
            wt[idx] = tat[idx] - bt[idx];
            completed[idx] = true;
            complete++;
        }

        // Calculate averages
        double avgWT = 0, avgTAT = 0;
        System.out.println("\nPID\tAT\tBT\tPR\tWT\tTAT");
        for (int i = 0; i < n; i++) {
            avgWT += wt[i];
            avgTAT += tat[i];
            System.out.println(pid[i] + "\t" + at[i] + "\t" + bt[i] + "\t" + pr[i] + "\t" + wt[i] + "\t" + tat[i]);
        }

        avgWT /= n;
        avgTAT /= n;

        // Display Execution Order
        System.out.println("\nExecution Order:");
        for (int x : order) {
            System.out.print("P" + x + " ");
        }

        // Display Averages
        System.out.printf("\n\nAverage Waiting Time:", avgWT);
        System.out.printf("\nAverage Turnaround Time:", avgTAT);
    }
}
