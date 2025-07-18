import java.io.*;
import java.util.*;

public class a2021510062_2021510072_HW3 {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the number of judges: ");
        int numJudges = scanner.nextInt();
        System.out.print("Please enter the cost of problem type changing: ");
        int changeCost = scanner.nextInt();

        List<Integer> problems = readProblemTypes("src/input.txt");
        System.out.println("\nThe input file is read.\n");

        // Initialize the assignment matrix for judges
        int[][] judgeAssignments = new int[numJudges][problems.size()];
        // Assign problems to judges using a greedy algorithm and calculate the total cost
        int totalCost = assignProblemsToJudgesGreedy(problems, judgeAssignments, numJudges, changeCost);

        System.out.println("Total cost: " + totalCost);
    }

    // Time complexity: O(n). Reads from a file with 'n' lines of data
    private static List<Integer> readProblemTypes(String filename) throws IOException {
        List<Integer> problems = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        reader.readLine();  // Skip header
        String line;
        while ((line = reader.readLine()) != null) {
            problems.add(Integer.parseInt(line.trim().substring(5)));
        }
        reader.close();
        return problems;
    }

    // Assigns problems to judges using a greedy algorithm and calculates the total cost
    // Time complexity: O(m * n) where n is the number of problems and m is the number of judges
    private static int assignProblemsToJudgesGreedy(List<Integer> problems, int[][] assignments, int numJudges, int changeCost) {
        // Arrays to store the list of problems assigned to each judge
        List<Integer>[] judgeLists = new ArrayList[numJudges];
        // Map to keep track of when each problem type appears next
        Map<Integer, Queue<Integer>> upcomingTypes = new HashMap<>();

        int totalCost = 0;
        // Populate the upcomingTypes map with the positions of each problem type
        for (int i = 0; i < numJudges; i++) {
            judgeLists[i] = new ArrayList<>();
        }

        for (int i = 0; i < problems.size(); i++) {
            upcomingTypes.putIfAbsent(problems.get(i), new LinkedList<>());
            upcomingTypes.get(problems.get(i)).add(i);
        }
        // Iterate over each problem to assign it to a judge
        for (int i = 0; i < problems.size(); i++) {
            int currentProblemType = problems.get(i);
            upcomingTypes.get(currentProblemType).remove(); // Remove current index

            int bestJudge = -1;
            int maxDistance = -1; // We look for the maximum effective distance
            // Determine the best judge for the current problem
            for (int j = 0; j < numJudges; j++) {
                if (judgeLists[j].isEmpty()) {
                    bestJudge = j;
                    break;
                } else if (judgeLists[j].get(judgeLists[j].size() - 1) == currentProblemType) {
                    // If the last problem assigned to the judge is the same type, keep assigning to the same judge
                    bestJudge = j;
                    break;
                } else {
                    int lastProblemType = judgeLists[j].get(judgeLists[j].size() - 1);
                    int nextOccurrenceDistance = upcomingTypes.containsKey(lastProblemType) && !upcomingTypes.get(lastProblemType).isEmpty()
                            ? upcomingTypes.get(lastProblemType).peek() - i
                            : Integer.MAX_VALUE; // Treat as infinite if no more occurrences

                    if (nextOccurrenceDistance > maxDistance) {
                        maxDistance = nextOccurrenceDistance;
                        bestJudge = j;
                    }
                }
            }
            // Assign the current problem type to the chosen judge
            assignments[bestJudge][i] = currentProblemType;
            judgeLists[bestJudge].add(currentProblemType);

            // Add change cost to the total cost if the last problem type was different or it is the first problem for this judge
            if ((judgeLists[bestJudge].size() > 1 && judgeLists[bestJudge].get(judgeLists[bestJudge].size() - 2) != currentProblemType) || judgeLists[bestJudge].size() == 1) {
                totalCost += changeCost;
            }
        }
        return totalCost;
    }
}