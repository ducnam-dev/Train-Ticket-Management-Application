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
    private String maChuyen;
    private Date ngayDi;

    private JTable tblKhach;
    private JTable tblHoaDon;
    private JLabel lblTongTien;
    private JComboBox<String> cbHinhThuc;
    private JTextField txtTienKhachDua;
    private JTextField txtTienThoiLai;

    // UI Khuyến mãi
    private JComboBox<KhuyenMai> cbKhuyenMai;
    private JButton btnApDungKM;
    private JButton btnXoaKM;
    private JLabel lblThongTinKM;

    // Khuyến mãi đang áp dụng
    private KhuyenMai khuyenMaiApDung = null;

    // DAO
    private KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();

    private static final Color GREEN_BTN = new Color(0, 180, 110);
    private static final Color ORANGE_BTN = new Color(255, 140, 0);

    public ManHinhXacNhanBanVe() {
        this(null, null, null, null);
    }

    public ManHinhXacNhanBanVe(Map<String, ChoDat> danhSachGhe,
                               Map<String, TempKhachHang> danhSachKhach,
                               String maChuyen, Date ngayDi) {
        this.danhSachGhe = danhSachGhe;
        this.danhSachKhach = danhSachKhach;
        this.maChuyen = maChuyen;
        this.ngayDi = ngayDi;

        initUI();

        // Tải khuyến mãi từ DB và cập nhật giao diện
        taiKhuyenMaiTuDB();
        capNhatTongVaGiaoDien();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Bán vé - xác nhận thanh toán");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setBorder(new EmptyBorder(6, 6, 12, 6));
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.5);
        split.setDividerSize(8);
        split.setBorder(null);
        split.setBackground(getBackground());

        split.setLeftComponent(buildLeftPanel());
        split.setRightComponent(buildRightPanel());

        add(split, BorderLayout.CENTER);
    }

    private JPanel buildLeftPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(new Color(255, 255, 255));
        left.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false),
                new EmptyBorder(12, 12, 12, 12)));

        left.add(buildBookingInfoPanel());
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(buildCustomerListPanel());
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(buildTotalPanel());
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(buildPaymentPanel());
        left.add(Box.createVerticalGlue());

        return left;
    }

    private JPanel buildBookingInfoPanel() {
        JPanel info = new JPanel(new BorderLayout());
        info.setOpaque(false);
        info.setBorder(BorderFactory.createTitledBorder("Thông tin đặt vé"));

        JPanel c = new JPanel(new GridLayout(0, 1));
        c.setOpaque(false);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String ngayStr = ngayDi != null ? df.format(ngayDi) : "-";

        c.add(new JLabel("Mã tàu: " + (maChuyen != null ? maChuyen : "-")));
        c.add(new JLabel("Ngày giờ: " + ngayStr));
        int count = danhSachGhe != null ? danhSachGhe.size() : 0;
        c.add(new JLabel("Số lượng ghế: " + count));

        info.add(c, BorderLayout.CENTER);
        return info;
    }

    private JPanel buildCustomerListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách khách hàng"));

        String[] columns = {"Ghế", "Họ và tên", "Tuổi", "Số điện thoại", "CCCD"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
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

        tblKhach = new JTable(model);
        tblKhach.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(tblKhach);
        sp.setPreferredSize(new Dimension(520, 160));
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTotalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Tổng hóa đơn"));

        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setOpaque(false);

        double giaVe = tinhGiaVe();
        Totals t = tinhTong(giaVe, khuyenMaiApDung);

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        c.add(new JLabel("Giá vé: " + nf.format((long) t.subtotal) + " VND"));
        String applied = (khuyenMaiApDung != null) ? khuyenMaiApDung.getMaKM() + " - " + khuyenMaiApDung.getTenKM() : "Không";
        c.add(new JLabel("Chiết khấu áp dụng: " + applied));
        c.add(new JLabel("Thuế VAT (10%): " + nf.format((long) t.vat) + " VND"));

        lblTongTien = new JLabel("Tổng số tiền phải thanh toán: " + nf.format((long) t.total) + " VND");
        lblTongTien.setFont(lblTongTien.getFont().deriveFont(Font.BOLD));
        lblTongTien.setForeground(new Color(200, 60, 10));
        c.add(Box.createRigidArea(new Dimension(0, 6)));
        c.add(lblTongTien);

        panel.add(c, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPaymentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Thanh toán"));

        // Khuyến mãi: combobox lấy từ DB
        JPanel rowPromo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowPromo.setOpaque(false);
        rowPromo.add(new JLabel("Khuyến mãi: "));

        cbKhuyenMai = new JComboBox<>();
        cbKhuyenMai.setPreferredSize(new Dimension(320, 26));
        rowPromo.add(cbKhuyenMai);

        btnApDungKM = new JButton("Áp dụng");
        btnApDungKM.addActionListener(e -> apDungKhuyenMaiChon());
        rowPromo.add(btnApDungKM);

        btnXoaKM = new JButton("Xóa KM");
        btnXoaKM.setEnabled(false);
        btnXoaKM.addActionListener(e -> xoaKhuyenMai());
        rowPromo.add(btnXoaKM);

        lblThongTinKM = new JLabel(" ");
        rowPromo.add(lblThongTinKM);

        panel.add(rowPromo);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.setOpaque(false);
        row1.add(new JLabel("Hình thức thanh toán: "));
        cbHinhThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"});
        row1.add(cbHinhThuc);
        panel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setOpaque(false);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        double tongNoPromo = tinhGiaVe() * 1.10;
        txtTienKhachDua = new JTextField(nf.format((long) tongNoPromo), 12);
        txtTienThoiLai = new JTextField(nf.format(0), 10);
        txtTienThoiLai.setEditable(false);
        row2.add(new JLabel("Tiền khách đưa: "));
        row2.add(txtTienKhachDua);
        row2.add(new JLabel("Tiền thối lại: "));
        row2.add(txtTienThoiLai);
        panel.add(row2);

        JPanel suggestions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        suggestions.setOpaque(false);
        suggestions.add(new JLabel("Gợi ý: "));
        List<Long> hints = Arrays.asList((long) Math.round(tongNoPromo), (long) (tongNoPromo + 30000), (long) (tongNoPromo + 50000), (long) (tongNoPromo + 100000));
        for (Long h : hints) {
            JButton b = new JButton(nf.format(h));
            b.setMargin(new Insets(4, 8, 4, 8));
            b.addActionListener(e -> {
                txtTienKhachDua.setText(nf.format(h));
                capNhatTienThoi();
            });
            suggestions.add(b);
        }
        panel.add(suggestions);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton btnConfirm = new JButton("Xác nhận thanh toán");
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setBackground(GREEN_BTN);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(180, 36));
        btnConfirm.addActionListener(e -> xacNhanThanhToan());
        btnPanel.add(btnConfirm);

        panel.add(btnPanel);

        txtTienKhachDua.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { capNhatTienThoi(); }
        });

        return panel;
    }

    private JPanel buildRightPanel() {
        // (giữ nguyên phần hóa đơn; khi lưu/in hóa đơn, gắn khuyenMaiApDung.getMaKM() vào hóa đơn)
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(new Color(255, 255, 255));
        right.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false),
                new EmptyBorder(12, 12, 12, 12)));

        JLabel hdTitle = new JLabel("Hóa đơn mua vé");
        hdTitle.setFont(hdTitle.getFont().deriveFont(Font.BOLD, 16f));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(hdTitle, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new GridLayout(0, 2));
        info.add(new JLabel("Mã hóa đơn: "));
        info.add(new JLabel(taoMaHoaDon()));
        info.add(new JLabel("Người lập hóa đơn: "));
        info.add(new JLabel("Nguyễn Bảo Duy"));
        info.add(new JLabel("Điện thoại: "));
        info.add(new JLabel("0332534500"));
        header.add(info, BorderLayout.CENTER);

        right.add(header, BorderLayout.NORTH);

        String[] colHd = {"STT", "Mã vé", "Số lượng", "Đơn giá", "Thuế VAT", "Thành tiền có thuế"};
        DefaultTableModel modelHd = new DefaultTableModel(colHd, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        if (danhSachGhe != null && !danhSachGhe.isEmpty()) {
            Map<String, Long> ticketCount = new LinkedHashMap<>();
            for (ChoDat c : danhSachGhe.values()) {
                String mav = "VSE" + (c.getMaToa() == null ? "" : c.getMaToa()) + c.getSoCho();
                ticketCount.put(mav, ticketCount.getOrDefault(mav, 0L) + 1);
            }
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            int stt = 1;
            for (Map.Entry<String, Long> e : ticketCount.entrySet()) {
                long unit = 800000;
                long qty = e.getValue();
                long thuet = Math.round(unit * 0.10);
                long total = Math.round((unit + thuet) * qty);
                modelHd.addRow(new Object[]{stt++, e.getKey(), qty, nf.format(unit), "10%", nf.format(total)});
            }
        } else {
            modelHd.addRow(new Object[]{"", "", "", "", "", ""});
        }

        tblHoaDon = new JTable(modelHd);
        tblHoaDon.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(tblHoaDon);
        sp.setPreferredSize(new Dimension(480, 300));
        right.add(sp, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JPanel sumPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sumPanel.setOpaque(false);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        double tong = tinhGiaVe() * 1.10;
        JLabel lbl = new JLabel("Tổng cộng: " + nf.format((long) tong));
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        sumPanel.add(lbl);
        footer.add(sumPanel, BorderLayout.NORTH);

        JPanel endBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        endBtnPanel.setOpaque(false);
        JButton btnEnd = new JButton("Kết thúc >");
        btnEnd.setBackground(ORANGE_BTN);
        btnEnd.setForeground(Color.WHITE);
        btnEnd.setPreferredSize(new Dimension(140, 36));
        btnEnd.addActionListener(e -> ketThuc());
        endBtnPanel.add(btnEnd);
        footer.add(endBtnPanel, BorderLayout.SOUTH);

        right.add(footer, BorderLayout.SOUTH);

        return right;
    }

    // Tải khuyến mãi từ DB vào combobox
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

    // Kiểm tra điều kiện khuyến mãi
    private boolean khuyenMaiHopLe(KhuyenMai km) {
        if (km == null || km.getMaKM() == null || km.getMaKM().isEmpty()) return false;
        double subtotal = tinhGiaVe();
        int ticketCount = (danhSachGhe == null) ? 0 : danhSachGhe.size();

        if (km.getDieuKienList() == null || km.getDieuKienList().isEmpty()) {
            return true;
        }

        for (DieuKienKhuyenMai dk : km.getDieuKienList()) {
            String loai = dk.getLoaiDieuKien();
            double val = dk.getGiaTriAsDouble();
            if ("MIN_TICKETS".equalsIgnoreCase(loai)) {
                if (ticketCount < (int) Math.round(val)) return false;
            } else if ("MIN_AMOUNT".equalsIgnoreCase(loai)) {
                if (subtotal < val) return false;
            } else {
                // loại không hỗ trợ => không áp dụng
                return false;
            }
        }
        return true;
    }

    // Áp dụng khuyến mãi đang chọn
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
        btnXoaKM.setEnabled(true);
        lblThongTinKM.setText("Đã áp dụng: " + sel.getMaKM());
        capNhatTongVaGiaoDien();
        JOptionPane.showMessageDialog(this, "Áp dụng khuyến mãi thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xoaKhuyenMai() {
        khuyenMaiApDung = null;
        btnXoaKM.setEnabled(false);
        lblThongTinKM.setText(" ");
        capNhatTongVaGiaoDien();
    }

    // Tính tổng (subtotal, discount, vat, total)
    private Totals tinhTong(double subtotal, KhuyenMai km) {
        Totals t = new Totals();
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
        Totals t = tinhTong(subtotal, khuyenMaiApDung);
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
        if (danhSachGhe == null) return 0;
        int count = danhSachGhe.size();
        double gia1 = 800000;
        return count * gia1;
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
            Totals t = tinhTong(subtotal, khuyenMaiApDung);
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

    private static class Totals {
        double subtotal;
        double discount;
        double vat;
        double total;
    }

    // main test nếu cần
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