package screens;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.File;

import components.Button;

public class MainMenu extends JPanel {
    final Button newGame, leaderboard, about;
    Image menuBackground;

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(menuBackground, 0, 0, 1472, 800, null);
    }

    public MainMenu(ActionListener actionListener) {
        setSize(1472, 800);
        newGame = new Button(533, 330, 403, 102, "Game Start", 1);
        leaderboard = new Button(533, 445, 402, 102, "Leaderboard", 1);
        about = new Button(533, 560, 402, 102, "About", 1);

        newGame.setActionCommand("Game");
        newGame.addActionListener(actionListener);
        leaderboard.setActionCommand("Leaderboard");
        leaderboard.addActionListener(actionListener);
        about.setActionCommand("About");
        about.addActionListener(actionListener);

        setLayout(null);

        add(newGame);
        add(leaderboard);
        add(about);

        try {
            menuBackground = ImageIO.read(new File("images/bg/main-menu.jpg"));
        } catch (Exception e) {
            System.out.println("Failed loading images");
        }
    }
}
