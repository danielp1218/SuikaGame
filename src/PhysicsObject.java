import java.util.List;
public interface PhysicsObject {
    enum State {
        MOVING, FROZEN, TRANSITION
    }
    Vector2D GRAVITY = new Vector2D(0,700);
    Vector2D FORCE_BIAS = new Vector2D(0, 0.2);
    double FORCE_CONSTANT = 7;


    void update(double timeDelta);
}
