import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

public class Button extends JButton implements MouseListener {
    Image buttonImage, rolloverImage, clickedImage;
    int width, height;
    String buttonState, text;

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        if(buttonState.equals("Clicked")){
            g2d.drawImage(clickedImage, 0,0, width, height, null);
        } else if (buttonState.equals("Hover")){
            g2d.drawImage(rolloverImage, 0, 0, width, height, null);
        } else{
            g2d.drawImage(buttonImage , 0,0, width, height, null);
        }
        g2d.setFont(SuikaText.getFont(30));
        SuikaText.setTextCenter(g2d, text, height, width);
    }




    public Button(int x, int y, int width, int height, String text){
        setBounds(x, y, width, height);
        setOpaque(false);
        setBorderPainted(false);
        this.width = width;
        this.height = height;
        this.text = text;

        try{
            buttonImage = ImageIO.read(new File("images/button.png"));
            rolloverImage = ImageIO.read(new File("images/rolloverButton.png"));
            clickedImage = ImageIO.read(new File("images/clickedButton.png"));
        } catch(Exception e){
            System.out.println("Error loading button images");
        }
        buttonState = "None";
        this.addMouseListener(this);
    }



    @Override
    public void mousePressed(MouseEvent e) {
        buttonState = "Clicked";
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        buttonState = "None";
        repaint();
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        buttonState = "Hover";
        repaint();
    }
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {
        buttonState = "None";
        repaint();
    }
}
