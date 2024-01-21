import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class MainMenu extends JPanel {
    Image menuBackground;
    Button newGame, leaderboard, about;
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(menuBackground, 0,0, 1472, 800, null);
    }

    public MainMenu(ActionListener actionListener){
        setSize(1472, 800);
        newGame = new Button(533,330, 403, 102, "Game Start");
        leaderboard = new Button(533,445, 402, 102, "Leaderboard");
        about = new Button(533,560, 402, 102, "About");
        newGame.setActionCommand("Game");
        newGame.addActionListener(actionListener);
        leaderboard.setActionCommand("Leaderboard");
        leaderboard.addActionListener(actionListener);
        about.setActionCommand("About");
        about.addActionListener(actionListener);
        setLayout(null);
        this.add(newGame);
        add(leaderboard);
        add(about);
        try{
            menuBackground = ImageIO.read(new File("images/main-menu.jpg"));
        } catch (Exception e){
            System.out.println("Failed loading images");
        }

    }
}
