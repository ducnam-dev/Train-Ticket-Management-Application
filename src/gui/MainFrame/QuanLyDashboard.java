package gui.MainFrame;

// Import các Panel cần hiển thị
import gui.Panel.*;
import gui.Panel.ManHinhDashboardQuanLy;
// import gui.Panel.ManHinhDashboardQuanLy; // Giả sử đây là Trang Chủ

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp này tạo MainFrame cho quyền Quản Lý, chứa Menu cố định và CardLayout để chuyển màn hình.
 */
public class QuanLyDashboard extends JFrame implements ActionListener {

    // =================================================================================
    // HẰNG SỐ VÀ KHAI BÁO
    // =================================================================================
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel navPanel;
    private final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private final Color SELECTED_COLOR = new Color(0, 51, 102);
    private final Color HOVER_COLOR = new Color(0, 130, 235);
    private final Map<String, JButton> menuButtons = new HashMap<>();

    // Các nút menu cần quản lý
    private JButton btnTrangChu, btnQLChuyenTau, btnQLNV, btnQLKhuyenMai, btnDangXuat;
    private JButton btnQLGiaVe;

    public QuanLyDashboard() {
        setTitle("Hệ thống Quản lý Vé Tàu");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Panel Menu bên trái
        navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        // 2. Panel nội dung (CardLayout)
        initContentPanel();
        setExtendedState(JFrame.MAXIMIZED_BOTH);


        // Mặc định hiển thị màn hình Trang Chủ (Hoặc Quản lý chuyến tàu)
        switchToCard("trangChu");

        // Thiết lập sự kiện cho các nút menu
        initEventHandlers();
    }

    // =================================================================================
    // KHU VỰC MENU (Sử dụng code của bạn)
    // =================================================================================

    /**
     * Tạo panel điều hướng bên trái. (Code đã sửa để điều hướng qua CardLayout)
     */
    private JPanel createNavPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PRIMARY_COLOR);
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Phần Header (Logo và ID) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ... (Logo và ID Label) ...
        JLabel logoLabel = new JLabel("GA XE");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idLabel = new JLabel("ID: NV200001");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(Color.WHITE);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        idLabel.setBorder(new EmptyBorder(5, 5, 20, 0));

        headerPanel.add(logoLabel);
        headerPanel.add(idLabel);
        headerPanel.setMaximumSize(headerPanel.getPreferredSize());
        panel.add(headerPanel);

        // --- Phần các mục menu ---

        // [1. Trang chủ]
        btnTrangChu = createNavItem("Trang chủ", "\uD83C\uDFE0"); // 🏠
        menuButtons.put("trangChu", btnTrangChu);
        panel.add(btnTrangChu);

        // [2. Tra cứu hóa đơn] - Giả định đây là một Panel
        JButton btnTraCuu = createNavItem("Tra cứu hóa đơn", "\uD83D\uDD0D"); // 🔍
        menuButtons.put("traCuuHD", btnTraCuu);
        panel.add(btnTraCuu);

        // [3. Quản lý chuyến tàu] - Màn hình hiện tại
        btnQLChuyenTau = createNavItem("Quản lý chuyến tàu", "\uD83D\uDE86"); // 🚆
        btnQLChuyenTau.setBackground(SELECTED_COLOR); // Mặc định chọn màn hình này
        menuButtons.put("qlChuyenTau", btnQLChuyenTau);
        panel.add(btnQLChuyenTau);

        // [4. Quản lý tài khoản NV]
        btnQLNV = createNavItem("Quản lý tài khoản NV", "\uD83D\uDC64"); // 👤
        menuButtons.put("qlNhanVien", btnQLNV);
        panel.add(btnQLNV);

        // [5. Quản lý giá vé] - Giả định đây là một Panel
        btnQLGiaVe = createNavItem("Quản lý giá vé", "\uD83D\uDCB2"); // 💲
        menuButtons.put("qlGiaVe", btnQLGiaVe);
        panel.add(btnQLGiaVe);

        // [6. Quản lý khuyến mãi]
        btnQLKhuyenMai = createNavItem("Quản lý khuyến mãi", "\uD83C\uDFF7"); // 🏷️
        menuButtons.put("qlKhuyenMai", btnQLKhuyenMai);
        panel.add(btnQLKhuyenMai);

        // [7. Thống kê báo cáo]
        JButton btnThongKe = createNavItem("Thống kê báo cáo", "\uD83D\uDCCA"); // 📊
        menuButtons.put("thongKe", btnThongKe);
        panel.add(btnThongKe);

        panel.add(Box.createVerticalGlue());

        // --- Nút Đăng xuất ---
        btnDangXuat = createNavItem("Đăng xuất", "\uD83D\uDEAA"); // 🚪
        menuButtons.put("dangXuat", btnDangXuat);
        panel.add(btnDangXuat);

        return panel;
    }

    /**
     * Phương thức tạo nút menu (được đơn giản hóa)
     */
    private JButton createNavItem(String text, String iconText) {
        JButton button = new JButton(text);

        // Sử dụng HTML cho icon và text
        String htmlText = "<html>" +
                "<span style='font-family:\"Segoe UI Emoji\"; font-size:15pt;'>" + iconText + "</span>" +
                "&nbsp;&nbsp;&nbsp;" +
                "<span style='font-family:\"Segoe UI\", Arial; font-size: 12pt; font-weight: bold;'>" + text + "</span>" +
                "</html>";
        button.setText(htmlText);

        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 25, 10, 25)); // Đảm bảo căn lề trái
        button.setOpaque(true);

        int fixedHeight = 50;
        Dimension itemSize = new Dimension(Integer.MAX_VALUE, fixedHeight);
        button.setMaximumSize(itemSize);

        // Xử lý hiệu ứng hover/màu sắc
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setBackground(HOVER_COLOR);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(HOVER_COLOR)) {
                    button.setBackground(PRIMARY_COLOR);
                }
            }
        });
        return button;
    }


    // =================================================================================
    // KHU VỰC CONTENT PANEL & CARDLAYOUT
    // =================================================================================

    /**
     * Khởi tạo Panel chứa CardLayout
     */
    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Thêm các màn hình quản lý vào CardLayout
        // Giả định ManHinhDashboardQuanLy là một JPanel
        contentPanel.add(new ManHinhDashboardQuanLy(), "trangChu"); // Placeholder cho Trang Chủ
        contentPanel.add(new ManhinhQuanLyChuyenTau(), "qlChuyenTau");
        contentPanel.add(new ManHinhQuanLyNhanVien(), "qlNhanVien");
        contentPanel.add(new ManHinhQuanLyKhuyenMai(), "qlKhuyenMai");
        contentPanel.add(new JPanel(), "traCuuHD"); // Placeholder
        contentPanel.add(new ManHinhQuanLyGiaVe(), "qlGiaVe"); // Placeholder
        contentPanel.add(new JPanel(), "thongKe"); // Placeholder


        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Thiết lập Action Listener cho tất cả các nút menu
     */
    private void initEventHandlers() {
        for (JButton button : menuButtons.values()) {
            button.addActionListener(this);
        }
    }

    /**
     * Chuyển đổi màn hình trong CardLayout và highlight nút menu tương ứng
     */
    public void switchToCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
        highlightActiveButton(menuButtons.get(cardName));
    }

    /**
     * Đổi màu nền của nút menu đang được chọn
     */
    private void highlightActiveButton(JButton active) {
        // Đặt tất cả các nút về màu ban đầu
        for (JButton button : menuButtons.values()) {
            if (button != null) {
                button.setBackground(PRIMARY_COLOR);
            }
        }
        // Highlight nút đang hoạt động
        if (active != null) {
            active.setBackground(SELECTED_COLOR);
        }
    }

    // =================================================================================
    // XỬ LÝ SỰ KIỆN CHUNG
    // =================================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        String cardName = null;

        if (src == btnTrangChu) {
            cardName = "trangChu";
        } else if (src == btnQLChuyenTau) {
            cardName = "qlChuyenTau";
        } else if (src == btnQLNV) {
            cardName = "qlNhanVien";
        } else if (src == btnQLKhuyenMai) {
            cardName = "qlKhuyenMai";
        } else if (src == btnDangXuat) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // new ManHinhDangNhap().setVisible(true); // Nếu có màn hình đăng nhập
                this.dispose();
            }
            return;
        } else {
            // Xử lý các nút placeholder (Tra cứu HD, QL Giá vé, Thống kê)
            for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
                if (entry.getValue() == src) {
                    cardName = entry.getKey();
                    break;
                }
            }
        }

        if (cardName != null) {
            switchToCard(cardName);
        }
    }

    // =================================================================================
    // MAIN
    // =================================================================================
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e){
            // Dùng giao diện mặc định
        }
        SwingUtilities.invokeLater(() -> {
            new QuanLyDashboard().setVisible(true);
        });
    }
}