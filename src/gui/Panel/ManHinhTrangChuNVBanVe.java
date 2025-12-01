package gui.Panel;

import javax.swing.*;
import javax.swing.border.Border; // Thêm import này
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
import java.util.Locale;

// Thêm các lớp cần thiết cho Session
import control.CaLamViec; // Giả định package control chứa CaLamViec
import entity.NhanVien; // Giả định package entity chứa NhanVien

/**
 * Lớp ManHinhTrangChuNVBanVe: Dashboard hiển thị thông tin chính cho Nhân viên bán vé
 */
public class ManHinhTrangChuNVBanVe extends JPanel {

    // --- HẰNG SỐ CƠ SỞ (Chỉ dùng cho Màu sắc/Định dạng) ---
    private static final Color MAU_NEN = Color.decode("#F5F5F5");
    private static final Color MAU_NEN_CARD = Color.WHITE;
    private static final Color MAU_CHINH = Color.decode("#3F51B5"); // Xanh dương
    private static final Color MAU_NHAN = Color.decode("#FF9800"); // Cam (dùng cho thống kê)

    // Định dạng ngày giờ với Giờ:Phút:Giây và Ngày tháng năm
    private static final DateTimeFormatter DINH_DANG_NGAY_GIO =
            DateTimeFormatter.ofPattern("HH:mm:ss EEEE, 'ngày' dd 'tháng' MM 'năm' yyyy", new Locale("vi", "VN"));

    // --- Dữ liệu động (Lấy từ CaLamViec) và dữ liệu Placeholder ---
    private String tenNhanVienHienThi;
    private String luongCoBanHienThi;
    private int ngayNghiConLaiHienThi;
    private static final String DOANH_THU_HOM_NAY = "15.200.000"; // Giả định dữ liệu này là tĩnh hoặc được tính toán ở nơi khác

    public ManHinhTrangChuNVBanVe() {

        // --- LẤY DỮ LIỆU TỪ CA LÀM VIỆC ---
        layDuLieuNhanVien();

        // --- THIẾT LẬP GIAO DIỆN ---
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(MAU_NEN);

        // =========================================================================
        // PHẦN TRÊN CÙNG (Chào, Ngày & Giờ, Avatar) - HEADER
        // =========================================================================
        JPanel panelTieuDe = taoPanelTieuDe();
        add(panelTieuDe, BorderLayout.NORTH);

        // =========================================================================
        // PHẦN TRUNG TÂM (Liên kết nhanh, Thống kê, Thông báo, Thông tin)
        // =========================================================================
        JPanel panelNoiDungChinh = new JPanel(new BorderLayout(20, 20));
        panelNoiDungChinh.setOpaque(false);

        // 1. Dòng trên: Liên kết nhanh
        panelNoiDungChinh.add(taoPanelLienKetNhanh(), BorderLayout.NORTH);

        // 2. Dòng dưới: 3 Card
        JPanel panelNoiDungDuoi = new JPanel(new GridLayout(1, 3, 20, 0));
        panelNoiDungDuoi.setOpaque(false);

        // Card 1: Thống kê & Hình ảnh Placeholder
        JPanel panelThongKeVaAnh = new JPanel(new BorderLayout(0, 20));
        panelThongKeVaAnh.setOpaque(false);
        panelThongKeVaAnh.add(taoPanelThongKe(), BorderLayout.NORTH);
        panelThongKeVaAnh.add(taoCardAnhNho(), BorderLayout.CENTER);

        // Card 2: Thông báo
        JPanel cardThongBao = taoPanelThongBao();

        // Card 3: Thông tin cá nhân
        JPanel cardThongTinNV = taoPanelThongTinNhanVien();

        panelNoiDungDuoi.add(panelThongKeVaAnh);
        panelNoiDungDuoi.add(cardThongBao);
        panelNoiDungDuoi.add(cardThongTinNV);

        panelNoiDungChinh.add(panelNoiDungDuoi, BorderLayout.CENTER);

        add(panelNoiDungChinh, BorderLayout.CENTER);
    }

    /**
     * Lấy dữ liệu nhân viên từ CaLamViec và gán vào các biến
     */
    private void layDuLieuNhanVien() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();

        if (nv != null) {
            this.tenNhanVienHienThi = nv.getHoTen();
            // Lấy các thông tin khác từ CSDL (nếu lớp NhanVien có các trường này)
            // Vì không có NhanVienDAO trong context này, ta dùng giá trị tĩnh làm placeholder
            // Nhưng vẫn ưu tiên tên động:
            this.luongCoBanHienThi = "7.567.000"; // Giả định lấy từ nv.getLuongCoBan()
            this.ngayNghiConLaiHienThi = 5;       // Giả định lấy từ hệ thống
        } else {
            // Dữ liệu dự phòng nếu chưa đăng nhập (Lỗi hệ thống)
            this.tenNhanVienHienThi = "Khách (Chưa đăng nhập)";
            this.luongCoBanHienThi = "N/A";
            this.ngayNghiConLaiHienThi = 0;
        }
    }


    // =========================================================================
    // PHƯƠNG THỨC TẠO PANEL ĐÃ VIỆT HÓA
    // =========================================================================

    /**
     * Tạo panel Tiêu đề (Chào, Ngày & Giờ, Avatar) với đồng hồ thời gian thực
     */
    private JPanel taoPanelTieuDe() {
        JPanel panelTieuDe = new JPanel(new BorderLayout(10, 0));
        panelTieuDe.setOpaque(false);

        // 1. Chào nhân viên
        JPanel panelChaoMung = new JPanel();
        panelChaoMung.setLayout(new BoxLayout(panelChaoMung, BoxLayout.Y_AXIS));
        panelChaoMung.setOpaque(false);
        panelChaoMung.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nhanChaoMung = new JLabel("👋 Dashboard | Xin chào,");
        nhanChaoMung.setFont(new Font("Arial", Font.PLAIN, 18));
        nhanChaoMung.setForeground(Color.GRAY);
        nhanChaoMung.setAlignmentX(LEFT_ALIGNMENT);

        // Dùng dữ liệu động
        JLabel nhanTen = new JLabel(tenNhanVienHienThi + "!");
        nhanTen.setFont(new Font("Arial", Font.BOLD, 28));
        nhanTen.setForeground(Color.BLACK);
        nhanTen.setAlignmentX(LEFT_ALIGNMENT);

        panelChaoMung.add(nhanChaoMung);
        panelChaoMung.add(nhanTen);

        // 2. Ngày & Giờ
        JLabel nhanNgayGio = new JLabel("", SwingConstants.RIGHT);
        nhanNgayGio.setFont(new Font("Arial", Font.PLAIN, 14));
        nhanNgayGio.setForeground(Color.DARK_GRAY);

        // Bắt đầu đồng hồ thời gian thực
        khoiDongDongHo(nhanNgayGio);

        // 3. Avatar
        String chuCaiDau = tenNhanVienHienThi.substring(0, 1).toUpperCase();
        JPanel panelAvatar = taoPanelAvatar(chuCaiDau);

        panelTieuDe.add(panelChaoMung, BorderLayout.WEST);
        panelTieuDe.add(nhanNgayGio, BorderLayout.CENTER);
        panelTieuDe.add(panelAvatar, BorderLayout.EAST);

        return panelTieuDe;
    }

    /**
     * Thiết lập Timer để cập nhật thời gian mỗi giây.
     */
    private void khoiDongDongHo(JLabel nhanNgayGio) {
        ActionListener capNhatDongHo = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String thoiGianHienTai = LocalDateTime.now().format(DINH_DANG_NGAY_GIO);
                nhanNgayGio.setText(thoiGianHienTai);
            }
        };

        Timer timer = new Timer(1000, capNhatDongHo);
        timer.setInitialDelay(0);
        timer.start();
    }

    /**
     * Tạo panel chứa biểu tượng chữ cái đầu (Avatar)
     */
    private JPanel taoPanelAvatar(String chuCaiDau) {
        JPanel panelAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(MAU_CHINH); // Màu xanh dương cho nền
                int diameter = Math.min(getWidth(), getHeight());
                g.fillOval(0, 0, diameter, diameter);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                FontMetrics fm = g.getFontMetrics();
                int x = (diameter - fm.stringWidth(chuCaiDau)) / 2;
                int y = (diameter - fm.getHeight()) / 2 + fm.getAscent();
                g.drawString(chuCaiDau, x, y);
            }
        };
        panelAvatar.setPreferredSize(new Dimension(50, 50));
        panelAvatar.setOpaque(false);
        return panelAvatar;
    }

    /**
     * Tạo panel Liên kết nhanh với các nút điều hướng
     */
    private JPanel taoPanelLienKetNhanh() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0)); // 4 nút
        panel.setOpaque(false);

        // Thêm các nút điều hướng nhanh
        panel.add(taoNutLienKetNhanh("🎫 Bán vé", "<html>Đến màn hình<br>tạo và bán vé</html>"));
        panel.add(taoNutLienKetNhanh("🔍 Tra cứu", "<html>Tra cứu thông tin<br>chuyến tàu, vé</html>"));
        panel.add(taoNutLienKetNhanh("💲 Khuyến mãi", "<html>Quản lý các chương<br>trình khuyến mãi</html>"));
        panel.add(taoNutLienKetNhanh("⚙️ Cài đặt", "<html>Thiết lập tài khoản<br>và hệ thống</html>"));

        return panel;
    }

    /**
     * Tạo một nút Liên kết nhanh
     */
    private JButton taoNutLienKetNhanh(String tieuDe, String moTa) {
        JButton nut = new JButton();
        nut.setLayout(new BorderLayout(5, 5));
        nut.setBackground(MAU_NEN_CARD);
        nut.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        nut.setHorizontalAlignment(SwingConstants.LEFT);
        nut.setFocusPainted(false);
        nut.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel nhanTieuDe = new JLabel(tieuDe);
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel nhanMoTa = new JLabel(moTa);
        nhanMoTa.setFont(new Font("Arial", Font.PLAIN, 12));
        nhanMoTa.setForeground(Color.GRAY);

        nut.add(nhanTieuDe, BorderLayout.NORTH);
        nut.add(nhanMoTa, BorderLayout.CENTER);

        nut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Chức năng " + tieuDe + " đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        nut.addChangeListener(e -> {
            AbstractButton btn = (AbstractButton) e.getSource();
            if (btn.getModel().isRollover()) {
                btn.setBackground(Color.decode("#E0E0E0"));
            } else {
                btn.setBackground(MAU_NEN_CARD);
            }
        });

        return nut;
    }

    /**
     * Tạo panel Thống kê (Doanh thu hôm nay)
     */
    private JPanel taoPanelThongKe() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MAU_NEN_CARD);
        panel.setBorder(taoBorderCard());
        panel.setPreferredSize(new Dimension(300, 100));

        JLabel nhanTieuDe = new JLabel("💰 Doanh thu hôm nay");
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        nhanTieuDe.setForeground(MAU_CHINH);
        panel.add(nhanTieuDe, BorderLayout.NORTH);

        JLabel nhanGiaTri = new JLabel(DOANH_THU_HOM_NAY + " VND");
        nhanGiaTri.setFont(new Font("Arial", Font.BOLD, 24));
        nhanGiaTri.setForeground(MAU_NHAN);

        JPanel panelGiaTri = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelGiaTri.setOpaque(false);
        panelGiaTri.add(nhanGiaTri);

        panel.add(panelGiaTri, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo card Hình ảnh đơn giản (thay thế cho hình ảnh tàu lớn)
     */
    private JPanel taoCardAnhNho() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_CARD);
        panel.setBorder(taoBorderCard());

        JLabel nhanTieuDe = new JLabel("🛤️ Tổng quan hệ thống");
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        nhanTieuDe.setBorder(new EmptyBorder(10, 10, 0, 10));

        JLabel nhanThongTin = new JLabel("<html><i>Xem thông tin về các chuyến tàu và lịch trình sắp tới.</i></html>");
        nhanThongTin.setFont(new Font("Arial", Font.PLAIN, 12));
        nhanThongTin.setBorder(new EmptyBorder(0, 10, 10, 10));
        nhanThongTin.setVerticalAlignment(SwingConstants.TOP);

        panel.add(nhanTieuDe, BorderLayout.NORTH);
        panel.add(nhanThongTin, BorderLayout.CENTER);

        // Placeholder
        JPanel panelPlaceholderDoThi = new JPanel();
        panelPlaceholderDoThi.setBackground(Color.decode("#E0F7FA"));
        panelPlaceholderDoThi.setPreferredSize(new Dimension(10, 100));
        panelPlaceholderDoThi.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel vanBanPlaceholder = new JLabel("Đồ thị Tải trọng/Vé đã bán", SwingConstants.CENTER);
        vanBanPlaceholder.setFont(new Font("Arial", Font.ITALIC, 12));
        panelPlaceholderDoThi.add(vanBanPlaceholder);

        panel.add(panelPlaceholderDoThi, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel Thông báo
     */
    private JPanel taoPanelThongBao() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MAU_NEN_CARD);
        panel.setBorder(taoBorderCard());

        JLabel nhanTieuDe = new JLabel("📢 Thông báo");
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        nhanTieuDe.setForeground(MAU_CHINH);
        panel.add(nhanTieuDe, BorderLayout.NORTH);

        JTextPane oVanBan = new JTextPane();
        oVanBan.setEditable(false);
        oVanBan.setOpaque(false);
        oVanBan.setFont(new Font("Arial", Font.PLAIN, 14));
        oVanBan.setBorder(new EmptyBorder(5, 0, 0, 0));

        StyledDocument doc = oVanBan.getStyledDocument();
        SimpleAttributeSet bulletSet = new SimpleAttributeSet();
        StyleConstants.setLeftIndent(bulletSet, 15);
        StyleConstants.setFirstLineIndent(bulletSet, -15);
        StyleConstants.setLineSpacing(bulletSet, 0.5f);

        String[] thongBao = {
                "Chương trình khuyến mãi 10% sẽ diễn ra từ 10/12/2025 - 25/12/2025.",
                "Nhân viên vui lòng cập nhật lại thông tin cá nhân trên hệ thống trước 05/12/2025.",
                "Lưu ý: Không được sử dụng thông tin khách hàng cho mục đích cá nhân.",
                "Lịch tập huấn nghiệp vụ bán vé mới sẽ được thông báo vào tuần tới."
        };

        try {
            for (String tb : thongBao) {
                doc.insertString(doc.getLength(), "• ", null);
                doc.insertString(doc.getLength(), tb + "\n", bulletSet);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        JScrollPane thanhCuon = new JScrollPane(oVanBan);
        thanhCuon.setBorder(BorderFactory.createEmptyBorder());
        thanhCuon.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(thanhCuon, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel Thông tin nhân viên
     */
    private JPanel taoPanelThongTinNhanVien() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MAU_NEN_CARD);
        panel.setBorder(taoBorderCard());

        JLabel nhanTieuDe = new JLabel("👨‍💼 Thông tin cá nhân");
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        nhanTieuDe.setForeground(MAU_CHINH);
        panel.add(nhanTieuDe, BorderLayout.NORTH);

        JPanel panelNoiDung = new JPanel(new GridBagLayout());
        panelNoiDung.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        int dong = 0;

        // --- Dòng 1: Ngày nghỉ phép ---
        JLabel nhanNgayNghi = new JLabel("Ngày nghỉ phép còn lại:");
        nhanNgayNghi.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel nhanGiaTriNgayNghi = new JLabel(ngayNghiConLaiHienThi + " ngày"); // Dữ liệu động
        nhanGiaTriNgayNghi.setFont(new Font("Arial", Font.BOLD, 14));
        nhanGiaTriNgayNghi.setForeground(MAU_NHAN);

        gbc.gridx = 0; gbc.gridy = dong; gbc.weightx = 0.5; panelNoiDung.add(nhanNgayNghi, gbc);
        gbc.gridx = 1; gbc.gridy = dong; gbc.weightx = 0.5; gbc.anchor = GridBagConstraints.EAST; panelNoiDung.add(nhanGiaTriNgayNghi, gbc);
        dong++;

        // Thêm đường kẻ ngang
        JSeparator duongKeNgang = new JSeparator(SwingConstants.HORIZONTAL);
        gbc.gridx = 0; gbc.gridy = dong; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0); panelNoiDung.add(duongKeNgang, gbc);
        dong++;

        // --- Dòng 2: Lương cơ bản ---
        JLabel nhanLuongCB = new JLabel("Mức lương cơ bản:");
        nhanLuongCB.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel nhanGiaTriLuong = new JLabel(luongCoBanHienThi + " VND"); // Dữ liệu động
        nhanGiaTriLuong.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = dong; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST; panelNoiDung.add(nhanLuongCB, gbc);
        gbc.gridx = 1; gbc.gridy = dong; gbc.anchor = GridBagConstraints.EAST; panelNoiDung.add(nhanGiaTriLuong, gbc);
        dong++;

        // Thêm nút xem chi tiết
        JButton nutXemChiTiet = new JButton("Xem chi tiết");
        nutXemChiTiet.setBackground(MAU_CHINH);
        nutXemChiTiet.setForeground(Color.WHITE);
        nutXemChiTiet.setFocusPainted(false);
        nutXemChiTiet.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0; gbc.gridy = dong; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST; gbc.insets = new Insets(20, 0, 0, 0);
        panelNoiDung.add(nutXemChiTiet, gbc);

        panel.add(panelNoiDung, BorderLayout.CENTER);

        return panel;
    }


    /**
     * Tạo Border chuẩn cho các Card/Widget (Đã sửa lỗi: sử dụng kiểu Border)
     */
    private Border taoBorderCard() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(15, 15, 15, 15)
        );
    }

    // ====================
    // MODULE: Main (để chạy độc lập)
    // ====================
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard Nhân viên Bán vé");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            frame.add(new ManHinhTrangChuNVBanVe(), BorderLayout.CENTER);

            frame.setSize(1200, 750);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}