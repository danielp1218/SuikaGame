import java.awt.*;

public class SuikaText {
    static final Font SuikaFont = new Font(Font.SANS_SERIF, Font.BOLD, 30);
    static Font getFont(float size){
        return SuikaFont.deriveFont(size);
    }
    static public void setTextCenter(Graphics2D g2d, String txt, int height, int width) {
        int stringWidthLength = (int) g2d.getFontMetrics().getStringBounds(txt, g2d).getWidth();

        g2d.drawString(txt, width / 2 - stringWidthLength / 2, height / 2);
    }
    static public void setTextCenter(Graphics2D g2d, String txt, int height, int width, int offX, int offY) {
        int stringWidthLength = (int) g2d.getFontMetrics().getStringBounds(txt, g2d).getWidth();

        g2d.drawString(txt, width / 2 - stringWidthLength / 2 + offX, height / 2 + offY);
    }
}
