import java.util.ArrayList;
import java.util.List;

public class CircleCollider {
    int id;
    Vector2D center;
    double radius;

    public Fruit colliding(Fruit other) {
        if(other.body.id == this.id){
            return null;
        }
        if (this.center.distanceFrom(other.body.center) < this.radius + other.body.radius) {
            return other;
        }
        return null;
    }
    public Intersection colliding(LineCollider other) {
        if (other.vertical) {
            // circle: (x-k)^2 + (y-h)^2 = r^2
            // line: x = a
            // intersection is at: y = h [+-] sqrt(r^2 -(a-k)^2)

            double discriminant = radius*radius - Math.pow(other.point1.x - center.x, 2);
            if(discriminant < 0){
                return null;
            }
            double sol1 = center.y + Math.sqrt(discriminant);
            double sol2 = center.y - Math.sqrt(discriminant);
            if (other.point2.y >= sol1 && sol1 >= other.point1.y
                    || other.point2.y >= sol2 && sol2 >= other.point1.y) {
                return new Intersection(other.point1.x, center.y, other);
            }
            return null;
        }

        // For circle: (x-k)^2 + (y-h)^2 = r^2
        //   and line: y = mx + b
        // Intersection: x = (k + mh - mb [+-] sqrt((mb -mh - k)^2 +2bh + r^2 - h^2 - b^2 - k^2))/(m^2+1)
        double discriminant = Math.pow(other.slope * other.yIntersect - other.slope * center.y - center.x, 2) + radius * radius - Math.pow(other.yIntersect - center.y, 2) - center.x * center.x;

        if (discriminant < 0) {
            return null;
        }
        double avg = center.x + other.slope * center.y - other.slope * other.yIntersect;
        double sol1 = (avg + Math.sqrt(discriminant)) / (other.slope * other.slope + 1);
        double sol2 = (avg - Math.sqrt(discriminant)) / (other.slope * other.slope + 1);

        if (other.point2.x >= sol1 && sol1 >= other.point1.x && other.point2.x >= sol2 && sol2 >= other.point1.x ) {
            return new Intersection(avg, other.slope*avg+other.yIntersect, other);
        } else if (other.point2.x >= sol1 && sol1 >= other.point1.x){
            return new Intersection(sol1, other.slope*sol1+other.yIntersect, other);
        }else if (other.point2.x >= sol2 && sol2 >= other.point1.x){
            return new Intersection(sol2, other.slope*sol2+other.yIntersect, other);
        }
        return null;
    }

    public List<Fruit> getFruitsColliding(List<Fruit> fruits) {
        List<Fruit> colliding = new ArrayList<>();
        for (Fruit fruit : fruits) {
            Fruit p = this.colliding(fruit);
            if (p != null) {
                colliding.add(p);
            }
        }
        return colliding;
    }

    public List<Intersection> getLinesColliding(List<LineCollider> lines) {
        List<Intersection> colliding = new ArrayList<>();
        for (LineCollider line : lines) {
            Intersection p = this.colliding(line);
            if (p != null) {
                colliding.add(p);
            }
        }
        return colliding;
    }

    public CircleCollider(Vector2D center, double radius, int id) {
        this.center = center;
        this.radius = radius;
        this.id = id;
    }
}
