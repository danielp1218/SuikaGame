package screens;

import components.Button;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.io.File;

public class AboutScreen extends JPanel {
    final Button menuButton, backButton, forwardButton;
    Image aboutScreen1, aboutScreen2;
    int page;

    @Override
    public void paintComponent(Graphics g) {
        if (page == 1) {
            g.drawImage(aboutScreen1, 0, 0, 1472, 800, null);
        } else if (page == 2) {
            g.drawImage(aboutScreen2, 0, 0, 1472, 800, null);
        }
    }

    public void update() {
        if (page == 1) {
            add(forwardButton);
            remove(backButton);
        } else if (page == 2) {
            remove(forwardButton);
            add(backButton);
        }
        repaint();
    }


    public AboutScreen(ActionListener actionListener) {
        setLayout(null);
        try {
            aboutScreen1 = ImageIO.read(new File("images/bg/about-bg.png"));
            aboutScreen2 = ImageIO.read(new File("images/bg/about-bg2.png"));
        } catch (Exception e) {
            System.out.println("Error loading about screen image");
        }
        menuButton = new Button(650, 670, 180, 40, "Menu", 1);
        backButton = new Button(115, 350, 50, 50, "<", 1);
        forwardButton = new Button(1300, 350, 50, 50, ">", 1);

        menuButton.setActionCommand("Menu");
        menuButton.addActionListener(actionListener);

        backButton.addActionListener(event -> {
            --page;
            update();
        });
        forwardButton.addActionListener(event -> {
            ++page;
            update();
        });
        add(menuButton);
        page = 1;
        update();

    }
}
