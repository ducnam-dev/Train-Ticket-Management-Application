package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Màn hình Đổi vé - Chia bố cục cơ bản (BorderLayout), không có nội dung chi tiết.
 * Các vùng: NORTH, CENTER, SOUTH, EAST, WEST để dễ theo dõi và phát triển tiếp.
 */
public class ManHinhDoiVe extends JPanel {

    public ManHinhDoiVe() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245));

        // NORTH
        JPanel northPanel = createAreaPanel("NORTH", Color.CYAN);
        add(northPanel, BorderLayout.NORTH);

        // CENTER
        JPanel centerPanel = createAreaPanel("CENTER", Color.WHITE);
        add(centerPanel, BorderLayout.CENTER);

        // SOUTH
        JPanel southPanel = createAreaPanel("SOUTH", Color.LIGHT_GRAY);
        add(southPanel, BorderLayout.SOUTH);

        // WEST
        JPanel westPanel = createAreaPanel("WEST", Color.PINK);
        add(westPanel, BorderLayout.WEST);

        // EAST
        JPanel eastPanel = createAreaPanel("EAST", Color.ORANGE);
        add(eastPanel, BorderLayout.EAST);
    }

    // Hàm tạo panel cho từng vùng, có nhãn tên vùng và màu dễ phân biệt
    private JPanel createAreaPanel(String label, Color color) {
        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.setPreferredSize(new Dimension(120, 80)); // Kích thước mẫu, có thể điều chỉnh
        panel.setBorder(BorderFactory.createTitledBorder(label));
        return panel;
    }

    // Hàm main test giao diện
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Demo Màn hình Đổi vé (Bố cục)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ManHinhDoiVe());
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}