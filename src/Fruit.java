import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Fruit implements PhysicsObject {
    static final FruitBody fruitBodies[] = {
            new FruitBody(15, 3, 1, "images/fruit1.png"),
            new FruitBody(25, 3.2, 3, "images/fruit2.png"),
            new FruitBody(40, 3.4, 6, "images/fruit3.png"),
            new FruitBody(55, 3.6, 10, "images/fruit4.png"),
            new FruitBody(70, 3.8, 15, "images/fruit5.png"),
            new FruitBody(85, 4, 21, "images/fruit6.png"),
            new FruitBody(115, 4.2, 28, "images/fruit1.png"),
            new FruitBody(135, 4.4, 36, "images/fruit2.png"),
            new FruitBody(160, 4.6, 45, "images/fruit3.png"),
            new FruitBody(185, 4.8, 55, "images/fruit4.png"),
            new FruitBody(210, 5, 66, "images/fruit5.png")
    };
    final static double RESTITUTION = 0.7;
    final static double GROUND_BIAS = 2;
    public double rotation;
    public Vector2D velocity;
    static double terminalVelocity = 600;
    public double mass;
    public int type;
    public CircleCollider body;
    public double groundSupport;

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
    void setID(int id){
        body.id = id;
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
        System.out.println(speed);
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
            body.center = new Vector2D((getX()+f.getX())/2, (getY()+f.getY())/2);
        }
    }
    public void setType(int newType){
        type = Math.min(fruitBodies.length-1, newType);
        body.radius = fruitBodies[type].radius;
        mass = fruitBodies[type].mass;
    }

    @Override
    public void update(double timeDelta){
        if(groundSupport<1){
            velocity.add(PhysicsObject.GRAVITY.scaled(timeDelta));
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
    }

    public Fruit clone(){
        return new Fruit(this.getX(), this.getY(), this.type, this.body.id);
    }
    public Fruit clone(int id){
        return new Fruit(this.getX(), this.getY(), this.type, id);
    }


}
