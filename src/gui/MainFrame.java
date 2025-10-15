package gui;

import javax.swing.*;
import java.awt.*;

// Lớp này là cửa sổ chính của ứng dụng
public class MainFrame extends JFrame {

    // 1. Phương thức khởi tạo MainFrame
    public MainFrame() {
        setTitle("Hệ thống Quản lý Bán vé Tàu hỏa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát khi đóng cửa sổ
        setMinimumSize(new Dimension(1000, 700)); // Kích thước tối thiểu

        // Thiết lập bố cục chính (ví dụ: BorderLayout cho khung ứng dụng)
        setLayout(new BorderLayout());

        // 2. Thêm Sidebar Menu (LEFT)
        JPanel sidebar = createSidebarMenu();
        add(sidebar, BorderLayout.WEST);

        // 3. Thêm Panel Nội dung Chính (CENTER)
        ManHinhBanVe manHinhBanVe = new ManHinhBanVe();
        add(manHinhBanVe, BorderLayout.CENTER);

        // Tự động điều chỉnh kích thước để vừa với các thành phần
        pack();
        setLocationRelativeTo(null); // Đặt cửa sổ ra giữa màn hình
    }
    
    // Phương thức tạo menu sidebar (trái)
    private JPanel createSidebarMenu() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(0, 123, 255)); // Màu xanh Blue
        sidebar.setPreferredSize(new Dimension(250, 768)); // Chiều rộng cố định
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Tiêu đề GA XE
        JLabel titleLabel = new JLabel("GA XE", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("System", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(new JSeparator(SwingConstants.HORIZONTAL));
        sidebar.add(Box.createVerticalStrut(20));

        // Các nút Menu
        String[] menuItems = {"Trang chủ", "Mở ca", "Bán vé", "Đổi vé", "Trả vé", "Tra cứu vé", "Tra cứu hóa đơn"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái trong VBox
            btn.setMaximumSize(new Dimension(250, 50));
            btn.setMinimumSize(new Dimension(250, 50));
            btn.setBackground(new Color(0, 123, 255));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("System", Font.PLAIN, 16));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMargin(new Insets(0, 20, 0, 0));
            btn.setBorderPainted(false);
            
            // Highlight nút "Bán vé"
            if (item.equals("Bán vé")) {
                 btn.setBackground(new Color(0, 105, 217)); // Màu xanh đậm hơn
            }
            
            sidebar.add(btn);
        }
        
        // Đăng xuất (đẩy xuống cuối)
        sidebar.add(Box.createVerticalGlue()); 
        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(250, 50));
        logoutBtn.setMinimumSize(new Dimension(250, 50));
        logoutBtn.setBackground(new Color(0, 123, 255));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("System", Font.PLAIN, 16));
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setMargin(new Insets(0, 20, 0, 0));
        logoutBtn.setBorderPainted(false);
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }


    // 4. Phương thức Main để khởi chạy ứng dụng
    public static void main(String[] args) {
        // Khởi chạy giao diện trong Event Dispatch Thread (Swing bắt buộc)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
