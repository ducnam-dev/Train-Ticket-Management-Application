package gui.MainFrame;

import gui.Panel.ManHinhBanVe;
import gui.Panel.ManHinhDoiVe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class TraCuuPanel extends JPanel { public TraCuuPanel() { add(new JLabel("Màn hình Tra cứu")); setBackground(new Color(240, 255, 240)); } }

public class BanVeDashboard extends JFrame implements ActionListener {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton btnBanVe, btnDoiVe, btnTraCuu;

    private final Color ACTIVE_COLOR = new Color(74, 184, 237);
    private final Color INACTIVE_COLOR = new Color(34, 137, 203);

    // Kích thước Icon tiêu chuẩn cho Menu
    private static final int ICON_SIZE = 20;

    public BanVeDashboard() {
        setTitle("Hệ thống Quản lý Bán vé Tàu");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        initMenuPanel();
        initContentPanel();
        initEventHandlers();

        cardLayout.show(contentPanel, "banVe");

        setVisible(true);
    }

    // --- HÀM TIỆN ÍCH TẢI ICON MỚI (ĐÃ CHỈNH SỬA ĐƯỜNG DẪN) ---
    /**
     * Tải và điều chỉnh kích thước icon từ thư mục resources (an toàn khi đóng gói JAR).
     */
    private ImageIcon loadAndScaleIcon(String iconName, int width, int height) {
        // >>> ĐÃ SỬA ĐƯỜNG DẪN: Sẽ tìm icon trong ClassPath tại /images/
        String path = "/images/" + iconName;

        java.net.URL imgURL = getClass().getResource(path);

        if (imgURL != null) {
            ImageIcon originalIcon = new ImageIcon(imgURL);
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } else {
            System.err.println("Lỗi: Không tìm thấy file icon tại đường dẫn: " + path);
            return createPlaceholderIcon(width, height);
        }
    }

    // Hàm tạo Icon Placeholder thay thế (Đã sửa lỗi trùng lặp biến)
    private ImageIcon createPlaceholderIcon(int width, int height) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return new ImageIcon(img);
    }
    // -----------------------------------------------------------------

    private JPanel createLogoIdPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(INACTIVE_COLOR);
        panel.setBorder(new EmptyBorder(15, 0, 15, 0));

        // ID
        JLabel lblId = new JLabel("ID: QL200001", SwingConstants.RIGHT);
        lblId.setForeground(Color.WHITE);
        lblId.setFont(new Font("Arial", Font.PLAIN, 12));
        lblId.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Placeholder Icon (Sử dụng logo-train.png làm logo chính)
        JLabel lblIcon = new JLabel("  [Icon GA XE/User]", SwingConstants.CENTER);
        // Thay placeholder bằng icon thực tế (logo-train.png)
        ImageIcon logoIcon = loadAndScaleIcon("logo-train.png", 60, 60);
        lblIcon.setIcon(logoIcon);
        lblIcon.setText(""); // Xóa placeholder text
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblId);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblIcon);

        return panel;
    }

    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(100, 180, 250));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        return separator;
    }

    // --- 1. Panel Menu bên trái (Cố định) ---
    private void initMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        menuPanel.setBackground(INACTIVE_COLOR);

        // Logo & ID Panel (Phần trên cùng)
        menuPanel.add(createLogoIdPanel());

        // >>> CẬP NHẬT TÊN ICON CHÍNH XÁC:
        menuPanel.add(createMenuItem("Trang chủ", "home.png", false));
        menuPanel.add(createSeparator());

        menuPanel.add(createMenuItem("Mở ca", "moca.png", false)); // Đã sửa từ shift_icon.png
        menuPanel.add(createSeparator());

        btnBanVe = createMenuItem("Bán vé", "ticket_icon.png", true); // Giữ ticket_icon.png nếu bạn có
        menuPanel.add(btnBanVe);
        menuPanel.add(createSeparator());

        btnDoiVe = createMenuItem("Đổi vé", "doive.png", false); // Đã sửa từ exchange_icon.png
        menuPanel.add(btnDoiVe);
        menuPanel.add(createSeparator());

        menuPanel.add(createMenuItem("Trả vé", "trave.png", false)); // Đã sửa từ return_icon.png
        menuPanel.add(createSeparator());
        btnTraCuu = createMenuItem("Tra cứu vé", "tracuu.png", false); // Đã sửa từ search_icon.png
        menuPanel.add(btnTraCuu);
        menuPanel.add(createSeparator());
        menuPanel.add(createMenuItem("Tra cứu hóa đơn", "hoadon.png", false)); // Đã sửa từ receipt_icon.png
        menuPanel.add(createSeparator());

        menuPanel.add(Box.createVerticalGlue());

        JButton btnLogout = createMenuItem("Đăng xuất", "logout.png", false); // Đã sửa từ logout_icon.png
        menuPanel.add(btnLogout);
        menuPanel.add(createSeparator());

        add(menuPanel, BorderLayout.WEST);
    }

    // --- Hàm tạo nút menu (Đã dùng hàm tải icon mới) ---
    private JButton createMenuItem(String text, String iconName, boolean isActive) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBackground(isActive ? ACTIVE_COLOR : INACTIVE_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 15, 10, 10));
        button.setFocusPainted(false);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);

        button.setIcon(loadAndScaleIcon(iconName, ICON_SIZE, ICON_SIZE));

        button.setFont(new Font("Arial", Font.PLAIN, 16));
        return button;
    }

    // --- Các phương thức thừa đã được giữ lại/xóa logic thừa ---
    private JPanel createLogoPanel() { return new JPanel(); }
    private Image getIconPath(String iconName) { return createPlaceholderIcon(ICON_SIZE, ICON_SIZE).getImage(); }

    // --- 2. Panel Nội dung Chính ---
    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new ManHinhBanVe(), "banVe");
        contentPanel.add(new ManHinhDoiVe(), "doiVe");
        contentPanel.add(new TraCuuPanel(), "traCuu");

        add(contentPanel, BorderLayout.CENTER);
    }

    private void initEventHandlers() {
        btnBanVe.addActionListener(this);
        btnDoiVe.addActionListener(this);
        btnTraCuu.addActionListener(this);
    }

    // --- 3. Xử lý sự kiện (Chuyển đổi trang) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        // Reset tất cả các nút về INACTIVE
        JButton[] buttons = {btnBanVe, btnDoiVe, btnTraCuu};
        for (JButton button : buttons) {
             if(button != null) { // Kiểm tra null vì các nút khác chưa được khai báo
                 button.setBackground(INACTIVE_COLOR);
                 button.setForeground(Color.WHITE);
             }
        }

        // Logic chuyển trang và làm nổi bật nút ACTIVE
        Object src = e.getSource();
        String cardName = null;

        if (src == btnBanVe) {
            cardName = "banVe";
        } else if (src == btnDoiVe) {
            cardName = "doiVe";
        } else if (src == btnTraCuu) {
            cardName = "traCuu";
        }

        if (cardName != null) {
            cardLayout.show(contentPanel, cardName);
            ((JButton) src).setBackground(ACTIVE_COLOR);
            ((JButton) src).setForeground(Color.WHITE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BanVeDashboard());
    }
}