public class Vector2D {
    public double x, y;
    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }
    public Vector2D(Vector2D v1, Vector2D v2){
        this.x = v1.x - v2.x;
        this.y = v1.y-v2.y;
    }

    public double distanceFrom(Vector2D other){
        return Math.sqrt(Math.pow((this.x-other.x), 2) + Math.pow((this.y-other.y), 2));
    }

    public double magnitude(){
        return Math.sqrt(x*x+y*y);
    }

    public void add(Vector2D other){
        this.x += other.x;
        this.y += other.y;
    }

    static Vector2D sum(Vector2D vec1, Vector2D vec2){
        return new Vector2D(vec1.x+vec2.x, vec1.y+ vec2.y);
    }
    static Vector2D difference(Vector2D vec1, Vector2D vec2){return new Vector2D(vec1.x-vec2.x, vec1.y - vec2.y);}
    static public Vector2D normalize(Vector2D v2d){
        return v2d.scaled(1/v2d.magnitude());
    }

    public void multiply(Vector2D other){
        this.x *= other.x;
        this.y *= other.y;
    }
    public Vector2D scaled(double factor){
        return new Vector2D(x*factor, y*factor);
    }

    public Vector2D directionVector(Vector2D other){
        Vector2D dir = Vector2D.difference(other, this);
        return Vector2D.normalize(dir);
    }

    public double dot(Vector2D other){
        return x*other.x + y*other.y;
    }

    @Override
    public String toString(){
        return String.format("(%.2f, %.2f)", x, y);
    }


}
