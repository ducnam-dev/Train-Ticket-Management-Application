package gui.MainFrame;

import gui.Panel.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BanVeDashboard extends JFrame implements ActionListener {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton btnBanVe, btnDoiVe, btnTraCuu, btnDangXuat, btnTrangChu, btnKetCa, btnMoCa;

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
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        // Đặt kích thước để BoxLayout biết cần căn chỉnh các thành phần con trong phạm vi nào
        // Cố định chiều rộng là 200px
        menuPanel.setMinimumSize(new Dimension(200, 0));
        menuPanel.setPreferredSize(new Dimension(200, 700));
        menuPanel.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));

        menuPanel.setBackground(INACTIVE_COLOR);

        // Logo & ID Panel (Phần trên cùng)
        menuPanel.add(createLogoIdPanel());

        // >>> CẬP NHẬT TÊN ICON CHÍNH XÁC:
        btnTrangChu = createMenuItem("Trang chủ", "home.png", false);
        menuPanel.add(btnTrangChu);
        menuPanel.add(createSeparator());

        btnMoCa = createMenuItem("Mở ca", "moca.png", false);
        menuPanel.add(btnMoCa); // Đã sửa từ shift_icon.png
        menuPanel.add(createSeparator());

        btnKetCa = createMenuItem("Kết ca", "ketca.png", false);
        menuPanel.add(btnKetCa);
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

        menuPanel.add(new Label("Test"));
        menuPanel.add(createSeparator());

        menuPanel.add(Box.createVerticalGlue());

        btnDangXuat = createMenuItem("Dang Xuat", "dangxuat.png", false);
        menuPanel.add(btnDangXuat);
        menuPanel.add(createSeparator());

        add(menuPanel, BorderLayout.WEST);
    }

    // --- Hàm tạo nút menu (Đã dùng hàm tải icon mới) ---
    private JButton createMenuItem(String text, String iconName, boolean isActive) {
        JButton button = new JButton(text);

        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Chiều cao cố định 50px
//        button.setMinimumSize(new Dimension(0, 50));
        button.setPreferredSize(new Dimension(200, 50));

// 3.   CĂN CHỈNH NỘI DUNG (Đã đúng): Căn icon và chữ bên trong nút sang trái
        button.setHorizontalAlignment(SwingConstants.RIGHT);

        button.setBackground(isActive ? ACTIVE_COLOR : INACTIVE_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 0, 10, 10));
        button.setFocusPainted(false);

        button.setIconTextGap(8);

        button.setIcon(loadAndScaleIcon(iconName, ICON_SIZE, ICON_SIZE));

        button.setFont(new Font("Arial", Font.PLAIN, 16));
        return button;
    }

    // --- 2. Panel Nội dung Chính ---
    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new ManHinhBanVe(), "banVe");
        contentPanel.add(new ManHinhDoiVe(), "doiVe");
        contentPanel.add(new TraCuuPanel(), "traCuu");
        contentPanel.add(new ManHinhTrangChuNVBanVe(), "trangChu");
        contentPanel.add(new ManHinhMoCa(), "moCa");
        contentPanel.add(new ManHinhKetCa(), "KetCa");


        add(contentPanel, BorderLayout.CENTER);
    }

    private void initEventHandlers() {
        // register action listeners
        JButton[] buttons = {btnBanVe, btnDoiVe, btnTraCuu, btnDangXuat, btnTrangChu, btnKetCa, btnMoCa};
        for (JButton b : buttons) {
            if (b != null) b.addActionListener(this);
        }
    }

    private void highlightActiveButton(JButton active) {
        JButton[] buttons = {btnBanVe, btnDoiVe, btnTraCuu, btnDangXuat, btnTrangChu, btnKetCa, btnMoCa};
        for (JButton b : buttons) {
            if (b == null) continue;
            b.setBackground(b == active ? ACTIVE_COLOR : INACTIVE_COLOR);
        }
    }

    // --- 3. Xử lý sự kiện (Chuyển đổi trang) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        // Reset tất cả các nút về INACTIVE
        JButton[] buttons = {btnBanVe, btnDoiVe, btnTraCuu, btnTrangChu, btnKetCa, btnMoCa, btnDangXuat};
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
        } else if (src == btnTrangChu) {
            cardName = "trangChu";
        } else if (src == btnMoCa) {
            cardName = "moCa";
        } else if (src == btnKetCa) {
            cardName = "KetCa";
        } else if (src == btnDangXuat) {
            // Xử lý đăng xuất (đơn giản đóng ứng dụng)
            dispose();
            return;
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
    /**
     * Thêm hoặc cập nhật một JPanel vào CardLayout.
     * Phương thức này giúp thêm các panel được khởi tạo với dữ liệu động.
     * * @param newPanel Panel mới cần thêm.
     * @param cardName Tên card (String) tương ứng.
     */
    public void addOrUpdateCard(JPanel newPanel, String cardName) {
        // 1. Tìm và xóa panel cũ dựa trên tên card
        // Note: Vì CardLayout không có getConstraints() công khai,
        // ta phải xóa panel cũ bằng cách duyệt qua tất cả và sử dụng remove().

        // Tạo một Component để giữ tham chiếu đến panel cũ cần xóa
        Component oldComponent = null;

        // Duyệt qua tất cả các Component trong contentPanel
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                oldComponent = comp;
                break;
            }
        }

        // Nếu tìm thấy component cũ, hãy xóa nó
        if (oldComponent != null) {
            contentPanel.remove(oldComponent);
        }

        // 2. Thêm panel mới
        // Đặt tên cho component mới, giúp việc tìm kiếm/xóa sau này dễ dàng hơn
        newPanel.setName(cardName);
        contentPanel.add(newPanel, cardName);

        // 3. Cập nhật giao diện
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Chuyển đổi hiển thị sang một card cụ thể.
     */
    public void switchToCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
        // Tùy chọn: Highlight nút menu tương ứng nếu cần
        JButton btnTuongUng = null;
        switch (cardName) {
            case "banVe" -> btnTuongUng = btnBanVe;
            case "doiVe" -> btnTuongUng = btnDoiVe;
            case "traCuu" -> btnTuongUng = btnTraCuu;
            case "trangChu" -> btnTuongUng = btnTrangChu;
            case "moCa" -> btnTuongUng = btnMoCa;
            case "KetCa" -> btnTuongUng = btnKetCa;
        }
        highlightActiveButton(btnTuongUng);
    }
}