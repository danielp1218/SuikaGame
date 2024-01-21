import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScreenManager implements ActionListener {
    JFrame frame;
    JPanel currentScreen;
    public ScreenManager(JFrame frame){
        this.frame = frame;
        currentScreen = new MainMenu(this);
        frame.add(currentScreen);
        frame.revalidate();
    }

    public void changeScreens(JPanel newScreen){
        frame.remove(currentScreen);
        currentScreen = newScreen;
        frame.add(currentScreen);
        frame.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Game" -> changeScreens(new Game(this));
            case "Menu" -> changeScreens(new MainMenu(this));
            case "About" -> changeScreens(new AboutScreen(this));
            case "Leaderboard" -> changeScreens(new Leaderboard(this));
        }
    }
}
