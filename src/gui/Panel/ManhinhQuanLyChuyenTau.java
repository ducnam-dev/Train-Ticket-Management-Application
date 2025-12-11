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

// Import JDateChooser
import com.toedter.calendar.JDateChooser;

// Import Swing, AWT, SQL, Util
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
import java.time.format.DateTimeFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

// Import DAO và Entity
import control.CaLamViec;
import dao.ChuyenTauDao; // Cần tạo
import dao.GaDao;
import dao.TauDAO;
import entity.ChuyenTau; // Cần tạo
import entity.Ga;       // Cần tạo
import entity.NhanVien; // Cần tạo
import entity.Tau;       // Cần tạo

// Import lớp kết nối
import database.ConnectDB;
import entity.lopEnum.TrangThaiChuyenTau;


/**
 * Lớp này tạo giao diện Quản lý Chuyến Tàu (JPanel).
 * Đã NÂNG CẤP: Kết nối CSDL, dùng JComboBox object, JDateChooser, JSpinner.
 * Đã THÊM: Xử lý nghiệp vụ Thêm, Sửa, Tìm, Xóa trắng, Click-bảng.
 */
public class ManhinhQuanLyChuyenTau extends JPanel {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);

    // =================================================================================
    // KHAI BÁO COMPONENT VÀ DAO
    // =================================================================================
    private JComboBox<Tau> cbMaTau; // [SỬA] Dùng JComboBox<Tau>
    private JComboBox<Ga> cbGaDi;  // [SỬA] Dùng JComboBox<Ga>
    private JComboBox<Ga> cbGaDen;  // [SỬA] Dùng JComboBox<Ga>
    private JDateChooser dateChooserNgayDi;
    private JSpinner timeSpinnerGioDi;
    private JTextField txtMaChuyenTau; // [MỚI] Biến cho Mã Chuyến Tàu
    private JButton btnTim, btnThem, btnSua, btnThemExcel, btnXoaTrang; // [MỚI] Biến cho các nút

    private JTable tableChuyenTau;
    private DefaultTableModel modelChuyenTau;

    // Khai báo DAO
    private ChuyenTauDao chuyenTauDao;
    private GaDao gaDao;
    private TauDAO tauDAO;

    // Định dạng ngày/giờ
    private final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

    // Dữ liệu Nhân viên
    private String maNVHienThi = "N/A";

    public ManhinhQuanLyChuyenTau() {
        // Khởi tạo DAO
        try {
            chuyenTauDao = new ChuyenTauDao();
            gaDao = new GaDao();
            tauDAO = new TauDAO();
        } catch (Exception e) {
            hienThiThongBaoLoi("Không thể khởi tạo DAO: " + e.getMessage());
            e.printStackTrace();
        }

        // Thiết lập layout
        setLayout(new BorderLayout());

        // Panel nội dung
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        // Lấy thông tin nhân viên
        layThongTinNhanVien();
        // Tải dữ liệu CSDL
        loadDuLieuMaTau();
        loadDuLieuGa();
        loadDuLieuChuyenTauLenBang(); // Tải dữ liệu ban đầu
    }
    private void layThongTinNhanVien() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv != null) {
            this.maNVHienThi = nv.getMaNV();
        } else {
            this.maNVHienThi = "Lỗi Phiên";
        }
    }

    // =================================================================================
    // KHU VỰC TẠO GIAO DIỆN
    // =================================================================================

    /**
     * Tạo panel nội dung chính (Tiêu đề, Form, Bảng).
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel title = new JLabel("Quản lý chuyến tàu");
        title.setFont(FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(title, BorderLayout.NORTH);
        JPanel mainArea = new JPanel();
        mainArea.setLayout(new BoxLayout(mainArea, BoxLayout.Y_AXIS));
        mainArea.setOpaque(false);
        JPanel formPanel = createFormPanel();
        mainArea.add(formPanel);
        mainArea.add(Box.createRigidArea(new Dimension(0, 20)));
        JPanel tablePanel = createTablePanel();
        mainArea.add(tablePanel);
        panel.add(mainArea, BorderLayout.CENTER);
        return panel;
    }

    /**
     * [ĐÃ SỬA] Tạo panel form, gán biến và thêm ActionListeners.
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1: Mã chuyến tàu
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Mã chuyến tàu:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMaChuyenTau = new JTextField(20); // [SỬA] Gán vào biến
        fieldsPanel.add(txtMaChuyenTau, gbc);

        // Hàng 2: Mã Tàu (JComboBox<Tau>)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Mã tàu:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbMaTau = new JComboBox<>(); // [SỬA] Khởi tạo JComboBox<Tau>
        cbMaTau.setFont(FONT_PLAIN_14);
        fieldsPanel.add(cbMaTau, gbc);

        // Hàng 3: Ga đi và Ga đến (JComboBox<Ga>)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Ga đi:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        cbGaDi = new JComboBox<>(); // [SỬA] Khởi tạo JComboBox<Ga>
        cbGaDi.setFont(FONT_PLAIN_14);
        fieldsPanel.add(cbGaDi, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets = new Insets(8, 20, 8, 8);
        fieldsPanel.add(new JLabel("Ga đến:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5; gbc.insets = new Insets(8, 8, 8, 8);
        cbGaDen = new JComboBox<>(); // [SỬA] Khởi tạo JComboBox<Ga>
        cbGaDen.setFont(FONT_PLAIN_14);
        fieldsPanel.add(cbGaDen, gbc);

        // Hàng 4: Giờ đi (JSpinner)
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Giờ đi:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        Date initTime = Calendar.getInstance().getTime();
        SpinnerDateModel timeModel = new SpinnerDateModel(initTime, null, null, Calendar.HOUR_OF_DAY);
        timeSpinnerGioDi = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinnerGioDi, "HH:mm");
        timeSpinnerGioDi.setEditor(timeEditor);
        timeSpinnerGioDi.setFont(FONT_PLAIN_14);
        fieldsPanel.add(timeSpinnerGioDi, gbc);

        // Hàng 5: Ngày đi (JDateChooser)
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Ngày đi:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        dateChooserNgayDi = new JDateChooser();
        dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayDi.setFont(FONT_PLAIN_14);
        fieldsPanel.add(dateChooserNgayDi, gbc);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        // --- Panel Nút bấm (Gán biến và thêm ActionListeners) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnTim = new JButton("Tìm");
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnThemExcel = new JButton("Thêm nhanh bằng excel");
        btnXoaTrang = new JButton("Xóa trắng"); // Nút mới

        buttonPanel.add(btnTim);
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnThemExcel);
        buttonPanel.add(btnXoaTrang);

        // [MỚI] Gán sự kiện
        btnThem.addActionListener(e -> themChuyenTau());
        btnSua.addActionListener(e -> suaChuyenTau());
        btnTim.addActionListener(e -> timChuyenTau());
        btnXoaTrang.addActionListener(e -> xoaTrangForm());
        btnThemExcel.addActionListener(e -> themBangExcel());

        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height + 20));
        return panel;
    }

    /**
     * [ĐÃ SỬA] Tạo panel bảng, gán biến và thêm MouseListener.
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));

        // Cập nhật tên cột (CSDL QuanLyVeTauTest2)
        String[] columnNames = {"Mã Chuyến Tàu", "Mã Tàu", "Ga Đi", "Ga Đến", "Giờ Khởi Hành", "Ngày Khởi Hành", "Trạng Thái"};
        modelChuyenTau = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableChuyenTau = new JTable(modelChuyenTau);

        // [MỚI] Thêm MouseListener
        tableChuyenTau.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dienThongTinVaoForm();
            }
        });

        tableChuyenTau.setFillsViewportHeight(true);
        tableChuyenTau.setFont(FONT_PLAIN_14);
        tableChuyenTau.setRowHeight(28);
        tableChuyenTau.getTableHeader().setFont(FONT_BOLD_14);
        tableChuyenTau.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableChuyenTau);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // =================================================================================
    // KHU VỰC TẢI DỮ LIỆU CSDL (ĐÃ SỬA DÙNG DAO VÀ JComboBox<Object>)
    // =================================================================================

    /**
     * [ĐÃ SỬA] Tải danh sách Tàu (Tau) từ DAO lên JComboBox.
     * Đổi sang bắt Exception chung vì DAO không ném SQLException.
     */
    private void loadDuLieuMaTau() {
        try {
            // Giả sử tauDAO (hoặc TauDAO) đã được khởi tạo trong constructor

            // 1. Lấy dữ liệu từ DAO (gọi phương thức đúng)
            // Hàm layTatCa() này tự bắt SQLException bên trong
            List<Tau> danhSachTau = tauDAO.layTatCa();

            // 2. Cập nhật JComboBox
            Vector<Tau> vectorTau = new Vector<>(danhSachTau); // Chuyển sang Vector
            DefaultComboBoxModel<Tau> model = new DefaultComboBoxModel<>(vectorTau);
            cbMaTau.setModel(model);
            cbMaTau.setRenderer(new TauRenderer()); // Dùng Renderer để hiển thị

        } catch (Exception e) { // <-- [SỬA LỖI Ở ĐÂY] Đổi thành Exception
            // Bắt các lỗi chung, ví dụ: NullPointerException (nếu tauDAO null)
            // hoặc các lỗi runtime khác có thể xảy ra.
            hienThiThongBaoLoi("Lỗi tải danh sách Tàu: " + e.getMessage());
            e.printStackTrace();
        }
        // Không cần 'catch (NullPointerException e)' riêng nữa vì 'Exception' đã bao gồm nó.
    }

    /**
     * [SỬA] Tải danh sách Ga (Ga) từ DAO lên JComboBox.
     */
    private void loadDuLieuGa() {
        try {
            // Giả sử gaDao đã được khởi tạo
            Vector<Ga> danhSachGa = gaDao.layDanhSachGa();

            DefaultComboBoxModel<Ga> modelGaDi = new DefaultComboBoxModel<>(danhSachGa);
            DefaultComboBoxModel<Ga> modelGaDen = new DefaultComboBoxModel<>(new Vector<>(danhSachGa)); // Tạo Vector mới

            cbGaDi.setModel(modelGaDi);
            cbGaDen.setModel(modelGaDen);

            GaRenderer renderer = new GaRenderer();
            cbGaDi.setRenderer(renderer);
            cbGaDen.setRenderer(renderer);

        } catch (Exception e) { // <-- SỬA Ở ĐÂY: Bắt Exception chung
            hienThiThongBaoLoi("Lỗi tải danh sách Ga: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * [SỬA] Tải dữ liệu chuyến tàu lên bảng (Sử dụng ChuyenTauDao).
     */
    private void loadDuLieuChuyenTauLenBang() {
        // Xóa tất cả các hàng hiện có trong bảng trước khi tải mới
        modelChuyenTau.setRowCount(0);

        // Chuỗi SQL truy vấn (Điều chỉnh tên cột/bảng nếu cần)
        String sql = "SELECT MaChuyenTau, MaTau, GaDi, GaDen, GioKhoiHanh, NgayKhoiHanh FROM ChuyenTau";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

            // Duyệt qua từng hàng kết quả trả về từ CSDL
            while (rs.next()) {
                String maChuyenTau = rs.getString("MaChuyenTau");
                String maTau = rs.getString("MaTau");
                String gaDi = rs.getString("GaDi");
                String gaDen = rs.getString("GaDen");

                // Giả sử GioDi là TIME/String và NgayDi là DATE/String trong CSDL
                Date gioDi = rs.getTime("GioKhoiHanh");
                Date ngayDi = rs.getDate("NgayKhoiHanh");

                // Định dạng lại giờ và ngày để hiển thị đẹp hơn
                String gioDiStr = (gioDi != null) ? sdfTime.format(gioDi) : "";
                String ngayDiStr = (ngayDi != null) ? sdfDate.format(ngayDi) : "";

                // Tạo một mảng đối tượng cho một hàng
                Object[] rowData = {
                        maChuyenTau,
                        maTau,
                        gaDi,
                        gaDen,
                        gioDiStr,
                        ngayDiStr
                };

                // Thêm hàng vào Model của bảng
                modelChuyenTau.addRow(rowData);
            }

        } catch (SQLException e) {
            hienThiThongBaoLoi("Lỗi tải dữ liệu chuyến tàu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =================================================================================
    // KHU VỰC XỬ LÝ NGHIỆP VỤ (MỚI)
    // =================================================================================

    /**
     * Xóa trắng các trường nhập liệu trên form.
     */
    private void xoaTrangForm() {
        txtMaChuyenTau.setText("");
        cbMaTau.setSelectedIndex(0);
        cbGaDi.setSelectedIndex(0);
        cbGaDen.setSelectedIndex(0);
        dateChooserNgayDi.setDate(new Date()); // Đặt về ngày hiện tại
        timeSpinnerGioDi.setValue(new Date()); // Đặt về giờ hiện tại
        txtMaChuyenTau.setEditable(true); // Cho phép sửa Mã
        tableChuyenTau.clearSelection(); // Bỏ chọn hàng trên bảng

        // Tải lại toàn bộ bảng (nếu đang ở chế độ tìm kiếm)
        if (btnTim.getText().equals("Hủy tìm")) {
            loadDuLieuChuyenTauLenBang();
            btnTim.setText("Tìm");
        }
    }

    /**
     * Lấy dữ liệu từ hàng được chọn trên bảng và điền vào form.
     */
    private void dienThongTinVaoForm() {
        int row = tableChuyenTau.getSelectedRow();
        if (row == -1) return; // Không có hàng nào được chọn

        // Lấy dữ liệu từ Model
        String maCT = modelChuyenTau.getValueAt(row, 0).toString();
        String maTau = modelChuyenTau.getValueAt(row, 1).toString();
        String tenGaDi = modelChuyenTau.getValueAt(row, 2).toString();
        String tenGaDen = modelChuyenTau.getValueAt(row, 3).toString();
        String gioDiStr = modelChuyenTau.getValueAt(row, 4).toString();
        String ngayDiStr = modelChuyenTau.getValueAt(row, 5).toString();

        // Điền vào Form
        txtMaChuyenTau.setText(maCT);
        txtMaChuyenTau.setEditable(false); // Không cho sửa Mã khi đang Sửa

        // Chọn đúng đối tượng trong ComboBox
        selectComboBoxItem(cbMaTau, maTau);
        selectComboBoxItem(cbGaDi, tenGaDi);
        selectComboBoxItem(cbGaDen, tenGaDen);

        // Chuyển đổi String sang Date cho JSpinner và JDateChooser
        try {
            dateChooserNgayDi.setDate(sdfDate.parse(ngayDiStr));
        } catch (ParseException e) {
            e.printStackTrace();
            dateChooserNgayDi.setDate(null);
        }
        try {
            timeSpinnerGioDi.setValue(sdfTime.parse(gioDiStr));
        } catch (ParseException e) {
            e.printStackTrace();
            timeSpinnerGioDi.setValue(new Date());
        }
    }

    /**
     * Xử lý nghiệp vụ Thêm Chuyến Tàu
     */
    private void themChuyenTau() {
        // 1. Lấy dữ liệu từ form
        ChuyenTau ct = layDuLieuTuForm();
        if (ct == null) return; // Dữ liệu không hợp lệ

        // 2. Gọi DAO
        try {
            // Kiểm tra trùng mã
            if (chuyenTauDao.layChuyenTauBangMa(ct.getMaChuyenTau()) != null) {
                hienThiThongBaoLoi("Mã chuyến tàu '" + ct.getMaChuyenTau() + "' đã tồn tại.");
                return;
            }

            boolean success = chuyenTauDao.addChuyenTau(ct);
            if (success) {
                hienThiThongBaoThanhCong("Thêm chuyến tàu thành công!");
                loadDuLieuChuyenTauLenBang(); // Tải lại bảng
                xoaTrangForm(); // Xóa trắng form
            } else {
                hienThiThongBaoLoi("Thêm chuyến tàu thất bại.");
            }
        } catch (SQLException e) {
            hienThiThongBaoLoi("Lỗi CSDL khi thêm chuyến tàu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xử lý nghiệp vụ Sửa Chuyến Tàu
     */
    private void suaChuyenTau() {
        int selectedRow = tableChuyenTau.getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBaoLoi("Vui lòng chọn một chuyến tàu trên bảng để sửa.");
            return;
        }

        // 1. Lấy dữ liệu từ form
        ChuyenTau ct = layDuLieuTuForm();
        if (ct == null) return;

        // 2. Gọi DAO
        try {
            boolean success = chuyenTauDao.updateChuyenTau(ct);
            if (success) {
                hienThiThongBaoThanhCong("Cập nhật chuyến tàu thành công!");
                loadDuLieuChuyenTauLenBang(); // Tải lại bảng
                xoaTrangForm(); // Xóa trắng form
            } else {
                hienThiThongBaoLoi("Cập nhật chuyến tàu thất bại.");
            }
        } catch (SQLException e) {
            hienThiThongBaoLoi("Lỗi CSDL khi cập nhật chuyến tàu: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void timChuyenTau() {
        // Xử lý logic cho nút "Hủy tìm"
        if (btnTim.getText().equals("Hủy tìm")) {
            xoaTrangForm(); // xoaTrangForm sẽ tải lại bảng và reset nút
            btnTim.setText("Tìm"); // Đặt lại tên nút
            return;
        }

        String maCT = txtMaChuyenTau.getText().trim();
        if (maCT.isEmpty()) {
            hienThiThongBaoLoi("Vui lòng nhập Mã chuyến tàu cần tìm.");
            return;
        }

        try {
            // [SỬA 1] Gọi đúng tên hàm (giả sử là findChuyenTauById)
            // Hàm này không ném SQLException ra ngoài
            ChuyenTau ct = chuyenTauDao.layChuyenTauBangMa(maCT);

            if (ct == null) {
                hienThiThongBaoLoi("Không tìm thấy chuyến tàu với mã: " + maCT);
            } else {
                // Hiển thị kết quả duy nhất lên bảng
                modelChuyenTau.setRowCount(0);

                // [SỬA 2] Lấy Mã Tàu (Số Hiệu) từ đối tượng Tau
                String maTau = (ct.getTau() != null) ? ct.getTau().getSoHieu() : "Lỗi";

                String gaDi = (ct.getGaDi() != null) ? ct.getGaDi().getTenGa() : "Lỗi";
                String gaDen = (ct.getGaDen() != null) ? ct.getGaDen().getTenGa() : "Lỗi";

                // Đảm bảo đã import java.time.format.DateTimeFormatter;
                String gioDiStr = (ct.getGioKhoiHanh() != null) ? ct.getGioKhoiHanh().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
                String ngayDiStr = (ct.getNgayKhoiHanh() != null) ? ct.getNgayKhoiHanh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";

                // [SỬA 3] Lấy Trạng thái từ phương thức getThct()
                String trangThai = (ct.getThct() != null) ? ct.getThct().toString() : "N/A";

                modelChuyenTau.addRow(new Object[]{ct.getMaChuyenTau(), maTau, gaDi, gaDen, gioDiStr, ngayDiStr, trangThai});

                // Cập nhật nút Tìm -> Hủy tìm
                btnTim.setText("Hủy tìm");
            }
        } catch (Exception e) { // <-- [SỬA LỖI Ở ĐÂY] Đổi thành Exception
            // Bắt các lỗi chung như NullPointerException (nếu ct.getTau() null, v.v.)
            hienThiThongBaoLoi("Lỗi khi tìm chuyến tàu: " + e.getMessage());
            e.printStackTrace();
        }
        // Không cần 'catch (NullPointerException npe)' riêng nữa vì 'Exception' đã bao gồm nó
    }

    /**
     * Xử lý nghiệp vụ Thêm bằng Excel (Placeholder)
     */
    private void themBangExcel() {
        JOptionPane.showMessageDialog(this,
                "Chức năng này yêu cầu thư viện Apache POI.\nChưa được triển khai trong bản demo này.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * [MỚI] Helper: Lấy dữ liệu từ form và tạo đối tượng ChuyenTau
     */
    /**
     * [ĐÃ SỬA] Helper: Lấy dữ liệu từ form và tạo đối tượng ChuyenTau
     */
    private ChuyenTau layDuLieuTuForm() {
        // 1. Lấy dữ liệu từ form
        String maCT = txtMaChuyenTau.getText().trim();
        if (maCT.isEmpty()) {
            hienThiThongBaoLoi("Mã chuyến tàu không được để trống.");
            return null;
        }

        Tau tau = (Tau) cbMaTau.getSelectedItem();
        Ga gaDi = (Ga) cbGaDi.getSelectedItem();
        Ga gaDen = (Ga) cbGaDen.getSelectedItem();

        if (tau == null || gaDi == null || gaDen == null) {
            hienThiThongBaoLoi("Vui lòng chọn Tàu, Ga đi và Ga đến.");
            return null;
        }

        // Giả định Ga entity có getTenGa(). So sánh Tên Ga.
        if (gaDi.getTenGa().equals(gaDen.getTenGa())) {
            hienThiThongBaoLoi("Ga đi và Ga đến không được trùng nhau.");
            return null;
        }

        // Lấy ngày
        Date ngay = dateChooserNgayDi.getDate();
        if (ngay == null) {
            hienThiThongBaoLoi("Vui lòng chọn Ngày đi.");
            return null;
        }
        LocalDate localDate = Instant.ofEpochMilli(ngay.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

        // Lấy giờ
        Date gio = (Date) timeSpinnerGioDi.getValue();
        LocalTime localTime = Instant.ofEpochMilli(gio.getTime()).atZone(ZoneId.systemDefault()).toLocalTime();

        // Giả sử NhanVien lấy từ context (Tạm thời là null)
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv == null) {
            hienThiThongBaoLoi("Lỗi phiên làm việc: Không tìm thấy thông tin Nhân viên đăng nhập.");
            return null;
        }

        // TODO: Cần logic để nhập/tính Ngày/Giờ đến. Tạm thời cộng 1 ngày.
        LocalDate ngayDenDuKien = localDate.plusDays(1);
        LocalTime gioDenDuKien = localTime; // Tạm thời

        // [SỬA LỖI 1] Lấy Trạng Thái Enum (Giả sử bạn có Enum này)
        // Bạn cần import entity.lopEnum.TrangThaiChuyenTau;
        entity.lopEnum.TrangThaiChuyenTau trangThai = TrangThaiChuyenTau.DANG_CHO;

        // [SỬA LỖI 2] Lấy maTau (String - là SoHieu) từ đối tượng Tau
        // Giả sử entity Tau của bạn có getSoHieu() khớp với CSDL
        String maTauString = tau.getSoHieu();

        // [SỬA LỖI 3] Gọi đúng constructor theo thứ tự của entity/ChuyenTau.java
        return new ChuyenTau(
                maCT,             // 1. String maChuyenTau
                maTauString,      // 2. String maTau (SoHieu)
                localDate,        // 3. LocalDate ngayKhoiHanh
                localTime,        // 4. LocalTime gioKhoiHanh
                gaDi,             // 5. Ga gaDi
                gaDen,            // 6. Ga gaDen
                tau,              // 7. Tau tau
                ngayDenDuKien,    // 8. LocalDate ngayDenDuKien
                gioDenDuKien,     // 9. LocalTime gioDenDuKien
                nv,               // 10. NhanVien nhanVien
                trangThai         // 11. TrangThaiChuyenTau thct
        );
    }

    // =================================================================================
    // KHU VỰC HELPERS (Renderers, Parsers, Selectors)
    // =================================================================================

    /**
     * Lớp Renderer để hiển thị Tên Ga trong JComboBox.
     */
    private class GaRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Ga) {
                setText(((Ga) value).getTenGa()); // Hiển thị Tên Ga
            }
            return this;
        }
    }

    /**
     * Lớp Renderer để hiển thị Mã Tàu (SoHieu) trong JComboBox.
     */
    /**
     * Lớp Renderer để hiển thị Mã Tàu (Số Hiệu) trong JComboBox.
     */
    private class TauRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            // Gọi phương thức gốc để xử lý màu nền, v.v.
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Tau) {
                // [SỬA LỖI] Gọi getSoHieu() thay vì getMaTau()
                Tau tau = (Tau) value;
                setText(tau.getSoHieu()); // Hiển thị Số Hiệu (ví dụ: "SE1")
            }
            return this;
        }
    }

    /**
     * [ĐÃ SỬA] Helper: Chọn item trong JComboBox<Object> dựa trên giá trị String.
     * Sửa lại để gọi getSoHieu() cho Tau.
     */
    private void selectComboBoxItem(JComboBox comboBox, String valueToSelect) {
        if (valueToSelect == null) { // Thêm kiểm tra null
            comboBox.setSelectedIndex(-1); // Không chọn gì cả
            return;
        }

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Object item = comboBox.getItemAt(i);
            if (item == null) continue; // Bỏ qua item null

            String itemString = "";

            // Lấy giá trị hiển thị của item (tùy thuộc vào renderer)
            if (item instanceof Ga) {
                itemString = ((Ga) item).getTenGa();
            } else if (item instanceof Tau) {
                // [SỬA LỖI] Gọi getSoHieu() thay vì getMaTau()
                itemString = ((Tau) item).getSoHieu();
            } else {
                itemString = item.toString();
            }

            // So sánh không phân biệt hoa thường
            if (itemString.equalsIgnoreCase(valueToSelect)) {
                comboBox.setSelectedIndex(i);
                return; // Đã tìm thấy, thoát
            }
        }

        // Nếu không tìm thấy, có thể đặt về mặc định
        // comboBox.setSelectedIndex(-1); // Hoặc 0, tùy logic
    }

    private void hienThiThongBaoLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void hienThiThongBaoThanhCong(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Hàm main để test giao diện Quản lý Chuyến Tàu.
     * Cần đảm bảo rằng các lớp DAO, Entity, ConnectDB đã được biên dịch.
     */
    public static void main(String[] args) {
        // 1. Thiết lập kết nối CSDL (Cần phải có)
        // Thay đổi thông tin kết nối nếu cần
        ConnectDB.getInstance().connect();
        System.out.println("Kết nối CSDL thành công.");

        // 2. Thiết lập phiên làm việc tạm thời cho Nhân viên (để layDuLieuTuForm() hoạt động)
        // Giả lập một nhân viên đã đăng nhập
        NhanVien nvTest = new NhanVien();
        nvTest.setMaNV("NVQL0001"); // Mã nhân viên giả định
        nvTest.setHoTen("Nguyễn Văn Test");
        // ... set các thuộc tính khác nếu cần
        CaLamViec.getInstance().batDauCa(nvTest);
        System.out.println("Thiết lập phiên Nhân viên: " + nvTest.getMaNV());


        // 3. Khởi tạo và hiển thị Panel trong JFrame
        SwingUtilities.invokeLater(() -> {
            // Thiết lập Look and Feel cho đẹp
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Test Giao Diện Quản Lý Chuyến Tàu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700); // Kích thước phù hợp
            frame.setLocationRelativeTo(null); // Đặt ra giữa màn hình

            ManhinhQuanLyChuyenTau panel = new ManhinhQuanLyChuyenTau();
            frame.add(panel);
            frame.setVisible(true);
        });
    }
}