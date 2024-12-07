
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class KDTreeRangeSearchSeq {

    private static List<PointKD> readInputFile(String inputFile, int numberOfDimensions) throws IOException {
        List<PointKD> vertices = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] dimensionValues = line.split(",");
                if (dimensionValues.length != numberOfDimensions) {
                    throw new IllegalArgumentException("The number of dimensions does not match the input.");
                }
                vertices.add(new PointKD(dimensionValues));
            }
        }
        return vertices;
    }


    public static void main(String[] args) {
        try {
            if (args.length < 3) {
                System.err.println("Usage: KDTreeRangeSearch <inputFile> <numberOfDimensions> <startRange> <endRange>");
                System.exit(1);
            }
            String inputFile = args[0];
            int numberOfDimensions = Integer.parseInt(args[1]);
            String start = args[2];
            String end = args[3];
            PointKD startRange = new PointKD(start.split(","));
            PointKD endRange = new PointKD(end.split(","));

            long startTime = System.currentTimeMillis();

            List<PointKD> vertices = readInputFile(inputFile, numberOfDimensions);

            KDTree tree = new KDTree(numberOfDimensions);
            PointKD root = tree.buildTree(vertices, 0);

            List<PointKD> resultList = tree.rangeSearch(root, startRange, endRange, numberOfDimensions);

            // Open BufferedWriter to write the result to "outputSeq.txt"
            BufferedWriter writer = new BufferedWriter(new FileWriter("outputSeq.txt", true));



            for (int i = 0; i < resultList.size(); i++) {
                PointKD vertex = resultList.get(i);
                String output = vertex.toString();
                //System.out.println(output);  
                writer.write(output);
                writer.newLine(); 
            }

            System.out.println("Result saved in outputSeq.txt");

            writer.close();

            long endTime = System.currentTimeMillis();
            System.out.println("Final Project : Execution time: " + ((double) (endTime - startTime) / 1000) + " seconds");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}