
import java.io.Serializable;

public class PointKD implements Serializable{
        public Double[] point;
        PointKD left,right;
        PointKD(String[] parsedPoints){
            point = new Double[parsedPoints.length];
            for(int i=0;i<parsedPoints.length;i++){
                point[i]=Double.parseDouble(parsedPoints[i]);
            }
            this.left = this.right = null;
        }

        PointKD(Double[] parsedPoints){
            this.point = parsedPoints;
            this.left = this.right = null;
        }
        @Override
        public String toString() {
            String vertexList ="(";
            for (int i = 0; i < point.length; i++) {
                vertexList += point[i];
                if (i < point.length - 1) {
                    vertexList += ", ";
                }
            }
            vertexList += ")";
            return vertexList;
        }
    }