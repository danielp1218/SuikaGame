import javax.swing.*;
import java.awt.*;

public class Main {

    public static void init() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(new Dimension(1472, 830));
        frame.setVisible(true);
        ScreenManager manager = new ScreenManager(frame);
    }

    public static void main(String[] args) {
        init();
    }
}