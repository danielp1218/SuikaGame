import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Leaderboard extends JPanel {
    final static String scoreFilePath = "scores.txt";
    static void save(int score){
        try {
            PrintWriter fileOut = new PrintWriter(new FileWriter(scoreFilePath, true));
            fileOut.println(score);
            fileOut.close();
        } catch (Exception e) {
            System.out.println("Error saving to file");
        }
    }

    static ArrayList<Integer> read(){
        ArrayList<Integer> ret = new ArrayList<>();
        try{
            Scanner fileIn = new Scanner(new FileReader(scoreFilePath));
            while(fileIn.hasNextInt()){
                ret.add(fileIn.nextInt());
            }
            Collections.sort(ret);
            Collections.reverse(ret);
            fileIn.close();
        } catch(Exception e){
            System.out.println("Error reading in scores");
            System.out.println(e.getMessage());
        }
        return ret;
    }

    Image leaderboardBG;
    ArrayList<Integer> scores;
    Button backButton;
    @Override
    public void paintComponent(Graphics g){
        g.drawImage(leaderboardBG, 0,0, 1472, 800, null);
    }

    public Leaderboard(ActionListener actionListener){
        setLayout(null);
        try{
            leaderboardBG = ImageIO.read(new File("images/fruit1.png"));
        } catch(Exception e){
            System.out.println("Error loading leaderboard image");
        }
        scores = read();
        backButton = new Button(0,0,50,50,"<");
        backButton.setActionCommand("Menu");
        backButton.addActionListener(actionListener);
        add(backButton);
    }
}
