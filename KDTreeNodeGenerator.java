import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.HashSet;

public class KDTreeNodeGenerator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get user inputs for K and N
        System.out.print("Enter the number of dimensions (K): ");
        int K = scanner.nextInt();
        System.out.print("Enter the number of nodes (N): ");
        int N = scanner.nextInt();

        // Ask the user if they want to specify types for each dimension
        System.out.print("Do you want to specify the type for each dimension? (Y/N): ");
        String choice = scanner.next();

        // Array to hold the data type of each dimension
        String[] dimensionTypes = new String[K];

        if (choice.equalsIgnoreCase("Y")) {
            System.out.println("Specify the type for each dimension (1 for integer, 2 for double):");
            for (int i = 0; i < K; i++) {
                System.out.print("Dimension " + (i + 1) + ": ");
                int typeChoice = scanner.nextInt();
                dimensionTypes[i] = (typeChoice == 1) ? "integer" : "double";
            }
        } else {
            // Default all dimensions to integers
            for (int i = 0; i < K; i++) {
                dimensionTypes[i] = "integer";
            }
        }

        // File to save the nodes
        String fileName = "kdtree_nodes1.txt";

        HashSet<String> nodes = new HashSet<String>();
        Random random = new Random();

        // Generate N nodes
        for (int i = 0; i < N; i++) {
            StringBuilder node = new StringBuilder("");//(");
            for (int j = 0; j < K; j++) {
                if (dimensionTypes[j].equals("integer")) {
                    node.append(random.nextInt(1000)); // Random integer between 0 and 99
                } else {
                    node.append(random.nextDouble() * 1000); // Random double between 0.0 and 99.9
                }
                if (j < K - 1) {
                    node.append(", ");
                }
            }
            //node.append(")");
            nodes.add(node.toString());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            for (String node : nodes) {
                // Write the node to the file
                writer.write(node.toString());
                writer.newLine();
            }

            System.out.println("Nodes generated and saved to " + fileName);

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        scanner.close();
    }
}