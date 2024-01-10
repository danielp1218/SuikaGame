import java.util.ArrayList;
import java.util.List;

public class CircleCollider {
    int id;
    Vector2D center;
    double radius;

    public boolean isColliding(Fruit other) {
        if(other.body.id == this.id){
            return false;
        }
        if (this.center.distanceFrom(other.body.center) < this.radius + other.body.radius) {
            return true;
        }
        return false;
    }


    public CircleCollider(Vector2D center, double radius, int id) {
        this.center = center;
        this.radius = radius;
        this.id = id;
    }
}
