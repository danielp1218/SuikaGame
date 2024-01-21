import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Game extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private static final int DELAY = 10;
    private static final double EDGE_RES = 0.5;
    private static final int lBound = 477;
    private static final int rBound = 1000;
    private static final int bBound = 779;
    private static final int tBound = 185;
    private static final Vector2D NEXT_FRUIT_POS = new Vector2D(1260, 200);
    private static final int DROP_DELAY = 0;
    ActionListener screenManager;
    Image background;
    Fruit curFruit, nextFruit;
    TimeTracker frameTracker;
    int curID;
    int score;
    ArrayList<Integer> highscores;
    List<Fruit> fruits;
    Timer timer;
    Timer lock;
    boolean lockout, closeToLoss;
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        g2d.drawImage(background, 0, 0, 1472, 800, null);

        g2d.setColor(Color.BLACK);
        g2d.setFont(SuikaText.getFont(60));
        SuikaText.setTextCenter(g2d, Integer.toString(score), 0,0,220, 170);
        g2d.setFont(SuikaText.getFont(30));
        for(int i = 0; i < 3 ; ++i){
            if(i >= highscores.size()){
                g2d.drawString("-----", 200, 515+i*57);
            } else{
                g2d.drawString(highscores.get(i).toString(), 200, 515+i*57);
            }
        }
        if(!lockout){
            g2d.setColor(Color.WHITE);
            g2d.drawLine(curFruit.getX(), curFruit.getY(), curFruit.getX(), bBound);
            curFruit.render(g2d);
        }
        nextFruit.render(g2d);
        for(Fruit fruit : fruits){
            fruit.render(g2d);
        }
        if(closeToLoss){
            g2d.setStroke(new BasicStroke(10));
            g2d.setColor(new Color(255, 0,0, (int) (255*((Math.sin(frameTracker.time/100000000)+1)/2))));
            g2d.drawLine(rBound, tBound, lBound, tBound);
        }

    }




    public Game(ActionListener actionListener){
        screenManager = actionListener;

        setPreferredSize(new Dimension(1472, 800));
        addMouseListener(this);
        addMouseMotionListener(this);

        try{
            background = ImageIO.read(new File("images/background.png"));
        }catch (IOException e) {
            System.out.println("Couldn't load images");
        }

        fruits = new ArrayList<>();
        highscores = Leaderboard.read();

        closeToLoss = false;

        nextFruit = new Fruit(NEXT_FRUIT_POS.x, NEXT_FRUIT_POS.y, 0, 0);
        curFruit = new Fruit((lBound+rBound)/2,100, 0, 0);
        curID = 1;
        lockout = false;
        lock = new Timer(DROP_DELAY, arg0 -> {
            lockout = false;
            nextFruit.setType(randFruit());
        });
        lock.setRepeats(false);
        frameTracker = new TimeTracker();
        timer = new Timer(DELAY, ev -> {
            update(frameTracker.timeFromLast());
            repaint();
        });
        timer.start();
    }

    public void update(double timeDelta) {
        closeToLoss = false;
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
            } else if (f.getY() - f.getRadius() < tBound){
                if(!f.gracePeriod){
                    lose();
                    return;
                }
            }
        }
        List<FruitCollision> collisions = Fruit.getCollidingFruits(fruits);
        Set<Fruit> disabled = new HashSet<>();
        for(FruitCollision c : collisions){
            if(disabled.contains(c.fruit1) || disabled.contains(c.fruit2)){
                continue;
            }
            if(c.fruit1.type == c.fruit2.type){
                combineFruits(c);
                disabled.add(c.fruit1);
                disabled.add(c.fruit2);
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
        for(Fruit f : fruits){
            if(f.getY() - f.getRadius() <= tBound+80 && f.groundSupport>0.5){
                closeToLoss = true;
            }
        }
    }
    public void combineFruits(FruitCollision collision){
        collision.fruit1.upgrade(collision.fruit2);
        fruits.remove(collision.fruit2);
        score += collision.fruit1.getScore();
    }

    public int randFruit(){
        int ret;
        if(score == 0){
            ret = (int) (Math.random()*2);
        } else if (score <=100){
            ret = (int) (Math.random()*3);
        } else{
            ret = (int) (Math.random() * 4);
        }
        return ret;
    }

    public void lose(){
        System.out.println("LOST");
        timer.stop();
        Leaderboard.save(score);
        screenManager.actionPerformed(new ActionEvent(this, 0, "Menu"));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!lockout){
            //creates a new fruit
            fruits.add(curFruit.clone(++curID));
            curFruit = nextFruit.clone();
            curFruit.setY(100);
            curFruit.setX(Math.min(Math.max(e.getX(), lBound+ nextFruit.getRadius()),rBound- nextFruit.getRadius()));

            repaint();
            lockout = true;
            lock.start();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //tracks the mouse movement
        curFruit.setX(Math.min(Math.max(e.getX(), lBound+ curFruit.getRadius()),rBound- curFruit.getRadius()));
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        //tracks the mouse movement
        curFruit.setX(Math.min(Math.max(e.getX(), lBound+ curFruit.getRadius()),rBound- curFruit.getRadius()));
    }

    // unused interface methods
    public void keyPressed(KeyEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e){}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
