package view;

import javax.swing.*;

import gui.MainFrame.ManHinhDangNhap;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ManHinhDangNhap().setVisible(true);
        });
    }
}