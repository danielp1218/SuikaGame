import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LineCollider {
    Vector2D point1, point2, repelDirection;
    boolean vertical;
    double slope, yIntersect;

    // old unused code
    /*
    @Override
    public IntersectionPoint colliding(Collider c) {
        if(c instanceof CircleCollider){
            return c.colliding(this);
        } else if (c instanceof LineCollider){
            LineCollider other = (LineCollider) c;
            if(other.id == this.id){
                return null;
            }
            if(vertical && other.vertical){
                return null;
            } else if (vertical){
                // given vertical line x = a
                //                 and y = mx+b
                // Intersection is at y = ma+b

                double y = other.slope*point1.x + other.yIntersect;
                if(Math.max(point2.y, point1.y) >= y && y>= Math.min(point2.y, point1.y)){
                    return new IntersectionPoint(point1.x, y, false);
                }
                return null;
            } else if (other.vertical){
                double y = slope*other.point1.x + yIntersect;
                if(Math.max(other.point2.y, other.point1.y) >= y && y>= Math.min(other.point2.y, other.point1.y)){
                    return new IntersectionPoint(other.point1.x, y, false);
                }
                return null;
            }

            // given lines y = ax + b
            //         and y = cx + d
            //
            // Intersection is at x = (d-b)/(a-c)

            double x = (other.yIntersect - yIntersect)/(slope-other.slope);
            if (point2.x >= x && x >= point1.x && other.point2.x >= x && x >= other.point1.x){
                return new IntersectionPoint(x, slope*x+yIntersect, false);
            }
        }
        return null;
    }

    @Override
    public List<IntersectionPoint> getColliding(List<Collider> colliderList) {
        List<IntersectionPoint> colliding = new ArrayList<>();
        for (Collider currentCollider : colliderList) {
            IntersectionPoint p = this.colliding(currentCollider);
            if (p != null) {
                colliding.add(p);
            }
        }
        return colliding;
    }
*/
    public LineCollider(Vector2D p1, Vector2D p2, Vector2D dir) {
        if (p1.x < p2.x) {
            this.point1 = p1;
            this.point2 = p2;
        } else if (p1.x > p2.x){
            this.point1 = p2;
            this.point2 = p1;
        } else if (p1.y < p2.y){
            this.point1 = p1;
            this.point2 = p2;
        } else{
            this.point1 = p2;
            this.point2 = p1;
        }

        if (p1.x == p2.x) {
            this.vertical = true;
        } else {
            this.vertical = false;
            this.slope = (p2.y - p1.y) / (p2.x - p1.x);
            this.yIntersect = p1.y - slope * p1.x;
        }
        this.repelDirection = dir;
    }


}
