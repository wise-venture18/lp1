import java.util.*;

class FCFS {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        int[] pid = new int[n]; // Process IDs
        int[] at = new int[n];  // Arrival Times
        int[] bt = new int[n];  // Burst Times
        int[] wt = new int[n];  // Waiting Times
        int[] tat = new int[n]; // Turnaround Times
        int[] ct = new int[n];  // Completion Times

        // Input process details
        for (int i = 0; i < n; i++) {
            System.out.print("Process ID: ");
            pid[i] = sc.nextInt();
            System.out.print("Arrival Time: ");
            at[i] = sc.nextInt();
            System.out.print("Burst Time: ");
            bt[i] = sc.nextInt();
            System.out.println();
        }

        // Sort processes by Arrival Time
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (at[i] > at[j]) {
                    // Swap arrival times
                    int temp = at[i]; at[i] = at[j]; at[j] = temp;
                    // Swap burst times
                    temp = bt[i]; bt[i] = bt[j]; bt[j] = temp;
                    // Swap process IDs
                    temp = pid[i]; pid[i] = pid[j]; pid[j] = temp;
                }
            }
        }

        // Calculate CT, TAT, WT
        int time = 0;
        double avgWT = 0, avgTAT = 0;

        for (int i = 0; i < n; i++) {
            if (time < at[i]) time = at[i]; // CPU idle
            time += bt[i];
            ct[i] = time;
            tat[i] = ct[i] - at[i];
            wt[i] = tat[i] - bt[i];
            avgWT += wt[i];
            avgTAT += tat[i];
        }

        // Display results
        System.out.println("\nPID\tAT\tBT\tWT\tTAT\tCT");
        for (int i = 0; i < n; i++) {
            System.out.println(pid[i] + "\t" + at[i] + "\t" + bt[i] + "\t" + wt[i] + "\t" + tat[i] + "\t" + ct[i]);
        }

        // Gantt Chart with time scale
        System.out.println("\nGantt Chart:");
        System.out.print("|");
        for (int i = 0; i < n; i++) {
            System.out.print(" P" + pid[i] + " |");
        }
        System.out.println();

        System.out.print("0");
        for (int i = 0; i < n; i++) {
            System.out.print("   " + ct[i]);
        }
        System.out.println();

        // Averages
        System.out.printf("\nAverage Waiting Time:", avgWT / n);
        System.out.printf("Average Turnaround Time:", avgTAT / n);
    }
}

