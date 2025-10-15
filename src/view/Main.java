package view;


import javax.swing.SwingUtilities;

import gui.BanVeUI;
import gui.MainFrame;

public class Main {
    public static void main(String[] args) {
    	 SwingUtilities.invokeLater(() -> new BanVeUI().setVisible(true));
    }
}