import java.util.List;
public interface PhysicsObject {
    enum State {
        MOVING, FROZEN, TRANSITION
    }
    double GRAVITY = 50;
    double FORCE_CONSTANT = 7;

    void update(double timeDelta);
    void repel (Vector2D other, double force);
    void repel (Fruit other, double force);
}
