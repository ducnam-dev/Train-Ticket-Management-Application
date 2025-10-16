package view;

import javax.swing.SwingUtilities;
import gui.BanVeDashboard;
import gui.ManHinhDangNhap;

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