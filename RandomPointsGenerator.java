import java.io.*;

/**
 * RandomPointsGenerator.java
 * Project: RangeSearch
 * University of Washington Bothell, Distributed Systems Laboratory
 * Spring 2020
 * @author Satine Paronyan
 */

public class RandomPointsGenerator {

    /* Accepts four arguments: .txt filename, number of points to generate,
     * min random val boundary , max random val boundary.
     *
     * Uses the passed arguments to generate the given number of points in the random
     * range between given minimum and maximum values. The generated points written to a file.
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err.println("Pass 4 arguments: .txt filename, number of points,  min random boundary, " +
                    "max random boundary");
            System.exit(0);
        }

        String fileName = args[0];
        int min = Integer.parseInt(args[2]);
        int max = Integer.parseInt(args[3]);
        int numOfPoints = Integer.parseInt(args[1]);

        generate(fileName, numOfPoints, min, max);
    }


    public static void generate(String fileName, int numOfPoints, int min, int max) throws IOException {
        File outFile = new File(fileName);
        if (!outFile.exists()) {
            outFile.createNewFile();
        } else {
            outFile.delete();
            outFile.createNewFile();
        }

        // write the content in file
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFile))) {
            for (int i = 1; i <= numOfPoints; i++) {
                // generates x values
                double xValue = min + Math.random() * (max - min);
                // generates y values
                double yValue = min + Math.random() * (max - min);

                // write point [x, y] to a file
                bufferedWriter.write(Integer.toString((int)xValue));
                bufferedWriter.write(",");
                bufferedWriter.write(Integer.toString((int)yValue));
                bufferedWriter.write("\n");
            }
        }
    }

}
