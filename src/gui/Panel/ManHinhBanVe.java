package gui.Panel;

import dao.ChoDatDAO;
import dao.ChuyenTauDao;
import dao.GaDao;
import dao.ToaDAO;
import entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;

/**
 * ManHinhBanVe: Panel bán vé - tách các phần UI và logic rõ ràng,
 * mỗi khu vực có 1 phương thức tạo, helper methods gom nhóm.
 */
public class ManHinhBanVe extends JPanel implements MouseListener {

    // UI components (fields)
    private JPanel pnlToa;
    private JPanel pnlSoDoGhe;
    private JComboBox<Ga> cbGaDi;
    private JComboBox<Ga> cbGaDen;
    private JTextField dateField;

    // Thay thế txtSoKhachTong bằng JLabel
    private JLabel lblTongSoKhach;
    private JTextField txtNguoiCaoTuoi;
    private JTextField txtNguoiLon;
    private JTextField txtTreCon;
    private JTextField txtSinhVien;

    private JTable tableChuyenTau;
    private DefaultTableModel tableModel;

    // UI State
    private JPanel pnlDanhSachGheDaCho; // Panel chứa danh sách các nút ghế đã chọn

    // Data
    private Date date;
    private List<ChuyenTau> ketQua = new ArrayList<>();
    private String maChuyenTauHienTai = null;

    // State
    private JButton lastSelectedToaButton = null;
    private String maToaHienTai = null; // Thêm biến lưu MaToa hiện tại

    // Map lưu trữ chi tiết Khách hàng tạm thời (MaChoDat -> TempKhachHang)
    private Map<String, TempKhachHang> danhSachKhachHang = new HashMap<>();

    // Map lưu trữ số lượng yêu cầu theo loại khách (đã tính tổng)
    private Map<String, Integer> soLuongYeuCau = new HashMap<>();

    // Map lưu ChoDat chi tiết của toa hiện tại (cho tra cứu nhanh)
    private Map<String, ChoDat> tatCaChoDatToaHienTai = new HashMap<>();

    // Danh sách các ghế đang được chọn (MaChoDat -> ChoDat)
    private Map<String, ChoDat> danhSachGheDaChon = new HashMap<>();
    // Map để theo dõi trạng thái button trên sơ đồ (MaCho -> Button)
    private Map<String, JButton> seatButtonsMap = new HashMap<>();

    // Constants
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private JScrollPane thongTinKhachScrollPane;

    public ManHinhBanVe() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245));

        add(taoPanelTieuDe(), BorderLayout.NORTH);
        add(taoNoiDungChinh(), BorderLayout.CENTER);
    }

    // ======= UI Builders =======

    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 50));
        panel.setBorder(new EmptyBorder(0, 10, 0, 10));

        JLabel titleLabel = new JLabel("Bán vé");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        panel.add(titleLabel, BorderLayout.WEST);

        JLabel idLabel = new JLabel("ID: QL200001");
        panel.add(idLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel taoNoiDungChinh() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 0));
        mainPanel.setBackground(Color.WHITE);

        JPanel contentLeftPanel = new JPanel();
        contentLeftPanel.setLayout(new BoxLayout(contentLeftPanel, BoxLayout.Y_AXIS));
        contentLeftPanel.setOpaque(false);
        contentLeftPanel.setBorder(new EmptyBorder(0, 0, 0, 5));

        // Thêm các khu vực con
        contentLeftPanel.add(createKhuVucTimKiem());
        contentLeftPanel.add(Box.createVerticalStrut(10));

        contentLeftPanel.add(createKhuVucDanhSachChuyenTau());
        contentLeftPanel.add(Box.createVerticalStrut(10));

        contentLeftPanel.add(createKhuVucChonLoaiKhach());
        contentLeftPanel.add(Box.createVerticalStrut(10));

        contentLeftPanel.add(createKhuVucChonViTriGhe());
        contentLeftPanel.add(Box.createVerticalStrut(10));

        contentLeftPanel.add(createKhuVucTongTien()); // Giữ lại khu vực tổng tiền
        contentLeftPanel.add(Box.createVerticalGlue());

        JScrollPane leftScrollPane = new JScrollPane(contentLeftPanel);
        leftScrollPane.setBorder(null);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Không cố định preferred size ở đây -> để split pane quản lý
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(true);

        // Đặt leftScrollPane vào BorderLayout.CENTER để chiếm tối đa chiều dài
        leftContainer.add(leftScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = createKhuVucThongTinKhach();

        // DÙNG JSPLITPANE để tự động co giãn
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftContainer, rightPanel);
        split.setResizeWeight(0.75); // tỷ lệ khi resize 8/2
        split.setOneTouchExpandable(true);
        split.setDividerSize(6);

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

        panel.add(new JLabel("Ngày đi"));
        dateField = new JTextField("10/11/2025", 8);
        dateField.setPreferredSize(new Dimension(80, 25));
        panel.add(dateField);

        JButton searchButton = new JButton("Tìm chuyến");
        styleNutChinh(searchButton);
        searchButton.addActionListener(e -> timKiemChuyenTau());
        panel.add(searchButton);

        datCanhKhuVuc(panel);
        return panel;
    }

    private JScrollPane createKhuVucDanhSachChuyenTau() {
        String[] columnNames = {"Tên Chuyến", "Ngày đi", "Giờ đi"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableChuyenTau = new JTable(tableModel);
        tableChuyenTau.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(tableChuyenTau);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        scrollPane.setMaximumSize(new Dimension(1200, 100));
        return scrollPane;
    }

    // Phương thức giúp parse an toàn từ JTextField
    private int parseTextFieldToInt(JTextField field) {
        try {
            if (field.getText().trim().isEmpty()) return 0;
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            return 0; // Trả về 0 nếu không phải số
        }
    }

    // Phương thức giúp thiết lập style cho JTextField trong khu vực chọn khách
    private void tuChinhTextField(JTextComponent field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        field.setMaximumSize(new Dimension(50, 25)); // Đảm bảo kích thước không quá lớn
        field.setPreferredSize(new Dimension(50, 25));
    }


    // Logic chính để tính toán tổng số khách và kiểm tra giới hạn chọn
    private void capNhatSoLuongYeuCau() {
        int nguoiCaoTuoi = parseTextFieldToInt(txtNguoiCaoTuoi);
        int nguoiLon = parseTextFieldToInt(txtNguoiLon);
        int treCon = parseTextFieldToInt(txtTreCon);
        int sinhVien = parseTextFieldToInt(txtSinhVien);

        // ⭐ TÍNH TỔNG SỐ KHÁCH TỪ CÁC LOẠI CHI TIẾT
        int tongSoKhachMoi = nguoiCaoTuoi + nguoiLon + treCon + sinhVien;

        // Cập nhật nhãn TỔNG SỐ KHÁCH
        if (lblTongSoKhach != null) {
            lblTongSoKhach.setText(String.valueOf(tongSoKhachMoi));
        }

        // Cập nhật Map trạng thái (dùng nếu cần logic kiểm tra từng loại khách sau này)
        soLuongYeuCau.clear();
        soLuongYeuCau.put("NguoiCaoTuoi", nguoiCaoTuoi);
        soLuongYeuCau.put("NguoiLon", nguoiLon);
        soLuongYeuCau.put("TreCon", treCon);
        soLuongYeuCau.put("SinhVien", sinhVien);

        // Kiểm tra và cảnh báo nếu số lượng ghế đã chọn vượt quá tổng số khách mới
        if (danhSachGheDaChon.size() > tongSoKhachMoi) {
            JOptionPane.showMessageDialog(this,
                    "Số lượng ghế đã chọn (" + danhSachGheDaChon.size() + ") vượt quá Tổng số khách mới (" + tongSoKhachMoi + "). Vui lòng hủy chọn bớt.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            // Lưu ý: Không tự động hủy chọn để tránh làm mất dữ liệu người dùng đã nhập trên form chi tiết.
        }
    }

    // Trong class ManHinhBanVe

    private JPanel createKhuVucChonLoaiKhach() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Chọn toa và ghế");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ⭐ HIỂN THỊ TỔNG SỐ KHÁCH (Được tính toán)
        topRow.add(new JLabel("Tổng số khách:"));
        lblTongSoKhach = new JLabel("0");
        lblTongSoKhach.setFont(lblTongSoKhach.getFont().deriveFont(Font.BOLD, 14f));
        lblTongSoKhach.setForeground(new Color(220, 53, 69));
        topRow.add(lblTongSoKhach);

        // Thay thế loaiKhachInfo bằng một JPanel dọc để chứa các SpinBox
        JPanel loaiKhachSpinBoxPanel = new JPanel();
        loaiKhachSpinBoxPanel.setLayout(new BoxLayout(loaiKhachSpinBoxPanel, BoxLayout.Y_AXIS));
        loaiKhachSpinBoxPanel.setOpaque(false);
        loaiKhachSpinBoxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Khởi tạo các fields (chỉ cần khởi tạo, giá trị được set trong createSpinBoxPanel)
        txtNguoiLon = new JTextField(1);
        txtTreCon = new JTextField(1);
        txtNguoiCaoTuoi = new JTextField(1);
        txtSinhVien = new JTextField(1);

        // 1. Người lớn
        loaiKhachSpinBoxPanel.add(createSpinBoxPanel("Người lớn (11-59 tuổi)", "1", null, txtNguoiLon));

        // 2. Trẻ em
        loaiKhachSpinBoxPanel.add(createSpinBoxPanel("Trẻ em (6-10 tuổi)", "0", "-25%", txtTreCon));

        // 3. Người cao tuổi
        loaiKhachSpinBoxPanel.add(createSpinBoxPanel("Người cao tuổi (> 60 tuổi)", "0", "-15%", txtNguoiCaoTuoi));

        // 4. Sinh viên
        loaiKhachSpinBoxPanel.add(createSpinBoxPanel("Sinh viên (Thẻ SV)", "0", "-10%", txtSinhVien));

        // Giãn cách
        topRow.add(Box.createHorizontalStrut(20));
        // Thêm Panel chứa các SpinBox vào topRow
        topRow.add(loaiKhachSpinBoxPanel);
        panel.add(topRow);

        pnlToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        pnlToa.setOpaque(false);
        pnlToa.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlToa.add(new JLabel("Chọn toa:"));
        panel.add(pnlToa);
        panel.setMaximumSize(new Dimension(1200, 100));

        // Cập nhật trạng thái ban đầu sau khi các fields đã được khởi tạo
        capNhatSoLuongYeuCau();

        datCanhKhuVuc(panel);
        return panel;
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


        // 1. Panel Chú Giải (Legend)
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setOpaque(false);
        legendPanel.add(taoMucChuGiai(Color.LIGHT_GRAY, "Chỗ trống"));
        legendPanel.add(taoMucChuGiai(Color.BLACK, "Đã đặt"));
        legendPanel.add(taoMucChuGiai(new Color(0, 123, 255), "Đang chọn")); // Cập nhật màu
        legendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(legendPanel);
        panel.add(Box.createVerticalStrut(5));


        // 3. Panel Ghế Đã Chọn
        pnlDanhSachGheDaCho = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlDanhSachGheDaCho.setOpaque(false);
        pnlDanhSachGheDaCho.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlDanhSachGheDaCho.add(new JLabel("Ghế đã chọn:"));

        // Khởi tạo lần đầu để hiển thị
        capNhatDanhSachGheDaChonUI();

        panel.add(pnlDanhSachGheDaCho);

        datCanhKhuVuc(panel);
        return panel;
    }


    // Hàm tạo nút ghế đã chọn
    private JButton taoNutGheDaChon(String maGhe, String soThuTuToa, String soCho) {
        // Định dạng hiển thị: T[STT_Toa] / G[SoCho]
        String text = "Chỗ " + soCho + ", Toa " + soThuTuToa;

        JButton btn = new JButton(text);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 25));

        // Logic để hủy chọn khi click vào nút này
        btn.addActionListener(e -> xuLyHuyChonGhe(maGhe));

        return btn;
    }


    private JPanel createKhuVucTongTien() {
        // Giả lập khu vực tổng tiền
        JPanel fullSummary = new JPanel(new BorderLayout());
        fullSummary.setBackground(Color.white);
        fullSummary.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        legendPanel.setOpaque(false);
        legendPanel.add(taoMucChuGiai(Color.LIGHT_GRAY, "Chỗ trống"));
        legendPanel.add(taoMucChuGiai(Color.BLACK, "Không trống"));
        legendPanel.add(taoMucChuGiai(new Color(0, 123, 255), "Đang chọn"));
        fullSummary.add(legendPanel, BorderLayout.WEST);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(new JLabel("Đã chọn: X/Y")); // Giá trị sẽ được cập nhật


        JLabel totalLabel = new JLabel("Tổng tiền vé: 0 VNĐ");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        totalLabel.setForeground(new Color(255, 165, 0));
        summaryPanel.add(totalLabel);

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

        // Dữ liệu giả lập
        infoScrollPanel.add(new JLabel("Chọn ghế để thêm thông tin."));
        //làm khoảng cách dài hơn
        infoScrollPanel.add(Box.createVerticalGlue());

        infoScrollPanel.setPreferredSize(new Dimension(400, 300));


        thongTinKhachScrollPane = new JScrollPane(infoScrollPanel);
        thongTinKhachScrollPane.setBorder(null);
        thongTinKhachScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(thongTinKhachScrollPane, BorderLayout.CENTER);


        // ⭐ GỌI CẬP NHẬT BAN ĐẦU
        SwingUtilities.invokeLater(this::capNhatThongTinKhachUI);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        JButton cancelButton = new JButton("< Hủy");
        cancelButton.setPreferredSize(new Dimension(80, 40));
        cancelButton.setFont(cancelButton.getFont().deriveFont(Font.BOLD, 14f));
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);

        JButton nextButton = new JButton("Tiếp theo >");
        nextButton.setPreferredSize(new Dimension(100, 40));
        nextButton.setFont(nextButton.getFont().deriveFont(Font.BOLD, 14f));
        nextButton.setBackground(new Color(0, 123, 255));
        nextButton.setForeground(Color.WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Trong ManHinhBanVe.java

    /**
     * Tạo panel nhập thông tin chi tiết cho 1 khách hàng.
     * Panel này hiển thị thông tin ghế và cho phép nhập chi tiết khách hàng,
     * đồng thời gắn sự kiện để lưu dữ liệu vào đối tượng TempKhachHang.
     * @param tempKhach Đối tượng TempKhachHang chứa Chi tiết ghế và Dữ liệu khách tạm thời.
     * @return JPanel chứa form nhập liệu
     */
    private JPanel createKhachPanel(TempKhachHang tempKhach) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Giá trị binding từ TempKhachHang
        String soCho = tempKhach.choDat.getSoCho();
        String soThuTuToa = laySoThuTuToa(tempKhach.choDat.getMaToa());
        String loaiKhachHienThi = getTenLoaiVeHienThi(tempKhach.maLoaiVe);
        String gia = "Giá vé sẽ được tính"; // Placeholder

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setOpaque(false);

        // Hiển thị Mã Ghế và Toa
        JLabel maGheLabel = new JLabel("Ghế: " + soCho + " / Toa: " + soThuTuToa);
        maGheLabel.setFont(maGheLabel.getFont().deriveFont(Font.BOLD));
        leftHeader.add(maGheLabel);

        // ⭐ COMBOBOX CHỌN LOẠI VÉ
        JComboBox<String> cbLoaiKhach = new JComboBox<>(getLoaiVeOptions());
        cbLoaiKhach.setSelectedItem(loaiKhachHienThi);
        cbLoaiKhach.setPreferredSize(new Dimension(120, 25));
        cbLoaiKhach.setMaximumSize(new Dimension(120, 25));

        cbLoaiKhach.addActionListener(e -> {
            // Logic cập nhật Mã Loại Vé trong TempKhachHang
            String maMoi = getMaLoaiVeFromHienThi((String) cbLoaiKhach.getSelectedItem());
            tempKhach.maLoaiVe = maMoi;
            // Cần gọi hàm tính lại giá vé ở đây nếu có logic giá
        });
        leftHeader.add(cbLoaiKhach);

        headerRow.add(leftHeader, BorderLayout.WEST);

        JLabel giaLabel = new JLabel(gia);
        giaLabel.setForeground(Color.BLUE);
        headerRow.add(giaLabel, BorderLayout.EAST);

        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(headerRow);

        JPanel detailGrid = new JPanel(new GridLayout(2, 4, 10, 5));
        detailGrid.setOpaque(false);
        detailGrid.setBorder(new EmptyBorder(5, 0, 5, 0));

        // Hàng 1 (Binding HoTen, Tuoi)
        detailGrid.add(new JLabel("Họ và tên*"));
        JTextField hoTenField = new JTextField(tempKhach.hoTen, 10);
        detailGrid.add(hoTenField);
        detailGrid.add(new JLabel("Tuổi"));
        JTextField tuoiField = new JTextField(String.valueOf(tempKhach.tuoi > 0 ? tempKhach.tuoi : ""), 3); // Hiện rỗng nếu tuổi = 0
        detailGrid.add(tuoiField);

        // Hàng 2 (Binding SĐT, CCCD)
        detailGrid.add(new JLabel("Số điện thoại"));
        JTextField sdtField = new JTextField(tempKhach.sdt, 10);
        detailGrid.add(sdtField);
        detailGrid.add(new JLabel("CCCD*"));
        JTextField cccdField = new JTextField(tempKhach.cccd, 10);
        detailGrid.add(cccdField);

        panel.add(detailGrid);

        // GẮN LISTENER ĐỂ LƯU DỮ LIỆU TỨC THỜI VÀO TEMPKHACHHANG (Focus Loss)
        hoTenField.addFocusListener(new java.awt.event.FocusAdapter() { public void focusLost(java.awt.event.FocusEvent evt) { tempKhach.hoTen = hoTenField.getText(); }});
        cccdField.addFocusListener(new java.awt.event.FocusAdapter() { public void focusLost(java.awt.event.FocusEvent evt) { tempKhach.cccd = cccdField.getText(); }});
        sdtField.addFocusListener(new java.awt.event.FocusAdapter() { public void focusLost(java.awt.event.FocusEvent evt) { tempKhach.sdt = sdtField.getText(); }});
        tuoiField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                try {
                    tempKhach.tuoi = Integer.parseInt(tuoiField.getText().trim());
                } catch (Exception e) {
                    tempKhach.tuoi = 0;
                    // Có thể cảnh báo người dùng nếu giá trị không hợp lệ
                }
            }
        });

        // Thiết lập kích thước tối đa cho các trường nhập liệu
        hoTenField.setMaximumSize(hoTenField.getPreferredSize());
        tuoiField.setMaximumSize(tuoiField.getPreferredSize());
        sdtField.setMaximumSize(sdtField.getPreferredSize());
        cccdField.setMaximumSize(cccdField.getPreferredSize());

        return panel;
    }


    // Trong class ManHinhBanVe.java

    /**
     * Lấy mảng String chứa các tùy chọn Loại vé để hiển thị trong JComboBox.
     * Các tùy chọn này được định dạng là "Tên Loại Vé (Mã Loại Vé)".
     * * @return Mảng String các tùy chọn loại vé.
     */
    private String[] getLoaiVeOptions() {
        // Sử dụng các hằng số MA_VE_... đã định nghĩa trong class ManHinhBanVe
        return new String[] {
                getTenLoaiVeHienThi(MA_VE_NL), // Người lớn
                getTenLoaiVeHienThi(MA_VE_TE),  // Trẻ em
                getTenLoaiVeHienThi(MA_VE_NCT), // Người cao tuổi
                getTenLoaiVeHienThi(MA_VE_SV)   // Sinh viên
        };
    }
    /**
     * Ánh xạ Mã Loại Vé (String) sang chuỗi hiển thị đầy đủ cho UI.
     * @param maLoaiVe Mã loại vé (VT01, VT02,...)
     * @return Tên hiển thị (ví dụ: "Người lớn (VT01)").
     */
    private String getTenLoaiVeHienThi(String maLoaiVe) {
        return switch (maLoaiVe) {
            case "VT01" -> "Người lớn (VT01)";
            case "VT02" -> "Trẻ em (VT02)";
            case "VT03" -> "Người cao tuổi (VT03)";
            case "VT04" -> "Sinh viên (VT04)";
            default -> "Người lớn (VT01)";
        };
    }

    private static final String MA_VE_NL = "VT01";
    private static final String MA_VE_TE = "VT02";
    private static final String MA_VE_NCT = "VT03";
    private static final String MA_VE_SV = "VT04";

    /**
     * Ánh xạ ngược từ chuỗi hiển thị trong JComboBox sang Mã Loại Vé (String).
     * @param tenHienThi Chuỗi hiển thị được chọn từ JComboBox.
     * @return Mã Loại Vé tương ứng (ví dụ: "VT01").
     */
    private String getMaLoaiVeFromHienThi(String tenHienThi) {
        if (tenHienThi.contains("(VT01)")) return "VT01";
        if (tenHienThi.contains("(VT02)")) return "VT02";
        if (tenHienThi.contains("(VT03)")) return "VT03";
        if (tenHienThi.contains("(VT04)")) return "VT04";
        return "VT01"; // Mặc định
    }

    /**
     * Tạo danh sách các MaLoaiVe ưu tiên dựa trên số lượng yêu cầu.
     */
    private Vector<String> taoDanhSachLoaiVeUuTien() {
        Vector<String> dsMaVe = new Vector<>();

        // 1. Người cao tuổi (Ưu tiên giảm giá cao)
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_NCT, soLuongYeuCau.getOrDefault("NguoiCaoTuoi", 0));
        // 2. Trẻ em
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_TE, soLuongYeuCau.getOrDefault("TreCon", 0));
        // 3. Sinh viên
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_SV, soLuongYeuCau.getOrDefault("SinhVien", 0));
        // 4. Người lớn (Còn lại)
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_NL, soLuongYeuCau.getOrDefault("NguoiLon", 0));

        return dsMaVe;
    }

    private void themLoaiVeVaoDanhSach(Vector<String> ds, String maVe, int soLuong) {
        for (int i = 0; i < soLuong; i++) {
            ds.add(maVe);
        }
    }

    // ======= Helpers / Styles =======

    private void datCanhKhuVuc(JPanel panel) {
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
    }

    private void styleNutChinh(JButton btn) {
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(100, 25));
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

    // ======= Data / Actions =======

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

        String maGaDi = gaDiSelected.getMaGa();
        String maGaDen = gaDenSelected.getMaGa();
        String ngayDiString = dateField.getText();
        String ngayDiSQL;
        try {
            date = INPUT_DATE_FORMAT.parse(ngayDiString);
            ngayDiSQL = SQL_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Ngày đi không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ChuyenTauDao dao = new ChuyenTauDao();
        ketQua = dao.timChuyenTau(maGaDi, maGaDen, ngayDiSQL);

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

    /**
     * Hiển thị danh sách toa tàu cho chuyến tàu được chọn
     * @param maTau
     */
    private void hienThiDanhSachToaTau(String maTau) {
        List<Toa> danhSachToa = new ArrayList<>();
        try {
            ToaDAO toaTauDAO = new ToaDAO();
            danhSachToa = toaTauDAO.getDanhSachToaByMaTau(maTau);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách toa tàu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        napNutToa(danhSachToa);
    }
    // Trong class ManHinhBanVe (Thêm vào phần Helpers / Styles)

    /**
     * Tạo một panel chứa label, JTextField và các nút +/- để tăng giảm giá trị.
     * @param labelText Nhãn mô tả (ví dụ: "Người lớn (11-59)")
     * @param initialValue Giá trị khởi tạo
     * @param discountText Tỷ lệ giảm giá (hoặc null nếu không có)
     * @param targetField JTextField sẽ được điều khiển
     * @return JPanel chứa toàn bộ control
     */
    private JPanel createSpinBoxPanel(String labelText, String initialValue, String discountText, JTextField targetField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));

        // --- LEFT: Label và Discount ---
        JPanel labelDiscountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelDiscountPanel.setOpaque(false);

        // Label chính
        JLabel mainLabel = new JLabel(labelText);
        mainLabel.setFont(mainLabel.getFont().deriveFont(Font.BOLD, 14f));
        labelDiscountPanel.add(mainLabel);

        // Discount (nếu có)
        if (discountText != null) {
            JLabel discountLabel = new JLabel(discountText);
            discountLabel.setForeground(new Color(230, 126, 34)); // Màu cam
            discountLabel.setFont(discountLabel.getFont().deriveFont(Font.BOLD, 12f));
            labelDiscountPanel.add(discountLabel);
        }

        panel.add(labelDiscountPanel, BorderLayout.WEST);

        // --- RIGHT: Nút tăng giảm và input ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        controlPanel.setOpaque(false);

        // 1. Nút Giảm (-)
        JButton btnMinus = new JButton("−");
        btnMinus.setPreferredSize(new Dimension(30, 30));
        btnMinus.setMargin(new Insets(0, 0, 0, 0));
        styleSpinButton(btnMinus);

        // 2. Input Field
        targetField.setText(initialValue);
        targetField.setHorizontalAlignment(JTextField.CENTER);
        targetField.setPreferredSize(new Dimension(30, 30));
        targetField.setMaximumSize(new Dimension(30, 30));
        targetField.setEditable(false); // CHỈNH SỬA: Chỉ cho phép chỉnh bằng nút

        // 3. Nút Tăng (+)
        JButton btnPlus = new JButton("+");
        btnPlus.setPreferredSize(new Dimension(30, 30));
        btnPlus.setMargin(new Insets(0, 0, 0, 0));
        styleSpinButton(btnPlus);

        // Gắn sự kiện cho nút (+) và (-)
        btnPlus.addActionListener(e -> changeQuantity(targetField, 1));
        btnMinus.addActionListener(e -> changeQuantity(targetField, -1));

        controlPanel.add(btnMinus);
        controlPanel.add(targetField);
        controlPanel.add(btnPlus);

        panel.add(controlPanel, BorderLayout.EAST);

        // Đảm bảo panel giãn ngang tối đa nhưng giữ chiều cao nhỏ
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        return panel;
    }

    /**
     * Helper để styling các nút tăng giảm
     */
    private void styleSpinButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    /**
     * Logic xử lý tăng/giảm số lượng
     * @param field JTextField cần thay đổi giá trị
     * @param delta Giá trị thay đổi (+1 hoặc -1)
     */
    private void changeQuantity(JTextField field, int delta) {
        int currentValue = parseTextFieldToInt(field);
        int newValue = currentValue + delta;

        if (newValue < 0) {
            newValue = 0; // Giới hạn dưới là 0
        }

        field.setText(String.valueOf(newValue));
        capNhatSoLuongYeuCau(); // Gọi hàm tính toán lại tổng
    }



    /**
     * Nạp nút toa vào panel
     * @param danhSachToa
     */
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

    /**
     * Xử lý khi chọn toa
     * @param currentButton
     * @param maToa
     */
    private ChoDatDAO choDatDao = new ChoDatDAO();

    private void xuLyChonToa(JButton currentButton, String maToa) {
        maToaHienTai = maToa; // Cập nhật toa hiện tại

        // 1. Đổi màu nút toa cũ
        if (lastSelectedToaButton != null) {
            lastSelectedToaButton.setBackground(Color.LIGHT_GRAY);
            lastSelectedToaButton.setForeground(Color.BLACK);
        }
        // 2. Đổi màu nút toa mới
        currentButton.setBackground(new Color(0, 123, 255));
        currentButton.setForeground(Color.WHITE);
        lastSelectedToaButton = currentButton;

        if (this.maChuyenTauHienTai == null || this.maChuyenTauHienTai.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn chuyến tàu trước.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            // Dọn dẹp sơ đồ cũ
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Chưa chọn chuyến tàu."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
            return;
        }

        // KHÔNG LÀM MỚI DANH SÁCH GHẾ ĐÃ CHỌN: danhSachGheDaChon.clear();

        System.out.println("Đã chọn Toa: " + maToa + ". Tiến hành tải sơ đồ ghế." );

        List<ChoDat> danhSachChoDat = choDatDao.getDanhSachChoDatByMaToaVaTrangThai(
                maToa,
                maChuyenTauHienTai
        );

        //Xử lý và hiện thị
        if (danhSachChoDat.isEmpty()) {
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Toa này chưa có dữ liệu chỗ đặt."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
        } else {
            // Kích hoạt phương thức vẽ sơ đồ ghế
            veSoDoGhe(danhSachChoDat);
        }
    }

    /**
     * Lấy số thứ tự toa từ mã toa
     * @param maToa
     * @return
     */
    private String laySoThuTuToa(String maToa) {
        if (maToa != null && maToa.contains("-")) {
            // Giả sử MaToa có dạng T[MaTau]-X[SoThuTu] ví dụ: T001-A
            return maToa.substring(maToa.lastIndexOf('-') + 1);
        }
        return maToa;
    }

    // Kích thước cố định cho nút ghế
    private static final Dimension SQUARE_SEAT_SIZE = new Dimension(60, 30);
    /**
     * Vẽ sơ đồ ghế dựa trên danh sách chỗ đặt.
     * @param danhSachChoDat List<ChoDat> danh sách chỗ đặt của toa được chọn.
     */
    private void veSoDoGhe(List<ChoDat> danhSachChoDat) {
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();
        tatCaChoDatToaHienTai.clear(); // Xóa Map ChoDat của toa cũ

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

            // Kiểm tra trạng thái ĐANG CHỌN (trong Map danhSachGheDaChon)
            boolean isSelected = danhSachGheDaChon.containsKey(cho.getMaCho());

            boolean isBooked = cho.isDaDat();
            tatCaChoDatToaHienTai.put(cho.getMaCho(), cho);

            if (isSelected) {
                // Trường hợp 1: ĐANG CHỌN
                btnCho.setBackground(new Color(0, 123, 255));
                btnCho.setForeground(Color.WHITE);
                btnCho.setEnabled(true);
                btnCho.setToolTipText("Ghế đang được chọn");
            } else if (isBooked) {
                // Trường hợp 2: KHÔNG KHẢ DỤ (Đã đặt, Bảo trì)
                btnCho.setBackground(Color.BLACK);
                btnCho.setForeground(Color.WHITE);
                btnCho.setEnabled(false);
                btnCho.setToolTipText("Ghế đã được bán");
            } else {
                // Trường hợp 3: CÒN TRỐNG
                btnCho.setBackground(Color.LIGHT_GRAY);
                btnCho.setForeground(Color.BLACK);
                btnCho.setEnabled(true);
            }

            // Gắn sự kiện (chỉ cho ghế còn trống và ghế đang chọn)
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

    /**
     * Logic chính để thêm/bỏ một ghế khỏi danh sách chọn.
     * @param btnCho Nút ghế được click.
     * @param cho ChoDat chi tiết của ghế.
     */
    private void xuLyChonGhe(JButton btnCho, ChoDat cho) {
        String maCho = cho.getMaCho();
        System.out.println("Xử lý chọn ghế: " + maCho);

        // ⭐ Lấy tổng số khách YÊU CẦU hiện tại
        int tongSoKhachYeuCau = parseTextFieldToInt(txtNguoiCaoTuoi) +
                parseTextFieldToInt(txtNguoiLon) +
                parseTextFieldToInt(txtTreCon) +
                parseTextFieldToInt(txtSinhVien);

        if (danhSachGheDaChon.containsKey(maCho)) {
            // Trường hợp 1: Ghế đã được chọn -> HỦY CHỌN
            danhSachGheDaChon.remove(maCho);
            btnCho.setBackground(Color.LIGHT_GRAY);
            btnCho.setForeground(Color.BLACK);
            System.out.println("Đã hủy chọn ghế: " + maCho);
        } else {
            // Trường hợp 2: Ghế chưa được chọn -> CHỌN

            // ⭐ KIỂM TRA LOGIC NGHIỆP VỤ: Đã chọn đủ số lượng chưa?
            if (danhSachGheDaChon.size() >= tongSoKhachYeuCau) {
                JOptionPane.showMessageDialog(this,
                        "Đã chọn đủ " + tongSoKhachYeuCau + " ghế. Vui lòng thay đổi số lượng khách hoặc hủy chọn ghế cũ trước.",
                        "Giới hạn chọn", JOptionPane.WARNING_MESSAGE);
                return;
            }

            danhSachGheDaChon.put(maCho, cho);
            btnCho.setBackground(new Color(0, 123, 255));
            btnCho.setForeground(Color.WHITE);
            System.out.println("Đã chọn ghế: " + maCho);

            // Tạo đối tượng TempKhachHang mới và lưu trữ
            TempKhachHang tempKhach = new TempKhachHang(cho); // Tạo TempKhach

            danhSachKhachHang.put(maCho, tempKhach); // ⭐ LƯU VÀO danhSachKhachHang (Đúng)
            danhSachGheDaChon.put(maCho, cho);

        }

        // Cập nhật UI của danh sách ghế đã chọn
        capNhatDanhSachGheDaChonUI();
        capNhatThongTinKhachUI();
    }
    /**
     * Xây dựng lại khu vực nhập thông tin khách hàng (bên phải) dựa trên danh sách ghế đã chọn.
     * Phương thức này thực hiện:
     * 1. Dọn dẹp khu vực hiển thị.
     * 2. Lấy danh sách Loại vé ưu tiên (theo số lượng yêu cầu từ SpinBox).
     * 3. Lặp qua danh sách ghế đã chọn (danhSachKhachHang), gán Mã Loại vé ưu tiên cho từng TempKhachHang.
     * 4. Tạo và hiển thị các KhachPanel tương ứng.
     */
    private void capNhatThongTinKhachUI() {

        // 1. Lấy infoScrollPanel từ JScrollPane
        // Giả định thongTinKhachScrollPane đã được gán giá trị trong createKhuVucThongTinKhach()
        if (thongTinKhachScrollPane == null) {
            System.out.println("Lỗi: thongTinKhachScrollPane chưa được khởi tạo.");
            return;
        }
        System.out.println("Cập nhật khu vực thông tin khách hàng...");

        JPanel infoScrollPanel = (JPanel) thongTinKhachScrollPane.getViewport().getView();
        infoScrollPanel.removeAll();

        List<TempKhachHang> danhSachTemp = new ArrayList<>(danhSachKhachHang.values());

        // Xử lý trường hợp không có ghế nào được chọn

        if (danhSachTemp.isEmpty()) {
            System.out.println("Không có ghế nào được chọn, hiển thị thông báo.");
            infoScrollPanel.add(new JLabel("Chưa có ghế nào được chọn."));
            infoScrollPanel.add(Box.createVerticalGlue());
            infoScrollPanel.revalidate();
            infoScrollPanel.repaint();
            return;
        }

        // ⭐ LOGIC PHÂN PHỐI/GÁN LOẠI VÉ:
        Vector<String> dsMaVeUuTien = taoDanhSachLoaiVeUuTien();

        int soFormHienThi = danhSachTemp.size();

        for (int i = 0; i < soFormHienThi; i++) {
            TempKhachHang tempKhach = danhSachTemp.get(i);

            // ⭐ Gán Mã Loại vé ưu tiên cho khách hàng theo thứ tự ưu tiên
            if (i < dsMaVeUuTien.size()) {
                String maUuTien = dsMaVeUuTien.get(i);

                // Chỉ gán nếu khách hàng này chưa chọn thủ công loại vé khác,
                // HOẶC nếu nó là lần cập nhật đầu tiên sau khi chọn ghế.
                // Để đơn giản, ta sẽ GHI ĐÈ theo ưu tiên:
                tempKhach.maLoaiVe = maUuTien;
            }
            // Nếu đã chọn nhiều ghế hơn tổng số lượng yêu cầu, các ghế thừa sẽ dùng mã mặc định ban đầu ("VT01").

            // Tạo form cho khách hàng
            JPanel khachPanel = createKhachPanel(tempKhach);
            infoScrollPanel.add(khachPanel);
        }


        infoScrollPanel.add(Box.createVerticalGlue());
        infoScrollPanel.revalidate();
        infoScrollPanel.repaint();
    }

    /**
     * Logic xử lý khi click vào nút ghế trên danh sách đã chọn để hủy chọn.
     * @param maCho Mã chỗ đặt cần hủy.
     */
    private void xuLyHuyChonGhe(String maCho) {
        // Hủy chọn trong danh sách Map
        danhSachGheDaChon.remove(maCho);

        // Cập nhật trạng thái nút trên sơ đồ ghế nếu nó thuộc toa hiện tại
        JButton btnCho = seatButtonsMap.get(maCho);
        if (btnCho != null) {
            btnCho.setBackground(Color.LIGHT_GRAY);
            btnCho.setForeground(Color.BLACK);
        }

        // Cập nhật UI danh sách đã chọn
        capNhatDanhSachGheDaChonUI();

        capNhatThongTinKhachUI();
        System.out.println("Đã hủy chọn ghế: " + maCho + " từ danh sách.");
    }

    /**
     * Xây dựng lại nội dung của pnlDanhSachGheDaCho dựa trên danh sách ghế đã chọn.
     */
    private void capNhatDanhSachGheDaChonUI() {
        pnlDanhSachGheDaCho.removeAll();
        pnlDanhSachGheDaCho.add(new JLabel("Ghế đã chọn (" + danhSachGheDaChon.size() + "):")); // Hiển thị số lượng

        // Lấy danh sách các ChoDat đã chọn từ Map
        for (ChoDat cho : danhSachGheDaChon.values()) {
            // Lấy Số thứ tự Toa và Số chỗ để hiển thị
            String soThuTuToa = laySoThuTuToa(cho.getMaToa());
            String soCho = cho.getSoCho();

            // Tạo nút với định dạng mới
            JButton btnGhe = taoNutGheDaChon(cho.getMaCho(), soThuTuToa, soCho);
            pnlDanhSachGheDaCho.add(btnGhe);
        }

        pnlDanhSachGheDaCho.revalidate();
        pnlDanhSachGheDaCho.repaint();
    }

    // ======= MouseListener =======

    @Override
    public void mouseClicked(MouseEvent e) {
        int selectedRow = tableChuyenTau.getSelectedRow();
        if (selectedRow != -1 && ketQua != null && selectedRow < ketQua.size()) {
            String maTau = ketQua.get(selectedRow).getMaTau();
            String maChuyenTauMoi = ketQua.get(selectedRow).getMaChuyenTau();

            // Kiểm tra xem có phải là chuyến tàu mới không
            if (!maChuyenTauMoi.equals(maChuyenTauHienTai)) {
                // ⭐ 1. RESET DỮ LIỆU LOGIC
                danhSachGheDaChon.clear();
                danhSachKhachHang.clear();

                // ⭐ 2. RESET CÁC BIẾN TRẠNG THÁI TOA VÀ MAPS
                lastSelectedToaButton = null;
                maToaHienTai = null;
                seatButtonsMap.clear();
                tatCaChoDatToaHienTai.clear();

                // ⭐ 3. RESET UI
                // Xóa danh sách ghế đã chọn hiển thị ở dưới
                capNhatDanhSachGheDaChonUI();

                // Xóa các nút toa (pnlToa)
                pnlToa.removeAll();
                pnlToa.add(new JLabel("Chọn toa: (Đang tải...)"));
                pnlToa.revalidate();
                pnlToa.repaint();

                // Xóa sơ đồ ghế (pnlSoDoGhe)
                pnlSoDoGhe.removeAll();
                pnlSoDoGhe.add(new JLabel("Vui lòng chọn Toa."));
                pnlSoDoGhe.revalidate();
                pnlSoDoGhe.repaint();
            }

            maChuyenTauHienTai = maChuyenTauMoi;
            System.out.println("Chuyến tàu được chọn: " + maChuyenTauHienTai + " (Mã Tàu: " + maTau + ")");
            // Hiển thị danh sách toa tàu cho chuyến tàu được chọn
            hienThiDanhSachToaTau(maTau);
        }


    }

    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }

    // ======= Main for standalone testing =======

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
}