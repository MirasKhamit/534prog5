package edu.uw.bothell.css.dsl.mass.apps.RangeSearch;

import java.util.ArrayList;
import edu.uw.bothell.css.dsl.MASS.GraphPlaces;
import edu.uw.bothell.css.dsl.MASS.Agents;
import edu.uw.bothell.css.dsl.MASS.MASS;
import edu.uw.bothell.css.dsl.MASS.*;
import edu.uw.bothell.css.dsl.MASS.Places;
import edu.uw.bothell.css.dsl.MASS.GraphPlaces;
import edu.uw.bothell.css.dsl.MASS.logging.LogLevel;
import java.util.*;

public class KDTree extends GraphPlaces {
    public int root;
    private final int dimensions;
    private int LEFTNODE_ = 1;
    private int RIGHTNODE_ = 2;

    public Comparator<PointKD>[] comparators; // Comparators for each dimension

    class DimensionalComparator implements Comparator<PointKD> {
        
        private final int dimension;
        public DimensionalComparator(int dimension) {
            this.dimension = dimension;
        }

        @Override
        public int compare(PointKD n1, PointKD n2) {
            return (n1.coordinates[dimension] < n2.coordinates[dimension])?-1:((n1.coordinates[dimension] == n2.coordinates[dimension])?0:1);
        }
    }

    public KDTree(int handle, String className, int dimensions) {
        super(handle, className, "KDTreespace");
        this.dimensions = dimensions;
        this.root=-1;
        MASS.setCurrentPlacesBase(this);
        comparators = new Comparator[dimensions];
        for (int i = 0; i < dimensions; i++) {
            comparators[i] = new DimensionalComparator(i);
        }
    }

    public void insert(PointKD p) {
        if (this.root == -1) {
            // Vertex rootVertex = new Vertex(p,0);
            this.root = this.addVertexWithParams(p);

            //Commented as a part of automated Agent migration by Vishnu
            //this.agent = new Agents(1, KDAgent.class.getName(), new int[]{-1, -1, 0}, this, 1);
            System.out.println("Constructing root of tree with point : ( " + Arrays.toString(p.coordinates)+" ) ROOT "+this.root+" value "+getVertex(this.root).point);
            MASS.getLogger().debug("Constructing root of tree with point : ( " + Arrays.toString(p.coordinates)+" )");
            return;
        }
        System.out.println("Root "+this.root+" constructing tree with point : ( " + Arrays.toString(p.coordinates)+" )");
        MASS.getLogger().debug("Constructing tree with point : ( " + Arrays.toString(p.coordinates)+" )");

        Vertex vp = getVertex(this.root);

        insertPoint(vp, this.root, p, 0);
    }

    public Vertex getVertex(int vertexID) {
        return (Vertex) super.getVertex(vertexID);
    }

    public void insertPoint(Vertex root, int rootID, PointKD p, int level) {
        System.out.println("INSERT POINT " +p);
        if (p == null) { return; }
        System.out.println("INSERT POINT array " + Arrays.toString(p.coordinates));
        int coordinatePosition = level % this.dimensions ;
            System.out.println("Root coordinates "+root.point);
            if (p.coordinates[coordinatePosition] <= root.point.coordinates[coordinatePosition]) {
                System.out.println("AAAA Constructing Tree with level ( "+level+" )and adding Left Vertex ( " + root.left + " ) to Vertex ( " + rootID + " " +
                            " and with coordinate value as: ( " + Arrays.toString(p.coordinates)+" )");
                if (root.left != -1) {
                    Vertex leftChild = getVertex(root.left);
                    insertPoint(leftChild, root.left, p, level+1);
                } else {
                    root.left = this.addVertexWithParams(p);
                    this.addEdge(rootID, root.left, 0);
                    this.addTreeNode(rootID, root.left, LEFTNODE_);
                    System.out.println("BBBBB Constructing Tree with level ( "+level+" )and adding Left Vertex ( " + root.left + " ) to Vertex ( " + rootID + " " +
                            " and with coordinate value as: ( " + Arrays.toString(p.coordinates)+" )");
                    MASS.getLogger().debug("Constructing Tree with level ( "+level+" )and adding Left Vertex ( " + root.left + " ) to Vertex ( " + rootID + " " +
                            " and with coordinate value as: ( " + Arrays.toString(p.coordinates)+" )");
                }

            } else {
                System.out.println("AAA Constructing Tree with level ( "+level+" )and adding Right Vertex ( " + root.right + " ) to Vertex ( " + rootID + " " +
                            " and with coordinate value as: ( " + Arrays.toString(p.coordinates)+" )");
                if (root.right != -1) {
                    Vertex rightChild = getVertex(root.right);
                    insertPoint(rightChild, root.right, p, level+1);
                } else {
                    root.right = this.addVertexWithParams(p);
                    this.addEdge(rootID, root.right, 0);
                    this.addTreeNode(rootID, root.right, RIGHTNODE_);
                    System.out.println("BBBB Constructing Tree with level ( "+level+" )and adding Right Vertex ( " + root.right + " ) to Vertex ( " + rootID + " " +
                            " and with coordinate value as: ( " + Arrays.toString(p.coordinates)+" )");
                    MASS.getLogger().debug("Constructing Tree with level ( "+level+" )and adding Right Vertex ( " + root.right + " ) to Vertex ( " + rootID + " " +
                            " and with coordinate value as: ( " + Arrays.toString(p.coordinates)+" )");
                }
            }

            // Migrate agent to the root node and update its left/right fields.
            //this.agent.callAll(KDAgent.update_, new int[]{root.left, root.right, rootID});
            //this.agent.manageAll();

            return;
    }

    public int getRoot(){
        return this.root;
    }


    public ArrayList<Vertex> rangeSearch(double[] minRange, double[] maxRange) {
        ArrayList<Vertex> result = new ArrayList<>();
        rangeSearchRec(getVertex(0), minRange, maxRange, 0, result);
        return result;
    }

    private void rangeSearchRec(Vertex node, double[] minRange, double[] maxRange, int depth, ArrayList<Vertex> result) {
        if (node == null) return;

        boolean inside = true;
        for (int i = 0; i < dimensions; i++) {
            if (node.point.coordinates[i] < minRange[i] || node.point.coordinates[i] > maxRange[i]) {
                inside = false;
                break;
            }
        }

        if (inside) {
            result.add(node);
        }

        int cd = depth % dimensions;
        if (node.point.coordinates[cd] >= minRange[cd]) {
            rangeSearchRec(getVertex(node.left), minRange, maxRange, depth + 1, result);
        }
        if (node.point.coordinates[cd] <= maxRange[cd]) {
            rangeSearchRec(getVertex(node.right), minRange, maxRange, depth + 1, result);
        }
    }
}
