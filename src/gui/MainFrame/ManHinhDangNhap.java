package gui.MainFrame; // Đảm bảo package này khớp với cấu trúc thư mục của bạn

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent; // Rất quan trọng nếu bạn dùng lớp này
// ... các imports khác
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Lớp ManHinhDangNhap: Tạo giao diện màn hình Đăng nhập
 * - Sử dụng BorderLayout và BoxLayout.
 * - Thêm Panel Ảnh Đoàn Tàu ở bên trái.
 */
public class ManHinhDangNhap extends JFrame {

    // Đường dẫn tương đối đến file ảnh (Cần thay đổi nếu file ảnh ở vị trí khác)
    private static final String LOGO_PATH = "src/images/logo-train.png";
    // Đường dẫn cho ảnh đoàn tàu (Cần thay đổi nếu file ảnh ở vị trí khác)
    private static final String TRAIN_IMAGE_PATH = "src/images/anh tau.jpg"; // Đổi tên file ảnh của bạn
    
    // Kích thước mong muốn của ảnh logo và ảnh tàu
    private static final int LOGO_SIZE = 80;
    private static final int TRAIN_IMAGE_WIDTH = 600; // Chiều rộng cố định cho panel ảnh tàu

    // LƯU Ý: Đặt tên file ảnh của bạn là "logo_ga_xe.png" và "train_bg.jpg" 
    // và đặt chúng vào thư mục "src/images/"

    public ManHinhDangNhap() {
        setTitle("Đăng nhập Hệ thống Quản lý Bán vé Tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Tăng kích thước để chứa thêm panel ảnh tàu
        setSize(1000, 700); 
        setLocationRelativeTo(null); // Căn giữa màn hình

        // Sử dụng BorderLayout cho JFrame chính
        setLayout(new BorderLayout());

        // 1. Thêm Panel Ảnh Đoàn Tàu (Bên trái - WEST)
        JPanel trainImagePanel = createTrainImagePanel();
        add(trainImagePanel, BorderLayout.WEST);

        // 2. Thêm Panel Form Đăng nhập (Trung tâm - CENTER)
        JPanel loginCenterPanel = createLoginCenterPanel();
        add(loginCenterPanel, BorderLayout.CENTER);
        
        // Màu nền cho Form đăng nhập (ví dụ: màu trắng)
        loginCenterPanel.setBackground(Color.WHITE);

        setVisible(true);
    }
    
    // --- Panel Bên Trái: Ảnh Đoàn Tàu ---
    private JPanel createTrainImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(TRAIN_IMAGE_WIDTH, this.getHeight()));
        
        // Màu nền cho panel ảnh (ví dụ: một màu xanh đậm)
        panel.setBackground(new Color(41, 128, 185)); 

        JLabel trainLabel = createScaledImageLabel(TRAIN_IMAGE_PATH, TRAIN_IMAGE_WIDTH, 550, "TRAIN IMAGE HERE");
        
        // Sử dụng BoxLayout để căn giữa ảnh trong panel
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);
        
        centerContainer.add(Box.createVerticalGlue());
        trainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(trainLabel);
        centerContainer.add(Box.createVerticalGlue());
        
        panel.add(centerContainer, BorderLayout.CENTER);

        return panel;
    }

    // --- Panel Bên Phải: Form Đăng Nhập ---
    private JPanel createLoginCenterPanel() {
        // Sử dụng BoxLayout để căn giữa form theo chiều dọc
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Form Đăng nhập thực tế
        JPanel loginPanel = createLoginFormPanel();
        
        // Cần căn giữa loginPanel theo chiều ngang trong centerPanel
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        
        centerPanel.add(Box.createVerticalGlue()); // Keo ở trên
        centerPanel.add(loginPanel);
        centerPanel.add(Box.createVerticalGlue()); // Keo ở dưới

        return centerPanel;
    }

    private JPanel createLoginFormPanel() {
        // Sử dụng BoxLayout để sắp xếp các thành phần theo chiều dọc
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50)); // Padding cho form

        // Đặt chiều rộng cố định cho form (để căn chỉnh tốt hơn)
        panel.setPreferredSize(new Dimension(400, 450));
        panel.setMaximumSize(new Dimension(400, 450)); 
        
        // --- 1. Logo và Tiêu đề (Sử dụng FlowLayout hoặc Box cho tiêu đề và logo) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        headerPanel.setOpaque(false);

        JLabel logoLabel = createScaledImageLabel(LOGO_PATH, LOGO_SIZE, LOGO_SIZE, "LOGO HERE");
        
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));

        headerPanel.add(logoLabel);
        headerPanel.add(titleLabel);
        
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái panel tiêu đề
        panel.add(headerPanel);
        
        panel.add(Box.createVerticalStrut(30)); // Khoảng cách

        // --- 2. Tên đăng nhập ---
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(usernameLabel);
        
        panel.add(Box.createVerticalStrut(5));

        JTextField usernameField = new JTextField("Tên đăng nhập");
        customizeTextField(usernameField);
        panel.add(usernameField);

        panel.add(Box.createVerticalStrut(15)); // Khoảng cách

        // --- 3. Mật khẩu ---
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passwordLabel);
        
        panel.add(Box.createVerticalStrut(5));

        JPasswordField passwordField = new JPasswordField("********");
        customizeTextField(passwordField);
        panel.add(passwordField);

        panel.add(Box.createVerticalStrut(30)); // Khoảng cách

        // --- 4. Nút Đăng nhập và Quên mật khẩu ---
        JPanel bottomRow = new JPanel(new BorderLayout(10, 0)); // Sử dụng BorderLayout để tách 2 nút
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái panel chứa nút

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setPreferredSize(new Dimension(110, 20));
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        loginButton.addActionListener((ActionEvent e) -> {
            // Thêm logic xác thực
             this.dispose();
             new BanVeDashboard().setVisible(true); // Mở màn hình Dashboard
        });

        JButton forgotPasswordButton = new JButton("Quên mật khẩu");
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setForeground(new Color(0, 123, 255));
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setFont(new Font("Arial", Font.PLAIN, 12));


        bottomRow.add(loginButton, BorderLayout.WEST);
        bottomRow.add(forgotPasswordButton, BorderLayout.EAST);
        
        panel.add(bottomRow);

        return panel;
    }

    /**
     * Tùy chỉnh JTextComponent
     */
    private void customizeTextField(JTextComponent field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding bên trong
        ));
        // Đảm bảo chiều rộng tối đa của field không vượt quá form
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    /**
     * Tải và tạo JLabel chứa ảnh, điều chỉnh kích thước
     */
    private JLabel createScaledImageLabel(String path, int width, int height, String fallbackText) {
        JLabel imageLabel = new JLabel();
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Lỗi: Không tìm thấy file ảnh tại đường dẫn: " + path);
                imageLabel.setText(fallbackText);
                return imageLabel;
            }

            ImageIcon originalIcon = new ImageIcon(path);
            Image image = originalIcon.getImage();
            // Sử dụng chiều rộng và chiều cao truyền vào
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH); 
            imageLabel.setIcon(new ImageIcon(scaledImage));

        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh: " + e.getMessage());
            e.printStackTrace();
            imageLabel.setText(fallbackText);
        }
        return imageLabel;
    }
    
    
    /**
     * Phương thức Main để chạy màn hình đăng nhập.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManHinhDangNhap::new);
    }
}