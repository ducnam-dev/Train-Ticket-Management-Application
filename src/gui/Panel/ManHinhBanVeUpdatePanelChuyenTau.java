package gui.Panel;

// ====================================================================================
// MODULE: 0. IMPORTS VÀ KHAI BÁO GÓI
// ====================================================================================

import com.toedter.calendar.JDateChooser;
import dao.*;
import entity.*;
import gui.MainFrame.BanVeDashboard;
import control.VeSoDoTau;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.time.format.DateTimeFormatter;


public class ManHinhBanVeUpdatePanelChuyenTau extends JPanel implements MouseListener, ActionListener {

    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Mã loại vé (hằng)
    private static final String MA_VE_NL = "VT01";
    private static final String MA_VE_TE = "VT02";
    private static final String MA_VE_NCT = "VT03";
    private static final String MA_VE_SV = "VT04";

    // ====================================================================================
    // MODULE: 1. KHAI BÁO BIẾN TRẠNG THÁI VÀ DAO (STATE & DATA ACCESS)
    // ====================================================================================

    // --- 1.1. BIẾN UI / COMPONENT ---
    private JComboBox<Ga> cbGaDi;
    private JComboBox<Ga> cbGaDen;
    private JDateChooser dateChooserNgayDi;
    private JTable tableChuyenTau;
    private DefaultTableModel tableModel;
    private JTextField txtTongSoKhach;
    private JButton btnTimChuyen;
    private JButton btbHuy, btnTiepTheo;
    private JLabel lblTotalPrice;
    private JPanel pnlToa;
    private JPanel pnlSoDoGhe;
    private JLabel lblGheDaChonTong;
    private JScrollPane thongTinKhachScrollPane;

    private JButton btnVeDoan;

    // --- 1.2. BIẾN TRẠNG THÁI DỮ LIỆU ---
    private List<ChuyenTau> ketQua = new ArrayList<>();
    private String maChuyenTauHienTai = null;
    private String maToaHienTai = null;
    private JButton lastSelectedToaButton = null;

    // === THÊM BIẾN MỚI CHO DANH SÁCH CHUYẾN TÀU ===
    private JPanel pnlChuyenTau;
    private JScrollPane scrChuyenTau;

    private Map<String, ChiTietKhach> danhSachKhachHang = new LinkedHashMap<>();
    private Map<String, ChoDat> danhSachGheDaChon = new LinkedHashMap<>();
    private Map<String, ChoDat> tatCaChoDatToaHienTai = new HashMap<>();

    // Map lưu giá mỗi ghế
    private Map<String, Long> danhSachGiaVe = new HashMap<>();

    // Map nút ghế:
    private Map<String, JButton> seatButtonsMap = new HashMap<>();
    // Map lưu trữ Label Giá cho mỗi MaCho (để cập nhật giá khi đổi loại vé/tuổi)
    private Map<String, JLabel> giaLabelMap = new HashMap<>();
    // Map lưu trữ các JTextField để dễ dàng truy cập và kiểm tra
    private Map<String, JTextField> inputFieldsMap = new HashMap<>();
    // Map lưu trữ các JLabel lỗi tương ứng
    private Map<String, JLabel> errorLabelsMap = new HashMap<>();

    // Biến điều khiển để dễ dàng bật/tắt Verifiers
    private boolean enableValidation = true;

    // Biến lưu panel chuyến tàu đã chọn trước đó (để đổi màu khi chọn)
    private JPanel lastSelectedChuyenTauPanel = null;

    // --- 1.3. DAO (Data Access Objects) ---
    private final ChoDatDAO choDatDao = new ChoDatDAO();
    private final GiaVeCoBanTheoGaDAO giaVeCoBanDAO = new GiaVeCoBanTheoGaDAO();
    private final LoaiChoDatDAO loaiChoDatDAO = new LoaiChoDatDAO();
    private final LoaiVeDAO loaiVeDAO = new LoaiVeDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();


    // ====================================================================================
    // MODULE: 2. RECORD CHI TIẾT KHÁCH (DỮ LIỆU TẠM THỜI)
    // ====================================================================================

    private static record ChiTietKhach(
            ChoDat choDat,
            String maLoaiVe,
            String hoTen,
            String cccd,
            String sdt,
            String ngaySinh
    ) {
        // PHƯƠNG THỨC HỖ TRỢ BẤT BIẾN (WITH)
        public ChiTietKhach withMaLoaiVe(String newMaLoaiVe) {
            return new ChiTietKhach(this.choDat, newMaLoaiVe, this.hoTen, this.cccd, this.sdt, this.ngaySinh);
        }

        public ChiTietKhach withHoTen(String newHoTen) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, newHoTen, this.cccd, this.sdt, this.ngaySinh);
        }

        public ChiTietKhach withCccd(String newCccd) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, this.hoTen, newCccd, this.sdt, this.ngaySinh);
        }

        public ChiTietKhach withSdt(String newSdt) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, this.hoTen, this.cccd, newSdt, this.ngaySinh);
        }

        public ChiTietKhach withNgaySinh(String newNgaySinh) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, this.hoTen, this.cccd, this.sdt, newNgaySinh);
        }
    }


    // ====================================================================================
    // MODULE: 3. CONSTRUCTOR VÀ LAYOUT CHÍNH (MAIN LAYOUT)
    // ====================================================================================

    public ManHinhBanVeUpdatePanelChuyenTau() {
        setLayout(new BorderLayout());

//        setBackground(new Color(240, 242, 245));
        //màu test để hiện rõ khung panel
        setBackground(Color.cyan);
        JLabel tieuDe = new JLabel("Bán vé");
        tieuDe.setFont(tieuDe.getFont().deriveFont(Font.BOLD, 25f));
        tieuDe.setBorder(new EmptyBorder(6, 15, 12, 6));
        add(tieuDe, BorderLayout.NORTH);

        JSplitPane chia = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        chia.setResizeWeight(0.5);
        chia.setDividerSize(5);
        chia.setBorder(null);
        chia.setBackground(getBackground());

        chia.setLeftComponent(taoPanelTrai());
        chia.setRightComponent(taoPanelPhai());

        add(chia, BorderLayout.CENTER);
    }
    private JPanel taoPanelTrai() {
        JPanel panelTrai = new JPanel();
        panelTrai.setLayout(new BoxLayout(panelTrai, BoxLayout.Y_AXIS));
        panelTrai.setBackground(new Color(255, 255, 255));
        //màu test để hiện rõ panel trái
//        panelTrai.setBackground(Color.yellow);

        panelTrai.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 3, false),
                new EmptyBorder(0, 0, 0, 0)));
        // emptyborder theo thứ tự trên, trái, dưới, phải

        panelTrai.add(createKhuVucTimKiem());
        panelTrai.add(Box.createVerticalStrut(10));
        panelTrai.add(createKhuVucDanhSachChuyenTau());
        panelTrai.add(Box.createVerticalStrut(10));
        panelTrai.add(createKhuVucChonToa());
        panelTrai.add(Box.createVerticalStrut(10));
        panelTrai.add(createKhuVucChonViTriGhe());
        panelTrai.add(Box.createVerticalStrut(10));

        panelTrai.add(Box.createVerticalGlue());
        return panelTrai;
    }

    private JPanel taoPanelPhai(){
        JPanel panelPhai = new JPanel(new BorderLayout());
        panelPhai.setBackground(new Color(255, 255, 255));
        panelPhai.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false),
                new EmptyBorder(0, 0, 0, 0)));

        JPanel khuVucThongTinKhach = createKhuVucThongTinKhach();
        panelPhai.add(khuVucThongTinKhach, BorderLayout.CENTER);

        return panelPhai;
    }

    // ====================================================================================
    // MODULE: 4. UI BUILDERS (TẠO CÁC KHU VỰC GIAO DIỆN CON)
    // ====================================================================================

    private JPanel createKhuVucTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.green);

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

        //test
        cbGaDi.setSelectedIndex(6);
        if (danhSachGa.size() > 1) {
            cbGaDen.setSelectedIndex(4);
        }

        JLabel lblNgayDi = new JLabel("Ngày đi");
        lblNgayDi.setPreferredSize(new Dimension(50, 25));
        panel.add(lblNgayDi);
        dateChooserNgayDi = new JDateChooser();
        dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayDi.setDate(new Date());
        dateChooserNgayDi.setPreferredSize(new Dimension(100, 25));
        panel.add(dateChooserNgayDi);

        btnTimChuyen = new JButton("Tìm chuyến");
        styleNutChinh(btnTimChuyen);
        btnTimChuyen.addActionListener(this);
        panel.add(btnTimChuyen);

        return panel;
    }

    private JScrollPane createKhuVucDanhSachChuyenTau() {
        // Tên chuyến tàu và thông tin sẽ được hiển thị trên các Panel tùy chỉnh
        pnlChuyenTau = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlChuyenTau.setOpaque(false);

        // Bọc pnlChuyenTau vào JScrollPane
        scrChuyenTau = new JScrollPane(pnlChuyenTau);
        scrChuyenTau.setBorder(null);

        // Thiết lập chỉ cuộn ngang, thanh cuộn dọc bị tắt (hoặc AS_NEEDED nếu muốn)
        scrChuyenTau.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrChuyenTau.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Giới hạn chiều cao và chiều rộng tối đa
        scrChuyenTau.setPreferredSize(new Dimension(400, 170)); // Cao hơn cho panel tùy chỉnh
        scrChuyenTau.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Khởi tạo trạng thái ban đầu
        pnlChuyenTau.add(new JLabel("Vui lòng chọn Ga đi, Ga đến và Ngày đi để tìm kiếm."));

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Danh sách chuyến tàu");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        scrChuyenTau.setBorder(title);

        return scrChuyenTau;
    }

    private JPanel createKhuVucChonToa() {
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
        txtTongSoKhach = new JTextField(2);
        txtTongSoKhach.setPreferredSize(new Dimension(60, 30));
        txtTongSoKhach.setMaximumSize(new Dimension(60, 30));
        JPanel pnlTongSoKhachControl = ToaPanelTangGiam("Tổng số khách", "1", txtTongSoKhach);
        pnlTongSoKhachControl.setMaximumSize(new Dimension(300, 40));

        topRow.add(pnlTongSoKhachControl);

        // --- THÊM NÚT VÉ ĐOÀN ---
        btnVeDoan = new JButton("Vé Đoàn");
        styleNutChinh(btnVeDoan);
        btnVeDoan.setBackground(new Color(255, 165, 0)); // Màu cam nổi bật
        btnVeDoan.addActionListener(this);
        topRow.add(btnVeDoan);
        panel.add(topRow);

        // --- KHU VỰC CHỌN TOA ---
        pnlToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        pnlToa.setOpaque(false);

        // Bọc pnlToa (nơi các nút được thêm vào) vào JScrollPane
        JScrollPane scrToa = new JScrollPane(pnlToa);
        scrToa.setBorder(null);
        scrToa.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrToa.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Chỉ cuộn ngang

        // Giới hạn chiều cao của JScrollPane để nó không chiếm quá nhiều không gian
        scrToa.setPreferredSize(new Dimension(550, 80)); // Đủ cao cho 1 hàng nút + padding
        scrToa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Thêm Label "Chọn toa:" ra ngoài ScrollPane để Label luôn hiển thị
        JPanel rowToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        rowToa.add(new JLabel("Chọn toa:"));
        rowToa.add(scrToa);
        rowToa.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(rowToa);
//        datCanhKhuVuc(panel);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        return panel;
    }

    private void datCanhKhuVuc(JPanel panel) {
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBackground(Color.green);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
    }

    private JPanel createKhuVucChonViTriGhe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.red);

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

        // --- KHU VỰC ĐÃ THAY ĐỔI: Chỉ hiện tóm tắt số lượng ---
        JPanel pnlSummary = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlSummary.setOpaque(false);

        lblGheDaChonTong = new JLabel("Ghế đã chọn: 0/1"); // Khởi tạo với giá trị mặc định
        lblGheDaChonTong.setFont(lblGheDaChonTong.getFont().deriveFont(Font.BOLD, 14f));
        lblGheDaChonTong.setForeground(COLOR_BLUE_LIGHT);

        pnlSummary.add(lblGheDaChonTong);
        pnlSummary.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(pnlSummary);
        panel.add(Box.createVerticalStrut(5));

        capNhatDanhSachGheDaChonUI(); // Lệnh này vẫn giữ lại để cập nhật summary khi khởi tạo

        return panel;
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
        summaryPanel.add(new JLabel("Đã chọn: X/Y")); // Label này cần được cập nhật thực tế

        lblTotalPrice = new JLabel("Tổng tiền vé: 0 VNĐ");
        lblTotalPrice.setFont(lblTotalPrice.getFont().deriveFont(Font.BOLD, 14f));
        lblTotalPrice.setForeground(new Color(255, 165, 0));
        summaryPanel.add(lblTotalPrice);

        fullSummary.add(summaryPanel, BorderLayout.EAST);

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);

        footerPanel.add(fullSummary);
        footerPanel.add(buttonPanel);

        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }


    // ====================================================================================
    // MODULE: 5. LOGIC XỬ LÝ DỮ LIỆU CHÍNH (DATA PROCESSING LOGIC)
    // ====================================================================================

    // --- 5.1. LOGIC TÌM KIẾM CHUYẾN TÀU VÀ NẠP DỮ LIỆU ---

    private void timKiemChuyenTau() {
        Ga gaDiSelected = (Ga) cbGaDi.getSelectedItem();
        Ga gaDenSelected = (Ga) cbGaDen.getSelectedItem();

        String tenGaDi =  gaDiSelected.getTenGa();
        String tenGaDen = gaDenSelected.getTenGa();

        Date date = dateChooserNgayDi.getDate();

        String ngayDiSQL = SQL_DATE_FORMAT.format(date);

        ChuyenTauDao dao = new ChuyenTauDao();
        System.out.println("Tìm chuyến tàu từ " + tenGaDi + " đến " + tenGaDen + " vào ngày " + ngayDiSQL);
        ketQua = dao.timChuyenTauTheoGaVaNgayDi(tenGaDi, tenGaDen, ngayDiSQL);

        if (ketQua == null || ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy chuyến tàu nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }

        taoDanhSachChuyenTauPanel(ketQua);
        // =============================================
    }

    private void taoDanhSachChuyenTauPanel(List<ChuyenTau> danhSach) {
        pnlChuyenTau.removeAll();
        // Thiết lập lại dữ liệu cũ
        maChuyenTauHienTai = null;
        pnlSoDoGhe.removeAll();
        pnlSoDoGhe.add(new JLabel("Vui lòng chọn chuyến tàu để chọn toa."));
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
        pnlToa.removeAll();
        pnlToa.revalidate();
        pnlToa.repaint();


        if (danhSach == null || danhSach.isEmpty()) {
            pnlChuyenTau.add(new JLabel("Không tìm thấy chuyến tàu nào phù hợp."));
        } else {
            for (int i = 0; i < danhSach.size(); i++) {
                ChuyenTau ct = danhSach.get(i);

                String ngayDiHienThi = ct.getNgayKhoiHanh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                String gioDiHienThi = ct.getGioKhoiHanh().format(TIME_FORMATTER);

                // Lấy ngày đến dự kiến
                String ngayDenHienThi = ct.getNgayDenDuKien().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String gioDenHienThi = ct.getGioDenDuKien().format(TIME_FORMATTER);

                JPanel pnlChuyenTauNut = taoNutChuyenTauVeSoDo(
                        ct.getMaChuyenTau(),
                        ngayDiHienThi,
                        gioDiHienThi,
                        ngayDenHienThi,
                        gioDenHienThi
                );


                final int index = i;
                pnlChuyenTauNut.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        xuLyChonChuyenTauPanel(pnlChuyenTauNut, ct, index);
                    }
                });

                pnlChuyenTau.add(pnlChuyenTauNut);
            }
        }

        pnlChuyenTau.revalidate();
        pnlChuyenTau.repaint();
    }

    private JPanel taoNutChuyenTauVeSoDo(String maChuyen, String ngayDi, String gioDi, String ngayDen, String gioDen) {

        // 1. Tạo đối tượng VeSoDoTau (Cơ sở đồ họa)
        // Truyền thông tin giờ đi/ngày đi (đã định dạng) vào constructor
        String thoiGianDiHienThi = ngayDi + " " + gioDi;
        String thoiGianDenHienThi = ngayDen + " "+ gioDen; // Thường cần thời gian đến từ CSDL

        String maTau = maChuyen.split("-")[0]; // Ví dụ: SE8-M1 -> SE8

        VeSoDoTau soDoTauPanel = new VeSoDoTau(maTau, thoiGianDiHienThi, thoiGianDenHienThi);

        // 2. Tạo Container đóng vai trò là NÚT (Sử dụng JPanel đơn giản)
        JPanel nutChuyenTauContainer = new JPanel(new BorderLayout());
        nutChuyenTauContainer.add(soDoTauPanel, BorderLayout.CENTER);




        // 3. Định dạng Panel để mô phỏng trạng thái nút (Default State)
        nutChuyenTauContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        nutChuyenTauContainer.setBackground(Color.WHITE);

        // Đặt kích thước để Panel phù hợp
        nutChuyenTauContainer.setPreferredSize(soDoTauPanel.getPreferredSize());

        // --- Lắng nghe sự kiện (Đóng vai trò như ActionListener) ---
        nutChuyenTauContainer.addMouseListener(new MouseAdapter() {
            private Color originalBackground;
            private Border originalBorder;

            @Override
            public void mouseEntered(MouseEvent e) {
                originalBackground = nutChuyenTauContainer.getBackground();
                originalBorder = nutChuyenTauContainer.getBorder();
                nutChuyenTauContainer.setBackground(new Color(220, 220, 220)); // Hiệu ứng hover
                nutChuyenTauContainer.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (nutChuyenTauContainer != lastSelectedChuyenTauPanel) {
                    nutChuyenTauContainer.setBackground(originalBackground);
                    nutChuyenTauContainer.setBorder(originalBorder);
                }
            }
        });

        return nutChuyenTauContainer;
    }

    // --- Hàm xử lý sự kiện chọn chuyến tàu (Tương tự xuLyChonToa) ---
    private void xuLyChonChuyenTauPanel(JPanel currentPanel, ChuyenTau chuyenTau, int index) {

        // 1. Reset trạng thái chọn của Panel cũ
        if (lastSelectedChuyenTauPanel != null) {
            lastSelectedChuyenTauPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
            lastSelectedChuyenTauPanel.setBackground(Color.WHITE);
        }

        // 2. Đặt trạng thái chọn cho Panel hiện tại
        currentPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 3, true)); // Viền xanh dày hơn
       // currentPanel.setBackground(); // Đổi màu thành màu cam đậm khi chọn
        currentPanel.setBackground(Color.orange);

        lastSelectedChuyenTauPanel = currentPanel; // Lưu Panel đã chọn

        // 3. Xử lý logic nghiệp vụ
        String maChuyenTauMoi = chuyenTau.getMaChuyenTau();

        if (!maChuyenTauMoi.equals(maChuyenTauHienTai)) {
            danhSachGheDaChon.clear();
            danhSachKhachHang.clear();
            danhSachGiaVe.clear();
            lastSelectedToaButton = null;
            maToaHienTai = null;
            seatButtonsMap.clear();
            tatCaChoDatToaHienTai.clear();

            capNhatDanhSachGheDaChonUI();
            capNhatThongTinKhachUI();

            pnlToa.removeAll();
            pnlToa.revalidate();
            pnlToa.repaint();

            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Vui lòng chọn Toa."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
        }

        maChuyenTauHienTai = maChuyenTauMoi;
        hienThiDanhSachToaTau(chuyenTau.getMaTau());
    }


    private void hienThiDanhSachToaTau(String maTau) {
        List<Toa> danhSachToa = new ArrayList<>();
        try {
            ToaDAO toaTauDAO = new ToaDAO();
            danhSachToa = toaTauDAO.layToaTheoMaTau(maTau);
            System.out.println(danhSachToa);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách toa tàu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        napNutToa(danhSachToa);
    }

    public void napNutToa(List<Toa> danhSachToa) {
        pnlToa.removeAll();

        for (Toa toa : danhSachToa) {
            String soThuTuToa = laySoThuTuToa(toa.getMaToa());
            String text = "Toa " + soThuTuToa + "\n" + toa.getLoaiToa();
            JButton btnToa = taoNutToa(text, null, null);
            btnToa.addActionListener(e -> xuLyChonToa(btnToa, toa.getMaToa()));
            pnlToa.add(btnToa);// Dòng này thêm nút vào panel
        }
        pnlToa.revalidate();
        pnlToa.repaint();
    }


    // --- 5.2. LOGIC TÍNH TOÁN & CẬP NHẬT TRẠNG THÁI ---

    private void capNhatSoLuongYeuCau() {
        // Chỉ lấy giá trị từ trường TỔNG SỐ KHÁCH
        int tongSoKhachMoi = parseTextFieldToInt(txtTongSoKhach);

        // Cảnh báo nếu số ghế đã chọn vượt quá tổng số khách mới
        if (danhSachGheDaChon != null && danhSachGheDaChon.size() > tongSoKhachMoi) {
            JOptionPane.showMessageDialog(null,
                    "Số lượng ghế đã chọn (" + danhSachGheDaChon.size() + ") vượt quá Tổng số khách mới (" + tongSoKhachMoi + "). Vui lòng hủy chọn bớt.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateKhachRecord(String maCho, String newValue, String fieldName) {
        ChiTietKhach original = danhSachKhachHang.get(maCho);
        if (original == null) return;

        ChiTietKhach updated = switch (fieldName) {
            case "hoTen" -> original.withHoTen(newValue);
            case "cccd" -> original.withCccd(newValue);
            case "sdt" -> original.withSdt(newValue);
            case "ngaySinh" -> original.withNgaySinh(newValue);
            default -> original;
        };
        danhSachKhachHang.put(maCho, updated);
    }

    private void xuLyNgaySinhThayDoi(String maCho, String ngaySinhText, JLabel giaLabel) {
        try {
            int age = calculateAge(ngaySinhText);

            // 1. Gợi ý loại vé dựa trên tuổi mới
            String suggestedMaLoaiVe = suggestLoaiVeByAge(age);

            ChiTietKhach khach = danhSachKhachHang.get(maCho);
            if (khach == null) return;

            if (!khach.maLoaiVe().equals(suggestedMaLoaiVe)) {
                // 2. Cập nhật loại vé nếu tuổi mới gợi ý loại khác
                ChiTietKhach updatedKhach = khach.withMaLoaiVe(suggestedMaLoaiVe);
                danhSachKhachHang.put(maCho, updatedKhach);

                // Cập nhật lại UI để ComboBox loại vé được chọn đúng
                capNhatThongTinKhachUI();

                // Sau đó tính lại giá
                long gia = tinhGiaVeTau(updatedKhach.choDat(), updatedKhach.maLoaiVe());
                danhSachGiaVe.put(maCho, gia);
            } else {
                // 3. Nếu loại vé không đổi, chỉ tính lại giá (để cập nhật giaLabel nếu có)
                long gia = tinhGiaVeTau(khach.choDat(), khach.maLoaiVe());
                danhSachGiaVe.put(maCho, gia);
                giaLabel.setText(formatVnd(gia));
            }
            capNhatTongTienUI();
        } catch (Exception ex) {
            System.err.println("Lỗi tính lại giá vé sau khi nhập ngày sinh: " + ex.getMessage());
        }
    }

    private void xuLyNhapNhanhKhachHang(JTextField cccdField, JTextField hoTenField,
                                        JTextField sdtField, JTextField ngaySinhField, String maCho) {
        String cccd = cccdField.getText().trim();
        if (cccd.isEmpty()) return;

        try {
            KhachHang kh = khachHangDAO.getKhachHangByCccd(cccd);

            if (kh != null) {
                hoTenField.setText(kh.getHoTen() != null ? kh.getHoTen() : "");
                sdtField.setText(kh.getSdt() != null ? kh.getSdt() : "");

                String ngaySinhStr = "";
                if (kh.getNgaySinh() != null) {
                    ngaySinhStr = kh.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                ngaySinhField.setText(ngaySinhStr);

                ChiTietKhach original = danhSachKhachHang.get(maCho);
                if (original != null) {
                    ChiTietKhach updated = original
                            .withHoTen(hoTenField.getText())
                            .withCccd(cccd)
                            .withSdt(sdtField.getText())
                            .withNgaySinh(ngaySinhStr);

                    danhSachKhachHang.put(maCho, updated);

                    JLabel finalGiaLabel = giaLabelMap.get(maCho);

                    if (finalGiaLabel != null) {
                        xuLyNgaySinhThayDoi(maCho, ngaySinhStr, finalGiaLabel);
                    }
                    capNhatThongTinKhachUI();
                }
                System.out.println("Nhập nhanh thành công cho CCCD: " + cccd);

            } else {
                System.out.println("Không tìm thấy khách hàng với CCCD: " + cccd);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(cccdField, "Lỗi khi truy vấn CSDL để tìm khách hàng: " + e.getMessage(), "Lỗi Nhập nhanh", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean kiemTraHopLeLoaiVeTheoNgaySinh(KhachHang khach, String maLoaiVeDaChon) {
        int tuoi = khach.getTuoi(); // Tự động tính toán từ ngày hiện tại

        return switch (maLoaiVeDaChon) {
            case "VT02" -> tuoi <= 17; // Trẻ em (tuổi <= 17)
            case "VT03" -> tuoi >= 60; // Người cao tuổi (tuổi >= 60)
            case "VT01" -> tuoi > 17 && tuoi < 60; // Người lớn
            default -> true;
        };
    }

    // --- 5.3. LOGIC XỬ LÝ SƠ ĐỒ GHẾ ---

    private void xuLyChonToa(JButton currentButton, String maToa) {
        maToaHienTai = maToa;

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

    private void xuLyChonGhe(JButton btnCho, ChoDat cho) {
        String maCho = cho.getMaCho();

        int tongSoKhachYeuCau = parseTextFieldToInt(txtTongSoKhach);

        if (danhSachGheDaChon.containsKey(maCho)) {
            // --- Logic Hủy chọn ghế ---
            danhSachGheDaChon.remove(maCho);
            danhSachGiaVe.remove(maCho);
            danhSachKhachHang.remove(maCho);
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

            ChiTietKhach chiTietKhach = new ChiTietKhach(
                    cho,
                    MA_VE_NL,
                    "",
                    "",
                    "",
                    ""
            );

            try {
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

            danhSachKhachHang.put(maCho, chiTietKhach);
        }

        capNhatDanhSachGheDaChonUI();
        capNhatThongTinKhachUI();
        capNhatTongTienUI();
    }

    // ====================================================================================
    // MODULE: 6. LOGIC VALIDATION
    // ====================================================================================

    private void showValidationError(JComponent input, JLabel errorLabel, String message) {
        input.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        errorLabel.setText(message);
        input.setToolTipText(message);
    }

    private void clearValidationError(JComponent input, JLabel errorLabel) {
        input.setBorder(UIManager.getBorder("TextField.border"));
        errorLabel.setText(" ");
        input.setToolTipText(null);
    }

    private boolean validateAllInputs() {
        if (!enableValidation) return true;

        boolean allValid = true;

        for (String maCho : danhSachKhachHang.keySet()) {
            JTextField hoTenField = inputFieldsMap.get(maCho + "_hoTen");
            JLabel hoTenErrorLabel = errorLabelsMap.get(maCho + "_hoTen");

            JTextField cccdField = inputFieldsMap.get(maCho + "_cccd");
            JLabel cccdErrorLabel = errorLabelsMap.get(maCho + "_cccd");

            JTextField ngaySinhField = inputFieldsMap.get(maCho + "_ngaySinh");
            JLabel ngaySinhErrorLabel = errorLabelsMap.get(maCho + "_ngaySinh");

            JTextField sdtField = inputFieldsMap.get(maCho + "_sdt");
            JLabel sdtErrorLabel = errorLabelsMap.get(maCho + "_sdt");

            // --- 1. Kiểm tra Họ tên (BẮT BUỘC) ---
            if (hoTenField != null && hoTenField.getText().trim().isEmpty()) {
                showValidationError(hoTenField, hoTenErrorLabel, "Họ và tên không được để trống.");
                allValid = false;
            } else if (hoTenField != null) {
                clearValidationError(hoTenField, hoTenErrorLabel);
            }

            // --- 2. Kiểm tra CCCD (BẮT BUỘC & Định dạng) ---
            String cccd = cccdField != null ? cccdField.getText().trim() : "";
            if (cccd.isEmpty()) {
                showValidationError(cccdField, cccdErrorLabel, "CCCD không được để trống.");
                allValid = false;
            } else if (!cccd.matches("^\\d{12}$")) {
                showValidationError(cccdField, cccdErrorLabel, "CCCD phải có đúng 12 chữ số.");
                allValid = false;
            } else {
                clearValidationError(cccdField, cccdErrorLabel);
            }

            // --- 3. Kiểm tra Ngày sinh (BẮT BUỘC & Định dạng/Tuổi) ---
            String ngaySinh = ngaySinhField != null ? ngaySinhField.getText().trim() : "";
            if (ngaySinh.isEmpty()) {
                showValidationError(ngaySinhField, ngaySinhErrorLabel, "Ngày sinh không được để trống.");
                allValid = false;
            } else {
                try {
                    Date dob = INPUT_DATE_FORMAT.parse(ngaySinh);
                    if (dob.after(new Date())) {
                        showValidationError(ngaySinhField, ngaySinhErrorLabel, "Ngày sinh không được ở tương lai.");
                        allValid = false;
                    } else if (calculateAge(ngaySinh) < 0) {
                        showValidationError(ngaySinhField, ngaySinhErrorLabel, "Ngày sinh không hợp lệ.");
                        allValid = false;
                    } else {
                        clearValidationError(ngaySinhField, ngaySinhErrorLabel);
                    }
                } catch (ParseException e) {
                    showValidationError(ngaySinhField, ngaySinhErrorLabel, "Ngày sinh phải theo định dạng dd/MM/yyyy.");
                    allValid = false;
                }
            }

            // --- 4. Kiểm tra SĐT (Tùy chọn: Định dạng nếu có) ---
            String sdt = sdtField != null ? sdtField.getText().trim() : "";
            if (!sdt.isEmpty() && !sdt.matches("^0\\d{9}$")) {
                showValidationError(sdtField, sdtErrorLabel, "SĐT gồm 10 số, bắt đầu 0.");
                allValid = false;
            } else if (sdtField != null) {
                clearValidationError(sdtField, sdtErrorLabel);
            }
        }
        return allValid;
    }


    // ====================================================================================
    // MODULE: 7. CẬP NHẬT GIAO DIỆN KHÁCH HÀNG (UI UPDATERS)
    // ====================================================================================

    private JPanel createKhachPanel(ChiTietKhach khach) {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(true);

        // ĐẶT MÀU TEST: PANEL CHÍNH CỦA KHÁCH HÀNG (Màu nền nhẹ)
        panel.setBackground(new Color(230, 240, 255));

        // Loại bỏ border bên trái (Giá trị thứ 2 = 0), giữ border phân tách dưới (1)
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setName(khach.choDat().getMaCho());

        String maCho = khach.choDat().getMaCho();
        String soCho = khach.choDat().getSoCho();
        String soThuTuToa = laySoThuTuToa(khach.choDat().getMaToa());
        String loaiKhachHienThi = getTenLoaiVeHienThi(khach.maLoaiVe());

        final int FIELD_HEIGHT = 20;
        // --- KHAI BÁO KÍCH THƯỚC CỐ ĐỊNH CHO CĂN CHỈNH BOXLAYOUT ---
        final int LABEL_WIDTH = 90; // Chiều rộng cố định cho Label (để thẳng hàng)
        final int FIELD_WIDTH = 100; // Chiều rộng cố định cho Text Field
        final int GAP_WIDTH = 15; // Khoảng cách giữa hai cặp input

        final Dimension FIXED_COMBO_SIZE = new Dimension(130, FIELD_HEIGHT);
        final Dimension FIXED_LABEL_SIZE = new Dimension(LABEL_WIDTH, FIELD_HEIGHT); // Kích thước cố định cho Label
        final Dimension FIXED_FIELD_SIZE = new Dimension(FIELD_WIDTH, FIELD_HEIGHT); // Kích thước cố định cho Field

        final Font ERROR_FONT = new Font("Segoe UI", Font.ITALIC, 11);
        final Color ERROR_COLOR = Color.RED;

        // --- 1. Header Row (Ghế, Loại vé, Giá) ---
        JPanel pnlGhe_LoaiVe_GiaVe = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        pnlGhe_LoaiVe_GiaVe.setOpaque(true);
        pnlGhe_LoaiVe_GiaVe.setBackground(new Color(255, 220, 180));
        pnlGhe_LoaiVe_GiaVe.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel maGheLabel = new JLabel("Chỗ: " + soCho + " / Toa: " + soThuTuToa);
        maGheLabel.setFont(maGheLabel.getFont().deriveFont(Font.BOLD));
        pnlGhe_LoaiVe_GiaVe.add(maGheLabel);

        JComboBox<String> cbLoaiKhach = new JComboBox<>(getLoaiVeOptions());
        cbLoaiKhach.setSelectedItem(loaiKhachHienThi);
        cbLoaiKhach.setPreferredSize(FIXED_COMBO_SIZE);
        cbLoaiKhach.setMaximumSize(FIXED_COMBO_SIZE);
        cbLoaiKhach.addActionListener(e -> {
            String maMoi = getMaLoaiVeFromHienThi((String) cbLoaiKhach.getSelectedItem());
            ChiTietKhach updatedKhach = khach.withMaLoaiVe(maMoi);
            danhSachKhachHang.put(maCho, updatedKhach);
            try {
                long gia = tinhGiaVeTau(updatedKhach.choDat(), updatedKhach.maLoaiVe());
                danhSachGiaVe.put(maCho, gia);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể tính lại giá: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                danhSachGiaVe.remove(maCho);
            }
            capNhatThongTinKhachUI();
            capNhatTongTienUI();
        });
        pnlGhe_LoaiVe_GiaVe.add(cbLoaiKhach);

        JLabel giaLabel = new JLabel("...");
        giaLabel.setFont(giaLabel.getFont().deriveFont(Font.BOLD, 14f));
        giaLabel.setForeground(COLOR_BLUE_LIGHT);

        // Đảm bảo căn chỉnh Y (trục dọc)
        giaLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        giaLabelMap.put(maCho, giaLabel);

        pnlGhe_LoaiVe_GiaVe.add(giaLabel);

        panel.add(pnlGhe_LoaiVe_GiaVe, BorderLayout.NORTH);

        // --- 2. Input Fields (Đã chuyển sang BoxLayout lồng nhau) ---

        // Container bao bọc tất cả inputs và canh chỉnh lề trái
        JPanel pnlInputContainer = new JPanel();
        pnlInputContainer.setLayout(new BoxLayout(pnlInputContainer, BoxLayout.Y_AXIS));
        pnlInputContainer.setOpaque(true);
        pnlInputContainer.setBackground(new Color(240, 240, 240));
        pnlInputContainer.setBorder(new EmptyBorder(5, 5, 5, 5)); // Padding cho khu vực input
        pnlInputContainer.setAlignmentX(Component.LEFT_ALIGNMENT);


        // --- Hàng 1: Họ tên và Ngày sinh (BoxLayout.X_AXIS) ---
        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.setOpaque(true);
        row1.setBackground(new Color(240, 240, 240));
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Cặp 1: Họ tên
        JLabel lblHoTen = new JLabel("Họ và tên*:");
        lblHoTen.setPreferredSize(FIXED_LABEL_SIZE);
        lblHoTen.setMaximumSize(FIXED_LABEL_SIZE);
        JTextField hoTenField = new JTextField(khach.hoTen());
        hoTenField.setPreferredSize(FIXED_FIELD_SIZE);
        hoTenField.setMaximumSize(FIXED_FIELD_SIZE);

        row1.add(lblHoTen);
        row1.add(hoTenField);
        row1.add(Box.createHorizontalStrut(GAP_WIDTH)); // Khoảng cách giữa 2 cặp

        // Cặp 2: Ngày sinh
        JLabel lblNgaySinh = new JLabel("Ngày sinh*:");
        lblNgaySinh.setPreferredSize(FIXED_LABEL_SIZE);
        lblNgaySinh.setMaximumSize(FIXED_LABEL_SIZE);
        JTextField ngaySinhField = new JTextField(khach.ngaySinh() != null ? khach.ngaySinh() : "");
        ngaySinhField.setPreferredSize(FIXED_FIELD_SIZE);
        ngaySinhField.setMaximumSize(FIXED_FIELD_SIZE);

        row1.add(lblNgaySinh);
        row1.add(ngaySinhField);
        row1.add(Box.createHorizontalGlue()); // Đẩy các thành phần sang trái

        pnlInputContainer.add(row1);
        pnlInputContainer.add(Box.createVerticalStrut(5)); // Khoảng cách giữa các hàng


        // --- Hàng 2: Số điện thoại và CCCD (BoxLayout.X_AXIS) ---
        JPanel row2 = new JPanel();
        row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
        row2.setOpaque(true);
        row2.setBackground(new Color(240, 240, 240));
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Cặp 1: Số điện thoại
        JLabel lblSdt = new JLabel("Số điện thoại:");
        lblSdt.setPreferredSize(FIXED_LABEL_SIZE);
        lblSdt.setMaximumSize(FIXED_LABEL_SIZE);
        JTextField sdtField = new JTextField(khach.sdt());
        sdtField.setPreferredSize(FIXED_FIELD_SIZE);
        sdtField.setMaximumSize(FIXED_FIELD_SIZE);

        row2.add(lblSdt);
        row2.add(sdtField);
        row2.add(Box.createHorizontalStrut(GAP_WIDTH)); // Khoảng cách giữa 2 cặp

        // Cặp 2: CCCD
        JLabel lblCccd = new JLabel("CCCD*:");
        lblCccd.setPreferredSize(FIXED_LABEL_SIZE);
        lblCccd.setMaximumSize(FIXED_LABEL_SIZE);
        JTextField cccdField = new JTextField(khach.cccd());
        cccdField.setPreferredSize(FIXED_FIELD_SIZE);
        cccdField.setMaximumSize(FIXED_FIELD_SIZE);

        row2.add(lblCccd);
        row2.add(cccdField);
        row2.add(Box.createHorizontalGlue()); // Đẩy các thành phần sang trái

        pnlInputContainer.add(row2);

        // Thêm Input Container vào Panel cha
        panel.add(pnlInputContainer, BorderLayout.CENTER);


        // --- 3. Error Row (Sử dụng lại FlowLayout, nhưng căn chỉnh bằng strusts) ---
        JPanel errorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        errorRow.setOpaque(true);

        errorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorRow.setBorder(new EmptyBorder(0, 5, 5, 5));

        JLabel hoTenErrorLabel = new JLabel(" "); hoTenErrorLabel.setFont(ERROR_FONT); hoTenErrorLabel.setForeground(ERROR_COLOR); errorLabelsMap.put(maCho + "_hoTen", hoTenErrorLabel);
        JLabel ngaySinhErrorLabel = new JLabel(" "); ngaySinhErrorLabel.setFont(ERROR_FONT); ngaySinhErrorLabel.setForeground(ERROR_COLOR); errorLabelsMap.put(maCho + "_ngaySinh", ngaySinhErrorLabel);
        JLabel sdtErrorLabel = new JLabel(" "); sdtErrorLabel.setFont(ERROR_FONT); sdtErrorLabel.setForeground(ERROR_COLOR); errorLabelsMap.put(maCho + "_sdt", sdtErrorLabel);
        JLabel cccdErrorLabel = new JLabel(" "); cccdErrorLabel.setFont(ERROR_FONT); cccdErrorLabel.setForeground(ERROR_COLOR); errorLabelsMap.put(maCho + "_cccd", cccdErrorLabel);

        // Lỗi HoTen (Cặp 1)
        errorRow.add(Box.createHorizontalStrut(LABEL_WIDTH + 15));
        errorRow.add(hoTenErrorLabel);

        // Khoảng trống giữa HoTen và NgaySinh (Gap + Độ rộng Label)
        errorRow.add(Box.createHorizontalStrut(GAP_WIDTH + LABEL_WIDTH));
        errorRow.add(ngaySinhErrorLabel);

        // Lỗi SĐT (Cặp 2 - Cặp 3)
        // Khoảng trống = (Khoảng cách giữa 2 cặp) + (Độ rộng Label) - (FlowLayout Gap cũ 15)
        errorRow.add(Box.createHorizontalStrut(GAP_WIDTH + LABEL_WIDTH - 15));
        errorRow.add(sdtErrorLabel);

        // Lỗi CCCD (Cặp 3 - Cặp 4)
        errorRow.add(Box.createHorizontalStrut(GAP_WIDTH + LABEL_WIDTH));
        errorRow.add(cccdErrorLabel);


        // Lưu trữ các trường Field vào Map (Dùng biến đã khai báo trong logic BoxLayout)
        inputFieldsMap.put(maCho + "_hoTen", hoTenField);
        inputFieldsMap.put(maCho + "_ngaySinh", ngaySinhField);
        inputFieldsMap.put(maCho + "_sdt", sdtField);
        inputFieldsMap.put(maCho + "_cccd", cccdField);

        panel.add(errorRow, BorderLayout.SOUTH);

        // --- FOCUS LISTENERS ---
        hoTenField.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent evt) { updateKhachRecord(maCho, hoTenField.getText(), "hoTen"); }
        });

        sdtField.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent evt) { updateKhachRecord(maCho, sdtField.getText(), "sdt"); }
        });

        ngaySinhField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                updateKhachRecord(maCho, ngaySinhField.getText(), "ngaySinh");
                JLabel finalGiaLabel = giaLabelMap.get(maCho);
                if (finalGiaLabel != null) {
                    xuLyNgaySinhThayDoi(maCho, ngaySinhField.getText(), finalGiaLabel);
                }
            }
        });

        cccdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                updateKhachRecord(maCho, cccdField.getText(), "cccd");
                xuLyNhapNhanhKhachHang(cccdField, hoTenField, sdtField, ngaySinhField, maCho);
            }
        });


        // --- Cập nhật giá ---
        Long giaTinh = danhSachGiaVe.get(maCho);
        if (giaTinh != null) { giaLabel.setText(formatVnd(giaTinh)); }
        else { try { long gia = tinhGiaVeTau(khach.choDat(), khach.maLoaiVe()); danhSachGiaVe.put(maCho, gia); giaLabel.setText(formatVnd(gia)); } catch (Exception ex) { giaLabel.setText("Lỗi giá"); giaLabel.setForeground(Color.RED); } }

        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private void capNhatThongTinKhachUI() {
        if (thongTinKhachScrollPane == null) {
            System.out.println("Lỗi: thongTinKhachScrollPane chưa được khởi tạo.");
            return;
        }
        JPanel infoScrollPanel = (JPanel) thongTinKhachScrollPane.getViewport().getView();
        infoScrollPanel.removeAll();

        List<ChiTietKhach> danhSachChiTiet = new ArrayList<>(danhSachKhachHang.values());

        if (danhSachChiTiet.isEmpty()) {
            infoScrollPanel.add(new JLabel("Chưa có chỗ nào được chọn."));
            infoScrollPanel.add(Box.createVerticalGlue());
            infoScrollPanel.revalidate();
            infoScrollPanel.repaint();
            return;
        }

        for (ChiTietKhach khach : danhSachChiTiet) {
            JPanel khachPanel = createKhachPanel(khach);
            infoScrollPanel.add(khachPanel);
        }

        infoScrollPanel.add(Box.createVerticalGlue());
        infoScrollPanel.revalidate();
        infoScrollPanel.repaint();
    }

    private void capNhatDanhSachGheDaChonUI() {
        // Lấy số ghế đã chọn
        int soGheDaChon = danhSachGheDaChon.size();
        // Lấy số khách yêu cầu
        int soKhachYeuCau = parseTextFieldToInt(txtTongSoKhach);
        // Kiểm tra để tránh lỗi nếu lblGheDaChonSummary chưa được khởi tạo (chỉ xảy ra khi gọi quá sớm)
        if (lblGheDaChonTong == null) return;
        // Cập nhật JLabel tóm tắt
        String summaryText = String.format("Chỗ đã chọn: %d/%d", soGheDaChon, soKhachYeuCau);
        lblGheDaChonTong.setText(summaryText);

        // Tùy chỉnh màu sắc để làm nổi bật (tùy chọn)
        if (soGheDaChon == soKhachYeuCau && soKhachYeuCau > 0) {
            lblGheDaChonTong.setForeground(new Color(39, 174, 96)); // Màu xanh lá khi đủ
        } else if (soGheDaChon > soKhachYeuCau) {
            lblGheDaChonTong.setForeground(Color.RED); // Màu đỏ nếu vượt quá yêu cầu
        } else {
            lblGheDaChonTong.setForeground(COLOR_BLUE_LIGHT);
        }


    }

    private void capNhatTongTienUI() {
        long total = 0L;
        for (Long v : danhSachGiaVe.values()) total += v == null ? 0L : v;
        lblTotalPrice.setText("Tổng tiền vé: " + formatVnd(total));
    }


    // ====================================================================================
    // MODULE: 8. HÀM TÍNH TOÁN GIÁ VÀ TUỔI (UTILITY CALCULATORS)
    // ====================================================================================

    private static int calculateAge(String dobString) {
        if (dobString == null || dobString.trim().isEmpty()) return 0;
        try {
            Date birthDate = INPUT_DATE_FORMAT.parse(dobString);
            Calendar dob = Calendar.getInstance();
            dob.setTime(birthDate);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            return -1;
        }
    }

    private String suggestLoaiVeByAge(int age) {
        if (age == -1 || age == 0) return MA_VE_NL;
        if (age <= 17) return MA_VE_TE;
        if (age >= 60) return MA_VE_NCT;
        return MA_VE_NL;
    }

    private long roundUpToNextTen(long value) {
        return ((value + 9) / 10) * 10;
    }

    private long tinhGiaVeTau(ChoDat cho, String maLoaiVe) throws Exception {
        Ga gaDi = (Ga) cbGaDi.getSelectedItem();
        Ga gaDen = (Ga) cbGaDen.getSelectedItem();
        if (gaDi == null || gaDen == null) throw new Exception("Ga đi hoặc ga đến chưa được chọn.");

        long base = giaVeCoBanDAO.getGiaCoBan(gaDi.getMaGa(), gaDen.getMaGa());
        if (base <= 0) throw new Exception("Không tìm thấy giá cơ bản cho cặp ga.");

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


    // ====================================================================================
    // MODULE: 9. XỬ LÝ SỰ KIỆN (EVENT HANDLERS)
    // ====================================================================================

    @Override
    public void mouseClicked(MouseEvent e) {
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
        } else if (src == btbHuy) {
            huyBoDatVe();
        } else if (src == btnTiepTheo) {
            xuLyNutTiepTheo();
        }
        else if (src == btnVeDoan) { // <<< THÊM: Xử lý Vé Đoàn
            hienThiPopupVeDoan();
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

    private void xuLyNutTiepTheo() {
        int required = parseTextFieldToInt(txtTongSoKhach);

        if (required <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn số lượng khách hợp lệ.",
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (danhSachGheDaChon.size() != required) {
            JOptionPane.showMessageDialog(this,
                    "Số chỗ đã chọn (" + danhSachGheDaChon.size() + ") không khớp với Tổng số khách (" + required + ").",
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateAllInputs()) {
            JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra và điền đầy đủ, chính xác thông tin khách hàng.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (ChiTietKhach khach : danhSachKhachHang.values()) {
            if (khach.hoTen() == null || khach.hoTen().trim().isEmpty() ||
                    khach.cccd() == null || khach.cccd().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ Họ tên và CCCD cho tất cả " + required + " khách hàng.",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Window w = SwingUtilities.getWindowAncestor(this);

        if (w instanceof BanVeDashboard) {
            BanVeDashboard dashboard = (BanVeDashboard) w;

            ManHinhXacNhanBanVe confirmPanel = new ManHinhXacNhanBanVe(
                    danhSachGheDaChon,
                    danhSachKhachHang,
                    maChuyenTauHienTai,
                    dateChooserNgayDi.getDate(), // Lấy Date từ JDateChooser
                    new HashMap<>(danhSachGiaVe)
            );

            dashboard.themHoacCapNhatCard(confirmPanel, "xacNhanBanVe");
            dashboard.chuyenManHinh("xacNhanBanVe");

        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể tìm thấy cửa sổ Dashboard. Vui lòng chạy ứng dụng từ BanVeDashboard.",
                    "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
    // ====================================================================================
    // MODULE: 9. XỬ LÝ SỰ KIỆN (EVENT HANDLERS) - Thêm phương thức mới
    // ====================================================================================

    /**
     * Hiển thị Pop-up nhập thông tin vé đoàn và xử lý đặt chỗ tự động.
     */
    // --- Đặt phương thức này trong lớp ManHinhBanVe ---

    private void hienThiPopupVeDoan() {
        if (maChuyenTauHienTai == null || maChuyenTauHienTai.isEmpty() || maToaHienTai == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn chuyến tàu và toa tàu trước khi đặt vé đoàn.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window ownerWindow = SwingUtilities.getWindowAncestor(this);
        Frame ownerFrame = (ownerWindow instanceof Frame) ? (Frame) ownerWindow : null;

        // Sử dụng ModalityType cho khả năng tương thích hiện đại
        JDialog dialog = new JDialog(ownerFrame, "Đặt vé Đoàn/Từ File", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Panel chính
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Khu vực 1: Nhập tay ---
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Nhập thủ công (Dùng cho số lượng nhỏ)"));

        JTextField txtSoNguoi = new JTextField("30");
        JTextField txtHoTenTruongDoan = new JTextField();
        JTextField txtSdtTruongDoan = new JTextField();

        inputPanel.add(new JLabel("Số lượng người (*):"));
        inputPanel.add(txtSoNguoi);
        inputPanel.add(new JLabel("Tên Trưởng đoàn (*):"));
        inputPanel.add(txtHoTenTruongDoan);
        inputPanel.add(new JLabel("SĐT Trưởng đoàn (*):"));
        inputPanel.add(txtSdtTruongDoan);

        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // --- Khu vực 2: Tải File ---
        JButton btnTaiFile = new JButton("Tải lên File Khách hàng (.xlsx, .xls, .csv)");
        btnTaiFile.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTaiFileLuuY = new JLabel("Hoặc dùng chức năng nhập thủ công bên dưới.");
        lblTaiFileLuuY.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnTaiFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            // Thêm File Filter cho các định dạng được hỗ trợ
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "File Khách hàng (*.xlsx, *.xls, *.csv, *.txt)", "xlsx", "xls", "csv", "txt");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                List<String[]> duLieuKhachHang = docDuLieuKhachHangTuFile(selectedFile);

                if (duLieuKhachHang != null && !duLieuKhachHang.isEmpty()) {
                    // Gọi logic xử lý chính từ file
                    xuLyDatChoTuFile(duLieuKhachHang);
                    dialog.dispose();
                } else if (duLieuKhachHang != null && duLieuKhachHang.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "File không chứa dữ liệu khách hàng hợp lệ.", "Lỗi File", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainPanel.add(btnTaiFile);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lblTaiFileLuuY);

        dialog.add(mainPanel, BorderLayout.NORTH);

        // Panel nút bấm (Chỉ dành cho nhập thủ công)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDatVe = new JButton("Đặt vé Thủ công");
        JButton btnHuy = new JButton("Hủy");

        btnHuy.addActionListener(e -> dialog.dispose());

        btnDatVe.addActionListener(e -> {
            try {
                int soNguoi = Integer.parseInt(txtSoNguoi.getText().trim());
                String tenTruongDoan = txtHoTenTruongDoan.getText().trim();
                String sdtTruongDoan = txtSdtTruongDoan.getText().trim();

                if (soNguoi <= 0 || tenTruongDoan.isEmpty() || sdtTruongDoan.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin bắt buộc và số lượng người > 0.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Số lượng người phải là số nguyên hợp lệ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnDatVe);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Logic chính xử lý việc chọn chỗ tự động cho vé đoàn và cập nhật UI.
     */
    private List<String[]> docDuLieuKhachHangTuFile(File file) {
        List<String[]> danhSachKhachHang = new ArrayList<>();

        // Yêu cầu: Đảm bảo có 5 cột dữ liệu chính
        // [0] HoTen, [1] CCCD, [2] SĐT, [3] NgaySinh, [4] MaLoaiVe
        final int SO_COT_CAN_THIET = 5;

        String fileName = file.getName().toLowerCase();

        // === Logic đọc CSV/TXT (Giả lập) ===
        if (fileName.endsWith(".csv") || fileName.endsWith(".txt")) {
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) scanner.nextLine(); // Bỏ qua tiêu đề
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] data = line.split(",");

                    if (data.length >= SO_COT_CAN_THIET) {
                        String[] khachData = new String[SO_COT_CAN_THIET];
                        for (int i = 0; i < SO_COT_CAN_THIET; i++) {
                            khachData[i] = data[i].trim();
                        }
                        danhSachKhachHang.add(khachData);
                    }
                }
                return danhSachKhachHang;
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy file CSV: " + e.getMessage(), "Lỗi File", JOptionPane.ERROR_MESSAGE);
                return null;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi đọc file CSV: " + e.getMessage(), "Lỗi File", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        // === Logic đọc EXCEL (Apache POI) ===

        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            try (FileInputStream fileInputStream = new FileInputStream(file);


                 Workbook workbook = WorkbookFactory.create(fileInputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();

                if (rowIterator.hasNext()) rowIterator.next(); // Bỏ qua dòng tiêu đề

                DataFormatter formatter = new DataFormatter();

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    // Kiểm tra ô đầu tiên để bỏ qua dòng trống
                    if (row.getCell(0) == null || formatter.formatCellValue(row.getCell(0)).trim().isEmpty()) {
                        continue;
                    }

                    String[] data = new String[SO_COT_CAN_THIET];
                    boolean duLieuHopLe = true;

                    for (int i = 0; i < SO_COT_CAN_THIET; i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String cellValue = formatter.formatCellValue(cell).trim();

                        if (i <= 2 && cellValue.isEmpty()) { // HoTen, CCCD, SĐT không được trống
                            duLieuHopLe = false;
                            break;
                        }
                        data[i] = cellValue;
                    }

                    if (duLieuHopLe) {
                        danhSachKhachHang.add(data);
                    }
                }
                return danhSachKhachHang;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi đọc file Excel: Vui lòng đảm bảo file không mở và định dạng đúng.",
                        "Lỗi Apache POI", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return null;
            }
        }

        // Nếu không phải định dạng được hỗ trợ
        JOptionPane.showMessageDialog(this, "Định dạng file không được hỗ trợ (.xlsx, .xls, .csv, .txt).", "Lỗi File", JOptionPane.ERROR_MESSAGE);
        return null;
    }

// --- Đặt phương thức này trong lớp ManHinhBanVe ---

    private void xuLyDatChoTuFile(List<String[]> duLieuKhachHangTuFile) {

        int soNguoi = duLieuKhachHangTuFile.size();
        if (soNguoi == 0) {
            JOptionPane.showMessageDialog(this, "File không chứa khách hàng nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        this.danhSachGheDaChon.clear();
        this.danhSachKhachHang.clear(); // <<< Dùng this.danhSachKhachHang
        this.danhSachGiaVe.clear();

        // 1. Kiểm tra số lượng ghế trống có sẵn
        List<ChoDat> choTrong = tatCaChoDatToaHienTai.values().stream()
                .filter(cho -> !cho.isDaDat() && !danhSachGheDaChon.containsKey(cho.getMaCho()))
                .limit(soNguoi)
                .toList();

        if (choTrong.size() < soNguoi) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ tìm thấy " + choTrong.size() + " chỗ trống trong toa này, nhưng cần " + soNguoi + " chỗ. Vui lòng chọn toa khác hoặc giảm số lượng.",
                    "Không đủ chỗ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Reset các lựa chọn cũ
        danhSachGheDaChon.clear();
        danhSachKhachHang.clear();
        danhSachGiaVe.clear();

        // 3. Tự động chọn ghế và điền thông tin từ file
        int count = 0;
        for (int i = 0; i < choTrong.size(); i++) {
            ChoDat cho = choTrong.get(i);
            String[] data = duLieuKhachHangTuFile.get(i); // [HoTen, CCCD, SĐT, NgaySinh, MaLoaiVe]
            String maCho = cho.getMaCho();

            // Kiểm tra loại vé hợp lệ
            String maLoaiVe = (data[4] != null && data[4].startsWith("VT")) ? data[4] : MA_VE_NL;

            // Tạo thông tin khách
            ChiTietKhach chiTietKhach = new ChiTietKhach(
                    cho,
                    maLoaiVe,  // MaLoaiVe
                    data[0],   // HoTen
                    data[1],   // CCCD
                    data[2],   // SĐT
                    data[3]    // NgaySinh (dd/MM/yyyy)
            );

            try {
                long gia = tinhGiaVeTau(cho, chiTietKhach.maLoaiVe());
                danhSachGiaVe.put(maCho, gia);
            } catch (Exception ex) {
                System.err.println("Lỗi tính giá cho ghế " + cho.getSoCho() + ": " + ex.getMessage());
                continue;
            }

            danhSachGheDaChon.put(maCho, cho);
            danhSachKhachHang.put(maCho, chiTietKhach);

            // Cập nhật trạng thái nút ghế
            JButton btnCho = seatButtonsMap.get(maCho);
            if (btnCho != null) {
                btnCho.setBackground(new Color(0, 123, 255));
                btnCho.setForeground(Color.WHITE);
            }
            count++;
        }

        // 4. Cập nhật giao diện
        txtTongSoKhach.setText(String.valueOf(soNguoi));

        capNhatDanhSachGheDaChonUI();
        capNhatThongTinKhachUI();
        capNhatTongTienUI();

        JOptionPane.showMessageDialog(this,
                "Đã đặt thành công " + count + " chỗ từ file. Vui lòng kiểm tra lại thông tin khách hàng.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }





    // ====================================================================================
    // MODULE: 10. HÀM HỖ TRỢ CHUNG (GENERAL UTILITIES)
    // ====================================================================================

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

    private void changeQuantity(JTextField field, int delta) {
        int currentValue = parseTextFieldToInt(field);
        int newValue = currentValue + delta;
        if (newValue < 0) newValue = 0;
        field.setText(String.valueOf(newValue));
        capNhatSoLuongYeuCau();
    }

    private int parseTextFieldToInt(JTextField field) {
        try {
            if (field.getText().trim().isEmpty()) return 0;
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void styleSpinButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
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


    private String layLoaiToa(String maToa) throws Exception {
        if (maToa == null || maChuyenTauHienTai == null) {
            throw new Exception("Thiếu Mã toa hoặc Mã chuyến tàu.");
        }

        String maTau = null;
        for (ChuyenTau ct : ketQua) {
            if (maChuyenTauHienTai.equals(ct.getMaChuyenTau())) {
                maTau = ct.getMaTau();
                break;
            }
        }
        if (maTau == null) throw new Exception("Không tìm thấy mã tàu cho chuyến tàu hiện tại.");

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

    private String formatVnd(long amount) {
        try {
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
            return nf.format(amount) + " VNĐ";
        } catch (Exception e) {
            return amount + " VNĐ";
        }
    }



    // ====================================================================================
    // MODULE: 11. XỬ LÝ SƠ ĐỒ GHẾ (SEAT MAP DRAWING)
    // ====================================================================================

    private static final Dimension SQUARE_SEAT_SIZE = new Dimension(47, 25);
    private static final Dimension BERTH_SEAT_SIZE = new Dimension(70, 45);

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

    private void veSoDoGiuongNam(List<ChoDat> danhSachChoDat){
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();
        tatCaChoDatToaHienTai.clear();

        pnlSoDoGhe.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlSoDoGhe.setOpaque(true);
        pnlSoDoGhe.setBackground(Color.WHITE);
        pnlSoDoGhe.setBorder(new EmptyBorder(10, 10, 10, 10));

        Map<String, List<ChoDat>> buongData = new LinkedHashMap<>();
        for (ChoDat cho : danhSachChoDat) {
            String soGhe = cho.getSoCho();
            String soBuong = soGhe.substring(0, soGhe.indexOf('T'));

            buongData.computeIfAbsent(soBuong, k -> new ArrayList<>()).add(cho);
        }

        final int ROWS_PER_BUONG = 3;
        final int COLUMNS_PER_BUONG = 2;

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        container.setOpaque(false);

        for (Map.Entry<String, List<ChoDat>> entry : buongData.entrySet()) {
            String soBuong = entry.getKey();
            List<ChoDat> choDats = entry.getValue();

            JPanel pnlBuong = new JPanel(new BorderLayout());
            pnlBuong.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                    "Khoang " + soBuong.substring(1)));
            pnlBuong.setOpaque(false);

            JPanel buongGrid = new JPanel(new GridLayout(ROWS_PER_BUONG, COLUMNS_PER_BUONG, 5, 5));
            buongGrid.setOpaque(false);
            buongGrid.setBorder(new EmptyBorder(5, 5, 5, 5));

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
    }


    // ====================================================================================
    // MODULE: 12. RESET DỮ LIỆU
    // ====================================================================================

    public void resetAllData() {
        danhSachGheDaChon.clear();
        danhSachKhachHang.clear();
        tatCaChoDatToaHienTai.clear();
        seatButtonsMap.clear();
        danhSachGiaVe.clear();
        inputFieldsMap.clear();
        errorLabelsMap.clear();

        maChuyenTauHienTai = null;
        maToaHienTai = null;
        lastSelectedToaButton = null;

        if (tableModel != null) {
            tableModel.setRowCount(0);
        }

        if (txtTongSoKhach != null) {
            txtTongSoKhach.setText("1"); // Reset số khách yêu cầu
        }

        SwingUtilities.invokeLater(() -> {
            capNhatDanhSachGheDaChonUI();

            if (pnlToa != null) {
                pnlToa.removeAll();
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


    // ====================================================================================
    // MODULE: 13. HÀM MAIN (CHẠY ĐỘC LẬP)
    // ====================================================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Bán vé (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ManHinhBanVeUpdatePanelChuyenTau(), BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}