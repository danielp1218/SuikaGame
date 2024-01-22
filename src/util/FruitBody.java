package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class FruitBody {
    final double mass;
    final int points, radius, addon;
    BufferedImage img;

    public FruitBody(int radius, int addon, double mass, int points, String imagePath) {
        this.radius = radius;
        this.addon = addon;
        this.mass = mass;
        this.points = points;
        try {
            this.img = ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            System.out.println("Image failed loading");
        }
    }
}
