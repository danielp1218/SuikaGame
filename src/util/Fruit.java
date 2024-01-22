package util;

import javax.swing.Timer;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class Fruit implements PhysicsObject {
    static final FruitBody[] fruitBodies = {
            new FruitBody(17, 11, 3, 1, "images/fruits/cherry.png"),
            new FruitBody(22, 4, 5, 3, "images/fruits/strawberry.png"),
            new FruitBody(28, 0, 7, 6, "images/fruits/blueberry.png"),
            new FruitBody(37, 8, 9, 10, "images/fruits/dekopon.png"),
            new FruitBody(50, 9, 11, 15, "images/fruits/orange.png"),
            new FruitBody(63, 7, 13, 21, "images/fruits/apple.png"),
            new FruitBody(76, 8, 15, 28, "images/fruits/pear.png"),
            new FruitBody(100, 3, 17, 36, "images/fruits/peach.png"),
            new FruitBody(120, 27, 19, 45, "images/fruits/pineapple.png"),
            new FruitBody(140, 0, 21, 55, "images/fruits/melon.png"),
            new FruitBody(160, 0, 23, 66, "images/fruits/watermelon.png")
    };
    final private static double RESTITUTION = 0.8;
    final private static double GROUND_BIAS = 10;
    final private static int GRACE_PERIOD = 2000;
    final private static double terminalVelocity = 600;
    final private CircleCollider body;
    private double rotation;
    private double mass;
    private int type;
    public Vector2D velocity;
    public double groundSupport;
    public boolean gracePeriod;


    //Getters and setters
    public double getRadius() {
        return body.radius;
    }

    public int getX() {
        return (int) body.center.x;
    }

    public void setX(double x) {
        body.center.x = x;
    }

    public int getY() {
        return (int) body.center.y;
    }

    public void setY(double y) {
        body.center.y = y;
    }

    public Vector2D getPos() {
        return body.center;
    }

    public Image getImage() {
        return fruitBodies[type].img;
    }

    public int getScore() {
        return fruitBodies[type].points;
    }

    public void setType(int newType) {
        type = Math.min(fruitBodies.length - 1, newType);
        body.radius = fruitBodies[type].radius;
        mass = fruitBodies[type].mass;
    }

    public int getType() {
        return this.type;
    }

    public int getImageSize() {
        return (int) (getRadius() + fruitBodies[type].addon) * 2;
    }

    public int getID() {
        return body.id;
    }

    //Description: gets which fruits are colliding with each other
    //Parameters: A list of all the fruits in the game
    //Return: an Arraylist of a wrapper class containing the colliding fruit pairs
    static public List<FruitCollision> getCollidingFruits(List<Fruit> fruits) {
        List<FruitCollision> collisions = new ArrayList<>();
        for (int i = 0; i < fruits.size(); ++i) {
            for (int j = i + 1; j < fruits.size(); ++j) {
                if (fruits.get(i).body.isColliding(fruits.get(j))) {
                    collisions.add(new FruitCollision(fruits.get(i), fruits.get(j)));
                }
            }
        }
        //Collections.shuffle(collisions);
        return collisions;
    }

    //Description: repels 2 fruits from each other
    //Parameters: 2 fruits to repel, and the time from the last frame in seconds
    //Return: None
    static public void repel(Fruit fruit1, Fruit fruit2, double timeDelta) {
        Vector2D dir = fruit1.getPos().directionVector(fruit2.getPos());
        double speed = dir.dot(Vector2D.difference(fruit1.velocity, fruit2.velocity)) * RESTITUTION;

        if (speed < 0) {
            return;
        }
        fruit1.velocity = fruit1.velocity.scaled(Math.pow(RESTITUTION, timeDelta));
        fruit2.velocity = fruit2.velocity.scaled(Math.pow(RESTITUTION, timeDelta));
        fruit1.velocity.add(dir.scaled(-(fruit2.mass + fruit2.groundSupport * GROUND_BIAS) / (fruit1.mass + fruit2.mass + fruit1.groundSupport * GROUND_BIAS) * RESTITUTION * speed));
        fruit2.velocity.add(dir.scaled((fruit1.mass + fruit1.groundSupport * GROUND_BIAS) / (fruit1.mass + fruit2.mass + fruit2.groundSupport * GROUND_BIAS) * RESTITUTION * speed));

    }

    //Description: Corrects when 2 fruits clip into each other
    //Parameters: The 2 fruits that are clipping
    //Return: None
    static public void correctClipping(Fruit fruit1, Fruit fruit2) {
        double overlap;
        if (fruit1.getY() + fruit1.getRadius() < fruit2.getY()) {
            fruit1.getPos().add(PhysicsObject.CORRECTION_BIAS);
            overlap = fruit1.getRadius() + fruit2.getRadius() - fruit1.getPos().distanceFrom(fruit2.getPos());
            fruit1.getPos().add(fruit2.getPos().directionVector(fruit1.getPos()).scaled(overlap));
        } else if (fruit1.getY() > fruit2.getY() + fruit2.getRadius()) {
            fruit2.getPos().add(PhysicsObject.CORRECTION_BIAS);
            overlap = fruit1.getRadius() + fruit2.getRadius() - fruit1.getPos().distanceFrom(fruit2.getPos());
            fruit2.getPos().add(fruit1.getPos().directionVector(fruit2.getPos()).scaled(overlap));
        } else {
            overlap = fruit1.getRadius() + fruit2.getRadius() - fruit1.getPos().distanceFrom(fruit2.getPos());
            fruit1.getPos().add(fruit2.getPos().directionVector(fruit1.getPos()).scaled(overlap / 2));
            fruit2.getPos().add(fruit1.getPos().directionVector(fruit2.getPos()).scaled(overlap / 2));
        }
    }

    //Description: Upgrades the current fruit
    //Parameters: The other fruit the current fruit is colliding with
    //Return: None
    public void upgrade(Fruit f) {
        if (type < fruitBodies.length - 1) {
            ++type;
            body.radius = fruitBodies[type].radius;
            mass = fruitBodies[type].mass;
            body.center = Vector2D.sum(body.center, f.body.center).scaled(0.5);
            velocity = Vector2D.sum(velocity, f.velocity).scaled(0.5);
        }
    }

    //Description: draws the fruit onto the graphics object
    //Parameters: The graphics object to draw onto
    //Return: None
    @Override
    public void render(Graphics2D g2d) {
        g2d.rotate(Math.toRadians(rotation), getX(), getY());
        g2d.drawImage(getImage(), getX() - getImageSize() / 2, getY() - getImageSize() / 2, getImageSize(), getImageSize(), null);
        g2d.rotate(Math.toRadians(-rotation), getX(), getY());
    }

    //Description: Updates the current fruits velocity, rotation, and position
    //Parameters: the time from the last frame in seconds
    //Return: None
    @Override
    public void update(double timeDelta) {
        if (groundSupport < 0.6) {
            velocity.add(PhysicsObject.GRAVITY.scaled(timeDelta));
        } else if (velocity.y < 0 && velocity.y > -200) {
            velocity.y = 0;
        }
        if (velocity.magnitude() > terminalVelocity) {
            velocity = Vector2D.normalize(velocity).scaled(terminalVelocity);
        }
        body.center.add(velocity.scaled(timeDelta));
        rotation += velocity.x * 180 / (Math.PI * getRadius()) * timeDelta;
    }

    public Fruit(double x, double y, int fruit, int id) {
        // TODO: fix hardcode later
        type = fruit;
        velocity = new Vector2D(0, 0);
        groundSupport = 0;
        body = new CircleCollider(new Vector2D(x, y), fruitBodies[type].radius, id);
        mass = fruitBodies[type].mass;
        rotation = 0;
        gracePeriod = true;
        Timer gracePeriodTimer = new Timer(GRACE_PERIOD, event -> gracePeriod = false);
        gracePeriodTimer.setRepeats(false); // Only execute once
        gracePeriodTimer.start();
    }

    //Description: Clone the current fruit, using the same id
    //Parameters: None
    //Return: The cloned fruit
    public Fruit clone() {
        return new Fruit(this.getX(), this.getY(), this.type, this.body.id);
    }

    //Description: Clone the current fruit, using a different id
    //Parameters: The id of the fruit
    //Return: The cloned fruit
    public Fruit clone(int id) {
        return new Fruit(this.getX(), this.getY(), this.type, id);
    }
}
