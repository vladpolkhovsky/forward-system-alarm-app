package by.forwardsystem;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--cli")) {
            // CLI режим
            System.out.println("Hello Native Java App!");
            System.out.println("OS: " + System.getProperty("os.name"));
            System.out.println("Arch: " + System.getProperty("os.arch"));
        } else {
            // GUI режим
            createAndShowGUI();
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Native Java App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JLabel label = new JLabel("Hello from Native Java Application!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));

        frame.add(label);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}