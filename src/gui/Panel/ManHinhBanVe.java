package gui.Panel;

import dao.ChoDatDAO;
import dao.ChuyenTauDao;
import dao.GaDao;
import dao.ToaDAO;
import dao.GiaVeCoBanTheoGaDAO;
import dao.LoaiChoDatDAO;
import dao.LoaiVeDAO;
import entity.*;
import gui.MainFrame.BanVeDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import com.toedter.calendar.JDateChooser;



public class ManHinhBanVe extends JPanel implements MouseListener, ActionListener {
    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);
    // ====================
    // MODULE: Trạng thái & dữ liệu (State)
    // ====================
    private JPanel pnlToa;
    private JPanel pnlSoDoGhe;
    private JComboBox<Ga> cbGaDi;
    private JComboBox<Ga> cbGaDen;
    private JTextField dateField;
    private JDateChooser dateChooserNgayDi;

    private JLabel lblTongSoKhach;


    private JTable tableChuyenTau;
    private DefaultTableModel tableModel;

    private JPanel pnlDanhSachGheDaCho;

    private Date date;
    private List<ChuyenTau> ketQua = new ArrayList<>();
    private String maChuyenTauHienTai = null;

    private JButton lastSelectedToaButton = null;
    private String maToaHienTai = null;

    // Map tạm thời: MaChoDat -> TempKhachHang
    private Map<String, ChiTietKhach> danhSachKhachHang = new LinkedHashMap<>();
    // Map số lượng yêu cầu theo loại
    private Map<String, Integer> soLuongYeuCau = new HashMap<>();

    // Map toàn bộ ChoDat của toa hiện tại để tra cứu nhanh
    private Map<String, ChoDat> tatCaChoDatToaHienTai = new HashMap<>();

    // Danh sách ghế đang chọn: MaCho -> ChoDat
    private Map<String, ChoDat> danhSachGheDaChon = new LinkedHashMap<>();

    // Map nút ghế: MaCho -> JButton (để cập nhật trạng thái nút khi hủy chọn)
    private Map<String, JButton> seatButtonsMap = new HashMap<>();

    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private JScrollPane thongTinKhachScrollPane;


    // Record tạm thời lưu thông tin khách hàng cho mỗi ghế đã chọn


    private static record ChiTietKhach(
            ChoDat choDat,
            String maLoaiVe,
            String hoTen,
            String cccd,
            String sdt,
            String ngaySinh
    ) {
        // PHƯƠNG THỨC HỖ TRỢ BẤT BIẾN (WITH)
        // Dùng khi người dùng thay đổi loại vé
        public ChiTietKhach withMaLoaiVe(String newMaLoaiVe) {
            return new ChiTietKhach(this.choDat, newMaLoaiVe, this.hoTen, this.cccd, this.sdt, this.ngaySinh);
        }

        // Dùng khi người dùng thay đổi Họ tên
        public ChiTietKhach withHoTen(String newHoTen) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, newHoTen, this.cccd, this.sdt, this.ngaySinh);
        }

        // Dùng khi người dùng thay đổi CCCD
        public ChiTietKhach withCccd(String newCccd) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, this.hoTen, newCccd, this.sdt, this.ngaySinh);
        }

        // Dùng khi người dùng thay đổi SDT
        public ChiTietKhach withSdt(String newSdt) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, this.hoTen, this.cccd, newSdt, this.ngaySinh);
        }

        // Dùng khi người dùng thay đổi ngay sinh
        public ChiTietKhach withNgaySinh(String newNgaySinh) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, this.hoTen, this.cccd, this.sdt, newNgaySinh);
        }

        // Helper để lấy Tuổi từ ngày sinh (cần thêm phương thức tính tuổi)
        public int getTuoi() {
            return calculateAge(this.ngaySinh); // Phương thức này cần được thêm
        }
    }

    // DAO
    private ChoDatDAO choDatDao = new ChoDatDAO();
    private GiaVeCoBanTheoGaDAO giaVeCoBanDAO = new GiaVeCoBanTheoGaDAO();
    private LoaiChoDatDAO loaiChoDatDAO = new LoaiChoDatDAO();
    private LoaiVeDAO loaiVeDAO = new LoaiVeDAO();

    // Mã loại vé (hằng)
    private static final String MA_VE_NL = "VT01";
    private static final String MA_VE_TE = "VT02";
    private static final String MA_VE_NCT = "VT03";
    private static final String MA_VE_SV = "VT04";
    private JButton btbHuy, btnTiepTheo;
    private JButton btnTimChuyen;

    // Map lưu giá mỗi ghế (maCho -> giaVe đã làm tròn VNĐ)
    private Map<String, Long> danhSachGiaVe = new HashMap<>();

    // Label tổng tiền (phải là trường lớp để cập nhật)
    private JLabel lblTotalPrice;
    private JPanel loaiKhachSpinBoxPanel;
    private JTextField txtTongSoKhach;
    private JPanel pnlTongSoKhachControl;

    // 1. Helper để cập nhật maLoaiVe (Dùng khi người dùng chọn ComboBox)

    // 2. Helper chung để cập nhật bất kỳ trường nào (Dùng trong FocusListener)
    private void updateKhachRecord(String maCho, String newValue, String fieldName) {
        ChiTietKhach original = danhSachKhachHang.get(maCho);
        if (original == null) return;

        ChiTietKhach updated = switch (fieldName) {
            case "hoTen" -> new ChiTietKhach(original.choDat(), original.maLoaiVe(), newValue, original.cccd(), original.sdt(), original.ngaySinh());
            case "cccd" -> new ChiTietKhach(original.choDat(), original.maLoaiVe(), original.hoTen(), newValue, original.sdt(), original.ngaySinh());
            case "sdt" -> new ChiTietKhach(original.choDat(), original.maLoaiVe(), original.hoTen(), original.cccd(), newValue, original.ngaySinh());
            case "ngaySinh" -> new ChiTietKhach(original.choDat(), original.maLoaiVe(), original.hoTen(), original.cccd(), original.sdt(), newValue);
            default -> original;
        };
        danhSachKhachHang.put(maCho, updated);
    }

    // ====================
    // MODULE: Constructor + Layout chính
    // ====================
    public ManHinhBanVe() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245));

        add(taoPanelTieuDe(), BorderLayout.NORTH);
        add(taoNoiDungChinh(), BorderLayout.CENTER);
    }

    // ====================
    // MODULE: UI BUILDERS (các phương thức tạo vùng giao diện)
    // ====================
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 50));
        panel.setBorder(new EmptyBorder(0, 10, 0, 10));

        JLabel titleLabel = new JLabel("Bán vé");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel taoNoiDungChinh() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 0));
        mainPanel.setBackground(Color.WHITE);

        JPanel contentLeftPanel = new JPanel();
        contentLeftPanel.setLayout(new BoxLayout(contentLeftPanel, BoxLayout.Y_AXIS));
        contentLeftPanel.setOpaque(false);
        contentLeftPanel.setBorder(new EmptyBorder(0, 0, 0, 5));

        // Các khu vực con (được tách modular)
        contentLeftPanel.add(createKhuVucTimKiem());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucDanhSachChuyenTau());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucChonLoaiKhach());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucChonViTriGhe());
        contentLeftPanel.add(Box.createVerticalStrut(10));

        contentLeftPanel.add(Box.createVerticalGlue());

        JScrollPane leftScrollPane = new JScrollPane(contentLeftPanel);
        leftScrollPane.setBorder(null);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(true);
        leftContainer.add(leftScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = createKhuVucThongTinKhach();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftContainer, rightPanel);
        split.setOneTouchExpandable(true);
        split.setDividerSize(6);

        split.setResizeWeight(0.70); // Đặt 0.70 để phù hợp với tỷ lệ 7/3

        SwingUtilities.invokeLater(() -> {
            final int PIXEL_LOCATION = 840;

            if (split.getWidth() > 0) {
                split.setDividerLocation(PIXEL_LOCATION);
            } else {
                split.setDividerLocation(PIXEL_LOCATION);
            }
        });

        mainPanel.add(split, BorderLayout.CENTER);
        return mainPanel;
    }


    private JPanel createKhuVucTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Tìm kiếm chuyến tàu");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        Vector<Ga> danhSachGa = new GaDao().layDanhSachGa();

        panel.add(new JLabel("Ga đi"));
        cbGaDi = new JComboBox<>(danhSachGa);
        panel.add(cbGaDi);

        panel.add(new JLabel("Ga đến"));
        cbGaDen = new JComboBox<>(danhSachGa);
        panel.add(cbGaDen);

        if (danhSachGa.size() > 1) {
            cbGaDen.setSelectedIndex(3);
        }

        panel.add(new JLabel("Ngày đi:"));
        dateChooserNgayDi = new JDateChooser();
        dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayDi.setDate(new Date());
        dateChooserNgayDi.setPreferredSize(new Dimension(120, 25));
        panel.add(dateChooserNgayDi);

        btnTimChuyen = new JButton("Tìm chuyến");
        styleNutChinh(btnTimChuyen);
        btnTimChuyen.addActionListener(this);
        panel.add(btnTimChuyen);

        datCanhKhuVuc(panel);
        return panel;
    }

    private JScrollPane createKhuVucDanhSachChuyenTau() {
        String[] columnNames = {"Tên Chuyến", "Ngày đi", "Giờ đi"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tableChuyenTau = new JTable(tableModel);
        tableChuyenTau.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(tableChuyenTau);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        scrollPane.setMaximumSize(new Dimension(1200, 100));
        return scrollPane;
    }

    private JPanel createKhuVucChonLoaiKhach() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Chọn toa và số lượng khách");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- KHU VỰC NHẬP TỔNG SỐ KHÁCH (DÙNG ToaPanelTangGiam) ---

        // 1. Khai báo và khởi tạo JTextField cho Tổng số khách
        //columns = 2 để giới hạn độ rộng hiển thị
        txtTongSoKhach = new JTextField(2);

        txtTongSoKhach.setPreferredSize(new Dimension(60, 30));
        txtTongSoKhach.setMaximumSize(new Dimension(60, 30));
        // 2. Tạo control tăng/giảm chỉ cho TỔNG SỐ KHÁCH
        pnlTongSoKhachControl = ToaPanelTangGiam("Tổng số khách", "1", txtTongSoKhach);

        // Giảm giới hạn chiều rộng để panel không chiếm hết không gian (tùy chọn)
        pnlTongSoKhachControl.setMaximumSize(new Dimension(300, 40));

        // Thêm control nhập số lượng vào topRow
        topRow.add(pnlTongSoKhachControl);

        // Thêm topRow (chứa control nhập tổng khách) vào panel chính
        panel.add(topRow);

        // --- KHU VỰC CHỌN TOA (Giữ nguyên) ---
        pnlToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        pnlToa.setOpaque(false);
        pnlToa.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlToa.add(new JLabel("Chọn toa:"));
        panel.add(pnlToa);

        // Điều chỉnh giới hạn chiều cao phù hợp
        panel.setMaximumSize(new Dimension(1200, 150));

        // Gọi hàm cập nhật trạng thái
        capNhatSoLuongYeuCau();
        datCanhKhuVuc(panel);
        return panel;
    }
    // ====================
    // MODULE: SpinBox helpers
    // ====================
    private JPanel ToaPanelTangGiam(String labelText, String initialValue, JTextField targetField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel labelDiscountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelDiscountPanel.setOpaque(false);

        JLabel mainLabel = new JLabel(labelText);
        mainLabel.setFont(mainLabel.getFont().deriveFont(Font.BOLD, 14f));
        labelDiscountPanel.add(mainLabel);

        panel.add(labelDiscountPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        controlPanel.setOpaque(false);

        JButton btnMinus = new JButton("−");
        btnMinus.setPreferredSize(new Dimension(30, 30));
        btnMinus.setMargin(new Insets(0, 0, 0, 0));
        styleSpinButton(btnMinus);

        targetField.setText(initialValue);
        targetField.setHorizontalAlignment(JTextField.CENTER);

        targetField.setPreferredSize(new Dimension(60, 30));
        targetField.setMaximumSize(new Dimension(60, 30));
        targetField.setEditable(false);

        JButton btnPlus = new JButton("+");
        btnPlus.setPreferredSize(new Dimension(30, 30));
        btnPlus.setMargin(new Insets(0, 0, 0, 0));
        styleSpinButton(btnPlus);

        btnPlus.addActionListener(e -> changeQuantity(targetField, 1));
        btnMinus.addActionListener(e -> changeQuantity(targetField, -1));

        controlPanel.add(btnMinus);
        controlPanel.add(targetField);
        controlPanel.add(btnPlus);

        panel.add(controlPanel, BorderLayout.EAST);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return panel;
    }

    private void styleSpinButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }
    private int parseTextFieldToInt(JTextField field) {
        try {
            if (field.getText().trim().isEmpty()) return 0;
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void changeQuantity(JTextField field, int delta) {
        int currentValue = parseTextFieldToInt(field);
        int newValue = currentValue + delta;
        if (newValue < 0) newValue = 0;
        field.setText(String.valueOf(newValue));
        capNhatSoLuongYeuCau();
    }


    // MODULE: Logic tính toán & cập nhật trạng thái (ĐÃ SỬA)
// ====================
    private void capNhatSoLuongYeuCau() {
        // Chỉ lấy giá trị từ trường TỔNG SỐ KHÁCH
        int tongSoKhachMoi = parseTextFieldToInt(txtTongSoKhach);

        // Cập nhật label (Nếu lblTongSoKhach không được sử dụng, có thể bỏ qua)

        // Cập nhật Map yêu cầu chỉ với TỔNG SỐ KHÁCH
        soLuongYeuCau.clear();

        soLuongYeuCau.put("TongSoKhach", tongSoKhachMoi);

        // Xóa các dòng cập nhật cho từng loại khách đã bị loại bỏ

        // Cảnh báo nếu số ghế đã chọn vượt quá tổng số khách mới
        if (danhSachGheDaChon != null && danhSachGheDaChon.size() > tongSoKhachMoi) {
            JOptionPane.showMessageDialog(null, // Thay thế this bằng null nếu đây không phải là class JDialog/JFrame
                    "Số lượng ghế đã chọn (" + danhSachGheDaChon.size() + ") vượt quá Tổng số khách mới (" + tongSoKhachMoi + "). Vui lòng hủy chọn bớt.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel createKhuVucChonViTriGhe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Chọn vị trí của ghế");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        pnlSoDoGhe = new JPanel();
        pnlSoDoGhe.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane soDoScrollPane = new JScrollPane(pnlSoDoGhe);
        soDoScrollPane.setBorder(BorderFactory.createEmptyBorder());
        soDoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        soDoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        soDoScrollPane.setPreferredSize(new Dimension(100, 150));
        soDoScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        panel.add(soDoScrollPane);
        panel.add(Box.createVerticalStrut(10));

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setOpaque(false);
        legendPanel.add(taoMucChuGiai(Color.LIGHT_GRAY, "Chỗ trống"));
        legendPanel.add(taoMucChuGiai(Color.BLACK, "Đã đặt"));
        legendPanel.add(taoMucChuGiai(new Color(0, 123, 255), "Đang chọn"));
        legendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(legendPanel);
        panel.add(Box.createVerticalStrut(5));

        pnlDanhSachGheDaCho = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlDanhSachGheDaCho.setOpaque(false);
        pnlDanhSachGheDaCho.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlDanhSachGheDaCho.add(new JLabel("Ghế đã chọn:"));
        capNhatDanhSachGheDaChonUI();
        panel.add(pnlDanhSachGheDaCho);

        datCanhKhuVuc(panel);
        return panel;
    }

    private JPanel createKhuVucTongTien() {
        JPanel fullSummary = new JPanel(new BorderLayout());
        fullSummary.setBackground(Color.white);
        fullSummary.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(new JLabel("Đã chọn: X/Y"));

        lblTotalPrice = new JLabel("Tổng tiền vé: 0 VNĐ");
        lblTotalPrice.setFont(lblTotalPrice.getFont().deriveFont(Font.BOLD, 14f));
        lblTotalPrice.setForeground(new Color(255, 165, 0));
        summaryPanel.add(lblTotalPrice);

        fullSummary.add(summaryPanel, BorderLayout.EAST);
        datCanhKhuVuc(fullSummary);
        return fullSummary;
    }

    private JPanel createKhuVucThongTinKhach() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Thông tin khách hàng"));

        JPanel infoScrollPanel = new JPanel();
        infoScrollPanel.setLayout(new BoxLayout(infoScrollPanel, BoxLayout.Y_AXIS));
        infoScrollPanel.setOpaque(false);
        infoScrollPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        infoScrollPanel.add(new JLabel("Chọn ghế để thêm thông tin."));
        infoScrollPanel.add(Box.createVerticalGlue());
//        infoScrollPanel.setPreferredSize(new Dimension(800, 300));

        thongTinKhachScrollPane = new JScrollPane(infoScrollPanel);
        thongTinKhachScrollPane.setBorder(null);
        thongTinKhachScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        thongTinKhachScrollPane.setPreferredSize(new Dimension(700, 300));
        panel.add(thongTinKhachScrollPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::capNhatThongTinKhachUI);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        btbHuy = new JButton("< Hủy");
        btbHuy.setPreferredSize(new Dimension(80, 40));
        btbHuy.setFont(btbHuy.getFont().deriveFont(Font.BOLD, 14f));
        btbHuy.setBackground(new Color(220, 53, 69));
        btbHuy.setForeground(Color.WHITE);

        btnTiepTheo = new JButton("Tiếp theo >");
        btnTiepTheo.setPreferredSize(new Dimension(120, 40));
        btnTiepTheo.setFont(btnTiepTheo.getFont().deriveFont(Font.BOLD, 14f));
        btnTiepTheo.setBackground(new Color(0, 123, 255));
        btnTiepTheo.setForeground(Color.WHITE);

        buttonPanel.add(btbHuy);
        buttonPanel.add(btnTiepTheo);
        //Đăng ký sự kiện
        btbHuy.addActionListener(this);
        btnTiepTheo.addActionListener(this);

        JPanel fullSummary = new JPanel(new BorderLayout());
        fullSummary.setBackground(Color.white);
        fullSummary.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(new JLabel("Đã chọn: X/Y"));

        lblTotalPrice = new JLabel("Tổng tiền vé: 0 VNĐ");
        lblTotalPrice.setFont(lblTotalPrice.getFont().deriveFont(Font.BOLD, 14f));
        lblTotalPrice.setForeground(new Color(255, 165, 0));
        summaryPanel.add(lblTotalPrice);

        fullSummary.add(summaryPanel, BorderLayout.EAST);
        datCanhKhuVuc(fullSummary);

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);

        footerPanel.add(fullSummary);
        footerPanel.add(buttonPanel);

        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }
    // ====================
    // MODULE: Validation Helpers (ĐÃ SỬA)
    // ====================


    /**
     * [ĐÃ SỬA] Hiển thị lỗi: Đổi border VÀ đặt text cho label lỗi.
     * @param input Component nhập liệu (JTextField).
     * @param errorLabel JLabel để hiển thị lỗi bên dưới.
     * @param message Thông báo lỗi.
     */
    private void showValidationError(JComponent input, JLabel errorLabel, String message) {
        input.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        errorLabel.setText(message); // Hiển thị lỗi trên label
        input.setToolTipText(message); // Vẫn giữ tooltip nếu muốn
    }

    /**
     * [ĐÃ SỬA] Xóa hiển thị lỗi: Reset border VÀ xóa text khỏi label lỗi.
     * @param input Component nhập liệu (JTextField).
     * @param errorLabel JLabel hiển thị lỗi.
     */
    private void clearValidationError(JComponent input, JLabel errorLabel) {
        input.setBorder(UIManager.getBorder("TextField.border")); // Trả về border mặc định
        errorLabel.setText(" "); // Đặt là khoảng trắng để giữ layout ổn định
        input.setToolTipText(null);
    }

    // --- Cần sửa lại các lớp InputVerifier để chúng nhận thêm JLabel lỗi ---

    /**
     * [ĐÃ SỬA] InputVerifier để kiểm tra trường không được rỗng.
     */
    private class NotEmptyVerifier extends InputVerifier {
        private String fieldName;
        private JLabel errorLabel; // Thêm label lỗi

        public NotEmptyVerifier(String fieldName, JLabel errorLabel) {
            this.fieldName = fieldName;
            this.errorLabel = errorLabel;
        }

        @Override
        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String text = textField.getText().trim();
            if (text.isEmpty()) {
                showValidationError(input, errorLabel, fieldName + " không được để trống.");
                return false;
            }
            clearValidationError(input, errorLabel);
            return true;
        }
    }

    /**
     * [ĐÃ SỬA] InputVerifier để kiểm tra Tuổi (số nguyên dương).
     */
    /**
     * [ĐÃ SỬA] InputVerifier để kiểm tra Tuổi (số nguyên dương, bắt buộc).
     */
    /**
     * Phương thức trợ giúp để tính tuổi từ ngày sinh (định dạng dd/MM/yyyy).
     * @param dobString Ngày sinh dưới dạng chuỗi.
     * @return Tuổi (số nguyên) hoặc -1 nếu có lỗi parse.
     */
    private static int calculateAge(String dobString) {
        if (dobString == null || dobString.trim().isEmpty()) return 0;
        try {
            Date birthDate = INPUT_DATE_FORMAT.parse(dobString); // Dùng INPUT_DATE_FORMAT
            Calendar dob = Calendar.getInstance();
            dob.setTime(birthDate);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            return -1; // Lỗi parse
        }
    }

    /**
     * [MỚI] InputVerifier để kiểm tra Ngày sinh (DOB) theo định dạng dd/MM/yyyy và tính tuổi.
     */
    private class DobVerifier extends InputVerifier {
        private JLabel errorLabel;

        public DobVerifier(JLabel errorLabel) { this.errorLabel = errorLabel; }

        @Override
        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String text = textField.getText().trim();

            // Kiểm tra trống
            if (text.isEmpty()) {
                showValidationError(input, errorLabel, "Ngày sinh không được để trống.");
                return false;
            }

            try {
                Date dob = INPUT_DATE_FORMAT.parse(text);

                // [Kiểm tra ngày sinh không được là ngày trong tương lai]
                if (dob.after(new Date())) {
                    showValidationError(input, errorLabel, "Ngày sinh không được ở tương lai.");
                    return false;
                }

                // Kiểm tra định dạng có đúng dd/MM/yyyy không (bằng cách parse lại với định dạng nghiêm ngặt)
                // (SimpleDateFormat đã làm việc này, nhưng thêm kiểm tra tuổi để đảm bảo)
                int age = calculateAge(text);
                if (age < 0) { // Lỗi nếu tuổi không tính được (lỗi logic/parse)
                    showValidationError(input, errorLabel, "Ngày sinh không hợp lệ.");
                    return false;
                }

                clearValidationError(input, errorLabel);
                return true;
            } catch (ParseException e) {
                showValidationError(input, errorLabel, "Ngày sinh phải theo định dạng dd/MM/yyyy.");
                return false;
            }
        }
    }

    /**
     * [ĐÃ SỬA] InputVerifier để kiểm tra Số điện thoại (10 số, bắt đầu bằng 0).
     */
    /**
     * [ĐÃ SỬA] InputVerifier để kiểm tra Số điện thoại (10 số, bắt đầu bằng 0, bắt buộc).
     */
    private class PhoneVerifier extends InputVerifier {
        private JLabel errorLabel;
        public PhoneVerifier(JLabel errorLabel) { this.errorLabel = errorLabel; }

        @Override
        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String text = textField.getText().trim();

            // [THAY ĐỔI] Kiểm tra trống trước tiên
            if (text.isEmpty()) {
                showValidationError(input, errorLabel, "Số điện thoại không được để trống.");
                return false; // Không hợp lệ nếu trống
            }

            if (!text.matches("^0\\d{9}$")) {
                showValidationError(input, errorLabel, "SĐT gồm 10 số, bắt đầu 0.");
                return false;
            }
            clearValidationError(input, errorLabel);
            return true;
        }
    }

    /**
     * [ĐÃ SỬA] InputVerifier để kiểm tra CCCD (12 số).
     */
    private class CccdVerifier extends InputVerifier {
        private JLabel errorLabel; // Thêm label lỗi
        public CccdVerifier(JLabel errorLabel) { this.errorLabel = errorLabel; }

        @Override
        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String text = textField.getText().trim();
            if (text.isEmpty()) { // Bắt buộc
                showValidationError(input, errorLabel, "CCCD không được để trống.");
                return false;
            }
            if (!text.matches("^\\d{12}$")) {
                showValidationError(input, errorLabel, "CCCD phải có đúng 12 chữ số.");
                return false;
            }
            clearValidationError(input, errorLabel);
            return true;
        }
    }
    /**
     * [ĐÃ SỬA] Tạo panel thông tin cho một khách hàng, bao gồm label lỗi.
     */
    /**
     * [ĐÃ SỬA LAYOUT] Tạo panel thông tin cho một khách hàng, bao gồm label lỗi.
     * Sử dụng GridBagLayout hiệu quả hơn.
     */
    /**
     * [ĐÃ SỬA LAYOUT] Tạo panel thông tin cho một khách hàng, cải thiện căn lề.
     */
    private JPanel createKhachPanel(ChiTietKhach khach) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Lấy thông tin ---
        String maCho = khach.choDat().getMaCho();
        String soCho = khach.choDat().getSoCho();
        String soThuTuToa = laySoThuTuToa(khach.choDat().getMaToa());
        String loaiKhachHienThi = getTenLoaiVeHienThi(khach.maLoaiVe());

        // --- Hằng số định dạng ---
        final int FIELD_HEIGHT = 28;
        final int W_COMBO = 150;
        final Dimension FIXED_COMBO_SIZE = new Dimension(W_COMBO, FIELD_HEIGHT);
        final Font ERROR_FONT = new Font("Segoe UI", Font.ITALIC, 11);
        final Color ERROR_COLOR = Color.RED;
        final Insets LABEL_INSETS = new Insets(2, 0, 2, 5); // Padding phải cho Label
        final Insets FIELD_INSETS = new Insets(2, 0, 1, 15); // Padding phải Field
        final Insets ERROR_INSETS = new Insets(0, 0, 5, 15); // Padding dưới Error

        // --- 1. Header Row (Giữ nguyên) ---
        JPanel headerRow = new JPanel(new BorderLayout(10, 0));
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(5, 5, 5, 5));
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT + 15));
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); leftHeader.setOpaque(false);
        JLabel maGheLabel = new JLabel("Ghế: " + soCho + " / Toa: " + soThuTuToa); maGheLabel.setFont(maGheLabel.getFont().deriveFont(Font.BOLD)); leftHeader.add(maGheLabel);
        JComboBox<String> cbLoaiKhach = new JComboBox<>(getLoaiVeOptions()); cbLoaiKhach.setSelectedItem(loaiKhachHienThi); cbLoaiKhach.setPreferredSize(FIXED_COMBO_SIZE); cbLoaiKhach.setMaximumSize(FIXED_COMBO_SIZE);
        cbLoaiKhach.addActionListener(e -> { /* ... Logic xử lý loại vé giữ nguyên ... */ String maMoi = getMaLoaiVeFromHienThi((String) cbLoaiKhach.getSelectedItem()); ChiTietKhach updatedKhach = khach.withMaLoaiVe(maMoi); danhSachKhachHang.put(maCho, updatedKhach); try { long gia = tinhGiaVeTau(updatedKhach.choDat(), updatedKhach.maLoaiVe()); danhSachGiaVe.put(maCho, gia); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Không thể tính lại giá: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); danhSachGiaVe.remove(maCho); } capNhatThongTinKhachUI(); capNhatTongTienUI(); });
        leftHeader.add(cbLoaiKhach); headerRow.add(leftHeader, BorderLayout.CENTER);
        JLabel giaLabel = new JLabel("..."); giaLabel.setFont(giaLabel.getFont().deriveFont(Font.BOLD, 14f)); giaLabel.setForeground(COLOR_BLUE_LIGHT); giaLabel.setHorizontalAlignment(SwingConstants.RIGHT); headerRow.add(giaLabel, BorderLayout.EAST);
        panel.add(headerRow);

        // --- 2. Detail Grid (Thông tin chi tiết - Sửa GridBagConstraints) ---
        JPanel detailGrid = new JPanel(new GridBagLayout());
        detailGrid.setOpaque(false);
        detailGrid.setBorder(new EmptyBorder(0, 5, 0, 5));
        detailGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Hàng 0 + 1 + 2: Họ tên* ---
        gbc.gridy = 0; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.insets = LABEL_INSETS; // Căn phải label
        detailGrid.add(new JLabel("Họ và tên*:"), gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.6; gbc.insets = FIELD_INSETS; gbc.gridwidth = 2; // Chiếm 2 cột field + label của cột kế
        JTextField hoTenField = new JTextField(khach.hoTen());
        hoTenField.setPreferredSize(new Dimension(100, FIELD_HEIGHT)); // Chiều rộng sẽ tự co giãn
        detailGrid.add(hoTenField, gbc);

        gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = ERROR_INSETS; gbc.gridwidth = 2;
        gbc.gridx = 0;
        JLabel hoTenErrorLabel = new JLabel(" "); hoTenErrorLabel.setFont(ERROR_FONT); hoTenErrorLabel.setForeground(ERROR_COLOR);
        detailGrid.add(hoTenErrorLabel, gbc);


        // --- Hàng 0 + 1 + 2: Ngày sinh ---
        gbc.gridy = 0; gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.insets = new Insets(2, 10, 2, 5); gbc.gridwidth = 1; // Reset gridwidth và thêm padding trái
        detailGrid.add(new JLabel("Ngày sinh*:"), gbc);

        gbc.gridy = 1; gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.1; gbc.insets = new Insets(2, 0, 1, 5); // Giảm padding phải

        JTextField ngaySinhField = new JTextField(khach.ngaySinh() != null ? khach.ngaySinh() : "", 8);
        ngaySinhField.setPreferredSize(new Dimension(80, FIELD_HEIGHT));
        detailGrid.add(ngaySinhField, gbc);

        gbc.gridy = 2; gbc.gridx = 3; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(0, 0, 5, 5);
        JLabel ngaySinhErrorLabel = new JLabel(" "); ngaySinhErrorLabel.setFont(ERROR_FONT); ngaySinhErrorLabel.setForeground(ERROR_COLOR);
        detailGrid.add(ngaySinhErrorLabel, gbc);


        // --- Hàng 3 + 4 + 5: Số điện thoại ---
        gbc.gridy = 3; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.insets = LABEL_INSETS;
        detailGrid.add(new JLabel("Số điện thoại:"), gbc);

        gbc.gridy = 4; gbc.gridx = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5; gbc.insets = FIELD_INSETS; gbc.gridwidth = 2; // Chiếm 2 cột
        JTextField sdtField = new JTextField(khach.sdt());
        sdtField.setPreferredSize(new Dimension(100, FIELD_HEIGHT));
        detailGrid.add(sdtField, gbc);

        gbc.gridy = 5; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = ERROR_INSETS; gbc.gridwidth = 2;
        gbc.gridx = 0;
        JLabel sdtErrorLabel = new JLabel(" "); sdtErrorLabel.setFont(ERROR_FONT); sdtErrorLabel.setForeground(ERROR_COLOR);
        detailGrid.add(sdtErrorLabel, gbc);


        // --- Hàng 3 + 4 + 5: CCCD* ---
        gbc.gridy = 3; gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.insets = new Insets(2, 10, 2, 5); gbc.gridwidth = 1; // Reset gridwidth
        detailGrid.add(new JLabel("CCCD*:"), gbc);

        gbc.gridy = 4; gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5; gbc.insets = new Insets(2, 0, 1, 5);
        JTextField cccdField = new JTextField(khach.cccd());
        cccdField.setPreferredSize(new Dimension(100, FIELD_HEIGHT));
        detailGrid.add(cccdField, gbc);

        gbc.gridy = 5; gbc.gridx = 3; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(0, 0, 5, 5);
        JLabel cccdErrorLabel = new JLabel(" "); cccdErrorLabel.setFont(ERROR_FONT); cccdErrorLabel.setForeground(ERROR_COLOR);
        detailGrid.add(cccdErrorLabel, gbc);


        panel.add(detailGrid);

        // --- Gắn Verifiers (Giữ nguyên) ---
        hoTenField.setInputVerifier(new NotEmptyVerifier("Họ và tên", hoTenErrorLabel));
        ngaySinhField.setInputVerifier(new DobVerifier(ngaySinhErrorLabel));
        sdtField.setInputVerifier(new PhoneVerifier(sdtErrorLabel));
        cccdField.setInputVerifier(new CccdVerifier(cccdErrorLabel));

        // --- Focus Listeners (Giữ nguyên) ---
        // --- Focus Listeners (gọi verify trước khi cập nhật Record) ---
        hoTenField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                // Chỉ cập nhật Record nếu dữ liệu hợp lệ (verify() trả về true)
                if (hoTenField.getInputVerifier().verify(hoTenField)) {
                    updateKhachRecord(maCho, hoTenField.getText(), "hoTen");
                }
            }
        });

        cccdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                if (cccdField.getInputVerifier().verify(cccdField)) {
                    updateKhachRecord(maCho, cccdField.getText(), "cccd");
                }
            }
        });

        sdtField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                if (sdtField.getInputVerifier().verify(sdtField)) {
                    updateKhachRecord(maCho, sdtField.getText(), "sdt");
                }
            }
        });

        ngaySinhField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                // Chỉ cập nhật Record nếu dữ liệu hợp lệ (verify() trả về true)
                if (ngaySinhField.getInputVerifier().verify(ngaySinhField)) {
                    // Cập nhật trường ngaySinh
                    updateKhachRecord(maCho, ngaySinhField.getText(), "ngaySinh");
                    // Sau khi cập nhật ngày sinh, cần tính lại giá vé vì tuổi có thể thay đổi
                    try {
                        // Tính tuổi mới dựa trên DOB mới nhập
                        int age = calculateAge(ngaySinhField.getText());
                        // Kiểm tra lại loại vé dựa trên tuổi mới
                        String suggestedMaLoaiVe = suggestLoaiVeByAge(age); // Cần thêm phương thức này
                        if (!khach.maLoaiVe().equals(suggestedMaLoaiVe)) {
                            // Cập nhật loại vé nếu tuổi mới gợi ý loại khác
                            ChiTietKhach updatedKhach = danhSachKhachHang.get(maCho).withMaLoaiVe(suggestedMaLoaiVe);
                            danhSachKhachHang.put(maCho, updatedKhach);
                            // Cập nhật ComboBox và giá
                            capNhatThongTinKhachUI(); // Tải lại UI để cập nhật ComboBox và giá
                        } else {
                            // Chỉ tính lại giá nếu loại vé không đổi
                            long gia = tinhGiaVeTau(danhSachKhachHang.get(maCho).choDat(), danhSachKhachHang.get(maCho).maLoaiVe());
                            danhSachGiaVe.put(maCho, gia);
                            giaLabel.setText(formatVnd(gia));
                        }
                        capNhatTongTienUI();
                    } catch (Exception ex) {
                        System.err.println("Lỗi tính lại giá vé sau khi nhập ngày sinh: " + ex.getMessage());
                    }
                }
            }
        });

        // --- Cập nhật giá (Giữ nguyên) ---
        Long giaTinh = danhSachGiaVe.get(maCho);
        if (giaTinh != null) { giaLabel.setText(formatVnd(giaTinh)); }
        else { try { long gia = tinhGiaVeTau(khach.choDat(), khach.maLoaiVe()); danhSachGiaVe.put(maCho, gia); giaLabel.setText(formatVnd(gia)); } catch (Exception ex) { giaLabel.setText("Lỗi giá"); giaLabel.setForeground(Color.RED); } }

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    /**
     * Gợi ý mã loại vé dựa trên tuổi.
     * Logic ví dụ: TE (dưới 18), SV (chưa có logic cụ thể, mặc định NL), NCT (trên 60), NL (còn lại)
     * @param age Tuổi của khách hàng.
     * @return Mã loại vé phù hợp.
     */
    private String suggestLoaiVeByAge(int age) {
        if (age == -1 || age == 0) return MA_VE_NL; // Lỗi parse hoặc tuổi chưa được nhập
        if (age <= 17) return MA_VE_TE; // Ví dụ: Trẻ em <= 17
        if (age >= 60) return MA_VE_NCT; // Ví dụ: Người cao tuổi >= 60
        // Thêm logic cho Sinh viên nếu cần (ví dụ: tuổi từ 18-22 và có thẻ SV)
        // Hiện tại chỉ trả về NL nếu không thuộc TE/NCT
        return MA_VE_NL;
    }


    public void resetAllData() {
        // 1. Dọn dẹp dữ liệu nội bộ (Ghế ngồi/Khách hàng)
        danhSachGheDaChon.clear();
        danhSachKhachHang.clear();
        tatCaChoDatToaHienTai.clear();
        seatButtonsMap.clear();
        danhSachGiaVe.clear();

        // 2. Dọn dẹp các biến trạng thái chuyến tàu
        maChuyenTauHienTai = null;
        maToaHienTai = null;
        lastSelectedToaButton = null;

        // =======================================================
        // FIX: DỌN DẸP DANH SÁCH CHUYẾN TÀU
        // =======================================================
        if (tableModel != null) {
            tableModel.setRowCount(0); // Xóa tất cả các dòng khỏi model
        }
        // =======================================================

        SwingUtilities.invokeLater(() -> {
            capNhatDanhSachGheDaChonUI();

            if (pnlToa != null) {
                pnlToa.removeAll();
                pnlToa.add(new JLabel("Chọn toa:"));
                pnlToa.revalidate();
                pnlToa.repaint();
            }

            if (pnlSoDoGhe != null) {
                pnlSoDoGhe.removeAll();
                pnlSoDoGhe.add(new JLabel("Chưa có sơ đồ ghế."));
                pnlSoDoGhe.revalidate();
                pnlSoDoGhe.repaint();
            }

            capNhatThongTinKhachUI();
            capNhatTongTienUI();
        });
    }


    private void capNhatThongTinKhachUI() {
        if (thongTinKhachScrollPane == null) {
            System.out.println("Lỗi: thongTinKhachScrollPane chưa được khởi tạo.");
            return;
        }
        JPanel infoScrollPanel = (JPanel) thongTinKhachScrollPane.getViewport().getView();
        infoScrollPanel.removeAll();

        // SỬA: Lấy danh sách ChiTietKhach
        List<ChiTietKhach> danhSachChiTiet = new ArrayList<>(danhSachKhachHang.values());

        if (danhSachChiTiet.isEmpty()) {
            infoScrollPanel.add(new JLabel("Chưa có ghế nào được chọn."));
            infoScrollPanel.add(Box.createVerticalGlue());
            infoScrollPanel.revalidate();
            infoScrollPanel.repaint();
            return;
        }

        // SỬA: Vòng lặp chỉ hiển thị các form ChiTietKhach
        for (ChiTietKhach khach : danhSachChiTiet) {
            JPanel khachPanel = createKhachPanel(khach);
            infoScrollPanel.add(khachPanel);
        }

        infoScrollPanel.add(Box.createVerticalGlue());
        infoScrollPanel.revalidate();
        infoScrollPanel.repaint();
    }

    // ====================
    // MODULE: UI Helper Methods (style / small components)
    // ====================
    private void datCanhKhuVuc(JPanel panel) {
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
    }

    private void styleNutChinh(JButton btn) {
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(110, 25));
    }

    private JButton taoNutToa(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(String.format("<html><center>%s</center></html>", text.replace("\n", "<br>")));
        btn.setPreferredSize(new Dimension(110, 60));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
        btn.setBackground(bgColor != null ? bgColor : Color.LIGHT_GRAY);
        btn.setForeground(fgColor != null ? fgColor : Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel taoMucChuGiai(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        JLabel square = new JLabel();
        square.setPreferredSize(new Dimension(15, 15));
        square.setOpaque(true);
        square.setBackground(color);
        square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(square);
        panel.add(new JLabel(text));
        return panel;
    }





    private String[] getLoaiVeOptions() {
        return new String[] {
                getTenLoaiVeHienThi(MA_VE_NL),
                getTenLoaiVeHienThi(MA_VE_TE),
                getTenLoaiVeHienThi(MA_VE_NCT),
                getTenLoaiVeHienThi(MA_VE_SV)
        };
    }

    private String getTenLoaiVeHienThi(String maLoaiVe) {
        return switch (maLoaiVe) {
            case "VT01" -> "Người lớn (VT01)";
            case "VT02" -> "Trẻ em (VT02)";
            case "VT03" -> "Người cao tuổi (VT03)";
            case "VT04" -> "Sinh viên (VT04)";
            default -> "Người lớn (VT01)";
        };
    }

    private String getMaLoaiVeFromHienThi(String tenHienThi) {
        if (tenHienThi.contains("(VT01)")) return "VT01";
        if (tenHienThi.contains("(VT02)")) return "VT02";
        if (tenHienThi.contains("(VT03)")) return "VT03";
        if (tenHienThi.contains("(VT04)")) return "VT04";
        return "VT01";
    }

    private Vector<String> taoDanhSachLoaiVeUuTien() {
        Vector<String> dsMaVe = new Vector<>();
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_NCT, soLuongYeuCau.getOrDefault("NguoiCaoTuoi", 0));
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_TE, soLuongYeuCau.getOrDefault("TreCon", 0));
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_SV, soLuongYeuCau.getOrDefault("SinhVien", 0));
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_NL, soLuongYeuCau.getOrDefault("NguoiLon", 0));
        return dsMaVe;
    }

    private void themLoaiVeVaoDanhSach(Vector<String> ds, String maVe, int soLuong) {
        for (int i = 0; i < soLuong; i++) ds.add(maVe);
    }

    // ====================
    // MODULE: Data / Actions (tìm chuyến, nạp dữ liệu)
    // ====================
    private void timKiemChuyenTau() {
        Ga gaDiSelected = (Ga) cbGaDi.getSelectedItem();
        Ga gaDenSelected = (Ga) cbGaDen.getSelectedItem();

        if (gaDiSelected == null || gaDenSelected == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn ga đi và ga đến.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (gaDiSelected.getMaGa().equals(gaDenSelected.getMaGa())) {
            JOptionPane.showMessageDialog(null, "Ga đi và Ga đến không được trùng nhau.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tenGaDi =  gaDiSelected.getTenGa(); //Sửa tối 26/10
        String tenGaDen = gaDenSelected.getTenGa();//Sửa tôí 26/10

        Date date = dateChooserNgayDi.getDate();
        if (date == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn Ngày đi.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String ngayDiSQL = SQL_DATE_FORMAT.format(date);


        ChuyenTauDao dao = new ChuyenTauDao();
        System.out.println("Tìm chuyến tàu từ " + tenGaDi + " đến " + tenGaDen + " vào ngày " + ngayDiSQL);
        ketQua = dao.timChuyenTau(tenGaDi, tenGaDen, ngayDiSQL); //Sửa tối 26/10

        if (ketQua == null || ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy chuyến tàu nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }

        napDuLieuLenBang(ketQua);
    }

    private void napDuLieuLenBang(List<ChuyenTau> danhSach) {
        DefaultTableModel model = (DefaultTableModel) tableChuyenTau.getModel();
        model.setRowCount(0);
        if (danhSach == null) return;
        for (ChuyenTau ct : danhSach) {
            Object[] rowData = {ct.getMaChuyenTau(), ct.getNgayKhoiHanh(), ct.getGioKhoiHanh()};
            model.addRow(rowData);
        }
    }

    private void hienThiDanhSachToaTau(String maTau) {
        List<Toa> danhSachToa = new ArrayList<>();
        try {
            ToaDAO toaTauDAO = new ToaDAO();
            danhSachToa = toaTauDAO.layToaTheoMaTau(maTau);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách toa tàu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        napNutToa(danhSachToa);
    }

    public void napNutToa(List<Toa> danhSachToa) {
        pnlToa.removeAll();
        pnlToa.add(new JLabel("Chọn toa:"));

        for (Toa toa : danhSachToa) {
            String soThuTuToa = laySoThuTuToa(toa.getMaToa());
            String text = "Toa " + soThuTuToa + "\n" + toa.getLoaiToa();
            JButton btnToa = taoNutToa(text, null, null);
            btnToa.addActionListener(e -> xuLyChonToa(btnToa, toa.getMaToa()));
            pnlToa.add(btnToa);
        }
        pnlToa.revalidate();
        pnlToa.repaint();
    }

    // ====================
    // MODULE: XỬ LÝ CHỌN TOA / VẼ SƠ ĐỒ GHẾ
    // ====================
    private void xuLyChonToa(JButton currentButton, String maToa) {
        maToaHienTai = maToa;

        // Đổi màu nút
        if (lastSelectedToaButton != null) {
            lastSelectedToaButton.setBackground(Color.LIGHT_GRAY);
            lastSelectedToaButton.setForeground(Color.BLACK);
        }
        currentButton.setBackground(new Color(0, 123, 255));
        currentButton.setForeground(Color.WHITE);
        lastSelectedToaButton = currentButton;

        if (this.maChuyenTauHienTai == null || this.maChuyenTauHienTai.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn chuyến tàu trước.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Chưa chọn chuyến tàu."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
            return;
        }

        System.out.println("Đã chọn Toa: " + maToa + ". Tiến hành tải sơ đồ ghế." );

        List<ChoDat> danhSachChoDat = choDatDao.getDanhSachChoDatByMaToaVaTrangThai(
                maToa,
                maChuyenTauHienTai
        );

        String loaiToa = "";
        try {
            loaiToa = layLoaiToa(maToa);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tra cứu loại toa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Không xác định được loại toa."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
            return;
        }

        if (danhSachChoDat.isEmpty()) {
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Toa này chưa có dữ liệu chỗ đặt."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
        } else {
            if (loaiToa.toLowerCase().contains("giường")) {
                veSoDoGiuongNam(danhSachChoDat);
            } else {
                veSoDoGhe(danhSachChoDat);
            }
        }
    }

    private String layLoaiToa(String maToa) throws Exception {
        if (maToa == null || maChuyenTauHienTai == null) {
            throw new Exception("Thiếu Mã toa hoặc Mã chuyến tàu.");
        }

        // Lấy mã tàu từ mã chuyến tàu hiện tại (cần tìm lại)
        String maTau = null;
        for (ChuyenTau ct : ketQua) {
            if (maChuyenTauHienTai.equals(ct.getMaChuyenTau())) {
                maTau = ct.getMaTau();
                break;
            }
        }
        if (maTau == null) throw new Exception("Không tìm thấy mã tàu cho chuyến tàu hiện tại.");

        // Lấy danh sách toa của tàu và tìm loại toa
        ToaDAO tdao = new ToaDAO();
        List<Toa> toas = tdao.layToaTheoMaTau(maTau);
        if (toas != null) {
            for (Toa t : toas) {
                if (t.getMaToa().equals(maToa)) {
                    return t.getLoaiToa();
                }
            }
        }
        throw new Exception("Không tìm thấy Loại toa cho mã toa: " + maToa);
    }

    private String laySoThuTuToa(String maToa) {
        if (maToa != null && maToa.contains("-")) {
            return maToa.substring(maToa.lastIndexOf('-') + 1);
        }
        return maToa;
    }

    private static final Dimension SQUARE_SEAT_SIZE = new Dimension(47, 25);

    private void veSoDoGhe(List<ChoDat> danhSachChoDat) {
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();
        tatCaChoDatToaHienTai.clear();

        pnlSoDoGhe.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlSoDoGhe.setOpaque(true);
        pnlSoDoGhe.setBackground(Color.WHITE);
        pnlSoDoGhe.setBorder(new EmptyBorder(10, 10, 10, 10));

        int rows = 4;
        int columns = (int) Math.ceil((double) danhSachChoDat.size() / rows);
        JPanel gridContainer = new JPanel(new GridLayout(rows, columns, 5, 5));
        gridContainer.setOpaque(false);

        for (ChoDat cho : danhSachChoDat) {
            JButton btnCho = new JButton(cho.getSoCho());
            btnCho.setPreferredSize(SQUARE_SEAT_SIZE);
            btnCho.setMinimumSize(SQUARE_SEAT_SIZE);
            btnCho.setMaximumSize(SQUARE_SEAT_SIZE);
            btnCho.setFont(btnCho.getFont().deriveFont(Font.BOLD, 12f));

            boolean isSelected = danhSachGheDaChon.containsKey(cho.getMaCho());
            boolean isBooked = cho.isDaDat();
            tatCaChoDatToaHienTai.put(cho.getMaCho(), cho);

            if (isSelected) {
                btnCho.setBackground(new Color(0, 123, 255));
                btnCho.setForeground(Color.WHITE);
                btnCho.setEnabled(true);
                btnCho.setToolTipText("Ghế đang được chọn");
            } else if (isBooked) {
                btnCho.setBackground(Color.BLACK);
                btnCho.setForeground(Color.WHITE);
                btnCho.setEnabled(false);
                btnCho.setToolTipText("Ghế đã được bán");
            } else {
                btnCho.setBackground(Color.LIGHT_GRAY);
                btnCho.setForeground(Color.BLACK);
                btnCho.setEnabled(true);
            }

            if (btnCho.isEnabled()) {
                btnCho.addActionListener(e -> xuLyChonGhe(btnCho, cho));
            }

            seatButtonsMap.put(cho.getMaCho(), btnCho);
            gridContainer.add(btnCho);
        }

        pnlSoDoGhe.add(gridContainer);
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }

    private static final Dimension BERTH_SEAT_SIZE = new Dimension(70, 45);

    private void veSoDoGiuongNam(List<ChoDat> danhSachChoDat){
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();
        tatCaChoDatToaHienTai.clear();

        pnlSoDoGhe.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlSoDoGhe.setOpaque(true);
        pnlSoDoGhe.setBackground(Color.WHITE);
        pnlSoDoGhe.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Gom nhóm chỗ đặt theo số buồng (ví dụ: "K1", "K2", ...)
        Map<String, List<ChoDat>> buongData = new LinkedHashMap<>();
        for (ChoDat cho : danhSachChoDat) {
            // SỬA ĐỔI LOGIC GOM NHÓM: Dùng trường 'Khoang' hoặc trích xuất số Khoang từ 'SoCho'
            String soGhe = cho.getSoCho();
            // Trích xuất số buồng/khoang (ví dụ: 'K1T1A' -> 'K1')
            String soBuong = soGhe.substring(0, soGhe.indexOf('T'));

            buongData.computeIfAbsent(soBuong, k -> new ArrayList<>()).add(cho);
        }

        // Số giường tối đa trong một buồng. Nếu cấu trúc là 3 tầng * 2 vị trí = 6 chỗ.
        int maxGiuongPerBuong = 0;
        for(List<ChoDat> list : buongData.values()) {
            if(list.size() > maxGiuongPerBuong) maxGiuongPerBuong = list.size();
        }

        // SỬA ĐỔI: Nếu 6 chỗ/buồng, ta có 3 tầng.
        // Tầng là hàng (Rows = 3), Vị trí A/B là cột (Columns = 2).
        final int ROWS_PER_BUONG = 3;
        final int COLUMNS_PER_BUONG = 2;

        // Container chính để chứa tất cả các buồng
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        container.setOpaque(false);

        for (Map.Entry<String, List<ChoDat>> entry : buongData.entrySet()) {
            String soBuong = entry.getKey();
            List<ChoDat> choDats = entry.getValue();

            // 1. Panel cho từng Buồng (có tiêu đề)
            JPanel pnlBuong = new JPanel(new BorderLayout());
            pnlBuong.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                    "Khoang " + soBuong.substring(1))); // Hiển thị chỉ số (ví dụ: Khoang 1)
            pnlBuong.setOpaque(false);

            // 2. Grid cho các giường trong buồng: 3 Hàng (Tầng), 2 Cột (Vị trí)
            JPanel buongGrid = new JPanel(new GridLayout(ROWS_PER_BUONG, COLUMNS_PER_BUONG, 5, 5));
            buongGrid.setOpaque(false);
            buongGrid.setBorder(new EmptyBorder(5, 5, 5, 5));

            // SẮP XẾP LẠI: Sắp xếp theo Tầng (Tang) và sau đó theo Vị trí (SoCho)
            // Ví dụ: K1T1A, K1T1B, K1T2A, K1T2B, K1T3A, K1T3B
            choDats.sort(Comparator
                    .comparing(ChoDat::getTang, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(ChoDat::getSoCho)
            );

            for (ChoDat cho : choDats) {
                JButton btnCho = new JButton(cho.getSoCho());
                btnCho.setPreferredSize(BERTH_SEAT_SIZE);
                btnCho.setFont(btnCho.getFont().deriveFont(Font.BOLD, 12f));

                boolean isSelected = danhSachGheDaChon.containsKey(cho.getMaCho());
                boolean isBooked = cho.isDaDat();
                tatCaChoDatToaHienTai.put(cho.getMaCho(), cho);

                if (isSelected) {
                    btnCho.setBackground(new Color(0, 123, 255));
                    btnCho.setForeground(Color.WHITE);
                    btnCho.setEnabled(true);
                    btnCho.setToolTipText("Ghế đang được chọn");
                } else if (isBooked) {
                    btnCho.setBackground(Color.BLACK);
                    btnCho.setForeground(Color.WHITE);
                    btnCho.setEnabled(false);
                    btnCho.setToolTipText("Ghế đã được bán");
                } else {
                    btnCho.setBackground(Color.LIGHT_GRAY);
                    btnCho.setForeground(Color.BLACK);
                    btnCho.setEnabled(true);
                }

                if (btnCho.isEnabled()) {
                    // Cần đảm bảo rằng xuLyChonGhe có sẵn trong context này.
                    // Nếu bạn đang làm việc với lớp Controller, nó sẽ tự động có.
                    // Nếu bạn đang làm việc với lớp View/Panel, bạn sẽ cần truyền Listener từ Controller.
                    btnCho.addActionListener(e -> xuLyChonGhe(btnCho, cho));
                }

                seatButtonsMap.put(cho.getMaCho(), btnCho);
                buongGrid.add(btnCho);
            }

            pnlBuong.add(buongGrid, BorderLayout.CENTER);
            container.add(pnlBuong);
        }

        pnlSoDoGhe.add(container);
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }    // ====================
    // MODULE: Chọn/Hủy ghế, cập nhật UI danh sách & form khách
    // ====================
    // ====================
// MODULE: Chọn/Hủy ghế, cập nhật UI danh sách & form khách
// ====================
    private void xuLyChonGhe(JButton btnCho, ChoDat cho) {
        String maCho = cho.getMaCho();

        int tongSoKhachYeuCau = parseTextFieldToInt(txtTongSoKhach);

        if (danhSachGheDaChon.containsKey(maCho)) {
            // --- Logic Hủy chọn ghế ---
            danhSachGheDaChon.remove(maCho);
            danhSachGiaVe.remove(maCho);
            danhSachKhachHang.remove(maCho); // Xóa Record ChiTietKhach
            btnCho.setBackground(Color.LIGHT_GRAY);
            btnCho.setForeground(Color.BLACK);
        } else {
            // --- Logic Chọn ghế ---
            if (danhSachGheDaChon.size() >= tongSoKhachYeuCau) {
                JOptionPane.showMessageDialog(this,
                        "Đã chọn đủ " + tongSoKhachYeuCau + " ghế. Vui lòng thay đổi số lượng khách hoặc hủy chọn ghế cũ trước.",
                        "Giới hạn chọn", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // SỬA: Tạo đối tượng ChiTietKhach (Record) mới
            ChiTietKhach chiTietKhach = new ChiTietKhach(
                    cho,            // choDat
                    MA_VE_NL,       // maLoaiVe mặc định
                    "",             // hoTen mặc định
                    "",             // cccd mặc định
                    "",             // sdt mặc định
                    ""               // ngay sinh mặc định
            );

            // Tính giá cho ghế ngay khi chọn
            try {
                // SỬA: Truyền maLoaiVe từ Record mới
                long gia = tinhGiaVeTau(cho, chiTietKhach.maLoaiVe());
                danhSachGiaVe.put(maCho, gia);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Không thể tính giá cho ghế " + cho.getSoCho() + ": " + ex.getMessage(),
                        "Lỗi tính giá", JOptionPane.ERROR_MESSAGE);
                return;
            }

            danhSachGheDaChon.put(maCho, cho);
            btnCho.setBackground(new Color(0, 123, 255));
            btnCho.setForeground(Color.WHITE);

            // SỬA: Thêm Record ChiTietKhach vào Map
            danhSachKhachHang.put(maCho, chiTietKhach);
        }

        capNhatDanhSachGheDaChonUI();
        capNhatThongTinKhachUI();
        capNhatTongTienUI();
    }

    private void xuLyHuyChonGhe(String maCho) {
        danhSachGheDaChon.remove(maCho);
        danhSachGiaVe.remove(maCho);
        danhSachKhachHang.remove(maCho);

        JButton btnCho = seatButtonsMap.get(maCho);
        if (btnCho != null) {
            btnCho.setBackground(Color.LIGHT_GRAY);
            btnCho.setForeground(Color.BLACK);
        }
        capNhatDanhSachGheDaChonUI();
        capNhatThongTinKhachUI();
        capNhatTongTienUI();
    }

    private void capNhatDanhSachGheDaChonUI() {
        pnlDanhSachGheDaCho.removeAll();
        pnlDanhSachGheDaCho.add(new JLabel("Ghế đã chọn (" + danhSachGheDaChon.size() + "):"));
        for (ChoDat cho : danhSachGheDaChon.values()) {
            String soThuTuToa = laySoThuTuToa(cho.getMaToa());
            String soCho = cho.getSoCho();
            JButton btnGhe = taoNutGheDaChon(cho.getMaCho(), soThuTuToa, soCho);
            pnlDanhSachGheDaCho.add(btnGhe);
        }
        pnlDanhSachGheDaCho.revalidate();
        pnlDanhSachGheDaCho.repaint();
    }

    private JButton taoNutGheDaChon(String maGhe, String soThuTuToa, String soCho) {
        String text = "Chỗ " + soCho + ", Toa " + soThuTuToa;
        JButton btn = new JButton(text);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 25));
        btn.addActionListener(e -> xuLyHuyChonGhe(maGhe));
        return btn;
    }



    // ====================
    // MODULE: Event handlers (Mouse)
    // ====================
    @Override
    public void mouseClicked(MouseEvent e) {
        int selectedRow = tableChuyenTau.getSelectedRow();
        if (selectedRow != -1 && ketQua != null && selectedRow < ketQua.size()) {
            String maTau = ketQua.get(selectedRow).getMaTau();
            String maChuyenTauMoi = ketQua.get(selectedRow).getMaChuyenTau();

            // Nếu chuyển sang chuyến tàu khác -> reset toàn bộ trạng thái liên quan
            if (!maChuyenTauMoi.equals(maChuyenTauHienTai)) {
                danhSachGheDaChon.clear();
                danhSachKhachHang.clear();
                danhSachGiaVe.clear();

                lastSelectedToaButton = null;
                maToaHienTai = null;
                seatButtonsMap.clear();
                tatCaChoDatToaHienTai.clear();

                capNhatDanhSachGheDaChonUI();
                pnlToa.removeAll();
                pnlToa.add(new JLabel("Chọn toa: (Đang tải...)"));
                pnlToa.revalidate();
                pnlToa.repaint();

                pnlSoDoGhe.removeAll();
                pnlSoDoGhe.add(new JLabel("Vui lòng chọn Toa."));
                pnlSoDoGhe.revalidate();
                pnlSoDoGhe.repaint();
            }

            maChuyenTauHienTai = maChuyenTauMoi;
            hienThiDanhSachToaTau(maTau);
        }
    }

    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }



    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src == btnTimChuyen){
            timKiemChuyenTau();
        }

        else if (src == btbHuy) {
            huyBoDatVe();
        } else if (src == btnTiepTheo) {
            xuLyNutTiepTheo();
        }
    }

    private void huyBoDatVe() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có muốn hủy toàn bộ dữ liệu và quay về Trang chủ?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        resetAllData();

        Window w = SwingUtilities.getWindowAncestor(this);

        if (w instanceof BanVeDashboard) {
            BanVeDashboard dashboard = (BanVeDashboard) w;

            ManHinhTrangChuNVBanVe confirmPanel = new ManHinhTrangChuNVBanVe();

            dashboard.themHoacCapNhatCard(confirmPanel, "trangChu");
            dashboard.chuyenManHinh("trangChu");

        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể tìm thấy cửa sổ Dashboard. Vui lòng chạy ứng dụng từ BanVeDashboard.",
                    "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
        }

    }

    // Sửa đổi phương thức xuLyNutTiepTheo
    private void xuLyNutTiepTheo() {
        // SỬA: Lấy tổng số khách yêu cầu từ trường nhập tổng duy nhất
        int required = parseTextFieldToInt(txtTongSoKhach);

        if (required <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn số lượng khách hợp lệ.",
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra số ghế đã chọn có khớp với yêu cầu không
        if (danhSachGheDaChon.size() != required) {
            JOptionPane.showMessageDialog(this,
                    "Số ghế đã chọn (" + danhSachGheDaChon.size() + ") không khớp với Tổng số khách (" + required + ").",
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Bổ sung: Kiểm tra thông tin khách hàng bắt buộc (Họ tên, CCCD)
        for (ChiTietKhach khach : danhSachKhachHang.values()) {
            // CẬP NHẬT: Sử dụng accessor methods (hoTen(), cccd()) của Record
            if (khach.hoTen() == null || khach.hoTen().trim().isEmpty() ||
                    khach.cccd() == null || khach.cccd().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ Họ tên và CCCD cho tất cả " + required + " khách hàng.",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }


        // --- Logic chuyển màn hình (Giữ nguyên) ---
        Window w = SwingUtilities.getWindowAncestor(this);

        if (w instanceof BanVeDashboard) {
            BanVeDashboard dashboard = (BanVeDashboard) w;

            ManHinhXacNhanBanVe confirmPanel = new ManHinhXacNhanBanVe(
                    danhSachGheDaChon,
                    danhSachKhachHang,
                    maChuyenTauHienTai,
                    date,
                    new HashMap<>(danhSachGiaVe) // pass a defensive copy of prices
            );

            dashboard.themHoacCapNhatCard(confirmPanel, "xacNhanBanVe");
            dashboard.chuyenManHinh("xacNhanBanVe");

        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể tìm thấy cửa sổ Dashboard. Vui lòng chạy ứng dụng từ BanVeDashboard.",
                    "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ====================
    // MODULE: Main (để chạy độc lập)
    // ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Bán vé (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ManHinhBanVe(), BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // ====================
    // MODULE: Giá vé helpers
    // ====================
    private long roundUpToNextTen(long value) {
        return ((value + 9) / 10) * 10;
    }

    private long tinhGiaVeTau(ChoDat cho, String maLoaiVe) throws Exception {
        Ga gaDi = (Ga) cbGaDi.getSelectedItem();
        Ga gaDen = (Ga) cbGaDen.getSelectedItem();
        if (gaDi == null || gaDen == null) throw new Exception("Ga đi hoặc ga đến chưa được chọn.");

        long base = giaVeCoBanDAO.getGiaCoBan(gaDi.getMaGa(), gaDen.getMaGa());
        if (base <= 0) throw new Exception("Không tìm thấy giá cơ bản cho cặp ga.");

        // Lấy loại toa từ ToaDAO bằng cách lấy danh sách toa của tàu hiện tại rồi tìm maToa
        ToaDAO tdao = new ToaDAO();
        String maTau = null;
        if (maChuyenTauHienTai != null) {
            for (ChuyenTau ct : ketQua) {
                if (maChuyenTauHienTai.equals(ct.getMaChuyenTau())) {
                    maTau = ct.getMaTau();
                    break;
                }
            }
        }
        if (maTau == null) throw new Exception("Không xác định được mã tàu để tra loại toa.");

        List<Toa> toas = tdao.layToaTheoMaTau(maTau);
        String loaiToa = null;
        if (toas != null) {
            for (Toa t : toas) {
                if (t.getMaToa().equals(cho.getMaToa())) {
                    loaiToa = t.getLoaiToa();
                    break;
                }
            }
        }
        if (loaiToa == null) throw new Exception("Không tìm thấy thông tin loại toa cho ghế.");

        double heSoToa = loaiChoDatDAO.getHeSoByLoaiToa(loaiToa);
        double heSoLoaiVe = loaiVeDAO.getHeSoByMaLoaiVe(maLoaiVe);

        double price = base * heSoToa * heSoLoaiVe;
        long rounded = roundUpToNextTen(Math.round(price));
        return rounded;
    }

    private void capNhatTongTienUI() {
        long total = 0L;
        for (Long v : danhSachGiaVe.values()) total += v == null ? 0L : v;
        lblTotalPrice.setText("Tổng tiền vé: " + formatVnd(total));
    }

    private String formatVnd(long amount) {
        try {
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
            return nf.format(amount) + " VNĐ";
        } catch (Exception e) {
            return amount + " VNĐ";
        }
    }

}