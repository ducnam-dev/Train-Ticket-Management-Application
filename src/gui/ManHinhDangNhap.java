package gui; // Đảm bảo package này khớp với cấu trúc thư mục của bạn

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Lớp ManHinhDangNhap: Tạo giao diện màn hình Đăng nhập
 * sử dụng GridBagLayout để căn giữa nội dung.
 */
public class ManHinhDangNhap extends JFrame {

    // Đường dẫn tương đối đến file ảnh (Cần thay đổi nếu file ảnh ở vị trí khác)
    private static final String LOGO_PATH = "src/images/logo_ga_xe.png";
    // LƯU Ý: Đặt tên file ảnh của bạn là "logo_ga_xe.png" và đặt nó vào thư mục "src/images/"

    public ManHinhDangNhap() {
        setTitle("Đăng nhập Hệ thống Quản lý Bán vé Tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500); // Kích thước cố định hoặc tùy chỉnh
        setLocationRelativeTo(null); // Căn giữa màn hình

        // Sử dụng BorderLayout cho JFrame
        setLayout(new BorderLayout());

        // Tạo Panel chính chứa nội dung đăng nhập (sẽ được căn giữa)
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createMainPanel() {
        // Sử dụng GridBagLayout để căn giữa một khối các thành phần
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các thành phần

        // Panel nội dung thực tế (Hình ảnh, tiêu đề, form)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0)); // FlowLayout cho phép chia 2 cột logic
        contentPanel.setOpaque(false); // Quan trọng để thấy nền trắng của mainPanel

        // === Cột 1: Khoảng trống (Để căn chỉnh form sang phải) ===
        // Có thể dùng một JPanel trống hoặc Box.createHorizontalGlue()
        JPanel emptySpace = new JPanel();
        emptySpace.setPreferredSize(new Dimension(200, 0)); // Kích thước khoảng trống tương đối
        emptySpace.setOpaque(false);
        // contentPanel.add(emptySpace);

        // === Cột 2: Form Đăng nhập và Logo ===
        JPanel loginPanel = createLoginFormPanel();

        // Thêm khoảng trống và form vào contentPanel (lúc này là một khối)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createHorizontalGlue(), gbc); // Keo đẩy nội dung sang phải

        gbc.gridx = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(loginPanel, gbc);

        gbc.gridx = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createHorizontalGlue(), gbc); // Keo đẩy nội dung sang trái

        return panel;
    }

    private JPanel createLoginFormPanel() {
        // Sử dụng GridBagLayout để căn chỉnh cột và căn trái các nhãn
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);

        // --- 1. Logo và Tiêu đề ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Đăng nhập");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 30f));

        JLabel logoLabel = createLogoLabel();

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createHorizontalStrut(20)); // Khoảng cách giữa tiêu đề và logo
        headerPanel.add(logoLabel);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 40, 0); // Padding dưới Header
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(headerPanel, gbc);

        // Reset insets và gridwidth
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridwidth = 1;

        // --- 2. Tên đăng nhập ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);

        JTextField usernameField = new JTextField("NV200001", 20);
        usernameField.setFont(usernameField.getFont().deriveFont(Font.PLAIN, 18f));
        usernameField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        usernameField.setPreferredSize(new Dimension(250, 40));
        gbc.gridy = 2;
        panel.add(usernameField, gbc);

        // --- 3. Mật khẩu ---
        gbc.gridy = 3;
        panel.add(new JLabel("Mật khẩu:"), gbc);

        JPasswordField passwordField = new JPasswordField("********", 20);
        passwordField.setFont(passwordField.getFont().deriveFont(Font.PLAIN, 18f));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        passwordField.setPreferredSize(new Dimension(250, 40));
        gbc.gridy = 4;
        panel.add(passwordField, gbc);

        // --- 4. Nút Đăng nhập và Quên mật khẩu ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setOpaque(false);

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setPreferredSize(new Dimension(110, 40));
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(loginButton.getFont().deriveFont(Font.BOLD, 14f));

        JButton forgotPasswordButton = new JButton("Quên mật khẩu");
        forgotPasswordButton.setContentAreaFilled(false); // Làm cho nút nhìn giống label
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setForeground(new Color(0, 123, 255));
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // Căn chỉnh nút đăng nhập sang trái và nút quên mật khẩu sang phải
        JPanel bottomRow = new JPanel(new BorderLayout(10, 0));
        bottomRow.setOpaque(false);
        bottomRow.add(loginButton, BorderLayout.WEST);
        bottomRow.add(forgotPasswordButton, BorderLayout.EAST);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0); // Padding trên
        gbc.anchor = GridBagConstraints.EAST; // Căn panel chứa nút sang phải để Quên MK ở vị trí đó
        panel.add(bottomRow, gbc);

        return panel;
    }

    /**
     * Tải và tạo JLabel chứa logo
     */
    private JLabel createLogoLabel() {
        JLabel logoLabel = new JLabel();
        try {
            // Kiểm tra đường dẫn file (Cần đảm bảo file ảnh tồn tại!)
            File file = new File(LOGO_PATH);
            if (!file.exists()) {
                System.err.println("Lỗi: Không tìm thấy file ảnh tại đường dẫn: " + LOGO_PATH);
                return new JLabel("LOGO HERE");
            }

            ImageIcon originalIcon = new ImageIcon(LOGO_PATH);
            // Thay đổi kích thước ảnh
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));

        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh logo: " + e.getMessage());
            e.printStackTrace();
            logoLabel.setText("LOGO HERE");
        }
        return logoLabel;
    }

    /**
     * Phương thức Main để chạy màn hình đăng nhập.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManHinhDangNhap::new);
    }
}