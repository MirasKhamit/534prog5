package edu.uw.bothell.css.dsl.mass.apps.RangeSearch;

import edu.uw.bothell.css.dsl.MASS.Place;

import java.util.Arrays;
import java.io.Serializable;
import edu.uw.bothell.css.dsl.MASS.VertexPlace;

/**
 * Vertex class represents a node in the KDTree. It is a Place in the MASS framework
 * and contains the data necessary for KDTree structure.
 */
public class Vertex extends VertexPlace{
    public PointKD point;
    public int level;       // Level in the KDTree for splitting dimension

    /**
     * Default constructor required by MASS framework.
     */
    public Vertex() {
        this.level = 0;
    }

    public Vertex(Object arg) {
        super(arg);
        this.point = (PointKD)arg;
        this.level=0;
    }

    /**
     * Constructor to initialize a Vertex with a point and level.
     *
     * @param point The point to store in the vertex.
     * @param level The level of the vertex in the KDTree.
     */
    public Vertex(PointKD point, int level) {
        this.point = point;
        this.level = level;
    }
    /**
     * Get the level of this vertex in the KDTree.
     *
     * @return The level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Set the level of this vertex in the KDTree.
     *
     * @param level The level.
     */
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "point=" + Arrays.toString(point.coordinates) +
                ", level=" + level +
                '}';
    }
}
