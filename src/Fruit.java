import java.util.ArrayList;
import java.util.List;

public class Fruit implements PhysicsObject {
    static final FruitBody fruitBodies[] = {
            new FruitBody(10, 3, 1, ""),
            new FruitBody(15, 3.2, 3, ""),
            new FruitBody(20, 3.4, 6, ""),
            new FruitBody(25, 3.6, 10, ""),
            new FruitBody(30, 3.8, 15, ""),
            new FruitBody(35, 4, 21, ""),
            new FruitBody(40, 4.2, 28, ""),
            new FruitBody(45, 4.4, 36, ""),
            new FruitBody(50, 4.6, 45, ""),
            new FruitBody(55, 4.8, 55, ""),
            new FruitBody(55, 4.8, 66, "")
    };

    public Vector2D velocity, acceleration;
    private double terminalVelocity = 80;
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
    Vector2D getPos(){
        return body.center;
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
        if(velocity.magnitude() > terminalVelocity){
            velocity = velocity.normalize().scale(terminalVelocity);
        }
        if(moving){
            velocity.add(acceleration.scale(Math.sqrt(timeDelta)));
            body.center.add(velocity.scale(Math.sqrt(timeDelta)));
            if(velocity.x < 1 && velocity.y < 1 && acceleration.x == 0 && acceleration.y == 0){
                moving = false;
            }
        }
    }

    @Override
    public void repel(Fruit other, double force) {
        velocity.add(body.center.directionVector(other.getPos()).scale(-force*velocity.distanceFrom(other.velocity)));
    }
    @Override
    public void repel(Vector2D other, double force) {
        velocity.add(body.center.directionVector(other).scale(-force*velocity.magnitude()));
    }

    public Fruit(double x,double y, int fruit, int id){
        // TODO: fix hardcode later
        type = fruit;
        acceleration = new Vector2D(0,50);
        velocity = new Vector2D(0,0);
        moving = true;
        body = new CircleCollider(new Vector2D(x, y),fruitBodies[type].radius, id);
        mass = fruitBodies[type].mass;
    }
}
