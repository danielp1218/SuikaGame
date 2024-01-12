import javax.swing.*;

public class Main {

    public static void init(){
        JFrame frame = new JFrame();
        Game game = new Game();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        init();
    }
}