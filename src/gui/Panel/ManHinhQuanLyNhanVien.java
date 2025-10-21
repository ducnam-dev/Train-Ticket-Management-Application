/*
 * @ (#) ManHinhQuanLyNhanVien.java    1.0 10/20/2025
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

import gui.MainFrame.ManHinhDashboardQuanLy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Lớp này tạo giao diện Quản lý Tài khoản Nhân viên
 * Đã thêm đầy đủ sự kiện điều hướng (navigation).
 * Đã điều chỉnh kích thước form bên phải.
 */
public class ManHinhQuanLyNhanVien extends JFrame {

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
    private static final Color COLOR_RED = new Color(231, 76, 60);

    // Font chữ
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD_24 = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);


    public ManHinhQuanLyNhanVien() {
        setTitle("Quản lý tài khoản NV");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Panel điều hướng bên trái
        JPanel navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        // 2. Panel nội dung chính (ở giữa)
        JPanel centerContentPanel = createCenterContentPanel();

        // 3. Panel form chi tiết (bên phải)
        JPanel eastFormPanel = createEastFormPanel();

        // 4. Gói nội dung chính và form vào một wrapper
        JPanel mainContentWrapper = new JPanel(new BorderLayout(15, 15));
        mainContentWrapper.setBackground(BG_COLOR);
        mainContentWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainContentWrapper.add(centerContentPanel, BorderLayout.CENTER);
        mainContentWrapper.add(eastFormPanel, BorderLayout.EAST);

        add(mainContentWrapper, BorderLayout.CENTER);
    }

    // =================================================================================
    // KHU VỰC MENU BÊN TRÁI
    // =================================================================================

    /**
     * [ĐÃ CẬP NHẬT] Tạo panel điều hướng bên trái.
     * Mục "Quản lý tài khoản NV" được chọn.
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

        // [3. Quản lý chuyến tàu]
        JButton btnQLChuyenTau = createNavItem("Quản lý chuyến tàu", "\uD83D\uDE86"); // 🚆
        btnQLChuyenTau.addActionListener(e -> {
            new ManhinhQuanLyChuyenTau().setVisible(true);
            this.dispose();
        });
        panel.add(btnQLChuyenTau);

        // [4. Quản lý tài khoản NV] - Màn hình hiện tại, không cần sự kiện
        JButton selectedButton = createNavItem("Quản lý tài khoản NV", "\uD83D\uDC64"); // 👤
        selectedButton.setBackground(SELECTED_COLOR);
        for (java.awt.event.MouseListener ml : selectedButton.getMouseListeners()) {
            selectedButton.removeMouseListener(ml);
        }
        panel.add(selectedButton);

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
    // KHU VỰC NỘI DUNG (QUẢN LÝ NHÂN VIÊN)
    // =================================================================================

    /**
     * [MỚI] Tạo panel nội dung trung tâm (chứa summary, search, table)
     */
    private JPanel createCenterContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Xếp chồng
        panel.setOpaque(false); // Trong suốt để lấy màu nền BG_COLOR

        // 1. Khu vực Summary (3 ô)
        panel.add(createSummaryPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 2. Khu vực Tìm kiếm
        panel.add(createSearchPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 3. Khu vực Bảng
        panel.add(createMainTablePanel());

        return panel;
    }

    /**
     * [MỚI] Tạo 3 ô tóm tắt
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 15)); // Lưới 1x3, cách nhau 15px
        panel.setOpaque(false);

        // TODO: Cập nhật các giá trị "0" này từ database
        panel.add(createSummaryBox("Tổng số tài khoản:", "0", COLOR_GREEN));
        panel.add(createSummaryBox("Nhân viên bán vé:", "0", COLOR_YELLOW));
        panel.add(createSummaryBox("Quản lý:", "0", COLOR_ORANGE));

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setPreferredSize(new Dimension(0, 100));
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
     * [MỚI] Tạo khu vực tìm kiếm
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // --- Hàng 1 ---
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tìm kiếm theo:"), gbc);

        gbc.gridx = 1;
        String[] searchOptions = {"Số điện thoại", "Số CMND/CCCD", "Họ tên nhân viên", "Mã nhân viên"};
        JComboBox<String> cbSearchType = new JComboBox<>(searchOptions);
        cbSearchType.setFont(FONT_PLAIN_14);
        panel.add(cbSearchType, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Nhập thông tin tìm kiếm:"), gbc);

        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtSearchInput = new JTextField(20);
        txtSearchInput.setFont(FONT_PLAIN_14);
        panel.add(txtSearchInput, gbc);

        // --- Hàng 2 ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Trạng thái:"), gbc);

        gbc.gridx = 1;
        String[] statusOptions = {"Đang hoạt động", "Ngừng hoạt động"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        cbStatus.setFont(FONT_PLAIN_14);
        panel.add(cbStatus, gbc);

        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 5, 5, 5);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(FONT_BOLD_14);
        btnSearch.setBackground(PRIMARY_COLOR);
        btnSearch.setForeground(Color.WHITE);
        panel.add(btnSearch, gbc);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.setPreferredSize(new Dimension(0, 150));
        return panel;
    }

    /**
     * [MỚI] Tạo khu vực bảng
     */
    private JPanel createMainTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // --- Tiêu đề và nút Thêm ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("DANH SÁCH TÀI KHOẢN");
        lblTitle.setFont(FONT_TITLE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnAdd = new JButton("+ Thêm nhân viên");
        btnAdd.setFont(FONT_BOLD_14);
        btnAdd.setBackground(COLOR_GREEN);
        btnAdd.setForeground(Color.WHITE);
        headerPanel.add(btnAdd, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // --- Bảng dữ liệu ---
        String[] columnNames = {"STT", "Mã nhân viên", "Tên nhân viên", "Giới tính", "Chức vụ", "Mật khẩu", "Trạng thái", "Tùy chọn"};
        Object[][] data = {}; // Dữ liệu trống

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Chỉ cho phép click cột "Tùy chọn"
            }
        };

        JTable table = new JTable(model);
        table.setFont(FONT_PLAIN_14);
        table.setRowHeight(30);
        table.getTableHeader().setFont(FONT_BOLD_14);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        table.getColumnModel().getColumn(7).setCellRenderer(new ButtonColumnRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ButtonColumnEditor(new JCheckBox()));
        table.getColumnModel().getColumn(7).setMaxWidth(120);
        table.getColumnModel().getColumn(7).setMinWidth(120);


        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * [ĐÃ SỬA] Tạo panel bên phải (chứa Ghi chú và Form chi tiết)
     */
    private JPanel createEastFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Tăng chiều rộng của panel bên phải để form vừa vặn
        panel.setPreferredSize(new Dimension(550, 0));

        // 1. Panel Ghi chú
        panel.add(createNotesPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 2. Panel Form nhân viên
        panel.add(createEmployeeFormPanel());

        return panel;
    }

    /**
     * [MỚI] Tạo panel "Ghi chú"
     */
    private JPanel createNotesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Ghi chú",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, Color.BLACK
        );
        panel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                new EmptyBorder(10, 10, 10, 10))
        );

        JTextArea txtNotes = new JTextArea();
        txtNotes.setBackground(new Color(255, 255, 224)); // Màu vàng nhạt
        txtNotes.setFont(FONT_PLAIN_14);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane scrollNotes = new JScrollPane(txtNotes);
        scrollNotes.setPreferredSize(new Dimension(0, 150)); // Giới hạn chiều cao

        JButton btnSaveNotes = new JButton("Lưu");
        btnSaveNotes.setFont(FONT_BOLD_14);
        btnSaveNotes.setBackground(PRIMARY_COLOR);
        btnSaveNotes.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSaveNotes);

        panel.add(scrollNotes, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        return panel;
    }

    /**
     * [ĐÃ SỬA] Tạo panel Form chi tiết nhân viên (Xóa JScrollPane)
     */
    private JPanel createEmployeeFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin nhân viên",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, PRIMARY_COLOR
        );
        panel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                new EmptyBorder(15, 15, 15, 15))
        );

        // Panel chứa các trường nhập liệu
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // --- CỘT 1 ---
        gbc.gridx = 0;
        gbc.gridy = 0; fieldsPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridy = 1; fieldsPanel.add(new JLabel("Email:"), gbc);
        gbc.gridy = 2; fieldsPanel.add(new JLabel("Số CCCD:"), gbc);
        gbc.gridy = 3; fieldsPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridy = 4; fieldsPanel.add(new JLabel("Chức vụ:"), gbc);
        gbc.gridy = 5; fieldsPanel.add(new JLabel("Ca làm việc:"), gbc);
        gbc.gridy = 6; fieldsPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridy = 7; fieldsPanel.add(new JLabel("Ngày tạo:"), gbc);

        // --- CỘT 2 (Components) ---
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.gridy = 0; fieldsPanel.add(new JTextField(), gbc);
        gbc.gridy = 1; fieldsPanel.add(new JTextField(), gbc);
        gbc.gridy = 2; fieldsPanel.add(new JTextField(), gbc);
        gbc.gridy = 3; fieldsPanel.add(new JTextField(), gbc);
        gbc.gridy = 4; fieldsPanel.add(new JComboBox<>(new String[]{"Nhân viên bán vé", "Quản lý"}), gbc);
        gbc.gridy = 5; fieldsPanel.add(new JComboBox<>(new String[]{"1", "2", "3"}), gbc);
        gbc.gridy = 6; fieldsPanel.add(new JTextField(), gbc);
        gbc.gridy = 7;
        JTextField txtNgayTao = new JTextField();
        txtNgayTao.setEditable(false);
        fieldsPanel.add(txtNgayTao, gbc);

        // --- CỘT 3 ---
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(5, 15, 5, 5); // Tăng lề trái
        gbc.gridy = 0; fieldsPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridy = 1; fieldsPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridy = 4; fieldsPanel.add(new JLabel("Ngày vào làm:"), gbc);
        gbc.gridy = 5; fieldsPanel.add(new JLabel("Giờ làm:"), gbc);
        gbc.gridy = 6; fieldsPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridy = 7; fieldsPanel.add(new JLabel("Trạng thái:"), gbc);

        // --- CỘT 4 (Components) ---
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 0;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.setOpaque(false);
        JRadioButton radNam = new JRadioButton("Nam"); radNam.setOpaque(false);
        JRadioButton radNu = new JRadioButton("Nữ"); radNu.setOpaque(false);
        ButtonGroup bgGender = new ButtonGroup();
        bgGender.add(radNam); bgGender.add(radNu);
        genderPanel.add(radNam); genderPanel.add(radNu);
        fieldsPanel.add(genderPanel, gbc);

        gbc.gridy = 1; fieldsPanel.add(new JTextField(), gbc);
        gbc.gridy = 4; fieldsPanel.add(new JTextField(), gbc); // TODO: Nên là JDatePicker
        gbc.gridy = 5;
        JTextField txtGioLam = new JTextField();
        txtGioLam.setEditable(false);
        fieldsPanel.add(txtGioLam, gbc);
        gbc.gridy = 6; fieldsPanel.add(new JPasswordField(), gbc);
        gbc.gridy = 7; fieldsPanel.add(new JComboBox<>(new String[]{"Đang hoạt động", "Ngừng hoạt động"}), gbc);

        // --- Panel Nút Bấm (Hủy, Xóa trắng, Lưu) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setFont(FONT_BOLD_14);
        btnHuy.setBackground(COLOR_RED);
        btnHuy.setForeground(Color.WHITE);

        JButton btnXoaTrang = new JButton("Xóa trắng");
        btnXoaTrang.setFont(FONT_BOLD_14);

        JButton btnLuu = new JButton("Lưu");
        btnLuu.setFont(FONT_BOLD_14);
        btnLuu.setBackground(COLOR_GREEN);
        btnLuu.setForeground(Color.WHITE);

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnXoaTrang);
        buttonPanel.add(btnLuu);

        // Thêm fieldsPanel trực tiếp (đã xóa JScrollPane)
        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // =================================================================================
    // LỚP NỘI TẠI (INNER CLASS) CHO CÁC NÚT TRONG BẢNG
    // =================================================================================

    /**
     * [MỚI] Lớp để render 2 nút (Sửa, Xóa) trong 1 ô của bảng
     */
    class ButtonColumnRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit;
        private final JButton btnDelete;

        public ButtonColumnRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setBackground(Color.WHITE);

            btnEdit = new JButton("\u270E");
            btnEdit.setToolTipText("Sửa");
            btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            btnEdit.setMargin(new Insets(2, 5, 2, 5));
            btnEdit.setBackground(new Color(255, 193, 7));
            btnEdit.setForeground(Color.BLACK);

            btnDelete = new JButton("\uD83D\uDDD1");
            btnDelete.setToolTipText("Xóa");
            btnDelete.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            btnDelete.setMargin(new Insets(2, 5, 2, 5));
            btnDelete.setBackground(new Color(220, 53, 69));
            btnDelete.setForeground(Color.WHITE);

            add(btnEdit);
            add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    /**
     * [MỚI] Lớp để xử lý sự kiện click cho 2 nút trong bảng
     */
    class ButtonColumnEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        private final JPanel panel;
        private final JButton btnEdit;
        private final JButton btnDelete;
        private JTable table;
        private int row;

        public ButtonColumnEditor(JCheckBox checkBox) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setBackground(Color.WHITE);

            btnEdit = new JButton("\u270E");
            btnEdit.setToolTipText("Sửa");
            btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            btnEdit.setMargin(new Insets(2, 5, 2, 5));
            btnEdit.setBackground(new Color(255, 193, 7));
            btnEdit.setForeground(Color.BLACK);

            btnDelete = new JButton("\uD83D\uDDD1");
            btnDelete.setToolTipText("Xóa");
            btnDelete.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            btnDelete.setMargin(new Insets(2, 5, 2, 5));
            btnDelete.setBackground(new Color(220, 53, 69));
            btnDelete.setForeground(Color.WHITE);

            panel.add(btnEdit);
            panel.add(btnDelete);

            btnEdit.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    String maNV = table.getValueAt(row, 1).toString();
                    JOptionPane.showMessageDialog(table, "Bạn đã chọn SỬA nhân viên: " + maNV);
                    // TODO: Thêm logic Sửa (ví dụ: đổ dữ liệu lên form bên phải)
                }
            });

            btnDelete.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    String maNV = table.getValueAt(row, 1).toString();
                    int confirm = JOptionPane.showConfirmDialog(table,
                            "Bạn có chắc chắn muốn XÓA nhân viên: " + maNV + "?",
                            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        // TODO: Thêm logic Xóa
                        ((DefaultTableModel) table.getModel()).removeRow(row);
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.table = table;
            this.row = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return panel;
        }
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
            ManHinhQuanLyNhanVien frame = new ManHinhQuanLyNhanVien();
            frame.setVisible(true);
        });
    }
}