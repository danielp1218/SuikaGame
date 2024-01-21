import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class AboutScreen extends JPanel{

    Image aboutScreen;
    Button backButton;
    @Override
    public void paintComponent(Graphics g){
        g.drawImage(aboutScreen, 0,0, 1472, 800, null);

    }

    public AboutScreen(ActionListener actionListener){
        setLayout(null);
        try{
            aboutScreen = ImageIO.read(new File("images/fruit1.png")); // TODO: CHANGE FILE
        }catch(Exception e){
            System.out.println("Error loading about screen image");
        }
        backButton = new Button(0,0,50,50,"<");
        backButton.setActionCommand("Menu");
        backButton.addActionListener(actionListener);
        add(backButton);

    }
}
