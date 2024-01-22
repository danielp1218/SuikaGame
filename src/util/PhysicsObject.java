package util;

import java.awt.Graphics2D;

public interface PhysicsObject {
    Vector2D GRAVITY = new Vector2D(0, 500);
    Vector2D CORRECTION_BIAS = new Vector2D(0, 1);

    void update(double timeDelta);

    void render(Graphics2D g2d);
}
