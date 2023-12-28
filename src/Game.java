import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private static final int DELAY = 10;
    Image background;
    Fruit nextFruit;
    TimeTracker frameTracker;
    int curID;
    List<Fruit> fruits;
    List<LineCollider> lineColliders;
    Timer timer;
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        g2d.drawImage(background, 0, 0, null);

        g2d.setColor(Color.BLACK);

        for(LineCollider line : lineColliders){
            g2d.drawLine((int)line.point1.x, (int)line.point1.y, (int)line.point2.x, (int)line.point2.y);
        }

        for(Fruit fruit : fruits){
            // INT CASTING MAY CAUSE BUG LATER
            g2d.drawOval((int)(fruit.getX() - fruit.getRadius()), (int)(fruit.getY() - fruit.getRadius()), (int)(2*fruit.getRadius()), (int)(2*fruit.getRadius()));
            g2d.drawString(fruit.acceleration.toString(), (int)(fruit.getX() - fruit.getRadius()), (int)(fruit.getY() - fruit.getRadius()));
        }
        g2d.drawOval((int)(nextFruit.getX() - nextFruit.getRadius()), (int)(nextFruit.getY() - nextFruit.getRadius()), (int)(2*nextFruit.getRadius()), (int)(2*nextFruit.getRadius()));
    }

    public Game(){
        setPreferredSize(new Dimension(1472, 800));
        addMouseListener(this);
        addMouseMotionListener(this);

        try{
            background = ImageIO.read(new File("images/background.png"));
        }catch (IOException e) {
            System.out.println("Couldn't load images");
        }



        fruits = new ArrayList<>();
        lineColliders = new ArrayList<>();
        lineColliders.add(new LineCollider(new Vector2D(477, 779), new Vector2D(1000, 779), new Vector2D(0, -1)));
        lineColliders.add(new LineCollider(new Vector2D(477, 776), new Vector2D(1000, 776), new Vector2D(0, -1)));
        lineColliders.add(new LineCollider(new Vector2D(477, 776), new Vector2D(477, 150), new Vector2D(1, 0)));
        lineColliders.add(new LineCollider(new Vector2D(1000, 776), new Vector2D(1000, 150), new Vector2D(-1, 0)));

        nextFruit = new Fruit(400, 100, 0, 0);
        curID = 1;
        frameTracker = new TimeTracker();
        timer = new Timer(DELAY, ev -> {
            update(frameTracker.timeFromLast());
            repaint();
        });
        timer.start();
    }

    public void update(double timeDelta){
        System.out.println(timeDelta);
        for(Fruit f : fruits){
            f.update(timeDelta);
        }
        for(int i = 0 ; i < fruits.size() ; ++i){
            Fruit f = fruits.get(i);
            // CONSIDER CHANGING TIME DELTA TO BE DYNAMIC ex. System.nanoTime()

            if(f.moving){
                List<Fruit> colliding = f.body.getFruitsColliding(fruits);
                List<Intersection> linesColliding = f.body.getLinesColliding(lineColliders);
                if(colliding.isEmpty() && linesColliding.isEmpty()){
                    f.acceleration.y = PhysicsObject.GRAVITY;
                } else {
                    f.acceleration = f.velocity.scale(-50*timeDelta*(colliding.size()+linesColliding.size()));
                }
                if (!linesColliding.isEmpty() && linesColliding.stream().allMatch(intersection -> intersection.lineCollider.repelDirection.y != -1)){
                    f.acceleration.y = PhysicsObject.GRAVITY;
                }


                for(Fruit collidingFruit : colliding){
                    Vector2D dir = new Vector2D(f.getPos(), collidingFruit.getPos());
                    f.getPos().add(dir.scale((f.getRadius()+collidingFruit.getRadius())/dir.magnitude()-1));
                    collidingFruit.moving = true;
                    if(collidingFruit.type == f.type && f.type < Fruit.fruitBodies.length-1){
                        collidingFruit.upgrade(f);
                        fruits.remove(f);
                        --i;
                        break;
                    } else {
                        f.repel(collidingFruit, collidingFruit.mass/f.mass*PhysicsObject.FORCE_CONSTANT*timeDelta);
                        //collidingFruit.repel(f, f.mass/collidingFruit.mass*0.2);
                    }
                    //System.out.println(f.velocity.x + " " + f.velocity.y);
                }

                for(Intersection intersect : linesColliding) {
                    f.getPos().add(intersect.lineCollider.repelDirection.scale(f.getRadius()-intersect.pos.distanceFrom(f.getPos())));
                    f.velocity.multiply(intersect.lineCollider.repelDirection.absreverse());
                    //f.repel(intersect.pos, 0.1);
                }
            }
        }

    }

    public int randFruit(){
        int ret = (int) (Math.random()*4);
        return ret;
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }


    @Override
    public void mouseClicked(MouseEvent e) {
        //creates a new fruit
        //adds a small bias to prevent pixel perfect placement that messes with the physics
        fruits.add(new Fruit(nextFruit.getX()+Math.random()-0.5, nextFruit.getY(), nextFruit.type, curID++));
        nextFruit.setType(randFruit());
        update(frameTracker.timeFromLast());
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //tracks the mouse movement
        nextFruit.setX(e.getX());
    }

    // unused interface methods
    public void mouseDragged(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e){}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
