package view;

import javax.swing.SwingUtilities;

import gui.MainFrame.ManHinhDangNhap;

public class Main {
    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new BanVeDashboard().setVisible(true);
//        });

        SwingUtilities.invokeLater(() -> {
            new ManHinhDangNhap().setVisible(true);
        });
    }
}