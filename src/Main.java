import javax.swing.*;

public class Main {

    public static void init(){
        JFrame frame = new JFrame();
        Game game = new Game();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    //TODO: Fix imports after done testing and sort into packages
    public static void main(String[] args) {
        init();
    }
}