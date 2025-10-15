package view;


import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gui.ManHinhBanVe;
import gui.BanVeUI;
import gui.MainFrame;

public class Main {
//    public static void main(String[] args) {
//   	 SwingUtilities.invokeLater(() -> new BanVeUI().setVisible(true));
//    }
    
    
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	        JFrame frame = new JFrame("Kiểm tra ManHinhBanVe");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.add(new ManHinhBanVe()); // Tạo và thêm panel
	        frame.pack();
	        frame.setVisible(true);
	    });
	}
}