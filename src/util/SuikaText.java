package util;

import java.awt.Font;
import java.awt.Graphics2D;
import java.io.FileInputStream;

public class SuikaText {
    static Font SuikaFont1 = new Font("serif", Font.PLAIN, 30);
    static Font SuikaFont2 = new Font("serif", Font.PLAIN, 30);

    static {
        try {
            SuikaFont1 = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("fonts/Gabarito-Bold.ttf"));
            SuikaFont2 = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("fonts/Como-Heavy.ttf"));
        } catch (Exception e) {
            System.out.println("Error loading fonts, check file routing");
        }
    }

    //Description: Get the SuikaGame font
    //Parameters: font size represented as a float, and font type represented with a 1 or 2
    //Return: The requested font
    public static Font getFont(float size, int type) {
        return switch (type) {
            case 1 -> SuikaFont1.deriveFont(size);
            default -> SuikaFont2.deriveFont(size);
        };
    }

    //Description: Draws centered text
    //Parameters: The graphics object to draw to, the text to draw, and the offsets
    //Return: None
    static public void setTextCenter(Graphics2D g2d, String txt, int offX, int offY) {
        int stringWidthLength = (int) g2d.getFontMetrics().getStringBounds(txt, g2d).getWidth();

        g2d.drawString(txt, offX - stringWidthLength / 2, offY);
    }
}
