/*
 * @ (#) ManhinhQuanLyChuyenTau.java    1.0 10/20/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
package gui.Panel;

/*
 * @description
 *@author: Viet Hung
 *@date: 10/20/2025
 *@version:  1.0
 */

// [THÊM] Import cho JDateChooser (cần có thư viện jcalendar.jar)
import com.toedter.calendar.JDateChooser;

// [THÊM] Import cho SQL và các thành phần Swing mới
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

// [THÊM] Import lớp kết nối của bạn
import database.ConnectDB;


/**
 * Lớp này tạo giao diện Quản lý Chuyến Tàu.
 * ĐÃ NÂNG CẤP: Kết nối CSDL, dùng JComboBox, JDateChooser, JSpinner.
 * Đã THÊM đầy đủ sự kiện điều hướng (navigation).
 */
public class ManhinhQuanLyChuyenTau extends JFrame {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color SELECTED_COLOR = new Color(0, 51, 102);
    private static final Color BG_COLOR = new Color(245, 245, 245);

    // Font chữ
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14); // Đã thêm
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);

    // [MỚI] Khai báo các component để có thể truy cập từ các hàm khác
    private JComboBox<String> cbMaTau;
    private JComboBox<String> cbGaDi;
    private JComboBox<String> cbGaDen;
    private JDateChooser dateChooserNgayDi;
    private JSpinner timeSpinnerGioDi;


    public ManhinhQuanLyChuyenTau() {
        setTitle("Quản lý chuyến tàu");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Panel điều hướng bên trái
        JPanel navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        // 2. Panel nội dung (Form và Bảng)
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        // [MỚI] Tải dữ liệu từ CSDL lên các ComboBox
        try {
            loadDuLieuMaTau();
            loadDuLieuGa();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu từ CSDL: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =================================================================================
    // KHU VỰC MENU BÊN TRÁI (Giữ nguyên)
    // =================================================================================

    /**
     * [ĐÃ CẬP NHẬT] Tạo panel điều hướng bên trái.
     * Mục "Quản lý chuyến tàu" được chọn.
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

        // [1. Trang chủ]
        JButton btnTrangChu = createNavItem("Trang chủ", "\uD83C\uDFE0"); // 🏠
        btnTrangChu.addActionListener(e -> {
            new ManHinhDashboardQuanLy().setVisible(true);
            this.dispose();
        });
        panel.add(btnTrangChu);

        // [2. Tra cứu hóa đơn]
        JButton btnTraCuu = createNavItem("Tra cứu hóa đơn", "\uD83D\uDD0D"); // 🔍
        btnTraCuu.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Tra cứu hóa đơn đang được phát triển.");
        });
        panel.add(btnTraCuu);

        // [3. Quản lý chuyến tàu] - Màn hình hiện tại, không cần sự kiện
        JButton selectedButton = createNavItem("Quản lý chuyến tàu", "\uD83D\uDE86"); // 🚆
        selectedButton.setBackground(SELECTED_COLOR);
        for (java.awt.event.MouseListener ml : selectedButton.getMouseListeners()) {
            selectedButton.removeMouseListener(ml);
        }
        panel.add(selectedButton);

        // [4. Quản lý tài khoản NV]
        JButton btnQLNV = createNavItem("Quản lý tài khoản NV", "\uD83D\uDC64"); // 👤
        btnQLNV.addActionListener(e -> {
            new ManHinhQuanLyNhanVien().setVisible(true);
            this.dispose();
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
    // KHU VỰC NỘI DUNG (QUẢN LÝ CHUYẾN TÀU) - ĐÃ SỬA
    // =================================================================================

    /**
     * Tạo panel nội dung chính bên phải. (Code từ màn hình đầu tiên)
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Thêm padding

        // --- Tiêu đề ---
        JLabel title = new JLabel("Quản lý chuyến tàu");
        title.setFont(FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(title, BorderLayout.NORTH);

        // --- Khu vực chính (chứa form và bảng) ---
        JPanel mainArea = new JPanel();
        mainArea.setLayout(new BoxLayout(mainArea, BoxLayout.Y_AXIS)); // Xếp chồng form và bảng
        mainArea.setOpaque(false); // Làm trong suốt để hiển thị màu nền của panel cha

        // 1. Form nhập liệu [ĐÃ GỌI HÀM SỬA]
        JPanel formPanel = createFormPanel();
        mainArea.add(formPanel);

        // Thêm khoảng cách giữa form và bảng
        mainArea.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Bảng dữ liệu [ĐÃ GỌI HÀM SỬA]
        JPanel tablePanel = createTablePanel();
        mainArea.add(tablePanel);

        panel.add(mainArea, BorderLayout.CENTER);

        return panel;
    }

    /**
     * [ĐÃ SỬA] Tạo panel chứa form nhập liệu.
     * Thay thế JTextField bằng JComboBox, JDateChooser, JSpinner.
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Sử dụng GridBagLayout để căn chỉnh
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Khoảng cách giữa các thành phần
        gbc.anchor = GridBagConstraints.WEST; // Căn lề trái
        gbc.fill = GridBagConstraints.HORIZONTAL; // Các component co giãn theo chiều ngang

        // Hàng 1: Mã chuyến tàu (Giữ nguyên JTextField)
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE; // Label không co giãn
        fieldsPanel.add(new JLabel("Mã chuyến tàu:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        fieldsPanel.add(new JTextField(20), gbc);

        // Hàng 2: Mã Tàu (Thay JTextField bằng JComboBox)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Mã tàu:"), gbc); // Sửa "Tên tàu" -> "Mã tàu"

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbMaTau = new JComboBox<>();
        cbMaTau.setFont(FONT_PLAIN_14);
        fieldsPanel.add(cbMaTau, gbc);

        // Hàng 3: Ga đi và Ga đến (Thay JTextField bằng JComboBox)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Ga đi:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        cbGaDi = new JComboBox<>();
        cbGaDi.setFont(FONT_PLAIN_14);
        fieldsPanel.add(cbGaDi, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets = new Insets(8, 20, 8, 8); // Thêm lề trái
        fieldsPanel.add(new JLabel("Ga đến:"), gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5; gbc.insets = new Insets(8, 8, 8, 8);
        cbGaDen = new JComboBox<>();
        cbGaDen.setFont(FONT_PLAIN_14);
        fieldsPanel.add(cbGaDen, gbc);

        // Hàng 4: Giờ đi (Thay JTextField bằng JSpinner)
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Giờ đi:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        // Tạo Spinner chọn giờ
        Date initTime = Calendar.getInstance().getTime();
        SpinnerDateModel timeModel = new SpinnerDateModel(initTime, null, null, Calendar.HOUR_OF_DAY);
        timeSpinnerGioDi = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinnerGioDi, "HH:mm");
        timeSpinnerGioDi.setEditor(timeEditor);
        timeSpinnerGioDi.setFont(FONT_PLAIN_14);
        fieldsPanel.add(timeSpinnerGioDi, gbc);

        // Hàng 5: Ngày đi (Thay JTextField bằng JDateChooser)
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Ngày đi:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        dateChooserNgayDi = new JDateChooser();
        dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayDi.setFont(FONT_PLAIN_14);
        fieldsPanel.add(dateChooserNgayDi, gbc);


        panel.add(fieldsPanel, BorderLayout.CENTER);

        // --- Panel chứa các nút bấm (Giữ nguyên) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(new JButton("Tìm"));
        buttonPanel.add(new JButton("Thêm"));
        buttonPanel.add(new JButton("Sửa"));
        buttonPanel.add(new JButton("Thêm nhanh bằng excel"));

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Đặt kích thước tối đa để form không bị co giãn dọc
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height + 20));

        return panel;
    }

    /**
     * [ĐÃ SỬA] Tạo panel chứa bảng dữ liệu.
     * Sửa lại tên cột cho khớp CSDL (Tên tàu -> Mã tàu)
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));

        // --- Tạo Bảng ---
        // Sửa "Tên tàu" -> "Mã tàu"
        String[] columnNames = {"Mã chuyến tàu", "Mã tàu", "Ga đi", "Ga đến", "Giờ đi", "Ngày đi"};
        Object[][] data = {}; // Dữ liệu trống

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);

        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setFont(FONT_PLAIN_14); // Sử dụng FONT_PLAIN_14

        table.getTableHeader().setFont(FONT_BOLD_14); // Sử dụng FONT_BOLD_14
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // =================================================================================
    // KHU VỰC TRUY VẤN CSDL (MỚI)
    // =================================================================================

    /**
     * [MỚI] Tải danh sách Mã Tàu từ CSDL lên JComboBox
     */
    private void loadDuLieuMaTau() throws SQLException {
        Connection conn = ConnectDB.getConnection();
        String sql = "SELECT MaTau FROM Tau";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cbMaTau.getModel();
            model.removeAllElements(); // Xóa dữ liệu cũ

            while (rs.next()) {
                model.addElement(rs.getString("MaTau"));
            }
        }
        // Lưu ý: Không đóng kết nối ở đây, để cho các hàm khác còn dùng
        // ConnectDB.disconnect(); // KHÔNG NÊN
    }

    /**
     * [MỚI] Tải danh sách Tên Ga từ CSDL lên 2 JComboBox
     */
    private void loadDuLieuGa() throws SQLException {
        Connection conn = ConnectDB.getConnection();
        String sql = "SELECT TenGa FROM Ga";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultComboBoxModel<String> modelGaDi = (DefaultComboBoxModel<String>) cbGaDi.getModel();
            DefaultComboBoxModel<String> modelGaDen = (DefaultComboBoxModel<String>) cbGaDen.getModel();
            modelGaDi.removeAllElements();
            modelGaDen.removeAllElements();

            while (rs.next()) {
                String tenGa = rs.getString("TenGa");
                modelGaDi.addElement(tenGa);
                modelGaDen.addElement(tenGa);
            }
        }
    }


    /**
     * Phương thức main để chạy ứng dụng.
     */
    public static void main(String[] args) {
        // [MỚI] Kết nối CSDL ngay khi ứng dụng khởi động
        try {
            ConnectDB.getInstance().connect();
            System.out.println("Kết nối CSDL thành công!");
        } catch (Exception e) {
            System.err.println("Lỗi kết nối CSDL!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể kết nối đến CSDL. Vui lòng kiểm tra lại.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            return; // Dừng ứng dụng nếu không kết nối được
        }


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
            ManhinhQuanLyChuyenTau frame = new ManhinhQuanLyChuyenTau();
            frame.setVisible(true);
        });
    }
}