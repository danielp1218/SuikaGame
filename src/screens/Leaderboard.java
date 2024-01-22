package screens;

import components.Button;
import util.SuikaText;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Graphics;
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
    public static int prevScore;
    public static ArrayList<Integer> lb = new ArrayList<>();

    //Description: Save a score into the scores.txt files
    //Parameters: The score to be saved as an integer
    //Return: None
    static void save(int score) {
        prevScore = score;
        if (score <= 0) {
            return;
        }
        int index = lb.size();
        for (int i = 0; i < lb.size(); ++i) {
            if (lb.get(i) <= score) {
                index = i;
                break;
            }
        }
        lb.add(index, score);


        try {
            PrintWriter fileOut = new PrintWriter(new FileWriter(scoreFilePath, true));
            fileOut.println(score);
            fileOut.close();
        } catch (Exception e) {
            System.out.println("Error saving to file");
        }
    }

    //Description: Interpret the scores.txt file and put it into an arraylist of integers sorted in descending order
    //Parameters: None
    //Return: None
    static void init() {
        try {
            Scanner fileIn = new Scanner(new FileReader(scoreFilePath));
            while (fileIn.hasNextInt()) {
                lb.add(fileIn.nextInt());
            }
            if (lb.isEmpty()) {
                prevScore = -1;
            } else {
                prevScore = lb.get(lb.size() - 1);
            }

            Collections.sort(lb);
            Collections.reverse(lb);
            fileIn.close();
        } catch (Exception e) {
            System.out.println("Error reading in scores");
            System.out.println(e.getMessage());
        }
    }

    final Button menuButton;
    Image leaderboardBG;


    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(leaderboardBG, 0, 0, 1472, 800, null);
        g.setFont(SuikaText.getFont(15, 1));
        for (int i = 0; i < 15; ++i) {
            if (i < lb.size()) {
                g.drawString(lb.get(i).toString(), 530, (int) (195 + i * 34.5));
            } else {
                g.drawString("-----", 530, (int) (195 + i * 34.5));
            }
        }
        for (int i = 15; i < 30; ++i) {
            if (i < lb.size()) {
                g.drawString(lb.get(i).toString(), 810, (int) (195 + (i - 15) * 34.5));
            } else {
                g.drawString("-----", 810, (int) (195 + (i - 15) * 34.5));
            }
        }
    }

    public Leaderboard(ActionListener actionListener) {
        setLayout(null);
        try {
            leaderboardBG = ImageIO.read(new File("images/bg/leaderboard-bg.png"));
        } catch (Exception e) {
            System.out.println("Error loading leaderboard image");
        }
        menuButton = new Button(650, 700, 180, 40, "menu", 1);
        menuButton.setActionCommand("Menu");
        menuButton.addActionListener(actionListener);
        add(menuButton);
    }
}
