import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Fruit implements PhysicsObject {
    static final FruitBody fruitBodies[] = {
            new FruitBody(15, 3, 1, "images/testfruit.png"),
            new FruitBody(25, 3.2, 3, "images/testfruit.png"),
            new FruitBody(35, 3.4, 6, "images/testfruit.png"),
            new FruitBody(45, 3.6, 10, "images/testfruit.png"),
            new FruitBody(55, 3.8, 15, "images/testfruit.png"),
            new FruitBody(70, 4, 21, "images/testfruit.png"),
            new FruitBody(90, 4.2, 28, "images/testfruit.png"),
            new FruitBody(110, 4.4, 36, "images/testfruit.png"),
            new FruitBody(130, 4.6, 45, "images/testfruit.png"),
            new FruitBody(150, 4.8, 55, "images/testfruit.png"),
            new FruitBody(180, 4.8, 66, "images/testfruit.png")
    };
    public double rotationalVelocity;
    public double rotation;
    public Vector2D velocity;
    static double restitution = 0.8;
    static double terminalVelocity = 500;
    public double mass;
    public int type;
    public CircleCollider body;
    public boolean moving;

    double getRadius(){
        return body.radius;
    }
    double getX(){
        return body.center.x;
    }
    void setX(double x){
        body.center.x = x;
    }
    double getY(){
        return body.center.y;
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
    static public List<FruitCollision> getCollidingFruits(List<Fruit> fruits) {
        List<FruitCollision> collisions = new ArrayList<FruitCollision>();
        for(int i = 0 ; i < fruits.size() ;++i){
            for(int j = i + 1; j < fruits.size() ; ++j){
                if(fruits.get(i).body.isColliding(fruits.get(j))){
                    collisions.add(new FruitCollision(fruits.get(i), fruits.get(j)));
                }
            }
        }
        return collisions;
    }
    static public void repel(Fruit fruit1, Fruit fruit2, double energyConserved, double timeDelta) {
        Vector2D dir = fruit1.getPos().directionVector(fruit2.getPos());
        double speed = dir.dot(Vector2D.difference(fruit1.velocity, fruit2.velocity));
        if(speed < 0){
            return;
        }
        if(fruit1.getX() < fruit2.getX()){
            fruit1.rotationalVelocity += -speed*timeDelta*5;
            fruit2.rotationalVelocity += speed*timeDelta*5;
        } else{
            fruit1.rotationalVelocity += speed*timeDelta*5;
            fruit2.rotationalVelocity += -speed*timeDelta*5;
        }


        fruit1.velocity = dir.scaled(-fruit1.mass/(fruit1.mass+fruit2.mass)*(energyConserved*timeDelta-timeDelta + 1)*speed);
        fruit2.velocity = dir.scaled(fruit2.mass/(fruit1.mass+fruit2.mass)*energyConserved*speed);
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
        velocity.add(PhysicsObject.GRAVITY.scaled(timeDelta));
        if(velocity.magnitude() > terminalVelocity){
            velocity = velocity.normalized().scaled(terminalVelocity);
        }
        body.center.add(velocity.scaled(timeDelta));
        body.center.x += rotationalVelocity/180*getRadius()*timeDelta;
        rotation += rotationalVelocity*timeDelta;


    }





    public Fruit(double x,double y, int fruit, int id){
        // TODO: fix hardcode later
        type = fruit;
        velocity = new Vector2D(0,0);
        rotationalVelocity = 0;
        moving = true;
        body = new CircleCollider(new Vector2D(x, y),fruitBodies[type].radius, id);
        mass = fruitBodies[type].mass;
        rotation = 0;
        rotationalVelocity= 10;
    }


}
