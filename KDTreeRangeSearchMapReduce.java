import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KDTreeRangeSearchMapReduce {

    // Mapper Class
    public static class KDTreeRangeSearchMapper extends Mapper<LongWritable, Text, Text, Text> {

        private int numberOfDimensions;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            numberOfDimensions = Integer.parseInt(context.getConfiguration().get("numberOfDimensions"));
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                String[] dimensions = value.toString().split(",");
                if (dimensions.length != numberOfDimensions) {
                    throw new IllegalArgumentException("Point dimensions do not match the specified number.");
                }

                context.write(new Text("Point"), new Text(value.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Reducer Class
    public static class KDTreeRangeSearchReducer extends Reducer<Text, Text, Text, Text> {

        private int numberOfDimensions;
        private PointKD lowerBound;
        private PointKD upperBound;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            // Initialize the configuration parameters
            numberOfDimensions = Integer.parseInt(context.getConfiguration().get("numberOfDimensions"));
            lowerBound = new PointKD(context.getConfiguration().get("lowerBound").split(","));
            upperBound = new PointKD(context.getConfiguration().get("upperBound").split(","));
        }

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            try {
                // Accumulate all points
                List<PointKD> points = new ArrayList<>();
                
                for (Text value : values) {
                    String[] dimensions = value.toString().split(",");
                    points.add(new PointKD(dimensions));  // Add the parsed point to the list
                }

                // Build KDTree
                KDTree tree = new KDTree(numberOfDimensions);
                PointKD root = tree.buildTree(points, 0);

                // Perform range search
                List<PointKD> results = tree.rangeSearch(root, lowerBound, upperBound, 0);
                for (PointKD result : results) {
                    context.write(new Text(""), new Text(result.toString()));  // Output the result
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Main Driver Class
    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.err.println("Usage: KDTreeRangeSearchMapReduce <inputFile> <outputDir> <numberOfDimensions> <lowerBound> <upperBound>");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        conf.set("numberOfDimensions", args[2]);
        conf.set("lowerBound", args[3]);
        conf.set("upperBound", args[4]);

        long startTime = System.currentTimeMillis();
        
        Job job = new Job(conf, "KDTree Range Search");
        job.setJarByClass(KDTreeRangeSearchMapReduce.class);
        job.setMapperClass(KDTreeRangeSearchMapper.class);
        job.setReducerClass(KDTreeRangeSearchReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // Wait for job completion
        boolean success = job.waitForCompletion(true);

        long endTime = System.currentTimeMillis();
        System.out.println("Final Project : Execution time: " + ((double) (endTime - startTime) / 1000) + " seconds");

        // Exit based on job success or failure
        System.exit(success ? 0 : 1);
    }
}