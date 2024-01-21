import java.awt.Graphics2D;
public interface PhysicsObject {
    Vector2D GRAVITY = new Vector2D(0,700);
    Vector2D FORCE_BIAS = new Vector2D(0, 0.2);

    void update(double timeDelta);
    void render(Graphics2D g2d);
}
