package screens;

import components.Button;
import util.SuikaText;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.awt.event.ActionListener;

public class GameOver extends JPanel {
    final Button restart, scores, menu;
    final int score;
    Image gameOverBg, lastFrame;

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g.drawImage(lastFrame, 0, 0, 1472, 800, null);
        g.drawImage(gameOverBg, 0, 0, 1472, 800, null);
        g.drawImage(lastFrame, 109, 215, 688, 374, null);

        g.setFont(SuikaText.getFont(100, 1));
        SuikaText.setTextCenter(g2d, Integer.toString(score), 1175, 300);

        g.setFont(SuikaText.getFont(30, 1));
        for (int i = 0; i < 3; ++i) {
            if (i < Leaderboard.lb.size()) {
                g2d.drawString(Leaderboard.lb.get(i).toString(), 1150, 530 + 60 * i);
            } else {
                g2d.drawString("-----", 1150, 530 + 60 * i);
            }

        }
    }

    public GameOver(ActionListener actionListener, Image finalFrame, int score) {
        setLayout(null);
        Leaderboard.save(score);
        this.lastFrame = finalFrame;
        this.score = score;

        try {
            gameOverBg = ImageIO.read(new File("images/bg/gameover.png"));
        } catch (Exception e) {
            System.out.println("Failed loading game over screen, check file routing.");
        }

        restart = new Button(55, 692, 273, 85, "Restart", 2);
        scores = new Button(333, 692, 273, 85, "Scores", 2);
        menu = new Button(613, 692, 273, 85, "Menu", 2);
        restart.setActionCommand("Game");
        scores.setActionCommand("Leaderboard");
        menu.setActionCommand("Menu");
        restart.addActionListener(actionListener);
        scores.addActionListener(actionListener);
        menu.addActionListener(actionListener);
        add(restart);
        add(scores);
        add(menu);
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File("audio/game-lose.wav"));
            Clip gameLose = AudioSystem.getClip();
            gameLose.open(audio);
            gameLose.setFramePosition(0);
            gameLose.start();
        } catch (Exception e) {
            System.out.println("Failed loading game loss sound effect, check file routing");
        }
    }
}
