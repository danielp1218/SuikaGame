import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Fruit implements PhysicsObject {
    static final FruitBody[] fruitBodies = {
            new FruitBody(17, 11,3, 1, "images/cherry.png"),
            new FruitBody(22, 4,4, 3, "images/strawberry.png"),
            new FruitBody(28, 0,5, 6, "images/blueberry.png"),
            new FruitBody(37, 8,6, 10, "images/dekopon.png"),
            new FruitBody(45, 8,7, 15, "images/orange.png"),
            new FruitBody(54, 6,8, 21, "images/apple.png"),
            new FruitBody(64, 7,9, 28, "images/pear.png"),
            new FruitBody(76, 2,10, 36, "images/peach.png"),
            new FruitBody(100, 27,11, 45, "images/pineapple.png"),
            new FruitBody(120, 0,12, 55, "images/melon.png"),
            new FruitBody(150, 0,13, 66, "images/watermelon.png")
    };
    final static double RESTITUTION = 0.7;
    final static double GROUND_BIAS = 1;
    final static int GRACE_PERIOD = 2000;
    static double terminalVelocity = 600;
    public double rotation;
    public Vector2D velocity;
    public double mass;
    public int type;
    public CircleCollider body;
    public double groundSupport;
    public boolean gracePeriod;


    //Getters and setters
    double getRadius(){
        return body.radius;
    }
    int getX(){
        return (int)body.center.x;
    }
    void setX(double x){
        body.center.x = x;
    }
    int getY(){
        return (int)body.center.y;
    }
    void setY(double y){
        body.center.y = y;
    }
    Vector2D getPos(){
        return body.center;
    }
    Image getImage() {
        return fruitBodies[type].img;
    }
    int getScore() {
        return fruitBodies[type].points;
    }
    void setType(int newType){
        type = Math.min(fruitBodies.length-1, newType);
        body.radius = fruitBodies[type].radius;
        mass = fruitBodies[type].mass;
    }
    public int getImageSize(){
        return (int)(getRadius() + fruitBodies[type].addon)*2;
    }


    static public List<FruitCollision> getCollidingFruits(List<Fruit> fruits) {
        List<FruitCollision> collisions = new ArrayList<>();
        for(int i = 0 ; i < fruits.size() ;++i){
            for(int j = i + 1; j < fruits.size() ; ++j){
                if(fruits.get(i).body.isColliding(fruits.get(j))){
                    collisions.add(new FruitCollision(fruits.get(i), fruits.get(j)));
                }
            }
        }
        return collisions;
    }
    static public void repel(Fruit fruit1, Fruit fruit2, Vector2D bias, double timeDelta) {
        Vector2D dir = fruit1.getPos().directionVector(fruit2.getPos());
        double speed = dir.dot(Vector2D.difference(fruit1.velocity, fruit2.velocity))*RESTITUTION;

        if(speed < 0){
            return;
        }
        fruit1.velocity = fruit1.velocity.scaled(Math.pow(RESTITUTION, timeDelta));
        fruit2.velocity = fruit2.velocity.scaled(Math.pow(RESTITUTION, timeDelta));

        if(fruit1.groundSupport>=1 && fruit2.groundSupport<1){
            fruit1.velocity.add(Vector2D.sum(dir,bias).scaled(-fruit2.mass/(fruit1.mass+fruit2.mass+GROUND_BIAS)*speed));
            fruit2.velocity.add(Vector2D.sum(dir,bias).scaled((fruit1.mass+GROUND_BIAS)/(fruit1.mass+fruit2.mass+GROUND_BIAS)*speed));
        } else if (fruit2.groundSupport>=1&& fruit1.groundSupport<1){
            fruit1.velocity.add(Vector2D.sum(dir,bias).scaled(-(fruit2.mass+GROUND_BIAS)/(fruit1.mass+fruit2.mass+GROUND_BIAS)*speed));
            fruit2.velocity.add(Vector2D.sum(dir,bias).scaled((fruit1.mass)/(fruit1.mass+fruit2.mass+GROUND_BIAS)*speed));
        } else{
            fruit1.velocity.add(Vector2D.sum(dir,bias).scaled(-(fruit2.mass)/(fruit1.mass+fruit2.mass)*speed));
            fruit2.velocity.add(Vector2D.sum(dir,bias).scaled((fruit1.mass)/(fruit1.mass+fruit2.mass)*speed));
        }
    }


    static public void correctClipping(Fruit fruit1, Fruit fruit2) {
        double overlap = fruit1.getRadius() + fruit2.getRadius() - fruit1.getPos().distanceFrom(fruit2.getPos());
        fruit1.getPos().add(fruit2.getPos().directionVector(fruit1.getPos()).scaled(overlap/2));
        fruit2.getPos().add(fruit1.getPos().directionVector(fruit2.getPos()).scaled(overlap/2));
    }

    public void upgrade(Fruit f){
        if (type < fruitBodies.length-1){
            ++type;
            body.radius = fruitBodies[type].radius;
            mass = fruitBodies[type].mass;
            body.center = Vector2D.sum(body.center, f.body.center).scaled(0.5);
            velocity = Vector2D.sum(velocity, f.velocity).scaled(0.5);
        }
    }

    @Override
    public void render(Graphics2D g2d){
        g2d.rotate(Math.toRadians(rotation), getX(), getY());

        g2d.drawImage(getImage(), getX() - getImageSize()/2,getY() - getImageSize()/2, getImageSize(), getImageSize(), null);
        g2d.drawOval((int)(getX() - getRadius()), getY() - (int)getRadius(), (int)getRadius()*2, (int)getRadius()*2);
        g2d.rotate(Math.toRadians(-rotation), getX(), getY());
    }

    @Override
    public void update(double timeDelta){
        if(groundSupport<1){
            velocity.add(PhysicsObject.GRAVITY.scaled(timeDelta));
        } else if (velocity.y < 0 && velocity.y >-5){
            velocity.y = 0;
        }
        if(velocity.magnitude() > terminalVelocity){
            velocity = Vector2D.normalize(velocity).scaled(terminalVelocity);
        }
        body.center.add(velocity.scaled(timeDelta));
        rotation += velocity.x*180/(Math.PI*getRadius())*timeDelta;
    }


    public Fruit(double x,double y, int fruit, int id){
        // TODO: fix hardcode later
        type = fruit;
        velocity = new Vector2D(0,0);
        groundSupport = 0;
        body = new CircleCollider(new Vector2D(x, y),fruitBodies[type].radius, id);
        mass = fruitBodies[type].mass;
        rotation = 0;
        gracePeriod = true;
        Timer gracePeriodTimer = new Timer(GRACE_PERIOD, event -> gracePeriod = false);
        gracePeriodTimer.setRepeats(false); // Only execute once
        gracePeriodTimer.start();
    }

    public Fruit clone(){
        return new Fruit(this.getX(), this.getY(), this.type, this.body.id);
    }
    public Fruit clone(int id){
        return new Fruit(this.getX(), this.getY(), this.type, id);
    }


}
