package screens;

import util.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.awt.event.KeyListener;

public class Game extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private static final int DELAY = 10;
    private static final double EDGE_RES = 0.3;
    private static final int lBound = 477;
    private static final int rBound = 1000;
    private static final int bBound = 779;
    private static final int tBound = 185;
    private static final Vector2D NEXT_FRUIT_POS = new Vector2D(1260, 200);
    private static final int DROP_DELAY = 500;
    final ActionListener screenManager;
    final Fruit nextFruit;
    final TimeTracker frameTracker;
    final List<Fruit> fruits;
    final Timer timer, lock;
    Image background;
    Fruit curFruit;
    int curID;
    int score;
    AudioInputStream audio;
    Clip combineSound, dropSound;
    boolean lockout, closeToLoss;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        g2d.drawImage(background, 0, 0, 1472, 800, null);

        // draw scores
        g2d.setColor(Color.BLACK);
        g2d.setFont(SuikaText.getFont(80, 2));
        SuikaText.setTextCenter(g2d, Integer.toString(score), 220, 190);
        g2d.setFont(SuikaText.getFont(30, 1));
        for (int i = 0; i < 3; ++i) {
            if (i >= Leaderboard.lb.size()) {
                SuikaText.setTextCenter(g2d, "-----", 225, 515 + i * 57);
            } else {
                SuikaText.setTextCenter(g2d, Leaderboard.lb.get(i).toString(), 225, 515 + i * 57);
            }
        }
        if (Leaderboard.prevScore == -1) {
            SuikaText.setTextCenter(g2d, "-----", 225, 710);
        } else {
            SuikaText.setTextCenter(g2d, Integer.toString(Leaderboard.prevScore), 225, 710);
        }

        if (!lockout) {
            g2d.setColor(Color.WHITE);
            g2d.drawLine(curFruit.getX(), curFruit.getY(), curFruit.getX(), bBound);
            curFruit.render(g2d);
        }

        //render all the fruits
        nextFruit.render(g2d);
        for (Fruit fruit : fruits) {
            fruit.render(g2d);
        }
        //render warning line
        if (closeToLoss) {
            g2d.setStroke(new BasicStroke(10));
            g2d.setColor(new Color(255, 0, 0, (int) (255 * ((Math.sin(frameTracker.getTime() / 100000000) + 1) / 2))));
            g2d.drawLine(rBound, tBound, lBound, tBound);
        }

    }


    public Game(ActionListener actionListener) {
        screenManager = actionListener;

        setPreferredSize(new Dimension(1472, 800));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        try {
            background = ImageIO.read(new File("images/bg/background.png"));
        } catch (IOException e) {
            System.out.println("Couldn't load game images, check file routing");
        }
        try {
            audio = AudioSystem.getAudioInputStream(new File("audio/select.wav"));
            dropSound = AudioSystem.getClip();
            dropSound.open(audio);
            audio = AudioSystem.getAudioInputStream(new File("audio/combine.wav"));
            combineSound = AudioSystem.getClip();
            combineSound.open(audio);
        } catch (Exception e) {
            System.out.println("Error loading game sounds, check file routing");
        }

        fruits = new ArrayList<>();

        closeToLoss = false;

        nextFruit = new Fruit(NEXT_FRUIT_POS.x, NEXT_FRUIT_POS.y, 0, 0);
        curFruit = new Fruit((lBound + rBound) / 2.0, 100, 0, 0);
        curID = 1;
        lockout = false;
        lock = new Timer(DROP_DELAY, event -> {
            lockout = false;
            nextFruit.setType(randFruit());
        }
        );
        lock.setRepeats(false);
        frameTracker = new TimeTracker();
        timer = new Timer(DELAY, ev -> {
            update(frameTracker.timeFromLast());
            repaint();
        });
        timer.start();
    }

    //Description: Updates the current game state
    //Parameters: The time from the last frame in seconds
    //Return: None
    public void update(double timeDelta) {
        closeToLoss = false;
        for (Fruit f : fruits) {
            f.update(timeDelta);
            f.groundSupport = 0;
            if (f.getX() - f.getRadius() <= lBound) {
                f.velocity.x = Math.abs(f.velocity.x) * EDGE_RES;
                f.setX(lBound + f.getRadius());
                f.groundSupport += 0.5;
            } else if (f.getX() + f.getRadius() >= rBound) {
                f.velocity.x = -Math.abs(f.velocity.x) * EDGE_RES;
                f.setX(rBound - f.getRadius());
                f.groundSupport += 0.5;
            }

            // Check for bottom and top
            if (f.getY() + f.getRadius() >= bBound) {
                f.velocity.y = 0;
                f.velocity.x *= Math.pow(EDGE_RES, timeDelta);
                f.setY(bBound - f.getRadius());
                f.groundSupport += 1;
            } else if (f.getY() - f.getRadius() < tBound) {
                if (!f.gracePeriod) {
                    lose();
                    return;
                }
            }
        }

        //handle collisions
        List<FruitCollision> collisions = Fruit.getCollidingFruits(fruits);
        Set<Fruit> disabled = new HashSet<>();
        for (FruitCollision c : collisions) {
            if (disabled.contains(c.fruit1) || disabled.contains(c.fruit2)) {
                continue;
            }
            if (c.fruit1.getType() == c.fruit2.getType() && c.fruit1.getType() < 10) {
                // combine the fruits together
                combineFruits(c);
                disabled.add(c.fruit1);
                disabled.add(c.fruit2);
            } else {
                // calculate the amount of support they have from the floor
                c.fruit2.groundSupport += c.fruit1.groundSupport * Math.max(0, 0.8 - Math.abs(c.fruit2.getPos().directionVector(c.fruit1.getPos()).x));
                c.fruit1.groundSupport += c.fruit2.groundSupport * Math.max(0.8 - Math.abs(c.fruit1.getPos().directionVector(c.fruit2.getPos()).x), 0);
            }
        }

        // repel the fruits from each other
        for (FruitCollision c : collisions) {
            if (!disabled.contains(c.fruit1) && !disabled.contains(c.fruit2)) {
                Fruit.repel(c.fruit1, c.fruit2, timeDelta);
                Fruit.correctClipping(c.fruit1, c.fruit2);


            }
        }
        for (Fruit f : fruits) {
            if (f.getY() - f.getRadius() <= tBound + 80 && f.groundSupport > 0.5) {
                closeToLoss = true;
            }
        }
    }

    //Description: Combines 2 fruits into a bigger fruit
    //Parameters: The collision of 2 fruits to be combined
    //Return: None
    public void combineFruits(FruitCollision collision) {
        // note: java clips do not allow for audio to play over itself
        // this will make it a bit odd when multiple things are combining
        combineSound.setFramePosition(0);
        combineSound.start();
        collision.fruit1.upgrade(collision.fruit2);
        fruits.remove(collision.fruit2);
        score += collision.fruit1.getScore();
    }

    //Description: Generates a random fruit. The range of what could be generated changes based on the current score
    //Parameters: None
    //Return: The type of fruit as an integer
    public int randFruit() {
        int ret;
        if (score == 0) {
            ret = (int) (Math.random() * 2);
        } else if (score <= 100) {
            ret = (int) (Math.random() * 3);
        } else {
            ret = (int) (Math.random() * 4);
        }
        return ret;
    }

    //Description: Handles what happens when you lose. Stops the thread, saves the score, and changes screens
    //Parameters: None
    //Return: None
    public void lose() {
        timer.stop();
        screenManager.actionPerformed(new ActionEvent(this, score, "Game Over"));
    }

    //Description: Called when the mouse gets clicked. Will drop a fruit.
    //Parameters: the mouse event that triggered the method
    //Return: None
    @Override
    public void mouseClicked(MouseEvent e) {
        requestFocusInWindow();
        if (!lockout) {
            // plays audio
            dropSound.setFramePosition(0);
            dropSound.start();

            //creates a new fruit
            fruits.add(curFruit.clone(++curID));
            curFruit = nextFruit.clone();
            curFruit.setY(100);
            curFruit.setX(Math.min(Math.max(e.getX(), lBound + nextFruit.getRadius()), rBound - nextFruit.getRadius()));

            lockout = true;
            lock.start();
        }
    }

    //Description: Cheat codes. If ESC is pressed, auto lose the game. If Q is pressed, override the drop delay
    //Parameters: The key event that triggered the method call
    //Return: None
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            lose();
        } else if (e.getKeyCode() == KeyEvent.VK_Q) {
            if (lock.getInitialDelay() == 0) {
                lock.setInitialDelay(DROP_DELAY);
            } else {
                lock.setInitialDelay(0);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_W) {
            curFruit.upgrade(curFruit);
        }
    }

    //Description: Move the fruit to the mouse
    //Parameters: The mouse event that triggered the method call
    //Return: None
    @Override
    public void mouseMoved(MouseEvent e) {
        //tracks the mouse movement
        curFruit.setX(Math.min(Math.max(e.getX(), lBound + curFruit.getRadius()), rBound - curFruit.getRadius()));
    }

    //Exact same as mouseMoved method
    @Override
    public void mouseDragged(MouseEvent e) {
        //tracks the mouse movement
        curFruit.setX(Math.min(Math.max(e.getX(), lBound + curFruit.getRadius()), rBound - curFruit.getRadius()));
    }

    // Unused interface methods
    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
}
