import java.util.List;
public interface PhysicsObject {
    enum State {
        MOVING, FROZEN, TRANSITION
    }
    Vector2D GRAVITY = new Vector2D(0,1000);
    double FORCE_CONSTANT = 7;

    void update(double timeDelta);
}
