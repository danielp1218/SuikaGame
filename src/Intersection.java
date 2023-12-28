public class Intersection {
    Vector2D pos;
    LineCollider lineCollider;

    public Intersection(double x, double y, LineCollider lineCollider){
        this.pos = new Vector2D(x, y);
        this.lineCollider = lineCollider;
    }
}
