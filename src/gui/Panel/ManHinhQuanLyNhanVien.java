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

// [THÊM] Các import cần thiết cho CSDL và các thành phần
import database.ConnectDB;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import com.toedter.calendar.JDateChooser; // Cần thư viện JCalendar
import gui.MainFrame.ManHinhDashboardQuanLy;

/**
 * Lớp này tạo giao diện Quản lý Tài khoản Nhân viên
 * [ĐÃ NÂNG CẤP] Kết nối CSDL, thực hiện đầy đủ chức năng CRUD.
 */
public class ManHinhQuanLyNhanVien extends JFrame {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color SELECTED_COLOR = new Color(0, 51, 102);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color COLOR_GREEN = new Color(46, 204, 113);
    private static final Color COLOR_YELLOW = new Color(241, 196, 15);
    private static final Color COLOR_ORANGE = new Color(230, 126, 34);
    private static final Color COLOR_RED = new Color(231, 76, 60);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD_24 = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);

    // [MỚI] Các component của form, đưa lên làm biến toàn cục
    private JTextField txtHoTen, txtEmail, txtSoCCCD, txtDiaChi, txtTenDangNhap, txtSoDienThoai;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cbChucVu, cbCaLamViec, cbTrangThai;
    private JDateChooser dateNgayVaoLam, dateNgayTao; // Sử dụng JDateChooser
    private JRadioButton radNam, radNu;
    private ButtonGroup bgGender;
    private TitledBorder formBorder; // Border của form
    private JTable table;
    private DefaultTableModel model;
    private JTextArea txtGhiChu;

    // [MỚI] Các component của summary
    private JLabel lblTongSo, lblNhanVien, lblQuanLy;

    // [MỚI] Các component tìm kiếm
    private JComboBox<String> cbSearchType;
    private JTextField txtSearchInput;
    private JComboBox<String> cbSearchStatus;

    // [MỚI] Biến theo dõi trạng thái (Thêm mới hay Cập nhật)
    // Dùng MaNV để theo dõi. Nếu là null -> Thêm mới. Nếu có giá trị -> Cập nhật.
    private String maNV_dangSua = null;

    public ManHinhQuanLyNhanVien() {
        // [MỚI] Kết nối CSDL ngay khi khởi tạo
        try {
            ConnectDB.getInstance().connect();
            System.out.println("Kết nối CSDL thành công (ManHinhQuanLyNhanVien)");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        setTitle("Quản lý tài khoản NV");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        JPanel centerContentPanel = createCenterContentPanel();
        JPanel eastFormPanel = createEastFormPanel();

        JPanel mainContentWrapper = new JPanel(new BorderLayout(15, 15));
        mainContentWrapper.setBackground(BG_COLOR);
        mainContentWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainContentWrapper.add(centerContentPanel, BorderLayout.CENTER);
        mainContentWrapper.add(eastFormPanel, BorderLayout.EAST);

        add(mainContentWrapper, BorderLayout.CENTER);

        // [MỚI] Tải dữ liệu ban đầu
        loadTableData();
        updateSummaryBoxes();
    }

    // =================================================================================
    // KHU VỰC MENU BÊN TRÁI (Đã có điều hướng)
    // =================================================================================

    private JPanel createNavPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PRIMARY_COLOR);
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

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

        // [1. Trang chủ]
        JButton btnTrangChu = createNavItem("Trang chủ", "\uD83C\uDFE0");
        btnTrangChu.addActionListener(e -> {
            new ManHinhDashboardQuanLy().setVisible(true);
            this.dispose();
        });
        panel.add(btnTrangChu);

        // [2. Tra cứu hóa đơn]
        JButton btnTraCuu = createNavItem("Tra cứu hóa đơn", "\uD83D\uDD0D");
        btnTraCuu.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng Tra cứu hóa đơn đang được phát triển."));
        panel.add(btnTraCuu);

        // [3. Quản lý chuyến tàu]
        JButton btnQLChuyenTau = createNavItem("Quản lý chuyến tàu", "\uD83D\uDE86");
        btnQLChuyenTau.addActionListener(e -> {
            new ManhinhQuanLyChuyenTau().setVisible(true);
            this.dispose();
        });
        panel.add(btnQLChuyenTau);

        // [4. Quản lý tài khoản NV] - Màn hình hiện tại
        JButton selectedButton = createNavItem("Quản lý tài khoản NV", "\uD83D\uDC64");
        selectedButton.setBackground(SELECTED_COLOR);
        for (java.awt.event.MouseListener ml : selectedButton.getMouseListeners()) {
            selectedButton.removeMouseListener(ml);
        }
        panel.add(selectedButton);

        // [5. Quản lý giá vé]
        JButton btnQLGiaVe = createNavItem("Quản lý giá vé", "\uD88D\uDCB2");
        btnQLGiaVe.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng Quản lý giá vé đang được phát triển."));
        panel.add(btnQLGiaVe);

        // [6. Quản lý khuyến mãi]
        JButton btnQLKhuyenMai = createNavItem("Quản lý khuyến mãi", "\uD83C\uDFF7");
        btnQLKhuyenMai.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng Quản lý khuyến mãi đang được phát triển."));
        panel.add(btnQLKhuyenMai);

        // [7. Thống kê báo cáo]
        JButton btnThongKe = createNavItem("Thống kê báo cáo", "\uD83D\uDCCA");
        btnThongKe.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng Thống kê báo cáo đang được phát triển."));
        panel.add(btnThongKe);

        panel.add(Box.createVerticalGlue());
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(255, 255, 255, 70));
        separator.setBackground(PRIMARY_COLOR);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        // --- Nút Đăng xuất ---
        JButton btnDangXuat = createNavItem("Đăng xuất", "\uD83D\uDEAA");
        btnDangXuat.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // new ManHinhDangNhap().setVisible(true);
                this.dispose();
            }
        });
        panel.add(btnDangXuat);

        return panel;
    }

    private JButton createNavItem(String text, String iconText) {
        JButton button = new JButton();
        String htmlText = "<html>" +
                "<span style='font-family:\"Segoe UI Emoji\"; font-size:15pt;'>" + iconText + "</span>" +
                "&nbsp;&nbsp;&nbsp;" +
                "<span style='font-family:\"Segoe UI\", Arial; font-size: 12pt; font-weight: bold;'>" + text.replace(" ", "&nbsp;") + "</span>" +
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
        final Color hoverColor = new Color(0, 130, 235);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(originalColor)) button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(hoverColor)) button.setBackground(originalColor);
            }
        });
        return button;
    }

    // =================================================================================
    // KHU VỰC NỘI DUNG (ĐÃ SỬA)
    // =================================================================================

    private JPanel createCenterContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createSummaryPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createSearchPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createMainTablePanel());

        return panel;
    }

    /**
     * [ĐÃ SỬA] Tạo 3 ô tóm tắt (Khai báo biến cho JLabel)
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 15));
        panel.setOpaque(false);

        // Khởi tạo các JLabel
        lblTongSo = new JLabel("0");
        lblNhanVien = new JLabel("0");
        lblQuanLy = new JLabel("0");

        panel.add(createSummaryBox("Tổng số tài khoản:", lblTongSo, COLOR_GREEN));
        panel.add(createSummaryBox("Nhân viên bán vé:", lblNhanVien, COLOR_YELLOW));
        panel.add(createSummaryBox("Quản lý:", lblQuanLy, COLOR_ORANGE));

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setPreferredSize(new Dimension(0, 100));
        return panel;
    }

    /**
     * [ĐÃ SỬA] Phương thức trợ giúp tạo 1 ô tóm tắt (Nhận JLabel)
     */
    private JPanel createSummaryBox(String title, JLabel lblValue, Color bgColor) {
        JPanel box = new JPanel(new BorderLayout(0, 5));
        box.setBackground(bgColor);
        box.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_BOLD_14);
        lblTitle.setForeground(Color.WHITE);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(Color.WHITE);

        box.add(lblTitle, BorderLayout.NORTH);
        box.add(lblValue, BorderLayout.CENTER);
        return box;
    }

    /**
     * [ĐÃ SỬA] Tạo khu vực tìm kiếm (Khai báo biến và thêm sự kiện)
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

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tìm kiếm theo:"), gbc);

        gbc.gridx = 1;
        String[] searchOptions = {"Mã nhân viên", "Số điện thoại", "Số CCCD", "Họ tên nhân viên"};
        cbSearchType = new JComboBox<>(searchOptions);
        cbSearchType.setFont(FONT_PLAIN_14);
        panel.add(cbSearchType, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Nhập thông tin tìm kiếm:"), gbc);

        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtSearchInput = new JTextField(20);
        txtSearchInput.setFont(FONT_PLAIN_14);
        panel.add(txtSearchInput, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Trạng thái:"), gbc);

        gbc.gridx = 1;
        String[] statusOptions = {"Đang hoạt động", "Ngừng hoạt động"};
        cbSearchStatus = new JComboBox<>(statusOptions);
        cbSearchStatus.setFont(FONT_PLAIN_14);
        panel.add(cbSearchStatus, gbc);

        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 5, 5, 5);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(FONT_BOLD_14);
        btnSearch.setBackground(PRIMARY_COLOR);
        btnSearch.setForeground(Color.WHITE);

        // [MỚI] Thêm sự kiện cho nút Tìm kiếm
        btnSearch.addActionListener(e -> searchEmployees());

        panel.add(btnSearch, gbc);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.setPreferredSize(new Dimension(0, 150));
        return panel;
    }

    /**
     * [ĐÃ SỬA] Tạo khu vực bảng (Khai báo biến và thêm sự kiện)
     */
    private JPanel createMainTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("DANH SÁCH TÀI KHOẢN");
        lblTitle.setFont(FONT_TITLE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnAdd = new JButton("+ Thêm nhân viên");
        btnAdd.setFont(FONT_BOLD_14);
        btnAdd.setBackground(COLOR_GREEN);
        btnAdd.setForeground(Color.WHITE);

        // [MỚI] Thêm sự kiện cho nút Thêm
        btnAdd.addActionListener(e -> {
            clearEmployeeForm();
            formBorder.setTitle("Thêm nhân viên mới");
            panel.repaint(); // Cập nhật lại tiêu đề
            txtTenDangNhap.setEnabled(true); // Cho phép nhập tên đăng nhập
            maNV_dangSua = null; // Đặt trạng thái là "thêm mới"
        });

        headerPanel.add(btnAdd, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"STT", "Mã nhân viên", "Tên nhân viên", "Giới tính", "Chức vụ", "Mật khẩu", "Trạng thái", "Tùy chọn"};

        // Khởi tạo model và table (biến toàn cục)
        model = new DefaultTableModel(columnNames, 0) { // 0 hàng ban đầu
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Chỉ cho phép click cột "Tùy chọn"
            }
        };
        table = new JTable(model);

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
     * [ĐÃ SỬA] Tạo panel bên phải (Khai báo biến)
     */
    private JPanel createEastFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(550, 0));

        panel.add(createNotesPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createEmployeeFormPanel());

        return panel;
    }

    /**
     * [ĐÃ SỬA] Tạo panel "Ghi chú" (Khai báo biến)
     */
    private JPanel createNotesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Ghi chú",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, Color.BLACK
        );
        panel.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        txtGhiChu = new JTextArea(); // Khai báo biến
        txtGhiChu.setBackground(new Color(255, 255, 224));
        txtGhiChu.setFont(FONT_PLAIN_14);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        JScrollPane scrollNotes = new JScrollPane(txtGhiChu);
        scrollNotes.setPreferredSize(new Dimension(0, 150));

        JButton btnSaveNotes = new JButton("Lưu");
        btnSaveNotes.setFont(FONT_BOLD_14);
        btnSaveNotes.setBackground(PRIMARY_COLOR);
        btnSaveNotes.setForeground(Color.WHITE);
        // TODO: Thêm sự kiện lưu Ghi chú

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSaveNotes);

        panel.add(scrollNotes, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        return panel;
    }

    /**
     * [ĐÃ SỬA] Tạo panel Form chi tiết (Khai báo biến)
     */
    private JPanel createEmployeeFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);

        // Khởi tạo border (biến toàn cục)
        formBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin nhân viên",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, PRIMARY_COLOR
        );
        panel.setBorder(BorderFactory.createCompoundBorder(formBorder, new EmptyBorder(15, 15, 15, 15)));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Khởi tạo các component (biến toàn cục)

        // --- CỘT 1 ---
        gbc.gridx = 0;
        gbc.gridy = 0; fieldsPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridy = 1; fieldsPanel.add(new JLabel("Email:"), gbc);
        gbc.gridy = 2; fieldsPanel.add(new JLabel("Số CCCD:"), gbc);
        gbc.gridy = 3; fieldsPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridy = 4; fieldsPanel.add(new JLabel("Chức vụ:"), gbc);
        gbc.gridy = 5; fieldsPanel.add(new JLabel("Ca làm việc: (Bỏ qua)"), gbc); // Sẽ bỏ qua trường này
        gbc.gridy = 6; fieldsPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridy = 7; fieldsPanel.add(new JLabel("Ngày tạo:"), gbc);

        // --- CỘT 2 (Components) ---
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.gridy = 0; txtHoTen = new JTextField(); fieldsPanel.add(txtHoTen, gbc);
        gbc.gridy = 1; txtEmail = new JTextField(); fieldsPanel.add(txtEmail, gbc);
        gbc.gridy = 2; txtSoCCCD = new JTextField(); fieldsPanel.add(txtSoCCCD, gbc);
        gbc.gridy = 3; txtDiaChi = new JTextField(); fieldsPanel.add(txtDiaChi, gbc);
        gbc.gridy = 4; cbChucVu = new JComboBox<>(new String[]{"Nhân viên bán vé", "Quản lý"}); fieldsPanel.add(cbChucVu, gbc);
        gbc.gridy = 5; cbCaLamViec = new JComboBox<>(new String[]{"1", "2", "3"}); fieldsPanel.add(cbCaLamViec, gbc);
        gbc.gridy = 6; txtTenDangNhap = new JTextField(); fieldsPanel.add(txtTenDangNhap, gbc);
        gbc.gridy = 7;
        dateNgayTao = new JDateChooser();
        dateNgayTao.setDateFormatString("dd/MM/yyyy");
        dateNgayTao.setEnabled(false); // Ngày tạo không cho sửa
        fieldsPanel.add(dateNgayTao, gbc);

        // --- CỘT 3 ---
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(5, 15, 5, 5);
        gbc.gridy = 0; fieldsPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridy = 1; fieldsPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridy = 4; fieldsPanel.add(new JLabel("Ngày vào làm:"), gbc);
        // gbc.gridy = 5; fieldsPanel.add(new JLabel("Giờ làm:"), gbc); // Bỏ qua
        gbc.gridy = 6; fieldsPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridy = 7; fieldsPanel.add(new JLabel("Trạng thái:"), gbc);

        // --- CỘT 4 (Components) ---
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 0;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.setOpaque(false);
        radNam = new JRadioButton("Nam"); radNam.setOpaque(false);
        radNu = new JRadioButton("Nữ"); radNu.setOpaque(false);
        bgGender = new ButtonGroup();
        bgGender.add(radNam); bgGender.add(radNu);
        genderPanel.add(radNam); genderPanel.add(radNu);
        fieldsPanel.add(genderPanel, gbc);

        gbc.gridy = 1; txtSoDienThoai = new JTextField(); fieldsPanel.add(txtSoDienThoai, gbc);
        gbc.gridy = 4;
        dateNgayVaoLam = new JDateChooser();
        dateNgayVaoLam.setDateFormatString("dd/MM/yyyy");
        fieldsPanel.add(dateNgayVaoLam, gbc);
        // gbc.gridy = 5; // Bỏ qua giờ làm
        gbc.gridy = 6; txtMatKhau = new JPasswordField(); fieldsPanel.add(txtMatKhau, gbc);
        gbc.gridy = 7; cbTrangThai = new JComboBox<>(new String[]{"Đang hoạt động", "Ngừng hoạt động"}); fieldsPanel.add(cbTrangThai, gbc);

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

        // [MỚI] Thêm sự kiện cho 3 nút
        btnLuu.addActionListener(e -> saveEmployee());
        btnXoaTrang.addActionListener(e -> clearEmployeeForm());
        btnHuy.addActionListener(e -> clearEmployeeForm());

        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // =================================================================================
    // LỚP NỘI TẠI (INNER CLASS) CHO CÁC NÚT TRONG BẢNG (ĐÃ SỬA)
    // =================================================================================

    class ButtonColumnRenderer extends JPanel implements TableCellRenderer {
        // ... Code giữ nguyên ...
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
            if (isSelected) setBackground(table.getSelectionBackground());
            else setBackground(Color.WHITE);
            return this;
        }
    }

    class ButtonColumnEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        // ... Code gần như giữ nguyên, chỉ SỬA LẠI ACTIONLISTENER ...
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

            // [SỬA] Sự kiện nút Sửa (Edit)
            btnEdit.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    String maNV = table.getValueAt(row, 1).toString();
                    // [MỚI] Gọi hàm fillFormFromTable
                    fillFormFromTable(maNV);
                }
            });

            // [SỬA] Sự kiện nút Xóa (Delete)
            btnDelete.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    String maNV = table.getValueAt(row, 1).toString();
                    int confirm = JOptionPane.showConfirmDialog(table,
                            "Bạn có chắc chắn muốn XÓA (ngừng hoạt động) nhân viên: " + maNV + "?",
                            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        // [MỚI] Gọi hàm softDelete
                        softDeleteEmployee(maNV);
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
        public Object getCellEditorValue() { return ""; }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return panel;
        }
    }

    // =================================================================================
    // CÁC HÀM XỬ LÝ NGHIỆP VỤ CSDL (MỚI)
    // =================================================================================

    /**
     * [MỚI] Tải toàn bộ dữ liệu từ CSDL lên JTable
     */
    private void loadTableData() {
        // Xóa dữ liệu cũ trên bảng
        model.setRowCount(0);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectDB.getConnection();
            // Lấy dữ liệu từ 2 bảng NhanVien và TaiKhoan
            String sql = "SELECT n.MaNV, n.HoTen, n.GioiTinh, n.ChucVu, t.MatKhau, t.TrangThai " +
                    "FROM NhanVien n " +
                    "JOIN TaiKhoan t ON n.MaNV = t.MaNV";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            int stt = 1;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(stt++);
                row.add(rs.getString("MaNV"));
                row.add(rs.getString("HoTen"));
                row.add(rs.getString("GioiTinh"));
                row.add(rs.getString("ChucVu"));
                row.add("********"); // Không hiển thị mật khẩu
                row.add(rs.getString("TrangThai"));
                row.add(""); // Cột Tùy chọn (để chứa nút)

                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu nhân viên: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                // Không đóng conn, để dành cho các thao tác khác
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * [MỚI] Cập nhật 3 ô tóm tắt
     */
    private void updateSummaryBoxes() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectDB.getConnection();

            // 1. Tổng số tài khoản (đang hoạt động)
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM TaiKhoan WHERE TrangThai = N'Đang hoạt động'");
            rs = pstmt.executeQuery();
            if (rs.next()) lblTongSo.setText(String.valueOf(rs.getInt(1)));
            rs.close();
            pstmt.close();

            // 2. Nhân viên bán vé
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM NhanVien nv JOIN TaiKhoan tk ON nv.MaNV = tk.MaNV WHERE nv.ChucVu = N'Nhân viên bán vé' AND tk.TrangThai = N'Đang hoạt động'");
            rs = pstmt.executeQuery();
            if (rs.next()) lblNhanVien.setText(String.valueOf(rs.getInt(1)));
            rs.close();
            pstmt.close();

            // 3. Quản lý
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM NhanVien nv JOIN TaiKhoan tk ON nv.MaNV = tk.MaNV WHERE nv.ChucVu = N'Quản lý' AND tk.TrangThai = N'Đang hoạt động'");
            rs = pstmt.executeQuery();
            if (rs.next()) lblQuanLy.setText(String.valueOf(rs.getInt(1)));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thống kê: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * [MỚI] Xóa trắng các trường trên form
     */
    private void clearEmployeeForm() {
        txtHoTen.setText("");
        txtEmail.setText("");
        txtSoCCCD.setText("");
        txtDiaChi.setText("");
        txtTenDangNhap.setText("");
        txtSoDienThoai.setText("");
        txtMatKhau.setText("");
        cbChucVu.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        dateNgayVaoLam.setDate(null);
        dateNgayTao.setDate(null);
        bgGender.clearSelection();
        txtGhiChu.setText("");

        maNV_dangSua = null; // Đặt về trạng thái thêm mới
        txtTenDangNhap.setEnabled(true); // Cho phép sửa tên đăng nhập
        formBorder.setTitle("Thông tin nhân viên"); // Đặt lại tiêu đề
        // Yêu cầu panel vẽ lại để cập nhật tiêu đề
        ((JPanel)txtHoTen.getParent().getParent()).getParent().repaint();
    }

    /**
     * [MỚI] Tải dữ liệu của 1 nhân viên lên form để sửa
     */
    private void fillFormFromTable(String maNV) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectDB.getConnection();
            String sql = "SELECT * FROM NhanVien n " +
                    "JOIN TaiKhoan t ON n.MaNV = t.MaNV " +
                    "WHERE n.MaNV = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maNV);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                maNV_dangSua = maNV; // Đặt trạng thái là "đang sửa"

                txtHoTen.setText(rs.getString("HoTen"));
                txtEmail.setText(rs.getString("Email"));
                txtSoCCCD.setText(rs.getString("SoCCCD"));
                txtDiaChi.setText(rs.getString("DiaChi"));
                cbChucVu.setSelectedItem(rs.getString("ChucVu"));
                txtSoDienThoai.setText(rs.getString("SDT"));
                dateNgayVaoLam.setDate(rs.getDate("NgayVaoLam"));

                String gioiTinh = rs.getString("GioiTinh");
                if (gioiTinh.equals("Nam")) radNam.setSelected(true);
                else radNu.setSelected(true);

                txtTenDangNhap.setText(rs.getString("TenDangNhap"));
                txtTenDangNhap.setEnabled(false); // Không cho sửa Tên đăng nhập (Khóa)

                txtMatKhau.setText(rs.getString("MatKhau"));
                cbTrangThai.setSelectedItem(rs.getString("TrangThai"));
                dateNgayTao.setDate(rs.getTimestamp("NgayTao"));

                // Cập nhật tiêu đề form
                formBorder.setTitle("Sửa thông tin nhân viên: " + maNV);
                ((JPanel)txtHoTen.getParent().getParent()).getParent().repaint();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin nhân viên: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * [MỚI] Thực hiện "Xóa mềm" (Cập nhật trạng thái)
     */
    private void softDeleteEmployee(String maNV) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = ConnectDB.getConnection();
            String sql = "UPDATE TaiKhoan SET TrangThai = N'Ngừng hoạt động' WHERE MaNV = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maNV);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái nhân viên " + maNV + " thành 'Ngừng hoạt động'.");
                loadTableData(); // Tải lại bảng
                updateSummaryBoxes(); // Cập nhật thống kê
                clearEmployeeForm(); // Xóa trắng form
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * [MỚI] Lưu (Thêm mới hoặc Cập nhật) nhân viên
     */
    private void saveEmployee() {
        // 1. Thu thập dữ liệu từ Form
        String hoTen = txtHoTen.getText();
        String email = txtEmail.getText();
        String cccd = txtSoCCCD.getText();
        String diaChi = txtDiaChi.getText();
        String chucVu = cbChucVu.getSelectedItem().toString();
        String sdt = txtSoDienThoai.getText();
        Date ngayVaoLam = dateNgayVaoLam.getDate();
        String gioiTinh = radNam.isSelected() ? "Nam" : (radNu.isSelected() ? "Nữ" : null);

        String tenDangNhap = txtTenDangNhap.getText();
        String matKhau = new String(txtMatKhau.getPassword());
        String trangThai = cbTrangThai.getSelectedItem().toString();

        // 2. Kiểm tra dữ liệu (Validation) - Cần làm kỹ hơn
        if (hoTen.isEmpty() || tenDangNhap.isEmpty() || matKhau.isEmpty() || gioiTinh == null) {
            JOptionPane.showMessageDialog(this, "Họ tên, Giới tính, Tên đăng nhập và Mật khẩu là bắt buộc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmtNV = null; // Statement cho bảng NhanVien
        PreparedStatement pstmtTK = null; // Statement cho bảng TaiKhoan

        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            String maNV;

            // 3. Phân biệt THÊM MỚI (maNV_dangSua == null) hay CẬP NHẬT
            if (maNV_dangSua == null) {
                // ==================
                // THÊM MỚI
                // ==================

                // 3a. Tạo MaNV mới
                maNV = generateNewMaNV(conn);

                // 3b. INSERT vào NhanVien
                String sqlNV = "INSERT INTO NhanVien (MaNV, HoTen, SoCCCD, NgaySinh, Email, SDT, GioiTinh, DiaChi, NgayVaoLam, ChucVu) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pstmtNV = conn.prepareStatement(sqlNV);
                pstmtNV.setString(1, maNV);
                pstmtNV.setString(2, hoTen);
                pstmtNV.setString(3, cccd);
                pstmtNV.setDate(4, null); // TODO: Thiếu trường Ngày Sinh trên form
                pstmtNV.setString(5, email);
                pstmtNV.setString(6, sdt);
                pstmtNV.setString(7, gioiTinh);
                pstmtNV.setString(8, diaChi);
                pstmtNV.setDate(9, new java.sql.Date(ngayVaoLam.getTime()));
                pstmtNV.setString(10, chucVu);
                pstmtNV.executeUpdate();

                // 3c. INSERT vào TaiKhoan
                String sqlTK = "INSERT INTO TaiKhoan (TenDangNhap, MaNV, MatKhau, NgayTao, TrangThai) " +
                        "VALUES (?, ?, ?, ?, ?)";
                pstmtTK = conn.prepareStatement(sqlTK);
                pstmtTK.setString(1, tenDangNhap);
                pstmtTK.setString(2, maNV);
                pstmtTK.setString(3, matKhau);
                pstmtTK.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Ngày tạo là ngày hiện tại
                pstmtTK.setString(5, trangThai);
                pstmtTK.executeUpdate();

                JOptionPane.showMessageDialog(this, "Thêm nhân viên mới thành công! Mã NV: " + maNV);

            } else {
                // ==================
                // CẬP NHẬT
                // ==================
                maNV = maNV_dangSua;

                // 3b. UPDATE NhanVien
                String sqlNV = "UPDATE NhanVien SET HoTen = ?, SoCCCD = ?, Email = ?, SDT = ?, GioiTinh = ?, DiaChi = ?, NgayVaoLam = ?, ChucVu = ? " +
                        "WHERE MaNV = ?";
                pstmtNV = conn.prepareStatement(sqlNV);
                pstmtNV.setString(1, hoTen);
                pstmtNV.setString(2, cccd);
                pstmtNV.setString(3, email);
                pstmtNV.setString(4, sdt);
                pstmtNV.setString(5, gioiTinh);
                pstmtNV.setString(6, diaChi);
                pstmtNV.setDate(7, new java.sql.Date(ngayVaoLam.getTime()));
                pstmtNV.setString(8, chucVu);
                pstmtNV.setString(9, maNV);
                pstmtNV.executeUpdate();

                // 3c. UPDATE TaiKhoan
                String sqlTK = "UPDATE TaiKhoan SET MatKhau = ?, TrangThai = ? " +
                        "WHERE MaNV = ?";
                pstmtTK = conn.prepareStatement(sqlTK);
                pstmtTK.setString(1, matKhau);
                pstmtTK.setString(2, trangThai);
                pstmtTK.setString(3, maNV);
                pstmtTK.executeUpdate();

                JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên " + maNV + " thành công!");
            }

            conn.commit(); // Hoàn tất Transaction

            // 4. Tải lại dữ liệu
            loadTableData();
            updateSummaryBoxes();
            clearEmployeeForm();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu nhân viên: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            try {
                if (conn != null) conn.rollback(); // Hoàn tác nếu có lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (pstmtNV != null) pstmtNV.close();
                if (pstmtTK != null) pstmtTK.close();
                if (conn != null) conn.setAutoCommit(true); // Trả về trạng thái tự động commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * [MỚI] Hàm tạo MaNV mới (Ví dụ: NV004 -> NV005)
     * Cần phải hoàn thiện dựa trên logic mã của bạn
     */
    private String generateNewMaNV(Connection conn) throws SQLException {
        // Đây là 1 cách đơn giản, bạn nên có 1 stored procedure hoặc logic tốt hơn
        PreparedStatement pstmt = conn.prepareStatement("SELECT TOP 1 MaNV FROM NhanVien ORDER BY MaNV DESC");
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            String lastMaNV = rs.getString(1);
            int lastNum = Integer.parseInt(lastMaNV.substring(2)); // Bỏ "NV"
            return String.format("NV%03d", lastNum + 1); // "NV" + 3 số (ví dụ: NV005)
        } else {
            return "NV001"; // Nếu là nhân viên đầu tiên
        }
    }

    /**
     * [MỚI] Tìm kiếm nhân viên và tải lại bảng
     */
    private void searchEmployees() {
        String searchBy = cbSearchType.getSelectedItem().toString();
        String searchTerm = txtSearchInput.getText().trim();
        String status = cbSearchStatus.getSelectedItem().toString();

        model.setRowCount(0); // Xóa bảng

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectDB.getConnection();

            // 1. Xây dựng câu SQL động
            StringBuilder sql = new StringBuilder(
                    "SELECT n.MaNV, n.HoTen, n.GioiTinh, n.ChucVu, t.MatKhau, t.TrangThai " +
                            "FROM NhanVien n JOIN TaiKhoan t ON n.MaNV = t.MaNV " +
                            "WHERE t.TrangThai = ? "
            );

            String searchColumn = "";
            switch (searchBy) {
                case "Mã nhân viên": searchColumn = "n.MaNV"; break;
                case "Số điện thoại": searchColumn = "n.SDT"; break;
                case "Số CCCD": searchColumn = "n.SoCCCD"; break;
                case "Họ tên nhân viên": searchColumn = "n.HoTen"; break;
            }

            if (!searchTerm.isEmpty()) {
                sql.append(" AND ").append(searchColumn).append(" LIKE ?");
            }

            // 2. Tạo PreparedStatement
            pstmt = conn.prepareStatement(sql.toString());

            // 3. Set tham số
            pstmt.setString(1, status);
            if (!searchTerm.isEmpty()) {
                pstmt.setString(2, "%" + searchTerm + "%");
            }

            // 4. Thực thi và tải lên bảng
            rs = pstmt.executeQuery();
            int stt = 1;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(stt++);
                row.add(rs.getString("MaNV"));
                row.add(rs.getString("HoTen"));
                row.add(rs.getString("GioiTinh"));
                row.add(rs.getString("ChucVu"));
                row.add("********");
                row.add(rs.getString("TrangThai"));
                row.add("");
                model.addRow(row);
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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