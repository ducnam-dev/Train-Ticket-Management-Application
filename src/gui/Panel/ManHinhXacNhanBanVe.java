// java
package gui.Panel;

import dao.*;
//import dao.VeCuaBanVeDAO;
import entity.*;
import gui.MainFrame.BanVeDashboard;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * ManHinhXacNhanBanVe - phiên bản cập nhật: khuyến mãi đọc từ DB và hiển thị combobox.
 * Tên biến và phương thức bằng tiếng Việt không dấu.
 */
public class ManHinhXacNhanBanVe extends JPanel {
    private Map<String, ChoDat> danhSachGhe;
    private Map<String, Object> danhSachKhach;
    private final Map<String, Long> danhSachGiaVe;
    private String maChuyen;
    private Date ngayDi;

    private JTable tbKhach;
    private JTable tbHoaDon;
    private JLabel lblTongTien;
    private JComboBox<String> cbHinhThuc;
    private JTextField txtTienKhachDua;
    private JTextField txtTienThoiLai;

    // UI Khuyen mai
    private JComboBox<KhuyenMai> cbKhuyenMai;
    private JButton btnApDungKhuyenMai;
    private JButton btnXoaKhuyenMai;
    private JLabel lblThongTinKhuyenMai;

    //UI Hoa don
    private JLabel lblMaHoaDon; // FIX: Biến mới để hiển thị Mã HD
    private JLabel lblNguoiLapHD; // FIX: Biến mới để hiển thị Người lập
    private JLabel lblDienThoaiNV; // FIX: Biến mới để hiển thị SĐT NV
    private JLabel lblTenNguoiDat; // FIX: Tên người đặt
    private JLabel lblSdtNguoiDat; // FIX: SĐT người đặt
    private JLabel lblPhuongThucTT; // FIX: Phương thức thanh toán (trong phần UI Tổng tiền)


    // Khuyen mai dang ap dung
    private KhuyenMai khuyenMaiApDung = null;

    // DAO
    private KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final VeCuaBanVeDAO veDao = new VeCuaBanVeDAO();

    private static final Color MAU_XANH_BTN = new Color(0, 180, 110);
    private static final Color MAU_CAM_BTN = new Color(255, 140, 0);

    // Thêm dòng này để khai báo biến mock
    private static  String MA_NV_LAP_HD_MOCK = "NVBV0001";

    public ManHinhXacNhanBanVe() {
        this(null, null, null, null, null);
    }

    public ManHinhXacNhanBanVe(Map<String, ChoDat> danhSachGheDaChon,
                               Map<String, ?> danhSachKhachHang, // Nhận Map với giá trị generic
                               String maChuyenTau,
                               Date ngayDi,
                               Map<String, Long> danhSachGiaVe) {

        this.danhSachGhe = new LinkedHashMap<>(danhSachGheDaChon);
        this.danhSachKhach = new LinkedHashMap<>((Map<String, Object>) danhSachKhachHang); // Ép kiểu an toàn (cần kiểm tra)
        this.maChuyen = maChuyenTau;
        this.ngayDi = ngayDi;
        this.danhSachGiaVe = danhSachGiaVe != null ? new HashMap<>(danhSachGiaVe) : new HashMap<>();


        khoiTaoGiaoDien();

        // Tai khuyen mai tu DB va cap nhat giao dien
        taiKhuyenMaiTuDB();
        capNhatTongVaGiaoDien();
    }



    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel tieuDe = new JLabel("Bán vé - xác nhận thanh toán");
        tieuDe.setFont(tieuDe.getFont().deriveFont(Font.BOLD, 20f));
        tieuDe.setBorder(new EmptyBorder(6, 6, 12, 6));
        add(tieuDe, BorderLayout.NORTH);

        JSplitPane chia = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        chia.setResizeWeight(0.5);
        chia.setDividerSize(8);
        chia.setBorder(null);
        chia.setBackground(getBackground());

        chia.setLeftComponent(taoPanelTrai());
        chia.setRightComponent(taoPanelHoaDon());

        add(chia, BorderLayout.CENTER);
    }

    private <T> T getKhachField(Object khach, String fieldName, Class<T> type) {
        if (khach == null) return null;
        try {
            // Thử truy cập accessor method (cho Record)
            Method method = khach.getClass().getMethod(fieldName);
            Object result = method.invoke(khach);
            return type.cast(result);
        } catch (Exception e) {
            // Thất bại, trả về null hoặc giá trị mặc định
            return null;
        }
    }
    // Helper để lấy tên loại vé (Vì Record ChiTietKhach có tenLoaiVeHienThi)
    private String getTenLoaiVeHienThi(Object khach) {
        String maLoaiVe = getKhachField(khach, "maLoaiVe", String.class);
        if (maLoaiVe == null) return "Chưa xác định";
        return switch (maLoaiVe) {
            case "VT01" -> "Người lớn";
            case "VT02" -> "Trẻ em";
            case "VT03" -> "Người cao tuổi";
            case "VT04" -> "Sinh viên";
            default -> "Khác";
        };
    }

    private JPanel taoPanelTrai() {
        JPanel panelTrai = new JPanel();
        panelTrai.setLayout(new BoxLayout(panelTrai, BoxLayout.Y_AXIS));
        panelTrai.setBackground(new Color(255, 255, 255));
        panelTrai.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false),
                new EmptyBorder(12, 12, 12, 12)));

        panelTrai.add(taoPanelThongTinDatVe());
        panelTrai.add(Box.createRigidArea(new Dimension(0, 12)));
        panelTrai.add(taoPanelDanhSachKhach());
        panelTrai.add(Box.createRigidArea(new Dimension(0, 12)));
        panelTrai.add(taoPanelTongHoaDon());
        panelTrai.add(Box.createRigidArea(new Dimension(0, 12)));
        panelTrai.add(taoPanelThanhToan());
        panelTrai.add(Box.createVerticalGlue());

        return panelTrai;
    }

    private JPanel taoPanelThongTinDatVe() {
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setOpaque(false);
        panelThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin đặt vé"));

        JPanel noiDung = new JPanel(new GridLayout(0, 1));
        noiDung.setOpaque(false);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String ngayStr = ngayDi != null ? df.format(ngayDi) : "-";

        noiDung.add(new JLabel("Mã tàu: " + (maChuyen != null ? maChuyen : "-")));
        noiDung.add(new JLabel("Ngày giờ: " + ngayStr));
        int count = danhSachGhe != null ? danhSachGhe.size() : 0;
        noiDung.add(new JLabel("Số lượng ghế: " + count));

        panelThongTin.add(noiDung, BorderLayout.CENTER);
        return panelThongTin;
    }

    private JPanel taoPanelDanhSachKhach() {
        JPanel panelDanhSach = new JPanel(new BorderLayout());
        panelDanhSach.setOpaque(false);
        panelDanhSach.setBorder(BorderFactory.createTitledBorder("Danh sách khách hàng"));

        String[] cot = {"Ghế", "Họ và tên", "Tuổi", "Số điện thoại", "CCCD"};
        DefaultTableModel model = new DefaultTableModel(cot, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        if (danhSachGhe != null && !danhSachGhe.isEmpty()) {
            List<ChoDat> choList = new ArrayList<>(danhSachGhe.values());
            choList.sort(Comparator.comparing(ChoDat::getMaToa).thenComparing(ChoDat::getSoCho));
            for (ChoDat c : choList) {
                // SỬA: Lấy đối tượng khách hàng dưới dạng Object
                Object khach = (danhSachKhach != null) ? danhSachKhach.get(c.getMaCho()) : null;

                // SỬA: Sử dụng helper để truy cập dữ liệu
                String ten = getKhachField(khach, "hoTen", String.class);
                Integer tuoiObj = getKhachField(khach, "tuoi", Integer.class);
                String sdt = getKhachField(khach, "sdt", String.class);
                String cccd = getKhachField(khach, "cccd", String.class);

                String tenStr = (ten != null && !ten.isEmpty()) ? ten : "-";
                String tuoiStr = (tuoiObj != null && tuoiObj > 0) ? String.valueOf(tuoiObj) : "-";
                String sdtStr = (sdt != null && !sdt.isEmpty()) ? sdt : "-";
                String cccdStr = (cccd != null && !cccd.isEmpty()) ? cccd : "-";

                model.addRow(new Object[]{c.getMaToa() + "-" + c.getSoCho(), tenStr, tuoiStr, sdtStr, cccdStr});
            }
        } else {
            model.addRow(new Object[]{"-", "Chưa có khách", "-", "-", "-"});
        }

        tbKhach = new JTable(model);
        tbKhach.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(tbKhach);
        sp.setPreferredSize(new Dimension(520, 160));
        panelDanhSach.add(sp, BorderLayout.CENTER);
        return panelDanhSach;
    }

    private JPanel taoPanelTongHoaDon() {
        JPanel panelTong = new JPanel(new BorderLayout());
        panelTong.setOpaque(false);
        panelTong.setBorder(BorderFactory.createTitledBorder("Tổng hóa đơn"));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        double giaVe = tinhGiaVe();
        Tong tong = tinhTongHoaDon(giaVe, khuyenMaiApDung);

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        box.add(new JLabel("Giá vé: " + nf.format((long) tong.subtotal) + " VND"));
        String applied = (khuyenMaiApDung != null) ? khuyenMaiApDung.getMaKM() + " - " + khuyenMaiApDung.getTenKM() : "Không";
        box.add(new JLabel("Chiết khấu áp dụng: " + applied));
        box.add(new JLabel("Thuế VAT (10%): " + nf.format((long) tong.vat) + " VND"));

        lblTongTien = new JLabel("Tổng số tiền phải thanh toán: " + nf.format((long) tong.total) + " VND");
        lblTongTien.setFont(lblTongTien.getFont().deriveFont(Font.BOLD));
        lblTongTien.setForeground(new Color(200, 60, 10));
        box.add(Box.createRigidArea(new Dimension(0, 6)));
        box.add(lblTongTien);

        panelTong.add(box, BorderLayout.CENTER);
        return panelTong;
    }

    private JPanel taoPanelThanhToan() {
        JPanel panelThanhToan = new JPanel();
        panelThanhToan.setLayout(new BoxLayout(panelThanhToan, BoxLayout.Y_AXIS));
        panelThanhToan.setOpaque(false);
        panelThanhToan.setBorder(BorderFactory.createTitledBorder("Thanh toán"));

        // Khuyen mai: combobox lay tu DB
        JPanel dongKm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dongKm.setOpaque(false);
        dongKm.add(new JLabel("Khuyến mãi: "));

        cbKhuyenMai = new JComboBox<>();
        cbKhuyenMai.setPreferredSize(new Dimension(320, 26));
        dongKm.add(cbKhuyenMai);

        btnApDungKhuyenMai = new JButton("Áp dụng");
        btnApDungKhuyenMai.addActionListener(e -> apDungKhuyenMaiChon());
        dongKm.add(btnApDungKhuyenMai);

        btnXoaKhuyenMai = new JButton("Xóa KM");
        btnXoaKhuyenMai.setEnabled(false);
        btnXoaKhuyenMai.addActionListener(e -> xoaKhuyenMai());
        dongKm.add(btnXoaKhuyenMai);

        lblThongTinKhuyenMai = new JLabel(" ");
        dongKm.add(lblThongTinKhuyenMai);

        panelThanhToan.add(dongKm);

        JPanel dong1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dong1.setOpaque(false);
        dong1.add(new JLabel("Hình thức thanh toán: "));
        cbHinhThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"});
        dong1.add(cbHinhThuc);
        panelThanhToan.add(dong1);

        JPanel dong2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dong2.setOpaque(false);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        double tongKhongKm = tinhGiaVe() * 1.10;
        txtTienKhachDua = new JTextField(nf.format((long) tongKhongKm), 12);
        txtTienThoiLai = new JTextField(nf.format(0), 10);
        txtTienThoiLai.setEditable(false);
        dong2.add(new JLabel("Tiền khách đưa: "));
        dong2.add(txtTienKhachDua);
        dong2.add(new JLabel("Tiền thối lại: "));
        dong2.add(txtTienThoiLai);
        panelThanhToan.add(dong2);

        JPanel goiY = new JPanel(new FlowLayout(FlowLayout.LEFT));
        goiY.setOpaque(false);
        goiY.add(new JLabel("Gợi ý: "));
        List<Long> goiYList = Arrays.asList((long) Math.round(tongKhongKm), (long) (tongKhongKm + 30000), (long) (tongKhongKm + 50000), (long) (tongKhongKm + 100000));
        for (Long h : goiYList) {
            JButton b = new JButton(nf.format(h));
            b.setMargin(new Insets(4, 8, 4, 8));
            b.addActionListener(e -> {
                txtTienKhachDua.setText(nf.format(h));
                capNhatTienThoi();
            });
            goiY.add(b);
        }
        panelThanhToan.add(goiY);

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelNut.setOpaque(false);

        // NÚT HỦY (MỚI)
        JButton btnHuy = new JButton("Hủy");
        btnHuy.setForeground(Color.BLACK); // Màu chữ đen
        btnHuy.setBackground(new Color(220, 220, 220)); // Màu nền xám nhạt
        btnHuy.setFocusPainted(false);
        btnHuy.setPreferredSize(new Dimension(140, 36));
        btnHuy.addActionListener(e -> huyBoThanhToan());
        panelNut.add(btnHuy);

        JButton btnXacNhan = new JButton("Xác nhận thanh toán");
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setBackground(MAU_XANH_BTN);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setPreferredSize(new Dimension(180, 36));
        btnXacNhan.addActionListener(e -> xacNhanThanhToan());
        panelNut.add(btnXacNhan);

        panelThanhToan.add(panelNut);

        txtTienKhachDua.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { capNhatTienThoi(); }
        });

        return panelThanhToan;
    }

    private JPanel taoPanelHoaDon() {
        JPanel panelHoaDon = new JPanel(new BorderLayout());
        panelHoaDon.setBackground(new Color(255, 255, 255));
        panelHoaDon.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false),
                new EmptyBorder(12, 12, 12, 12)));

        JLabel tieuDeHD = new JLabel("Hóa đơn mua vé");
        tieuDeHD.setFont(tieuDeHD.getFont().deriveFont(Font.BOLD, 16f));
        JPanel dau = new JPanel(new BorderLayout());
        dau.setOpaque(false);
        dau.add(tieuDeHD, BorderLayout.NORTH);

        JPanel thongTinHD = new JPanel();
        thongTinHD.setOpaque(false);
        thongTinHD.setLayout(new GridLayout(0, 2));

        // FIX: Hiển thị giá trị giữ chỗ ban đầu
        lblMaHoaDon = new JLabel("Chưa tạo");
        lblNguoiLapHD = new JLabel("Đang tải...");
        lblDienThoaiNV = new JLabel("Đang tải...");

        // Gán giá trị ban đầu cho các component
        thongTinHD.add(new JLabel("Mã hóa đơn: "));
        thongTinHD.add(lblMaHoaDon);
        thongTinHD.add(new JLabel("Người lập hóa đơn: "));
        thongTinHD.add(lblNguoiLapHD);
        thongTinHD.add(new JLabel("Điện thoại: "));
        thongTinHD.add(lblDienThoaiNV);
        dau.add(thongTinHD, BorderLayout.CENTER);

        JPanel panelNguoiDat = new JPanel();
        panelNguoiDat.setLayout(new BoxLayout(panelNguoiDat, BoxLayout.Y_AXIS));
        panelNguoiDat.setBorder(BorderFactory.createTitledBorder("Người đặt vé / Thanh toán"));
        panelNguoiDat.setOpaque(false);

        lblTenNguoiDat = new JLabel("Họ tên: [Tên khách đầu tiên]");
        lblSdtNguoiDat = new JLabel("Số điện thoại: [SĐT khách đầu tiên]");
        lblPhuongThucTT = new JLabel("Phương thức: [Hình thức TT]");

        panelNguoiDat.add(lblTenNguoiDat);
        panelNguoiDat.add(lblSdtNguoiDat);
        panelNguoiDat.add(lblPhuongThucTT);

        // Gộp tất cả vào phần NORTH
        JPanel headerWrapper = new JPanel(new BorderLayout(0, 5));
        headerWrapper.setOpaque(false);
        headerWrapper.add(dau, BorderLayout.NORTH);
        headerWrapper.add(panelNguoiDat, BorderLayout.CENTER);

        panelHoaDon.add(headerWrapper, BorderLayout.NORTH); // Thay thế dau bằng headerWrapper

        String[] cotHd = {"STT", "Loại vé", "Số lượng", "Đơn giá", "Thuế VAT", "Thành tiền có thuế"};
        DefaultTableModel modelHd = new DefaultTableModel(cotHd, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        if (danhSachKhach != null && !danhSachKhach.isEmpty()) {
            // Nhóm các ghế có cùng loại vé và đơn giá
            Map<String, MapGroup> groups = new LinkedHashMap<>();

            // Lặp qua danhSachKhach để nhóm
            for (Map.Entry<String, Object> entry : danhSachKhach.entrySet()) {
                Object khach = entry.getValue();

                // SỬA: Sử dụng helper để lấy các trường cần thiết
                String maLoaiVe = getKhachField(khach, "maLoaiVe", String.class);
                Long giaDonVi = danhSachGiaVe.get(entry.getKey()); // Giá đã được tính

                if (giaDonVi != null && maLoaiVe != null) {
                    // Nhóm theo MaLoaiVe để nhóm các vé có cùng loại và giá
                    if (!groups.containsKey(maLoaiVe)) {
                        // Sử dụng MaLoaiVe làm key nhóm
                        groups.put(maLoaiVe, new MapGroup(getTenLoaiVeHienThi(khach), giaDonVi));
                    }
                    groups.get(maLoaiVe).soLuong++;
                }
            }

            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            int stt = 1;
            for (Map.Entry<String, MapGroup> entry : groups.entrySet()) {
                MapGroup group = entry.getValue();

                // Tính toán: group.giaDonVi đã bao gồm VAT.
                // Giá trước thuế = Giá/1.1
                double giaTruocVAT = group.giaDonVi / 1.1;
                double thanhTienCoVAT = group.giaDonVi * group.soLuong;

                modelHd.addRow(new Object[]{
                        stt++,
                        group.tenLoaiVe, // GÁN TÊN LOẠI VÉ VÀO CỘT "Loại vé"
                        group.soLuong,
                        nf.format(Math.round(giaTruocVAT)),
                        "10%",
                        nf.format(Math.round(thanhTienCoVAT))
                });
            }
        } else {
            modelHd.addRow(new Object[]{"", "Chưa có ghế", "", "", "", ""});
        }
// --- KẾT THÚC SỬA ĐOẠN TÍNH TOÁN VÀ HIỂN THỊ BẢNG ---
        tbHoaDon = new JTable(modelHd);
        tbHoaDon.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(tbHoaDon);
        sp.setPreferredSize(new Dimension(480, 300));
        panelHoaDon.add(sp, BorderLayout.CENTER);

        JPanel chan = new JPanel(new BorderLayout());
        chan.setOpaque(false);

        JPanel panelTongCong = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTongCong.setOpaque(false);

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        double tongVoiKM = tinhTongHoaDon(tinhGiaVe(), khuyenMaiApDung).total;
        JLabel lbl = new JLabel("Tổng cộng: " + nf.format(Math.round(tongVoiKM)));lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));

        panelTongCong.add(lbl);
        chan.add(panelTongCong, BorderLayout.NORTH);

        JPanel panelKetThuc = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelKetThuc.setOpaque(false);
        JButton btnKetThuc = new JButton("Kết thúc >");
        btnKetThuc.setBackground(MAU_CAM_BTN);
        btnKetThuc.setForeground(Color.WHITE);
        btnKetThuc.setPreferredSize(new Dimension(140, 36));
        btnKetThuc.addActionListener(e -> ketThuc());
        panelKetThuc.add(btnKetThuc);
        chan.add(panelKetThuc, BorderLayout.SOUTH);

        panelHoaDon.add(chan, BorderLayout.SOUTH);

        // FIX: Cập nhật thông tin người lập HD ban đầu (Mock)
        lblNguoiLapHD.setText("Nguyễn Bảo Duy (" + MA_NV_LAP_HD_MOCK + ")");
        lblDienThoaiNV.setText("0332534500");

        capNhatThongTinNguoiDat();
        return panelHoaDon;
    }

    /**
     * Cập nhật thông tin Người đặt vé (khách hàng đại diện) và Phương thức TT.
     */
    private void capNhatThongTinNguoiDat() {
        // Chỉ lấy Khách hàng đầu tiên làm đại diện (Khách vãng lai nếu danh sách rỗng)
        String tenKhach = "Khách Vãng Lai";
        String sdtKhach = "";
        String hinhThuc = (String) cbHinhThuc.getSelectedItem();

        if (danhSachKhach != null && !danhSachKhach.isEmpty()) {
            Object khachDauTien = danhSachKhach.values().iterator().next();
            String hoTen = getKhachField(khachDauTien, "hoTen", String.class);
            String sdt = getKhachField(khachDauTien, "sdt", String.class);

            if (hoTen != null && !hoTen.isEmpty()) tenKhach = hoTen;
            if (sdt != null && !sdt.isEmpty()) sdtKhach = sdt;
        }

        lblTenNguoiDat.setText("Họ tên: " + tenKhach);
        lblSdtNguoiDat.setText("Số điện thoại: " + sdtKhach);
        lblPhuongThucTT.setText("Phương thức: " + hinhThuc);
    }

//// FIX: Cần gán sự kiện cho cbHinhThuc trong taoPanelThanhToan()
//// ...
//cbHinhThuc.addActionListener(e -> capNhatThongTinNguoiDat());
//// ...

    // Thêm class MapGroup để hỗ trợ nhóm hóa đơn
    private static class MapGroup {
        String tenLoaiVe;
        long giaDonVi; // Giá đã bao gồm chiết khấu loại vé, chưa bao gồm KM tổng và VAT
        int soLuong;

        public MapGroup(String tenLoaiVe, long giaDonVi) {
            this.tenLoaiVe = tenLoaiVe;
            this.giaDonVi = giaDonVi;
            this.soLuong = 0;
        }
    }

    // Tai khuyen mai tu DB vao combobox
    private void taiKhuyenMaiTuDB() {
        SwingUtilities.invokeLater(() -> {
            cbKhuyenMai.removeAllItems();
            KhuyenMai none = new KhuyenMai();
            none.setMaKM("");
            none.setTenKM("-- Khong ap dung --");
            cbKhuyenMai.addItem(none);

            List<KhuyenMai> promos = khuyenMaiDAO.layTatCaKMHoatDongVoiDK();
            if (promos != null) {
                System.out.println("Đã tải " + promos.size() + " khuyến mãi từ DB.");
                for (KhuyenMai km : promos) {
                    cbKhuyenMai.addItem(km);
                }
            }
            cbKhuyenMai.setSelectedIndex(0);
        });
    }

    // Kiem tra dieu kien khuyen mai
    private boolean khuyenMaiHopLe(KhuyenMai km) {
        if (km == null || km.getMaKM() == null || km.getMaKM().isEmpty()) return false;
        double subtotal = tinhGiaVe();
        int soLuongVe = (danhSachGhe == null) ? 0 : danhSachGhe.size();

        if (km.getDieuKienList() == null || km.getDieuKienList().isEmpty()) {
            return true;
        }

        for (DieuKienKhuyenMai dk : km.getDieuKienList()) {
            String loai = dk.getLoaiDieuKien();
            double val = dk.getGiaTriAsDouble();
            if ("MIN_TICKETS".equalsIgnoreCase(loai)) {
                if (soLuongVe < (int) Math.round(val)) return false;
            } else if ("MIN_AMOUNT".equalsIgnoreCase(loai)) {
                if (subtotal < val) return false;
            } else {
                // loai khong ho tro => khong ap dung
                return false;
            }
        }
        return true;
    }

    // Ap dung khuyen mai dang chon
    private void apDungKhuyenMaiChon() {
        KhuyenMai sel = (KhuyenMai) cbKhuyenMai.getSelectedItem();
        if (sel == null || sel.getMaKM() == null || sel.getMaKM().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi hợp lệ hoặc 'Không áp dụng'.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!khuyenMaiHopLe(sel)) {
            JOptionPane.showMessageDialog(this, "Khuyến mãi không thỏa điều kiện áp dụng.", "Không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        khuyenMaiApDung = sel;
        btnXoaKhuyenMai.setEnabled(true);
        lblThongTinKhuyenMai.setText("Đã áp dụng: " + sel.getMaKM());
        capNhatTongVaGiaoDien();
        JOptionPane.showMessageDialog(this, "Áp dụng khuyến mãi thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xoaKhuyenMai() {
        khuyenMaiApDung = null;
        btnXoaKhuyenMai.setEnabled(false);
        lblThongTinKhuyenMai.setText(" ");
        capNhatTongVaGiaoDien();
    }

    // Tinh tong (subtotal, discount, vat, total)
    private Tong tinhTongHoaDon(double subtotal, KhuyenMai km) {
        Tong t = new Tong();
        t.subtotal = subtotal;
        double discount = 0;

        if (km != null && km.getMaKM() != null && !km.getMaKM().isEmpty()) {
            if (km.getPhanTramGiam() > 0) {
                discount = subtotal * km.getPhanTramGiam();
            } else if (km.getGiaTienGiamTru() > 0) {
                discount = km.getGiaTienGiamTru();
            }
            discount = Math.min(discount, subtotal);
        }
        t.discount = discount;

        double taxable = Math.max(0, subtotal - discount);
        t.vat = taxable * 0.10;
        t.total = taxable + t.vat;

        return t;
    }

    private void capNhatTongVaGiaoDien() {
        double subtotal = tinhGiaVe();
        Tong t = tinhTongHoaDon(subtotal, khuyenMaiApDung);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        if (lblTongTien != null) {
            lblTongTien.setText("Tổng số tiền phải thanh toán: " + nf.format(Math.round(t.total)) + " VND");
        }
        if (txtTienKhachDua != null) {
            txtTienKhachDua.setText(nf.format(Math.round(t.total)));
        }
        capNhatTienThoi();
    }

    private double tinhGiaVe() {
        // SỬA: Tính tổng giá vé từ Map danhSachGiaVe (giá mỗi ghế đã được tính)
        if (danhSachGiaVe == null || danhSachGiaVe.isEmpty()) {
            return 0;
        }

        double subtotal = 0;

        // Cộng tổng giá tiền của tất cả các ghế đã chọn
        for (Long gia : danhSachGiaVe.values()) {
            if (gia != null) {
                subtotal += gia.doubleValue();
            }
        }
        return subtotal;
    }

    private void capNhatTienThoi() {
        try {
            String raw = txtTienKhachDua.getText().replaceAll("[^0-9]", "");
            if (raw.isEmpty()) {
                txtTienThoiLai.setText("0");
                return;
            }
            long given = Long.parseLong(raw);
            double subtotal = tinhGiaVe();
            Tong t = tinhTongHoaDon(subtotal, khuyenMaiApDung);
            long tong = Math.round(t.total);
            long change = given - tong;
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            txtTienThoiLai.setText(nf.format(Math.max(0, change)));
        } catch (Exception ex) {
            txtTienThoiLai.setText("0");
        }
    }


    /**
     * Lấy Mã số thứ tự cuối cùng của Hóa đơn trong ngày/ca/NV hiện tại.
     * (Hàm Helper, gọi HoaDonDAO)
     */
    private String getLastSoThuTuHoaDon(String soHieuCa, String maNV) throws SQLException {
        String maNVRutGon;
        if (maNV != null && maNV.length() >= 4) {
            // Lấy 4 ký tự cuối (ví dụ: "0001" từ "NVBV0001")
            maNVRutGon = maNV.substring(maNV.length() - 4);
        } else {
            maNVRutGon = "0000";
        }

        String ngayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // Tiền tố truy vấn: HD[CC][YYMMDD][NNNN]%
        String maHdPatternPrefix = "HD" + soHieuCa + ngayStr + maNVRutGon + "%";

        String lastSTT = null;
        String lastMaHD = HoaDonDAO.getLastMaHoaDonByPrefix(maHdPatternPrefix);

        if (lastMaHD != null) {
            lastSTT = lastMaHD.substring(lastMaHD.length() - 4);
        }
        return lastSTT;
    }



    // =====================================================================================
// PHƯƠNG THỨC XỬ LÝ KHÁCH HÀNG (B1)
// =====================================================================================
    /**
     * Xử lý Khách hàng: Tìm kiếm, tạo mới (nếu chưa có), và trả về Map các Entity KhachHang đã hoàn chỉnh MaKH.
     *
     * @param maKhachHangDaiDienOut (Output) Biến array 1 phần tử để chứa MaKhachHang đại diện cho Hóa đơn.
     * @return Map<MaCho, KhachHang entity đã xử lý>.
     * @throws SQLException Nếu có lỗi CSDL khi tạo mã KH.
     */
    private Map<String, KhachHang> xuLyVaTaoKhachHangEntities(String[] maKhachHangDaiDienOut) throws SQLException {
        Map<String, KhachHang> danhSachKhachHangEntity = new HashMap<>();

        // Khởi tạo danh sách khách hàng MỚI cần được INSERT vào DB trong transaction
        List<KhachHang> danhSachKhachHangMoiCanLuu = new ArrayList<>();

        // Nếu không có khách nào, trả về Map rỗng
        if (danhSachKhach == null || danhSachKhach.isEmpty()) {
            maKhachHangDaiDienOut[0] = "KHVL001"; // Gán mặc định
            return danhSachKhachHangEntity;
        }
        // [FIX START] TÌM STT LỚN NHẤT MỘT LẦN VÀ DÙNG BIẾN ĐẾM TẠM
        LocalDate homNay = LocalDate.now();
        // Lấy giá trị số nguyên lớn nhất (VD: 10)
        int currentSTT = khachHangDAO.getLastKhachHangSTTValue(homNay);
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));
        // [FIX END]

        // Lặp qua danh sách khách hàng từ UI để tạo/tìm Entity KhachHang
        for (Map.Entry<String, Object> entry : danhSachKhach.entrySet()) {
            Object khach = entry.getValue();

            // Trích xuất các trường dữ liệu thô
            String cccd = getKhachField(khach, "cccd", String.class);
            String sdt = getKhachField(khach, "sdt", String.class);
            String hoTen = getKhachField(khach, "hoTen", String.class);
            Integer tuoi = getKhachField(khach, "tuoi", Integer.class);
            String gioiTinh = getKhachField(khach, "gioiTinh", String.class); // Thêm nếu cần

            KhachHang khachHangDaTonTai = null;
            // Chỉ tìm khi có CCCD
            if (cccd != null && !cccd.isEmpty()) {
                khachHangDaTonTai = khachHangDAO.findKhachHangByCCCD(cccd);
            }

            KhachHang khachHangCanLuu;

            if (khachHangDaTonTai == null) {
                // [FIX] TĂNG BIẾN ĐẾM VÀ TẠO MÃ BẰNG BIẾN ĐẾM TẠM
                currentSTT++; // 10 -> 11
                String soThuTuStr = String.format("%04d", currentSTT);
                String maKHNew = "KH" + ngayStr + soThuTuStr;

                System.out.println("Tạo mã mới: " + maKHNew); // Kiểm tra

                khachHangCanLuu = new KhachHang(
                        maKHNew,
                        hoTen,
                        cccd,
                        tuoi != null ? tuoi : 0,
                        sdt
                );

                // THÊM VÀO DANH SÁCH CẦN LƯU
                danhSachKhachHangMoiCanLuu.add(khachHangCanLuu);
            } else {
                khachHangCanLuu = khachHangDaTonTai;
            }

            danhSachKhachHangEntity.put(entry.getKey(), khachHangCanLuu);

            // Gán Mã KH đại diện: Mã KH của người đầu tiên trong Map
            if (maKhachHangDaiDienOut[0] == null) {
                maKhachHangDaiDienOut[0] = khachHangCanLuu.getMaKH();
            }
        }

        // Nếu danh sách không rỗng nhưng chưa gán được MaKH đại diện (lỗi logic), dùng KHVL001
        if (maKhachHangDaiDienOut[0] == null) {
            maKhachHangDaiDienOut[0] = "KHVL001";
        }
        // In danh sách KhachHang Entity để kiểm tra
        inDanhSachKhachHangEntity(danhSachKhachHangEntity);

        return danhSachKhachHangEntity;
    }

    private void inDanhSachKhachHangEntity(Map<String, KhachHang> danhSachKhachHangEntity) {

        System.out.println("--- Bắt đầu in Danh sách Khách hàng Entity ---");

        // Lặp qua toàn bộ Map
        danhSachKhachHangEntity.forEach((maGhe, khachHangEntity) -> {

            // In toàn bộ Map: key (Mã ghế) và value (Đối tượng Khách hàng)
            System.out.println("Danh sách khách hàng thực thể Ghế [" + maGhe + "]: " + khachHangEntity.toString());

            // Kiểm tra cụ thể trường 'maKhachHang' (Sử dụng getMaKH() thay vì getKhachField)
            String maKhach = khachHangEntity.getMaKH(); // Giả định phương thức getter là getMaKH()

            System.out.println("  -> MaKhachHang trích xuất: " + maKhach);
        });

        System.out.println("--- Kết thúc in Danh sách Khách hàng Entity ---");
    }

    // =====================================================================================
// PHƯƠNG THỨC TẠO ENTITY VÀ GỌI TRANSACTION (B2, B3, B4)
// =====================================================================================

    private void thucHienGiaoDichBanVe(Map<String, KhachHang> danhSachKhachHangEntity, String maKhachHangDaiDienStr, KhachHang khachHangDaiDienObj) {

        // --- B1: Lấy và Chuẩn bị Dữ liệu ---
        String maHD = taoMaHoaDon();

        Tong tongKetQua = tinhTongHoaDon(tinhGiaVe(), khuyenMaiApDung);
        double tongTienPhaiThanhToan = tongKetQua.total;
        String phuongThuc = cbHinhThuc.getSelectedItem().toString();
        String loaiHoaDon = "Bán vé trực tiếp";
        String maNVLap = MA_NV_LAP_HD_MOCK;

        // Kiểm tra tiền khách đưa
        // ... (logic kiểm tra tiền khách đưa, giữ nguyên) ...
        try {
            String raw = txtTienKhachDua.getText().replaceAll("[^0-9]", "");
            long tienKhachDua = Long.parseLong(raw);
            if (tienKhachDua < Math.round(tongTienPhaiThanhToan)) {
                JOptionPane.showMessageDialog(this, "Tiền khách đưa không đủ để thanh toán.", "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền khách đưa hợp lệ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // --- B2: Tạo thực thể Hóa đơn (HoaDon) ---
        HoaDon hoaDon = new HoaDon(
                maHD,
                maKhachHangDaiDienStr, // FIX: Sử dụng Mã KH đã được xác định/tìm thấy
                maNVLap,
                khuyenMaiApDung != null ? khuyenMaiApDung.getMaKM() : null,
                Math.round(tongTienPhaiThanhToan),
                java.time.LocalDateTime.now(),
                phuongThuc,
                loaiHoaDon
        );

        // --- B3: Tạo danh sách thực thể Vé (VeCuaBanVe) ---
        List<VeCuaBanVe> danhSachVeMoi = new ArrayList<>();

        for (Map.Entry<String, ChoDat> entry : danhSachGhe.entrySet()) {
            ChoDat cho = entry.getValue();

            // Lấy KhachHang Entity đã xử lý từ Map mới
            KhachHang khachHangChoVe = danhSachKhachHangEntity.get(entry.getKey());

            // Lấy giá cơ bản đã có chiết khấu loại vé (từ UI)
            long giaVeBase = danhSachGiaVe.get(cho.getMaCho());

            // Tính toán lại giá trị cuối cùng sau khi áp dụng chiết khấu KM (nếu có)
            double phanTramGiam = (khuyenMaiApDung != null) ? khuyenMaiApDung.getPhanTramGiam() : 0.0;
            double giaSauKhuyenMai = giaVeBase * (1 - phanTramGiam);

            // Lấy MaKhachHang đã được xác định từ Entity
            String maKhach = khachHangChoVe.getMaKH();

            // Lấy MaLoaiVe (Giả định nằm trong đối tượng khách thô, hoặc lấy mặc định)
            Object khachTho = danhSachKhach.get(cho.getMaCho());
            String maLoaiVe = (khachTho != null) ? getKhachField(khachTho, "maLoaiVe", String.class) : "VT01";


            // Tạo đối tượng VeCuaBanVe
            VeCuaBanVe ve = new VeCuaBanVe(
                    null, // MaVe sẽ được tạo tự động trong DAO
                    maChuyen,
                    cho.getMaCho(),
                    maNVLap,
                    maKhach, // FIX: MaKhach đã được xử lý
                    maLoaiVe,
                    Math.round(giaSauKhuyenMai),
                    "DA_BAN"
            );
            danhSachVeMoi.add(ve);
        }

        // --- B4: Gọi DAO để thực hiện Transaction ---
        try {
            // FIX: Truyền KhachHang entity đại diện (đã có MaKH hợp lệ)
            boolean success = veDao.banVeTrongTransaction(hoaDon, danhSachVeMoi, khachHangDaiDienObj);


            if (success) {
                hienThiThongTinHoaDon(hoaDon);
                System.out.println("Số lượng vé được thêm " + danhSachVeMoi.size());
                for (VeCuaBanVe ve : danhSachVeMoi){
                    System.out.println("Mã vé được tạo: " + ve.getMaVe());
                }
                System.out.println("Đã thêm được hóa đơn: " + maHD);
                // Xây dựng thông báo chi tiết
                StringBuilder sb = new StringBuilder("Thanh toán thành công!\n");
                sb.append("Mã Hóa đơn: ").append(maHD).append("\n");
                sb.append("--- Danh sách Mã Vé đã tạo ---\n");



                // Lặp qua danh sách Entity đã được cập nhật MaVe từ DAO
                for (VeCuaBanVe ve : danhSachVeMoi) {
                    sb.append(ve.getMaVe()).append(" (Ghế: ").append(ve.getMaChoDat()).append(")\n");
                }

                JOptionPane.showMessageDialog(this,
                        sb.toString(), // Hiển thị thông tin chi tiết
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                

            } else {
                JOptionPane.showMessageDialog(this, "Giao dịch không hoàn tất. Vui lòng kiểm tra lại.", "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: Không thể hoàn tất giao dịch. " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật các JLabel hiển thị thông tin hóa đơn sau khi transaction thành công.
     * @param hd Đối tượng HoaDon đã được lưu vào CSDL.
     */
    public void hienThiThongTinHoaDon(HoaDon hd) {
        if (hd != null) {
            lblMaHoaDon.setText(hd.getMaHD());
            lblMaHoaDon.setForeground(MAU_XANH_BTN);

            // NOTE: Thông tin người lập HD và SĐT đã được gán giá trị mock ban đầu.
            // Nếu bạn muốn hiển thị tên thật, cần truy vấn thêm từ NhanVienDAO.
        }
    }


    // =====================================================================================
// PHƯƠNG THỨC GỐC ĐƯỢC CẬP NHẬT
// ====================================================================================

    private void xacNhanThanhToan() {
        String[] maKhachHangDaiDienArr = new String[1]; // Biến hứng Mã KH đại diện
        Map<String, KhachHang> danhSachKhachHangEntity;

        try {
            // BƯỚC 1: Xử lý và tạo Entity Khách hàng
            danhSachKhachHangEntity = xuLyVaTaoKhachHangEntities(maKhachHangDaiDienArr);

            // FIX: Trích xuất MaKH đại diện và tìm Entity tương ứng
            String maKhachHangDaiDienStr = maKhachHangDaiDienArr[0];

// FIX: Xác định đối tượng KhachHang Entity đại diện đầy đủ
            KhachHang khachHangDaiDienObj;

            if (maKhachHangDaiDienStr.equals("KHVL001")) {
                // Nếu là khách vãng lai, tạo đối tượng KHVL001 mặc định.
                // CẦN ĐẢM BẢO constructor này khớp với lớp KhachHang của bạn (6 tham số giả định).
                khachHangDaiDienObj = new KhachHang(
                        "KHVL001",
                        "Khách Vãng Lai",
                        "000000000000", // CCCD mặc định (Nếu bảng yêu cầu NOT NULL)
                        0,
                        "",
                        null
                );
            } else {
                // Nếu là khách đã có dữ liệu, lấy đối tượng Entity đã được tạo/tìm thấy trong vòng lặp xử lý khách hàng.
                // Vì đây là khách đầu tiên, ta có thể tìm nó trong Map danhSachKhachHangEntity.
                // Ta lấy khách hàng bất kỳ từ Map, vì chúng ta đang giả định tất cả đều là một giao dịch
                khachHangDaiDienObj = danhSachKhachHangEntity.values().iterator().next();
            }



            // BƯỚC 2, 3, 4: Tạo Entity, gọi Transaction
            thucHienGiaoDichBanVe(danhSachKhachHangEntity, maKhachHangDaiDienStr, khachHangDaiDienObj);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL nghiêm trọng: " + e.getMessage(), "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void ketThuc() {
        JOptionPane.showMessageDialog(this, "Kết thúc (chưa nối backend).");
    }

    /**
     * Lấy số hiệu ca làm việc hiện tại (Mặc định là "01").
     * Cần thay thế bằng logic truy vấn thực tế.
     * @return Chuỗi 2 ký tự đại diện cho số hiệu ca.
     */
    private String getSoHieuCaHienTai() {
        // TODO: Thay thế bằng logic truy vấn thực tế.
        return "01";
    }

    /**
     * Tạo mã Hóa đơn mới theo quy tắc: HD[CC][DDMMYY][MaNV][STT].
     * @return Mã Hóa đơn mới, ví dụ: HD0127102500010001
     */
    private String taoMaHoaDon() {
        try {
            String soHieuCa = getSoHieuCaHienTai();
            String maNVDayDu = MA_NV_LAP_HD_MOCK;

            // Trích xuất 4 ký tự cuối của Mã NV (V001)
            String maNVRutGon;
            if (maNVDayDu != null && maNVDayDu.length() >= 4) {
                maNVRutGon = maNVDayDu.substring(maNVDayDu.length() - 4);
            } else {
                maNVRutGon = "0000";
            }

            String ngayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));

            // Tiền tố đầy đủ (HD01251027V001)
            String maHdPatternPrefixDayDu = "HD" + soHieuCa + ngayStr + maNVRutGon;

            // 1. GỌI DAO ĐỂ TÌM MÃ ĐẦY ĐỦ LỚN NHẤT
            String lastMaHD = HoaDonDAO.getLastMaHoaDonByPrefix(maHdPatternPrefixDayDu);
            int nextNumber = 1;

            if (lastMaHD != null) {
                // 2. TRÍCH XUẤT STT TỪ MÃ ĐẦY ĐỦ (lấy 4 ký tự cuối)
                try {
                    String lastSTTStr = lastMaHD.substring(lastMaHD.length() - 4);
                    nextNumber = Integer.parseInt(lastSTTStr) + 1; // 0004 -> 5
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.err.println("Lỗi trích xuất STT từ Mã HD: " + lastMaHD + ". Bắt đầu lại từ 1.");
                    nextNumber = 1;
                }
            }

            // Định dạng số thứ tự (0005)
            String soThuTuStr = String.format("%04d", nextNumber);

            // Gộp và trả về mã mới (Ví dụ: HD0128102500010005)
            return maHdPatternPrefixDayDu + soThuTuStr;

        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi tạo Mã Hóa Đơn: " + e.getMessage());
            return "HDERR" + System.currentTimeMillis();
        }
    }

    private static class Tong {
        double subtotal;
        double discount;
        double vat;
        double total;
    }

    private void huyBoThanhToan() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có muốn hủy toàn bộ dữ liệu và quay về Trang chủ?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        Window w = SwingUtilities.getWindowAncestor(this);

        if (w instanceof BanVeDashboard) {
            BanVeDashboard dashboard = (BanVeDashboard) w;

            ManHinhTrangChuNVBanVe confirmPanel = new ManHinhTrangChuNVBanVe();

            dashboard.addOrUpdateCard(confirmPanel, "trangChu");
            dashboard.switchToCard("trangChu");

        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể tìm thấy cửa sổ Dashboard. Vui lòng chạy ứng dụng từ BanVeDashboard.",
                    "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    // main test neu can
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Xác nhận Bán vé (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ManHinhXacNhanBanVe(), BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}