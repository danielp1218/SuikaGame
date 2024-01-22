package util;

public class CircleCollider {
    final int id;
    Vector2D center;
    double radius;

    //Description: Checks if the collider is colliding into a fruit
    //Parameters: The fruit to check if it's colliding with
    //Return: If it is colliding, as a boolean
    public boolean isColliding(Fruit other) {
        if (other.getID() == this.id) {
            return false;
        }
        return this.center.distanceFrom(other.getPos()) < this.radius + other.getRadius();
    }

    public CircleCollider(Vector2D center, double radius, int id) {
        this.center = center;
        this.radius = radius;
        this.id = id;
    }
}
