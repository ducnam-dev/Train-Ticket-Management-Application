/*
 * @ (#) ManHinhDashboardQuanLy.java    1.0 10/20/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
package gui.Panel; // Đã đổi package thành gui.Panel

/*
 * @description
 *@author: Viet Hung
 *@date: 10/20/2025
 *@version:  1.0
 */

// Import các lớp cần thiết
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Lớp này tạo giao diện Dashboard (Trang chủ) cho Quản lý.
 * Đã chuyển thành JPanel để nhúng vào MainFrame (QuanLyDashboard).
 */
public class ManHinhDashboardQuanLy extends JPanel { // <-- ĐÃ THAY ĐỔI THÀNH JPanel

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);

    // Màu cho các ô summary
    private static final Color COLOR_GREEN = new Color(46, 204, 113);
    private static final Color COLOR_YELLOW = new Color(241, 196, 15);
    private static final Color COLOR_ORANGE = new Color(230, 126, 34);
    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);

    // Font chữ
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);


    public ManHinhDashboardQuanLy() {
        // [ĐÃ XÓA]: setTitle, setSize, setDefaultCloseOperation, setLocationRelativeTo
        setLayout(new BorderLayout(15, 15)); // Giữ lại setLayout

        // 1. Panel nội dung Dashboard
        JPanel dashboardContent = createDashboardContent();
        add(dashboardContent, BorderLayout.CENTER);
    }

    // =================================================================================
    // KHU VỰC MENU BÊN TRÁI (ĐÃ XÓA)
    // =================================================================================

    // [ĐÃ XÓA]: createNavPanel() và createNavItem() vì không còn menu

    // =================================================================================
    // KHU VỰC NỘI DUNG (DASHBOARD)
    // =================================================================================

    /**
     * Tạo nội dung chính cho Dashboard
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
     * Tạo 4 ô tóm tắt (KPI)
     */
    private JPanel createKpiPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 15)); // Lưới 1x4
        panel.setOpaque(false);

        panel.add(createSummaryBox("Doanh thu hôm nay", "0 VNĐ", COLOR_GREEN));
        panel.add(createSummaryBox("Vé đã bán", "0", COLOR_BLUE_LIGHT));
        panel.add(createSummaryBox("Chuyến tàu hôm nay", "0", COLOR_ORANGE));
        panel.add(createSummaryBox("Nhân viên online", "0", COLOR_YELLOW));

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panel.setPreferredSize(new Dimension(0, 120));
        return panel;
    }

    /**
     * Phương thức trợ giúp tạo 1 ô tóm tắt (KPI box)
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
     * Tạo panel chứa các widget chính (Biểu đồ và Bảng)
     */
    private JPanel createMainWidgetsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15)); // Lưới 1x2
        panel.setOpaque(false);

        panel.add(createRevenueChartPanel());
        panel.add(createUpcomingTrainsPanel());

        return panel;
    }

    /**
     * Tạo panel (giữ chỗ) cho biểu đồ doanh thu
     */
    private JPanel createRevenueChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Doanh thu 7 ngày qua",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, Color.BLACK
        ));

        JLabel lblPlaceholder = new JLabel("Biểu đồ doanh thu sẽ được hiển thị ở đây");
        lblPlaceholder.setFont(FONT_PLAIN_14);
        lblPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
        lblPlaceholder.setForeground(Color.GRAY);
        panel.add(lblPlaceholder, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel cho bảng "Chuyến tàu sắp khởi hành"
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
        // Dữ liệu trống
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
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
     * Phương thức main để chạy ứng dụng (kiểm thử).
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Dùng giao diện mặc định
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Kiểm thử Dashboard (JPanel)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Tạo instance của JPanel mới
            ManHinhDashboardQuanLy dashboardPanel = new ManHinhDashboardQuanLy();

            frame.add(dashboardPanel); // Thêm JPanel vào JFrame
            frame.setSize(1400, 900); // Đặt kích thước cho JFrame
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}