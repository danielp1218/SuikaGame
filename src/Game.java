import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private static final int DELAY = 10;
    private static final double EDGE_RES = 0.9;
    private static final int lBound = 477;
    private static final int rBound = 1000;
    private static final int bBound = 776;
    private static final int tBound = 150;
    private static final Font SCORE_FONT  = new Font(Font.SERIF, Font.PLAIN,  30);
    Image background;
    Fruit nextFruit;
    TimeTracker frameTracker;
    int curID;
    int score;
    List<Fruit> fruits;
    Timer timer;
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));


        g2d.drawImage(background, 0, 0, null);
        g2d.setFont(SCORE_FONT);
        g2d.setColor(Color.BLACK);
        g2d.drawString(""+score, 150, 150);

        for(Fruit fruit : fruits){
            drawFruit(g2d, fruit);
        }

        drawFruit(g2d, nextFruit);
    }

    public void drawFruit(Graphics2D g2d, Fruit fruit){
        g2d.rotate(Math.toRadians(fruit.rotation), fruit.getX(), fruit.getY());
        g2d.drawImage(fruit.getImage(), (int)(fruit.getX() - fruit.getRadius()), (int)(fruit.getY() - fruit.getRadius()), (int)(2*fruit.getRadius()), (int)(2*fruit.getRadius()), null);
        g2d.rotate(Math.toRadians(-fruit.rotation), fruit.getX(), fruit.getY());
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

        nextFruit = new Fruit(400, 100, 0, 0);
        curID = 1;
        frameTracker = new TimeTracker();
        timer = new Timer(DELAY, ev -> {
            update(frameTracker.timeFromLast());
            repaint();
        });
        timer.start();
    }

    public void update(double timeDelta) {
        System.out.println(timeDelta);
        for (Fruit f : fruits) {
            f.update(timeDelta);
            if (f.getX()-f.getRadius() <= lBound){
                f.velocity.x = Math.abs(f.velocity.x) * EDGE_RES;
                f.setX(lBound + f.getRadius());
                f.rotationalVelocity *= Math.pow(EDGE_RES, timeDelta);
            }else if (f.getX() + f.getRadius() >= rBound){
                f.velocity.x = -Math.abs(f.velocity.x) * EDGE_RES;
                f.setX(rBound - f.getRadius());
                f.rotationalVelocity *= Math.pow(EDGE_RES, timeDelta);
            }

            // Check for bottom and top
            if (f.getY() + f.getRadius() >= bBound){
                f.velocity.y = Math.abs(f.velocity.y) * EDGE_RES;
                f.velocity.x *= Math.pow(EDGE_RES, timeDelta);
                f.rotationalVelocity *= Math.pow(EDGE_RES, timeDelta);
                f.setY(bBound-f.getRadius());
            } else if (f.getY() - f.getRadius() <= tBound){
                System.out.println("Lost");
            }
        }
        List<FruitCollision> collisions = Fruit.getCollidingFruits(fruits);
        for(FruitCollision c : collisions){
            if(c.fruit1.type == c.fruit2.type){
                combineFruits(c);
            } else {
                Fruit.repel(c.fruit1, c.fruit2, Fruit.restitution, timeDelta);
                Fruit.correctClipping(c.fruit1, c.fruit2);
            }
        }

    }
    public void combineFruits(FruitCollision collision){
        collision.fruit1.upgrade(collision.fruit2);
        fruits.remove(collision.fruit2);
        score += collision.fruit1.getScore();
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
        nextFruit.setX(Math.min(Math.max(e.getX(), lBound),rBound));
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
