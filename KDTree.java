
import java.util.ArrayList;
import java.util.*;

public class KDTree {
    int maxDimension; // Dimensionality of the KD-tree
    Comparator<PointKD>[] comparators; // Comparators for each dimension

    // Constructor to set dimensions
    public KDTree(int k) {
        this.maxDimension = k;
        // Initialize comparators for each dimension
        comparators = new Comparator[k];
        for (int i = 0; i < k; i++) {
            comparators[i] = new DimensionalComparator(i);
        }
    }

    class DimensionalComparator implements Comparator<PointKD> {
        
        private final int dimension;
        public DimensionalComparator(int dimension) {
            this.dimension = dimension;
        }

        @Override
        public int compare(PointKD n1, PointKD n2) {
            return n1.point[dimension].compareTo(n2.point[dimension]);
        }
    }

    // Build the KD-tree recursively from the given nodes
    public PointKD buildTree(List<PointKD> nodes, int depth) {
        if (nodes.isEmpty()) {
            return null;
        }

        // Sort nodes using the comparator for the current dimension
        int axis = depth % maxDimension;
        nodes.sort(comparators[axis]);

        // Find the median node
        int medianIndex = nodes.size() / 2;
        PointKD medianNode = nodes.get(medianIndex);

        // Create the current node
        PointKD root = new PointKD(medianNode.point);

        // Recursively build the left and right subtrees
        root.left = buildTree(nodes.subList(0, medianIndex), depth + 1);
        root.right = buildTree(nodes.subList(medianIndex + 1, nodes.size()), depth + 1);

        return root;
    }

    // Range search method
    public List<PointKD> rangeSearch(PointKD root, PointKD lowerBound, PointKD upperBound, int depth) {
        List<PointKD> results = new ArrayList<>();

        if (root == null) {
            return results;
        }

        // Check if the current node is in range
        boolean inRange = true;
        for (int i = 0; i < maxDimension; i++) {
            if (root.point[i].compareTo(lowerBound.point[i]) < 0 || root.point[i].compareTo(upperBound.point[i]) > 0) {
                inRange = false;
                break;
            }
        }
        if (inRange) {
            results.add(root);
        }

        // Determine the axis for comparison
        int axis = depth % maxDimension;

        // Recur on left subtree if there could be points in range
        if (root.point[axis].compareTo(lowerBound.point[axis]) >= 0) {
            results.addAll(rangeSearch(root.left, lowerBound, upperBound, depth + 1));
        }

        // Recur on right subtree if there could be points in range
        if (root.point[axis].compareTo(upperBound.point[axis]) <= 0) {
            results.addAll(rangeSearch(root.right, lowerBound, upperBound, depth + 1));
        }

        return results;
    }
    
    // Helper method to print the KD-tree
    public void printTree(PointKD root, int depth) {
        if (root == null) {
            return;
        }   
        // Recursively print left and right subtrees
        printTree(root.left, depth + 1);
        printTree(root.right, depth + 1);
    }

}