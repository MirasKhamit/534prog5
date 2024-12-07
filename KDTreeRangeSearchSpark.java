
import org.apache.spark.SparkConf; // Spark Configuration
import org.apache.spark.api.java.JavaSparkContext; // Spark Context created from SparkConf
import org.apache.spark.api.java.JavaRDD; // JavaRDD(T) created from SparkContext
import org.apache.spark.api.java.JavaPairRDD; // JavaRDD(T) created from SparkContext
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import java.lang.IllegalArgumentException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class KDTreeRangeSearchSpark{

    public static void main(String[] args){
        try{
            if (args.length < 3) {
                System.err.println("Usage: KDTreeRangeSearch <inputFile> <numberOfDimensions> <startRange> <endRange>");
                System.exit(1);
            }
            // Read input arguments
            String inputFile = args[0];
            int numberOfDimensions = Integer.parseInt(args[1]);
            String start = args[2];
            String end = args[3];
            PointKD startRange = new PointKD(start.split(","));
            PointKD endRange = new PointKD(end.split(","));

            // Spark configuration
            SparkConf conf = new SparkConf().setAppName(
                    "Final Project : Range search " + inputFile + " btwn " + start + " " + end);
            JavaSparkContext jsc = new JavaSparkContext(conf);

             // Start the timer
            long startTime = System.currentTimeMillis();
            System.out.println("Final Project : start time: " + startTime);
            // Read input file
            JavaRDD<String> lines = jsc.textFile(inputFile);
            JavaRDD<PointKD> vertices = lines.map(line->{
                String[] dimensionValues = line.split(",");
                if(dimensionValues.length!=numberOfDimensions){
                    throw new IllegalArgumentException();
                }
                return new PointKD(dimensionValues);
            });
            JavaRDD<PointKD> workerPartition = vertices.mapPartitions(iterator -> {
                List<PointKD> vertexList = new ArrayList();
                while(iterator.hasNext()){
                    PointKD currentVertex= iterator.next();
                    vertexList.add(currentVertex);
                }
                KDTree tree = new KDTree(numberOfDimensions);
                // median will be the root.
                PointKD root = tree.buildTree( vertexList, 0);
                List<PointKD> resultList = tree.rangeSearch(root,startRange,endRange,numberOfDimensions);   
                return resultList.iterator();
            });

            // Collect the results
            List<PointKD> resultList = workerPartition.collect();
            // Open BufferedWriter to write the result to "outputSpark.txt"
            BufferedWriter writer = new BufferedWriter(new FileWriter("outputSpark.txt", true));

            System.out.println("Collected range: ");


            for (int i = 0; i < resultList.size(); i++) {
                PointKD vertex = resultList.get(i);
                String output = vertex.toString();
                //System.out.println(output);  
                writer.write(output);
                writer.newLine(); 
            }

            System.out.println("Result saved in outputSpark.txt");

            writer.close();

            // end timer
            long endTime = System.currentTimeMillis();
            System.out.println("Final Project : Execution time: " + ((double) (endTime - startTime) / 1000) + " seconds");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}