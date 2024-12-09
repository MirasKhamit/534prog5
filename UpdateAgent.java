package edu.uw.bothell.css.dsl.mass.apps.RangeSearch;

import edu.uw.bothell.css.dsl.MASS.Agent;
import edu.uw.bothell.css.dsl.MASS.GraphAgent;
import edu.uw.bothell.css.dsl.MASS.*;
import edu.uw.bothell.css.dsl.MASS.MASSBase;
import edu.uw.bothell.css.dsl.MASS.logging.LogLevel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.*;
import java.util.stream.*;
import java.io.Serializable;

/**
 * UpdateAgent is responsible for performing a range search on a KDTree.
 */
public class UpdateAgent extends GraphAgent implements Serializable {
    public static final int DoSearch_ = 1; // Identifier for the search method

    private int dimensions;
    private KDTree kdTree;

    public int[] range;  //Array storing arguments received
    public int[] minRange;
    public int[] maxRange;

    public UpdateAgent(Object arg) {
        // super(arg);
        if (arg != null) {
            SmartArgs2Agents arguments = (SmartArgs2Agents) arg;
            this.range = (arguments.searchRange != null) ? arguments.searchRange.clone() : null;
            this.dimensions = this.range.length / 2;
            this.minRange = Arrays.copyOfRange(this.range, 0, this.range.length / 2);
            this.maxRange = Arrays.copyOfRange(this.range, this.range.length / 2, this.range.length);
            System.err.println("Syserr Agent created with range: " + Arrays.toString(this.range)+ " Dimension "+this.dimensions);
            MASS.getLogger().debug("Agent created with range: " + Arrays.toString(this.range));
            MASSBase.getLogger().debug("Base Agent created with range: " + Arrays.toString(this.range));
            //Commented as a part of automated Agent migration by Vishnu
            //this.left = args[0];
            //this.right = args[1];
        }
    }

    public void setArguments(Object args) {
        Object[] argArray = (Object[]) args;
        this.dimensions = (int) argArray[0];
        this.kdTree = (KDTree) argArray[1];
    }

    @Override
    public Object callMethod(int method, Object args) {
        System.out.println("callMethod invoked ...  ");
        switch (method) {
            case DoSearch_:
                System.out.println("DoSearch_ Called ...  ");
                MASSBase.getLogger().debug("DoSearch_ Called ...  "+args);
                return performRangeSearch((Object[]) args);
            default:
                return null;
        }
    }

    /**
     * Perform range search on the KDTree for the specified range.
     *
     * @param args Contains the range and level for searching
     * @return List of points within the range
     */
    private Object performRangeSearch(Object[] args) {
        System.err.println("A.... Args "+args);
        System.err.println("range "+Arrays.toString(range));
        System.err.println(" dimensions "+dimensions);
        System.err.println("currentLevel "+level);
        // Perform range search for the current range
        List<Vertex> results = new ArrayList<>();
        Vertex currentVertex = (Vertex) getPlace();
        System.err.println("performRangeSearch (" + getAgentId() + ") is at place "+currentVertex.getIndex()[0] + " Left Node is " + currentVertex.left + "" +
                " and Right Node is " + currentVertex.right);
        rangeSearch(currentVertex, this.range, results, level % dimensions);
        level++;
        System.err.println("Vertex class loader: " + Vertex.class.getClassLoader());
        System.err.println("Returned object's class loader: " + results.getClass().getClassLoader());
        Object response = results.toArray(new Vertex[0]);
        System.err.println("Response : " + Arrays.toString((Object[])response));
        return results.toArray(new Vertex[0]);
    }

    /**
     * Recursively performs a range search on the KDTree.
     *
     * @param node       The current node in the KDTree
     * @param range      The search range
     * @param results    The list to store results
     * @param dimension  The current dimension for comparison
     */
    private void rangeSearch(Vertex node, int[] range, List<Vertex> results, int dimension) {
        if (node == null) {
            return;
        }

        double value = node.point.coordinates[dimension];
        boolean inRange = true;

        // Check if the point is within the range for all dimensions
        for (int i = 0; i < dimensions; i++) {
            if (node.point.coordinates[i] < range[2 * i] || node.point.coordinates[i] > range[2 * i + 1]) {
                inRange = false;
                break;
            }
        }
        System.err.println(" in Range "+inRange);
        if (inRange) {
            System.err.println("Adding to results in Range "+Arrays.toString(node.point.coordinates));
            results.add(node);
        }

        // Recur to the left and right child based on the current dimension
        if (value >= range[2 * dimension]) {
            propagateTree(LeftBranch_,range);
            // rangeSearch(node.getLeft(), range, results, (dimension + 1) % dimensions);
        }
        if (value <= range[2 * dimension + 1]) {
            propagateTree(RightBranch_,range);
            // rangeSearch(node.getRight(), range, results, (dimension + 1) % dimensions);
        }
        propagateTree(BothBranch_,range);
    }
}
