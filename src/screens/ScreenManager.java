package screens;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Container;
import java.io.File;

public class ScreenManager extends JFrame implements ActionListener {
    JPanel currentScreen;
    AudioInputStream audio;
    Clip backgroundMusic;

    public ScreenManager() {
        currentScreen = new MainMenu(this);
        add(currentScreen);
        revalidate();
        try {
            audio = AudioSystem.getAudioInputStream(new File("audio/bg-music.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audio);
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.out.println("Error loading audio, check file routing");
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new Dimension(1472, 830));
        setVisible(true);
        Leaderboard.init();
    }

    //Description: Change from one screen to another
    //Parameters: A JPanel that holds the screen to change too
    //Return: None
    public void changeScreens(JPanel newScreen) {
        remove(currentScreen);
        currentScreen = newScreen;
        add(currentScreen);
        revalidate();
    }

    //Description: Saves the image of the current frame and passes it into the game over screen
    //Parameters: The score of the game
    //Return: None
    private void gameOver(int score) {
        Container c = getContentPane();
        BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
        c.paint(im.getGraphics());
        changeScreens(new GameOver(this, im, score));
    }

    //Description: Handles the changing of screens. When the child JPanel want's to change, it will call this method
    //Parameters: The ActionEvent defining what to change the screen to
    //Return: None
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Game" -> changeScreens(new Game(this));
            case "Menu" -> changeScreens(new MainMenu(this));
            case "About" -> changeScreens(new AboutScreen(this));
            case "Leaderboard" -> changeScreens(new Leaderboard(this));
            case "Game Over" -> gameOver(e.getID());
        }
    }
}
