package util;

public class Vector2D {
    public double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //Description: Calculates the magnitude of the current vector (Euclidean distance from origin)
    //Parameters: None
    //Return: The magnitude
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }


    //Description: Add another vector to the current vector
    //Parameters: The other vector to add
    //Return: None
    public void add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
    }

    //Description: Calculate the vector sum of 2 vectors
    //Parameters: The 2 vectors to add
    //Return: The resulting vector sum
    static public Vector2D sum(Vector2D vec1, Vector2D vec2) {
        return new Vector2D(vec1.x + vec2.x, vec1.y + vec2.y);
    }

    //Description: Calculate the vector difference of 2 vectors
    //Parameters: The 2 vectors to subtract
    //Return: the resulting vector difference
    static public Vector2D difference(Vector2D vec1, Vector2D vec2) {
        return new Vector2D(vec1.x - vec2.x, vec1.y - vec2.y);
    }

    //Description: Normalize a vector (convert it into a unit vector)
    //Parameters: The vector to convert
    //Return: The resulting normalized vector
    static public Vector2D normalize(Vector2D v2d) {
        return v2d.scaled(1 / v2d.magnitude());
    }

    //Description: Calculate the Euclidean distance from this vector to another
    //Parameters: The other vector
    //Return: The distance between vectors
    public double distanceFrom(Vector2D other) {
        return Vector2D.difference(this, other).magnitude();
    }

    //Description: Scale the current vector by a scalar factor
    //Parameters: The scalar factor
    //Return: A new scaled vector
    public Vector2D scaled(double factor) {
        return new Vector2D(x * factor, y * factor);
    }

    //Description: Calculate the direction vector starting from the current vector and pointing to another vector
    //Parameters: The other vector
    //Return: The direction vector (as a unit vector)
    public Vector2D directionVector(Vector2D other) {
        Vector2D dir = Vector2D.difference(other, this);
        return Vector2D.normalize(dir);
    }

    //Description: calculate the dot product between the current vector and another vector
    //Parameters: The other vector
    //Return: The resulting dot product
    public double dot(Vector2D other) {
        return x * other.x + y * other.y;
    }
}
