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
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;

// [THÊM] Import lớp kết nối của bạn
import dao.GaDao;
import dao.TauDAO;
import database.ConnectDB;
//import gui.MainFrame.ManHinhDashboardQuanLy;


/**
 * Lớp này tạo giao diện Quản lý Chuyến Tàu.
 * ĐÃ NÂNG CẤP: Kết nối CSDL, dùng JComboBox, JDateChooser, JSpinner.
 * Đã THÊM đầy đủ sự kiện điều hướng (navigation).
 */
public class ManhinhQuanLyChuyenTau extends JPanel {

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
        setSize(1600, 900);
        setLayout(new BorderLayout());

        // 1. Panel điều hướng bên trái

        // 2. Panel nội dung (Form và Bảng)
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        // [MỚI] Tải dữ liệu từ CSDL lên các ComboBox
        loadDuLieuMaTau();
        loadDuLieuGa();
    }

    // =================================================================================
    // KHU VỰC MENU BÊN TRÁI (Giữ nguyên)
    // =================================================================================

    /**
     * [ĐÃ CẬP NHẬT] Tạo panel điều hướng bên trái.
     * Mục "Quản lý chuyến tàu" được chọn.
     * Đã thêm ActionListeners để điều hướng.
     */


    /**
     * [CHUNG] Phương thức trợ giúp để tạo một nút menu.
     */

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
     * Tải danh sách Mã Tàu từ DAO lên JComboBox.
     */
    private void loadDuLieuMaTau() {
        try {
            // 1. Lấy dữ liệu từ DAO
            List<String> danhSachMaTau = TauDAO.layDanhSachMaTau();

            // 2. Cập nhật JComboBox
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cbMaTau.getModel();
            model.removeAllElements(); // Xóa dữ liệu cũ

            for (String maTau : danhSachMaTau) {
                model.addElement(maTau);
            }
        } catch (SQLException e) {
            // Xử lý lỗi CSDL
            hienThiThongBaoLoi("Lỗi tải danh sách Mã Tàu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tải danh sách Tên Ga từ DAO lên 2 JComboBox.
     */
    private void loadDuLieuGa() {
        try {
            // 1. Lấy dữ liệu từ DAO (SỬA ĐỔI TẠI ĐÂY: Dùng Vector thay vì List)
            List<String> danhSachTenGa = GaDao.layDanhSachTenGa();

            // 2. Cập nhật JComboBox
            DefaultComboBoxModel<String> modelGaDi = (DefaultComboBoxModel<String>) cbGaDi.getModel();
            DefaultComboBoxModel<String> modelGaDen = (DefaultComboBoxModel<String>) cbGaDen.getModel();

            // Bạn có thể dùng constructor của DefaultComboBoxModel để nạp dữ liệu nhanh hơn
            // hoặc tiếp tục dùng vòng lặp như sau:

            modelGaDi.removeAllElements();
            modelGaDen.removeAllElements();

            for (String tenGa : danhSachTenGa) {
                modelGaDi.addElement(tenGa);
                modelGaDen.addElement(tenGa);
            }

        /* // CÁCH KHÁC VÀ HIỆU QUẢ HƠN: Tạo Model mới từ Vector (áp dụng cho cả 2 JComboBox)
        DefaultComboBoxModel<String> newModel = new DefaultComboBoxModel<>(danhSachTenGa);

        cbGaDi.setModel(newModel);

        // Cần tạo một model mới cho cbGaDen nếu bạn muốn model của nó độc lập
        DefaultComboBoxModel<String> newModelGaDen = new DefaultComboBoxModel<>(danhSachTenGa);
        cbGaDen.setModel(newModelGaDen);
        */

        } catch (SQLException e) {
            // Xử lý lỗi CSDL
            hienThiThongBaoLoi("Lỗi tải danh sách Tên Ga: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức giả định để hiển thị lỗi
    private void hienThiThongBaoLoi(String message) {
        System.err.println(message);
        // Trong ứng dụng Swing thực tế, bạn sẽ dùng JOptionPane.showMessageDialog
    }

    /**
     * Phương thức main để chạy ứng dụng.
     */
    public static void main(String[] args) {
        // [MỚI] Kết nối CSDL ngay khi ứng dụng khởi động
//        try {
//            ConnectDB.getInstance().connect();
//            System.out.println("Kết nối CSDL thành công!");
//        } catch (Exception e) {
//            System.err.println("Lỗi kết nối CSDL!");
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, "Không thể kết nối đến CSDL. Vui lòng kiểm tra lại.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
//            return; // Dừng ứng dụng nếu không kết nối được
//        }
//
//
//        try {
//            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            // Dùng giao diện mặc định
//        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Mở ca (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ManhinhQuanLyChuyenTau(), BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}