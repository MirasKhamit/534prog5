package edu.uw.bothell.css.dsl.mass.apps.RangeSearch;

import edu.uw.bothell.css.dsl.MASS.Agents;
import edu.uw.bothell.css.dsl.MASS.MASS;
import edu.uw.bothell.css.dsl.MASS.*;
import edu.uw.bothell.css.dsl.MASS.MASSBase;
import edu.uw.bothell.css.dsl.MASS.Places;
import edu.uw.bothell.css.dsl.MASS.GraphPlaces;
import edu.uw.bothell.css.dsl.MASS.logging.LogLevel;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class KDTreeRangeSearch {

    public static int level = 0;
    public static KDTree kdTree;

    static public void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar ./KDTreeRangeSearch <inputPath> <rangeValues...>");
            System.exit(-1);
        }

        String inputPath = args[0];
        int[] range = Arrays.stream(Arrays.copyOfRange(args, 1, args.length))
                            .mapToInt(Integer::parseInt)
                            .toArray();

        int dimensions = range.length / 2;
        if (range.length % 2 != 0 || dimensions < 1) {
            System.err.println("Error: Range must have min and max values for each dimension.");
            System.exit(-1);
        }

        // Init MASS
        MASS.setLoggingLevel(LogLevel.DEBUG);
        

        /* ---------------------- KDTree Construction ---------------------- */
        long kdStart = System.currentTimeMillis();
        System.out.println("inputPath: " + inputPath+" dimensions "+dimensions);
        ArrayList<PointKD> points = getPoints(inputPath, dimensions);
        System.out.println("Points: " + points.size());
        MASS.init(points.size());
        kdTree = new KDTree(1, Vertex.class.getName(), dimensions);
        // GraphPlaces tree = new GraphPlaces(1, Vertex.class.getName());
        constructTree(kdTree,points,0,dimensions);

        // printTree(kdTree.root,0);
        long kdEnd = System.currentTimeMillis();

        System.out.println("Creating initial agents at the root ...");

        /* -------------------------- RangeSearch -------------------------- */
        long rsStart = System.currentTimeMillis();
        Object[] arg = {range, 0};
        SmartArgs2Agents arguments = new SmartArgs2Agents(SmartArgs2Agents.rangeSearch_, arg, -1, -1);
        Agents agents = new Agents(1, KDAgent.class.getName(), arguments, kdTree, points.size());
        // Agents crawlers = new Agents(sequentialId++, CrawlerGraphMASS.class.getName(), null, network, nVertices);

        System.out.println("Agents Created ...");
        ArrayList<Vertex> results = new ArrayList<>();
        int stepcount = 0;

        while (true) {
            //  Graph graph = tree;

        // List<VertexModel> vertices = tree.getGraph().getVertices();

        // int nVertices = vertices.size();

        // System.out.println("Number of Vertices is : " + nVertices);

        // try {

        //     network.callAll(NodeGraphMASS.init_);

        //     // network.callAll(NodeGraphMASS.display_);

        //     // Time measurement starts
        //     System.out.println("Go! Graph");
        //     long startTime = System.currentTimeMillis();

        //     // Instantiate an agent at each node
        //     Agents crawlers = new Agents(sequentialId++, CrawlerGraphMASS.class.getName(), null, network, nVertices);
            Object[] argArrays = {range};
            Vertex[] resultTemp = Arrays.stream((Object[]) agents.callAll(KDAgent.DoSearch_, null))
                                        .filter(Objects::nonNull)
                                        .map(o -> (Vertex) o)
                                        .toArray(Vertex[]::new);

            Collections.addAll(results, resultTemp);
            agents.manageAll();

            System.out.println("Step: " + stepcount + ", Results: " + results);
            stepcount++;

            if (agents.nAgents() == 0)
                break;
            }

        long rsEnd = System.currentTimeMillis();

        System.out.println("Results: " + results);

        System.out.println("KD-Tree Construction Time (ms): " + (kdEnd - kdStart));
        System.out.println("RangeSearch Exec Time (ms): " + (rsEnd - rsStart));
        /* --------------------------- Complete ---------------------------- */
        MASS.finish();

    }

    // Build the KD-tree recursively from the given nodes
    public static void constructTree(KDTree kdTree,List<PointKD> nodes, int depth,int dimensions) {
        if (nodes.isEmpty()) {
            return;
        }

        // Sort nodes using the comparator for the current dimension
        int axis = depth % dimensions;
        nodes.sort(kdTree.comparators[axis]);

        int medianIndex = nodes.size() / 2;
        PointKD medianNode = nodes.get(medianIndex);
        kdTree.insert(medianNode);
        // Create the current node
        // Vertex root = new Vertex(medianNode.coordinates,axis);
        // root.left = this.addVertexWithParams(p);
        // Recursively build the left and right subtrees
        constructTree(kdTree,nodes.subList(0, medianIndex), depth + 1,dimensions);
        constructTree(kdTree,nodes.subList(medianIndex + 1, nodes.size()), depth + 1,dimensions);
    }
    // private static void constructTree(KDTree kdTree, ArrayList<PointK> points, int begin, int end, int level, int dimensions) {
    //     if (begin >= end) {
    //         return;
    //     }

    //     int median = (begin + end - 1) / 2;
    //     int k = ((end - 1 - begin) / 2) + 1;

    //     PointK p = kthSmallest(points, begin, end - 1, k, level % dimensions);
    //     kdTree.insert(p);

    //     constructTree(kdTree, points, begin, median, level + 1, dimensions);
    //     constructTree(kdTree, points, median + 1, end, level + 1, dimensions);
    // }

    // private static PointK kthSmallest(ArrayList<PointK> points, int l, int r, int k, int dimension) {
    //     if (k > 0 && k <= r - l + 1) {
    //         int pos = partition(points, l, r, dimension);

    //         if (pos - l == k - 1) {
    //             return points.get(pos);
    //         }

    //         if (pos - l > k - 1) {
    //             return kthSmallest(points, l, pos - 1, k, dimension);
    //         }

    //         return kthSmallest(points, pos + 1, r, k - pos + l - 1, dimension);
    //     }

    //     return null;
    // }

    // private static int partition(ArrayList<PointK> points, int l, int r, int dimension) {
    //     double pivot = points.get(r).coordinates[dimension];
    //     int i = l;

    //     for (int j = l; j <= r - 1; j++) {
    //         if (points.get(j).coordinates[dimension] <= pivot) {
    //             Collections.swap(points, i, j);
    //             i++;
    //         }
    //     }

    //     Collections.swap(points, i, r);
    //     return i;
    // }

    private static ArrayList<PointKD> getPoints(String filePath, int dimensions) {
        File fd = new File(filePath);
        if (!fd.exists()) {
            // System.out.println("Input file doesn't exist");
            MASS.getLogger().error("Input file doesn't exist");
            System.exit(-1);
        }

        HashSet<PointKD> pointSet = new HashSet<>();
        try (Scanner sc = new Scanner(fd)) {
            while (sc.hasNextLine()) {
                String[] tokens = sc.nextLine().split(",");
                if (tokens.length != dimensions) {
                    MASS.getLogger().error("Invalid point data: " + Arrays.toString(tokens));
                    continue;
                }
                double[] coords = Arrays.stream(tokens).mapToDouble(Double::parseDouble).toArray();
                // for(String token : tokens){
                //     System.out.println(" token "+token);
                // }

                // for(Double coord : coords){
                //     System.out.println(" coords "+coords);
                // }
                pointSet.add(new PointKD(coords));
            }
        } catch (FileNotFoundException e) {
            MASS.getLogger().error("Input file doesn't exist");
            System.exit(-1);
        }

        return new ArrayList<>(pointSet);
    }
    // public static void printTree(Vertex root, int depth) {
    //     if (root == null) {
    //         return;
    //     }   
    //     System.out.println("Coordinates ");
    //     System.out.print(" "+ Arrays.toString(root.coordinates));

    //     // Recursively print left and right subtrees
    //     printTree(root.left, depth + 1);
    //     printTree(root.right, depth + 1);
    // }

}
