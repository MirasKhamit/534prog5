package edu.uw.bothell.css.dsl.mass.apps.RangeSearch;

import edu.uw.bothell.css.dsl.MASS.VertexPlace;
import java.io.Serializable;
import java.util.*;

// Point2D is class for working two dimensional point data.
public class PointKD implements Serializable {
    public static final long serialVersionUID = 12962345938L;
    public double[] coordinates;

    public PointKD(double[] coordinates) {
        this.coordinates = coordinates.clone();
    }

    @Override
    public String toString() {
        return Arrays.toString(this.coordinates);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PointKD) {
            PointKD otherPoint = (PointKD) o;
            double[] inputArray = otherPoint.coordinates;

            // Check if the arrays are of the same length
            if (this.coordinates.length != inputArray.length) {
                return false;
            }

            // Compare each element of the two arrays
            for (int i = 0; i < this.coordinates.length; i++) {
                if (this.coordinates[i]!=inputArray[i]) {
                    return false;
                }
            }

            // All elements are equal
            return true;
        }
        return false;
    }

}