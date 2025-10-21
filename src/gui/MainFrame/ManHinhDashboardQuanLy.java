/*
 * @ (#) ManHinhDashboardQuanLy.java    1.0 10/20/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
package gui.MainFrame;

/*
 * @description
 *@author: Viet Hung
 *@date: 10/20/2025
 *@version:  1.0
 */

import gui.Panel.ManHinhQuanLyNhanVien;
import gui.Panel.ManhinhQuanLyChuyenTau;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Lớp này tạo giao diện Dashboard (Trang chủ) cho Quản lý.
 * Đã thêm đầy đủ sự kiện điều hướng (navigation).
 */
public class ManHinhDashboardQuanLy extends JFrame {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color SELECTED_COLOR = new Color(0, 51, 102);
    private static final Color BG_COLOR = new Color(245, 245, 245);

    // Màu cho các ô summary
    private static final Color COLOR_GREEN = new Color(46, 204, 113);
    private static final Color COLOR_YELLOW = new Color(241, 196, 15);
    private static final Color COLOR_ORANGE = new Color(230, 126, 34);
    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219); // Màu mới cho KPI

    // Font chữ
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);


    public ManHinhDashboardQuanLy() {
        setTitle("Trang chủ / Dashboard");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Panel điều hướng bên trái
        JPanel navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        // 2. Panel nội dung Dashboard
        JPanel dashboardContent = createDashboardContent();
        add(dashboardContent, BorderLayout.CENTER);
    }

    // =================================================================================
    // KHU VỰC MENU BÊN TRÁI
    // =================================================================================

    /**
     * [ĐÃ CẬP NHẬT] Tạo panel điều hướng bên trái.
     * Mục "Trang chủ" được chọn.
     * Đã thêm ActionListeners để điều hướng.
     */
    private JPanel createNavPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PRIMARY_COLOR);
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Phần Header (Logo và ID) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        // [1. Trang chủ] - Màn hình hiện tại, không cần sự kiện
        JButton selectedButton = createNavItem("Trang chủ", "\uD83C\uDFE0"); // 🏠
        selectedButton.setBackground(SELECTED_COLOR);
        for (java.awt.event.MouseListener ml : selectedButton.getMouseListeners()) {
            selectedButton.removeMouseListener(ml);
        }
        panel.add(selectedButton);

        // [2. Tra cứu hóa đơn]
        JButton btnTraCuu = createNavItem("Tra cứu hóa đơn", "\uD83D\uDD0D"); // 🔍
        btnTraCuu.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Tra cứu hóa đơn đang được phát triển.");
        });
        panel.add(btnTraCuu);

        // [3. Quản lý chuyến tàu]
        JButton btnQLChuyenTau = createNavItem("Quản lý chuyến tàu", "\uD83D\uDE86"); // 🚆
        btnQLChuyenTau.addActionListener(e -> {
            new ManhinhQuanLyChuyenTau().setVisible(true);
            this.dispose(); // Đóng màn hình hiện tại
        });
        panel.add(btnQLChuyenTau);

        // [4. Quản lý tài khoản NV]
        JButton btnQLNV = createNavItem("Quản lý tài khoản NV", "\uD83D\uDC64"); // 👤
        btnQLNV.addActionListener(e -> {
            new ManHinhQuanLyNhanVien().setVisible(true);
            this.dispose(); // Đóng màn hình hiện tại
        });
        panel.add(btnQLNV);

        // [5. Quản lý giá vé]
        JButton btnQLGiaVe = createNavItem("Quản lý giá vé", "\uD88D\uDCB2"); // 💲
        btnQLGiaVe.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Quản lý giá vé đang được phát triển.");
        });
        panel.add(btnQLGiaVe);

        // [6. Quản lý khuyến mãi]
        JButton btnQLKhuyenMai = createNavItem("Quản lý khuyến mãi", "\uD83C\uDFF7"); // 🏷️
        btnQLKhuyenMai.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Quản lý khuyến mãi đang được phát triển.");
        });
        panel.add(btnQLKhuyenMai);

        // [7. Thống kê báo cáo]
        JButton btnThongKe = createNavItem("Thống kê báo cáo", "\uD83D\uDCCA"); // 📊
        btnThongKe.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Thống kê báo cáo đang được phát triển.");
        });
        panel.add(btnThongKe);

        panel.add(Box.createVerticalGlue());

        // --- Thêm đường kẻ ngang ---
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(255, 255, 255, 70));
        separator.setBackground(PRIMARY_COLOR);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        // --- Nút Đăng xuất ---
        JButton btnDangXuat = createNavItem("Đăng xuất", "\uD83D\uDEAA"); // 🚪
        btnDangXuat.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // new ManHinhDangNhap().setVisible(true); // Mở lại màn hình đăng nhập
                this.dispose();
            }
        });
        panel.add(btnDangXuat);

        return panel;
    }

    /**
     * [CHUNG] Phương thức trợ giúp để tạo một nút menu.
     */
    private JButton createNavItem(String text, String iconText) {
        JButton button = new JButton();

        String htmlText = "<html>" +
                "<span style='font-family:\"Segoe UI Emoji\"; font-size:15pt;'>" +
                iconText +
                "</span>" +
                "&nbsp;&nbsp;&nbsp;" +
                "<span style='font-family:\"Segoe UI\", Arial; font-size: 12pt; font-weight: bold;'>" +
                text.replace(" ", "&nbsp;") +
                "</span>" +
                "</html>";
        button.setText(htmlText);

        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(10, 25, 10, 25));

        int fixedHeight = 50;
        Dimension itemSize = new Dimension(Integer.MAX_VALUE, fixedHeight);
        button.setMaximumSize(itemSize);
        button.setPreferredSize(new Dimension(260, fixedHeight));
        button.setMinimumSize(new Dimension(0, fixedHeight));

        final Color originalColor = PRIMARY_COLOR;
        final Color hoverColor = new Color(0, 130, 235); // Sáng hơn một chút

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(originalColor)) {
                    button.setBackground(hoverColor);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(hoverColor)) {
                    button.setBackground(originalColor);
                }
            }
        });
        return button;
    }

    // =================================================================================
    // KHU VỰC NỘI DUNG (DASHBOARD)
    // =================================================================================

    /**
     * [MỚI] Tạo nội dung chính cho Dashboard
     */
    private JPanel createDashboardContent() {
        JPanel panel = new JPanel(new BorderLayout(15, 15)); // Khoảng cách giữa các thành phần
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- 1. Tiêu đề ---
        JLabel lblTitle = new JLabel("Trang chủ / Dashboard");
        lblTitle.setFont(FONT_TITLE);
        panel.add(lblTitle, BorderLayout.NORTH);

        // --- 2. Khu vực chính (chứa KPI và Biểu đồ) ---
        JPanel mainArea = new JPanel(new BorderLayout(15, 15));
        mainArea.setOpaque(false);

        // 2.1. Hàng KPI (4 ô tóm tắt)
        mainArea.add(createKpiPanel(), BorderLayout.NORTH);

        // 2.2. Hàng Widget (Biểu đồ và Bảng)
        mainArea.add(createMainWidgetsPanel(), BorderLayout.CENTER);

        panel.add(mainArea, BorderLayout.CENTER);
        return panel;
    }

    /**
     * [MỚI] Tạo 4 ô tóm tắt (KPI)
     */
    private JPanel createKpiPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 15)); // Lưới 1x4
        panel.setOpaque(false);

        // TODO: Cập nhật các giá trị "0" này từ database
        panel.add(createSummaryBox("Doanh thu hôm nay", "0 VNĐ", COLOR_GREEN));
        panel.add(createSummaryBox("Vé đã bán", "0", COLOR_BLUE_LIGHT));
        panel.add(createSummaryBox("Chuyến tàu hôm nay", "0", COLOR_ORANGE));
        panel.add(createSummaryBox("Nhân viên online", "0", COLOR_YELLOW));

        // Đặt chiều cao cố định cho panel KPI
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panel.setPreferredSize(new Dimension(0, 120));
        return panel;
    }

    /**
     * [CHUNG] Phương thức trợ giúp tạo 1 ô tóm tắt (KPI box)
     */
    private JPanel createSummaryBox(String title, String value, Color bgColor) {
        JPanel box = new JPanel(new BorderLayout(0, 5));
        box.setBackground(bgColor);
        box.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_BOLD_14);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(Color.WHITE);

        box.add(lblTitle, BorderLayout.NORTH);
        box.add(lblValue, BorderLayout.CENTER);
        return box;
    }

    /**
     * [MỚI] Tạo panel chứa các widget chính (Biểu đồ và Bảng)
     */
    private JPanel createMainWidgetsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15)); // Lưới 1x2
        panel.setOpaque(false);

        panel.add(createRevenueChartPanel());
        panel.add(createUpcomingTrainsPanel());

        return panel;
    }

    /**
     * [MỚI] Tạo panel (giữ chỗ) cho biểu đồ doanh thu
     */
    private JPanel createRevenueChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Doanh thu 7 ngày qua",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, Color.BLACK
        ));

        // TODO: Thêm thư viện biểu đồ (ví dụ: JFreeChart) vào đây
        JLabel lblPlaceholder = new JLabel("Biểu đồ doanh thu sẽ được hiển thị ở đây");
        lblPlaceholder.setFont(FONT_PLAIN_14);
        lblPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
        lblPlaceholder.setForeground(Color.GRAY);
        panel.add(lblPlaceholder, BorderLayout.CENTER);

        return panel;
    }

    /**
     * [MỚI] Tạo panel cho bảng "Chuyến tàu sắp khởi hành"
     */
    private JPanel createUpcomingTrainsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Các chuyến tàu sắp khởi hành",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, Color.BLACK
        ));

        String[] columnNames = {"Mã tàu", "Ga đi", "Ga đến", "Giờ khởi hành"};
        Object[][] data = {}; // Dữ liệu trống

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(FONT_PLAIN_14);
        table.setRowHeight(28);
        table.getTableHeader().setFont(FONT_BOLD_14);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Phương thức main để chạy ứng dụng.
     */
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Dùng giao diện mặc định
        }

        SwingUtilities.invokeLater(() -> {
            ManHinhDashboardQuanLy frame = new ManHinhDashboardQuanLy();
            frame.setVisible(true);
        });
    }
}