import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class FruitBody {
    double radius, mass;
    int points;
    BufferedImage img;

    public FruitBody(int radius, double mass, int points, String imagePath){
        this.radius = radius;
        this.mass = mass;
        this.points = points;
        try{
            this.img = ImageIO.read(new File(imagePath));
        } catch (Exception e){
            System.out.println("Image failed loading");
        }


    }
}
