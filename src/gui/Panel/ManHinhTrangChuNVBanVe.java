package gui.Panel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.Timer;

import control.CaLamViec;
import dao.DashboardDAO;
import entity.NhanVien;
import gui.MainFrame.BanVeDashboard;

/**
 * Lớp ManHinhTrangChuNVBanVe: Dashboard hiển thị thông tin chính cho Nhân viên bán vé
 * Đã xóa bỏ các dữ liệu mẫu, dữ liệu được đổ động từ DashboardDAO.
 */
public class ManHinhTrangChuNVBanVe extends JPanel {

    // --- HẰNG SỐ GIAO DIỆN ---
    private static final Color MAU_NEN = Color.decode("#F5F5F5");
    private static final Color MAU_NEN_CARD = Color.WHITE;
    private static final Color MAU_CHINH = Color.decode("#3F51B5");
    private static final Color MAU_NHAN = Color.decode("#FF9800");

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO =
            DateTimeFormatter.ofPattern("HH:mm:ss EEEE, 'ngày' dd 'tháng' MM 'năm' yyyy", new Locale("vi", "VN"));

    // --- THÀNH PHẦN GUI CẦN CẬP NHẬT DỮ LIỆU ---
    private JLabel nhanGiaTriDoanhThu;
    private JTextPane oVanBanThongBao;
    private JPanel panelPlaceholderDoThi;
    private JLabel nhanTen;
    private JLabel nhanGiaTriNgayNghi;
    private JLabel nhanGiaTriLuong;

    // --- BIẾN DỮ LIỆU ---
    private String tenNhanVienHienThi = "";
    private String luongCoBanHienThi = "0";
    private int ngayNghiConLaiHienThi = 0;
    private BanVeDashboard mainFrame;

    public ManHinhTrangChuNVBanVe(BanVeDashboard mainFrame) {
        this.mainFrame = mainFrame;

        // 1. Lấy thông tin cơ bản của nhân viên đăng nhập
        layDuLieuNhanVien();

        // 2. Thiết lập Layout chính
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(MAU_NEN);

        // Header
        add(taoPanelTieuDe(), BorderLayout.NORTH);

        // Nội dung chính
        JPanel panelNoiDungChinh = new JPanel(new BorderLayout(20, 20));
        panelNoiDungChinh.setOpaque(false);
        panelNoiDungChinh.add(taoPanelLienKetNhanh(), BorderLayout.NORTH);

        JPanel panelNoiDungDuoi = new JPanel(new GridLayout(1, 3, 20, 0));
        panelNoiDungDuoi.setOpaque(false);

        // Card 1: Thống kê & Chuyến tàu
        JPanel panelThongKeVaAnh = new JPanel(new BorderLayout(0, 20));
        panelThongKeVaAnh.setOpaque(false);
        panelThongKeVaAnh.add(taoPanelThongKe(), BorderLayout.NORTH);
        panelThongKeVaAnh.add(taoCardAnhNho(), BorderLayout.CENTER);

        // Card 2: Thông báo (Khuyến mãi)
        panelNoiDungDuoi.add(panelThongKeVaAnh);
        panelNoiDungDuoi.add(taoPanelThongBao());
        panelNoiDungDuoi.add(taoPanelThongTinNhanVien());

        panelNoiDungChinh.add(panelNoiDungDuoi, BorderLayout.CENTER);
        add(panelNoiDungChinh, BorderLayout.CENTER);

        // 3. Đổ dữ liệu từ Database vào các thành phần đã tạo
        capNhatDuLieuDashboard();
    }

    private void layDuLieuNhanVien() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv != null) {
            this.tenNhanVienHienThi = nv.getHoTen();
            this.luongCoBanHienThi = "Chưa cập nhật";
            this.ngayNghiConLaiHienThi = 0;
        } else {
            this.tenNhanVienHienThi = "Người dùng";
        }
    }

    /**
     * Hàm quan trọng nhất: Lấy dữ liệu thực từ DAO và hiển thị lên giao diện
     */
    private void capNhatDuLieuDashboard() {
        DashboardDAO dao = new DashboardDAO();
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();

        if (nv == null) return;

        // 1. Cập nhật Doanh thu
        Map<String, Object> thongKe = dao.getThongKeTrongNgay(nv.getMaNV());
        double doanhThu = (double) thongKe.get("doanhThu");
        nhanGiaTriDoanhThu.setText(String.format("%,.0f VND", doanhThu));

        // 2. Cập nhật Thông báo (Khuyến mãi từ DB)
        List<Map<String, String>> dsKM = dao.getKhuyenMaiHienNay(); // Danh sách bây giờ là List<Map>


        pnlKhuyenMaiContainer.removeAll();
        pnlKhuyenMaiContainer.setLayout(new BoxLayout(pnlKhuyenMaiContainer, BoxLayout.Y_AXIS));

        if (dsKM.isEmpty()) {
            pnlKhuyenMaiContainer.add(new JLabel("  Hiện không có khuyến mãi nào."));
        } else {
            // SỬA LỖI TẠI ĐÂY: Đổi String km thành Map<String, String> km
            for (Map<String, String> km : dsKM) {
                // Lấy dữ liệu từ Map thông qua Key
                String ten = km.get("ten");
                String dk = km.get("dieukien");
                String giam = km.get("giamgia");

                // Thêm thẻ khuyến mãi vào giao diện
                pnlKhuyenMaiContainer.add(taoCardKhuyenMaiChiTiet(ten, dk, giam));

                // Tạo khoảng cách 10 pixel giữa các thẻ
                pnlKhuyenMaiContainer.add(Box.createVerticalStrut(10));
            }
        }

        // 3. Cập nhật Chuyến tàu sắp chạy
        List<String[]> dsTau = dao.getChuyenTauSapChay();
        panelPlaceholderDoThi.removeAll();
        panelPlaceholderDoThi.setLayout(new GridLayout(6, 1, 0, 2));

        JLabel tieuDeTau = new JLabel("🚀 CHUYẾN TÀU SẮP KHỞI HÀNH:");
        tieuDeTau.setFont(new Font("Arial", Font.BOLD, 12));
        tieuDeTau.setForeground(MAU_CHINH);
        panelPlaceholderDoThi.add(tieuDeTau);

        if (dsTau.isEmpty()) {
            panelPlaceholderDoThi.add(new JLabel("  Không có chuyến tàu nào sắp khởi hành."));
        } else {
            for (String[] tau : dsTau) {
                panelPlaceholderDoThi.add(new JLabel(String.format("  [%s] %s → %s (%s)", tau[0], tau[1], tau[2], tau[3])));
            }
        }
        panelPlaceholderDoThi.revalidate();
        panelPlaceholderDoThi.repaint();
    }
    private JPanel taoCardKhuyenMaiChiTiet(String ten, String dieuKien, String giamGia) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.decode("#E3F2FD")); // Màu xanh nhạt cực sang
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.decode("#BBDEFB"), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));



        // Thông tin text ở giữa
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);

        JLabel lblTen = new JLabel(ten.toUpperCase());
        lblTen.setFont(new Font("Arial", Font.BOLD, 13));
        lblTen.setForeground(Color.decode("#1976D2"));

        JLabel lblDK = new JLabel("<html>ĐK: " + dieuKien + "</html>");
        lblDK.setFont(new Font("Arial", Font.ITALIC, 11));
        lblDK.setForeground(Color.DARK_GRAY);


        info.add(lblTen);
        info.add(lblDK);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    // --- CÁC PHƯƠNG THỨC TẠO PANEL (Đã dọn dẹp dữ liệu tĩnh) ---

    private JPanel taoPanelTieuDe() {
        JPanel panelTieuDe = new JPanel(new BorderLayout(10, 0));
        panelTieuDe.setOpaque(false);

        JPanel panelChaoMung = new JPanel();
        panelChaoMung.setLayout(new BoxLayout(panelChaoMung, BoxLayout.Y_AXIS));
        panelChaoMung.setOpaque(false);

        JLabel nhanChaoMung = new JLabel("Dashboard | Xin chào,");
        nhanChaoMung.setFont(new Font("Arial", Font.PLAIN, 18));
        nhanChaoMung.setForeground(Color.GRAY);

        nhanTen = new JLabel(tenNhanVienHienThi + "!");
        nhanTen.setFont(new Font("Arial", Font.BOLD, 28));

        panelChaoMung.add(nhanChaoMung);
        panelChaoMung.add(nhanTen);

        JLabel nhanNgayGio = new JLabel("", SwingConstants.RIGHT);
        khoiDongDongHo(nhanNgayGio);

        String chuCaiDau = tenNhanVienHienThi.isEmpty() ? "U" : tenNhanVienHienThi.substring(0, 1).toUpperCase();

        panelTieuDe.add(panelChaoMung, BorderLayout.WEST);
        panelTieuDe.add(nhanNgayGio, BorderLayout.CENTER);
        panelTieuDe.add(taoPanelAvatar(chuCaiDau), BorderLayout.EAST);

        return panelTieuDe;
    }

    private void khoiDongDongHo(JLabel nhanNgayGio) {
        new javax.swing.Timer(1000, e -> {
            nhanNgayGio.setText(LocalDateTime.now().format(DINH_DANG_NGAY_GIO));
        }).start();
    }

    private JPanel taoPanelThongKe() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MAU_NEN_CARD);
        panel.setBorder(taoBorderCard());

        JLabel nhanTieuDe = new JLabel("Doanh thu hôm nay");
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        nhanTieuDe.setForeground(MAU_CHINH);

        nhanGiaTriDoanhThu = new JLabel("0 VND"); // Mặc định là 0
        nhanGiaTriDoanhThu.setFont(new Font("Arial", Font.BOLD, 24));
        nhanGiaTriDoanhThu.setForeground(MAU_NHAN);

        panel.add(nhanTieuDe, BorderLayout.NORTH);
        panel.add(nhanGiaTriDoanhThu, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoCardAnhNho() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_CARD);
        panel.setBorder(taoBorderCard());

        JLabel nhanTieuDe = new JLabel("Lịch trình vận hành");
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(nhanTieuDe, BorderLayout.NORTH);

        panelPlaceholderDoThi = new JPanel();
        panelPlaceholderDoThi.setBackground(Color.decode("#F0F8FF"));
        panelPlaceholderDoThi.setPreferredSize(new Dimension(10, 120));

        panel.add(panelPlaceholderDoThi, BorderLayout.CENTER);
        return panel;
    }
    //biến toàn cực
    private JPanel pnlKhuyenMaiContainer;

    private JPanel taoPanelThongBao() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MAU_NEN_CARD);
        panel.setBorder(taoBorderCard());

        JLabel nhanTieuDe = new JLabel("Chương trình Khuyến mãi");
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        nhanTieuDe.setForeground(MAU_CHINH);
        panel.add(nhanTieuDe, BorderLayout.NORTH);

        pnlKhuyenMaiContainer = new JPanel();
        pnlKhuyenMaiContainer.setBackground(MAU_NEN_CARD);

        JScrollPane thanhCuon = new JScrollPane(pnlKhuyenMaiContainer);
        thanhCuon.setBorder(null);
        thanhCuon.getVerticalScrollBar().setUnitIncrement(16); // Cuộn mượt hơn

        panel.add(thanhCuon, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelThongTinNhanVien() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MAU_NEN_CARD);
        panel.setBorder(taoBorderCard());

        JLabel nhanTieuDe = new JLabel("Thông tin cá nhân");
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        nhanTieuDe.setForeground(MAU_CHINH);
        panel.add(nhanTieuDe, BorderLayout.NORTH);

        JPanel panelNoiDung = new JPanel(new GridBagLayout());
        panelNoiDung.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        nhanGiaTriNgayNghi = new JLabel(ngayNghiConLaiHienThi + " ngày");
        nhanGiaTriLuong = new JLabel(luongCoBanHienThi + " VND");

        gbc.gridy = 0; panelNoiDung.add(new JLabel("Ngày nghỉ còn lại:"), gbc);
        gbc.gridx = 1; panelNoiDung.add(nhanGiaTriNgayNghi, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelNoiDung.add(new JLabel("Lương cơ bản:"), gbc);
        gbc.gridx = 1; panelNoiDung.add(nhanGiaTriLuong, gbc);

        panel.add(panelNoiDung, BorderLayout.CENTER);
        return panel;
    }

    // --- CÁC PHƯƠNG THỨC HỖ TRỢ KHÁC (GIỮ NGUYÊN) ---

    private JPanel taoPanelAvatar(String chuCaiDau) {
        JPanel panelAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(MAU_CHINH);
                g.fillOval(0, 0, 50, 50);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString(chuCaiDau, 16, 34);
            }
        };
        panelAvatar.setPreferredSize(new Dimension(50, 50));
        panelAvatar.setOpaque(false);
        return panelAvatar;
    }

    private JPanel taoPanelLienKetNhanh() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        // 1. Nút Bán vé (Giữ nguyên)
        panel.add(taoNutLienKetNhanh("Bán vé",
                "<html>Màn hình tạo vé<br>mới cho khách hàng</html>",
                "banVeMoi"));

        // 2. Nút Trả vé (Thay thế 'Tra cứu' cũ)
        panel.add(taoNutLienKetNhanh("Trả vé",
                "<html>Xử lý hoàn tiền<br>và hủy vé hệ thống</html>",
                "traVe"));

        // 3. Nút Đổi vé (Thay thế 'Khuyến mãi' cũ)
        panel.add(taoNutLienKetNhanh("Đổi vé",
                "<html>Thay đổi lịch trình<br>hoặc thông tin vé</html>",
                "doiVe"));

        // 4. Nút Tra cứu vé (Thay thế 'Cài đặt' cũ)
        panel.add(taoNutLienKetNhanh("Tra cứu vé",
                "<html>Tìm kiếm thông tin<br>vé qua mã hoặc SĐT</html>",
                "traCuuVe"));return panel;
    }

    private JButton taoNutLienKetNhanh(String tieuDe, String moTa, String maCard) {
        JButton nut = new JButton();
        nut.setLayout(new BorderLayout(5, 5));
        nut.setBackground(MAU_NEN_CARD);
        nut.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1), new EmptyBorder(15, 15, 15, 15)));
        nut.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel t = new JLabel(tieuDe); t.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel m = new JLabel(moTa); m.setFont(new Font("Arial", Font.PLAIN, 12)); m.setForeground(Color.GRAY);

        nut.add(t, BorderLayout.NORTH);
        nut.add(m, BorderLayout.CENTER);
        nut.addActionListener(e -> {
            if (mainFrame != null) mainFrame.chuyenManHinh(maCard);
        });
        return nut;
    }

    private Border taoBorderCard() {
        return BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1), new EmptyBorder(15, 15, 15, 15));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ManHinhTrangChuNVBanVe(null));
            frame.setSize(1200, 750);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}