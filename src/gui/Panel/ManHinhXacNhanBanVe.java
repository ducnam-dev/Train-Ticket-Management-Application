// java
package gui.Panel;

import entity.ChoDat;
import entity.DieuKienKhuyenMai;
import entity.TempKhachHang;
import entity.KhuyenMai;
import dao.KhuyenMaiDAO;
import gui.MainFrame.BanVeDashboard;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * ManHinhXacNhanBanVe - phiên bản cập nhật: khuyến mãi đọc từ DB và hiển thị combobox.
 * Tên biến và phương thức bằng tiếng Việt không dấu.
 */
public class ManHinhXacNhanBanVe extends JPanel {
    private Map<String, ChoDat> danhSachGhe;
    private Map<String, TempKhachHang> danhSachKhach;
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

    // Khuyen mai dang ap dung
    private KhuyenMai khuyenMaiApDung = null;

    // DAO
    private KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();

    private static final Color MAU_XANH_BTN = new Color(0, 180, 110);
    private static final Color MAU_CAM_BTN = new Color(255, 140, 0);

    public ManHinhXacNhanBanVe() {
        this(null, null, null, null, null);
    }

    public ManHinhXacNhanBanVe(Map<String, ChoDat> danhSachGheDaChon,
                               Map<String, TempKhachHang> danhSachKhachHang,
                               String maChuyenTau,
                               Date ngayDi,
                               Map<String, Long> danhSachGiaVe) {
        this.danhSachGhe = new LinkedHashMap<>(danhSachGheDaChon);
        this.danhSachKhach = new LinkedHashMap<>(danhSachKhachHang);
        this.maChuyen = maChuyenTau;
        this.ngayDi = ngayDi;
        // keep a copy to avoid external modification
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
                TempKhachHang tk = (danhSachKhach != null) ? danhSachKhach.get(c.getMaCho()) : null;
                String ten = tk != null && tk.hoTen != null ? tk.hoTen : "-";
                String tuoi = (tk != null && tk.tuoi != 0) ? String.valueOf(tk.tuoi) : "-";
                String sdt = (tk != null && (tk.sdt != null && !tk.sdt.isEmpty())) ? tk.sdt : "-";
                String cccd = (tk != null && tk.cccd != null && !tk.cccd.isEmpty()) ? tk.cccd : "-";
                model.addRow(new Object[]{c.getMaToa() + "-" + c.getSoCho(), ten, tuoi, sdt, cccd});
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
        thongTinHD.add(new JLabel("Mã hóa đơn: "));
        thongTinHD.add(new JLabel(taoMaHoaDon()));
        thongTinHD.add(new JLabel("Người lập hóa đơn: "));
        thongTinHD.add(new JLabel("Nguyễn Bảo Duy"));
        thongTinHD.add(new JLabel("Điện thoại: "));
        thongTinHD.add(new JLabel("0332534500"));
        dau.add(thongTinHD, BorderLayout.CENTER);

        panelHoaDon.add(dau, BorderLayout.NORTH);

        String[] cotHd = {"STT", "Mã vé", "Số lượng", "Đơn giá", "Thuế VAT", "Thành tiền có thuế"};
        DefaultTableModel modelHd = new DefaultTableModel(cotHd, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        if (danhSachKhach != null && !danhSachKhach.isEmpty()) {
            // Nhóm các ghế có cùng loại vé và đơn giá
            Map<String, MapGroup> groups = new LinkedHashMap<>();

            for (Map.Entry<String, TempKhachHang> entry : danhSachKhach.entrySet()) {
                String maCho = entry.getKey();
                TempKhachHang khach = entry.getValue();
                String maLoaiVe = khach.maLoaiVe;
                Long giaDonVi = danhSachGiaVe.get(maCho); // Giá đã được tính (đơn giá + chiết khấu nếu có)

                if (giaDonVi != null) {
                    // Sử dụng mã loại vé làm key nhóm
                    if (!groups.containsKey(maLoaiVe)) {
                        // Giá đơn vị ở đây là giá đã giảm (nếu có chiết khấu loại vé)
                        groups.put(maLoaiVe, new MapGroup(khach.tenLoaiVeHienThi(), giaDonVi));
                    }
                    groups.get(maLoaiVe).soLuong++;
                }
            }

            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            int stt = 1;
            for (Map.Entry<String, MapGroup> entry : groups.entrySet()) {
                MapGroup group = entry.getValue();

                // Giả định: Thuế VAT 10% được tính trên giá TRƯỚC VAT
                // Trong hàm computeTicketPrice, bạn đã tính giá cuối cùng
                // Để đơn giản, ta tính ngược lại:
                double giaTruocVAT = group.giaDonVi / 1.1; // Giá trước VAT
                double thueVAT = group.giaDonVi - giaTruocVAT; // Phần thuế VAT

                double tongTienTruocVAT = giaTruocVAT * group.soLuong;
                double tongThue = thueVAT * group.soLuong;
                double thanhTienCoVAT = group.giaDonVi * group.soLuong;

                modelHd.addRow(new Object[]{
                        stt++,
                        group.tenLoaiVe,
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

        return panelHoaDon;
    }
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

            List<KhuyenMai> promos = khuyenMaiDAO.getAllActivePromosWithConditions();
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

        // Code cũ:
        // if (danhSachGhe == null) return 0;
        // int count = danhSachGhe.size();
        // double gia1 = 800000;
        // return count * gia1;
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

    private void xacNhanThanhToan() {
        // TODO: khi lưu hóa đơn vào DB, lưu khuyenMaiApDung.getMaKM() vào trường MaKM của hóa đơn nếu có
        JOptionPane.showMessageDialog(this, "Xác nhận thanh toán (chưa nối backend). Mã KM: " +
                (khuyenMaiApDung != null ? khuyenMaiApDung.getMaKM() : "Không có"), "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void ketThuc() {
        JOptionPane.showMessageDialog(this, "Kết thúc (chưa nối backend).");
    }

    private String taoMaHoaDon() {
        return "HD" + System.currentTimeMillis();
    //    return "HD" + ca + ddmmyy + MaNV + stt

    }

    private static class Tong {
        double subtotal;
        double discount;
        double vat;
        double total;
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