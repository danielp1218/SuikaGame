package components;

import util.SuikaText;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

public class Button extends JButton implements MouseListener {
    final int width, height, fontType;
    final String text;
    Image buttonImage, rolloverImage, clickedImage;
    String buttonState;
    AudioInputStream audio;
    Clip select;

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
        g2d.setFont(SuikaText.getFont(30, fontType));
        SuikaText.setTextCenter(g2d, text, width/2, (height+10)/2);
    }

    public Button(int x, int y, int width, int height, String text, int fontType){
        setBounds(x, y, width, height);
        setOpaque(false);
        setBorderPainted(false);
        this.width = width;
        this.height = height;
        this.text = text;
        this.fontType = fontType;
        try{
            buttonImage = ImageIO.read(new File("images/button/defaultButton.png"));
            rolloverImage = ImageIO.read(new File("images/button/rolloverButton.png"));
            clickedImage = ImageIO.read(new File("images/button/clickedButton.png"));
        } catch(Exception e){
            System.out.println("Error loading button images, check file routing");
        }

        try{
            audio = AudioSystem.getAudioInputStream(new File("audio/select.wav"));
            select = AudioSystem.getClip();
            select.open(audio);
        } catch(Exception e){
            System.out.println("Error loading button audio, check file routing");
        }
        buttonState = "None";
        this.addMouseListener(this);
    }

    // Mouse Listener methods
    // Used to perform the on hover and on click darkening effect

    @Override
    public void mousePressed(MouseEvent e) {
        buttonState = "Clicked";
        select.setFramePosition(0);
        select.start();
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
    public void mouseClicked(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {
        buttonState = "None";
        repaint();
    }
}
