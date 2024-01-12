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
    private static final double EDGE_RES = 0.5;
    private static final int lBound = 477;
    private static final int rBound = 1000;
    private static final int bBound = 776;
    private static final int tBound = 150;
    private static final Font SCORE_FONT  = new Font(Font.SERIF, Font.PLAIN,  30);
    private static final Vector2D NEXT_FRUIT_POS = new Vector2D(1260, 200);
    Image background;
    Fruit curFruit, nextFruit;
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
        drawFruit(g2d, curFruit);
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

        nextFruit = new Fruit(NEXT_FRUIT_POS.x, NEXT_FRUIT_POS.y, 0, 0);
        curFruit = new Fruit((lBound+rBound)/2,100, 0, 0);
        curID = 1;
        frameTracker = new TimeTracker();
        timer = new Timer(DELAY, ev -> {
            update(frameTracker.timeFromLast());
            repaint();
        });
        timer.start();
    }

    public void update(double timeDelta) {

        for (Fruit f : fruits) {
            f.update(timeDelta);
            f.groundSupport = 0;
            if (f.getX()-f.getRadius() <= lBound){
                f.velocity.x = Math.abs(f.velocity.x) * EDGE_RES;
                f.setX(lBound + f.getRadius());
                f.groundSupport += 0.5;
            }else if (f.getX() + f.getRadius() >= rBound){
                f.velocity.x = -Math.abs(f.velocity.x) * EDGE_RES;
                f.setX(rBound - f.getRadius());
                f.groundSupport += 0.5;
            }

            // Check for bottom and top
            if (f.getY() + f.getRadius() >= bBound){
                f.velocity.y = 0;
                f.velocity.x *= Math.pow(EDGE_RES, timeDelta);
                f.setY(bBound-f.getRadius());
                f.groundSupport += 1;
            } else if (f.getY() - f.getRadius() <= tBound){
                System.out.println("Lost");
            }
        }
        List<FruitCollision> collisions = Fruit.getCollidingFruits(fruits);
        for(FruitCollision c : collisions){
            if(c.fruit1.type == c.fruit2.type){
                combineFruits(c);
            } else {
                Fruit.repel(c.fruit1, c.fruit2, PhysicsObject.FORCE_BIAS, timeDelta);
                Fruit.correctClipping(c.fruit1, c.fruit2);
                if(c.fruit1.groundSupport>=1&& c.fruit2.groundSupport<1) {
                    c.fruit2.groundSupport += 1.5-Math.abs(c.fruit2.getPos().directionVector(c.fruit1.getPos()).x);
                } else if (c.fruit2.groundSupport>=1&& c.fruit1.groundSupport<1){
                    c.fruit1.groundSupport += 1.5-Math.abs(c.fruit1.getPos().directionVector(c.fruit2.getPos()).x);
                }

            }
        }
    }
    public void combineFruits(FruitCollision collision){
        collision.fruit1.upgrade(collision.fruit2);
        fruits.remove(collision.fruit2);
        score += collision.fruit1.getScore();
    }

    public int randFruit(){
        int ret = 0;
        if(score == 0){
            ret = (int) (Math.random()*2);
        } else if (score <=100){
            ret = (int) (Math.random()*3);
        } else{
            ret = (int) (Math.random() * 4);
        }
        return ret;
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }


    @Override
    public void mouseClicked(MouseEvent e) {
        //creates a new fruit
        fruits.add(curFruit.clone(++curID));
        curFruit = nextFruit.clone();
        curFruit.setX(Math.min(Math.max(e.getX(), lBound+ nextFruit.getRadius()),rBound- nextFruit.getRadius()));
        nextFruit.setType(randFruit());
        update(frameTracker.timeFromLast());
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //tracks the mouse movement
        curFruit.setX(Math.min(Math.max(e.getX(), lBound+ curFruit.getRadius()),rBound- curFruit.getRadius()));
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
