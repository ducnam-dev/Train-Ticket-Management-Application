package gui.MainFrame;

import control.CaLamViec; // Import CaLamViec
import entity.NhanVien; // Import NhanVien
import gui.Panel.*; // Đảm bảo đã import các Panel cần thiết

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp này tạo ManFrame cho quyền Nhân viên Bán Vé, chứa Menu cố định và CardLayout.
 */
public class BanVeDashboard extends JFrame implements ActionListener {

    // --- HẰNG SỐ VÀ KHAI BÁO VIỆT HÓA ---
    private CardLayout boCucCard;
    private JPanel panelNoiDung;
    private final Color MAU_CHINH = new Color(34, 137, 203); // Màu xanh
    private final Color MAU_DUOC_CHON = new Color(74, 184, 237); // Màu xanh sáng
    private final Color MAU_HOVER = new Color(45, 150, 215);
    private final Map<String, JButton> nutMenu = new HashMap<>();

    // Các nút menu cần quản lý
    private JButton nutTrangChu, nutMoCa, nutKetCa, nutBanVe, nutDoiVe, nutTraCuuVe, nutTraCuuHD, nutDangXuat, nutTraVe;

    // Dữ liệu Nhân viên
    private String maNVHienThi = "N/A";
    private String tenNVHienThi = "Đang tải...";


    public BanVeDashboard() {
        setTitle("Hệ thống Bán Vé Tàu - Nhân viên Bán Vé");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Lấy thông tin NV ngay khi khởi tạo
        layThongTinNhanVien();

        // 1. Panel Menu bên trái
        JPanel panelDieuHuong = taoPanelDieuHuong();
        add(panelDieuHuong, BorderLayout.WEST);

        // 2. Panel nội dung (CardLayout)
        khoiTaoPanelNoiDung();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        chuyenManHinh("trangChuNV");
        dangKiSuKien();
        setVisible(true);
    }

    /**
     * Lấy thông tin NV từ CaLamViec và cập nhật các biến instance.
     */
    private void layThongTinNhanVien() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv != null) {
            this.maNVHienThi = nv.getMaNV();
            this.tenNVHienThi = nv.getHoTen();
        } else {
            // Trường hợp lỗi/chưa đăng nhập
            this.maNVHienThi = "Lỗi Phiên";
            this.tenNVHienThi = "Không tìm thấy";
        }
    }

    /**
     * Tạo panel điều hướng bên trái cho Nhân viên Bán Vé.
     */
    private JPanel taoPanelDieuHuong() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setPreferredSize(new Dimension(220, 0)); // Tăng chiều rộng
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Phần Header (Logo và ID) ---
        JPanel panelTieuDe = new JPanel();
        panelTieuDe.setLayout(new BoxLayout(panelTieuDe, BoxLayout.Y_AXIS));
        panelTieuDe.setBackground(MAU_CHINH);
        panelTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanLogo = new JLabel("GA XE");
        nhanLogo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        nhanLogo.setForeground(Color.WHITE);
        nhanLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelTieuDe.add(nhanLogo);

        panelTieuDe.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelTieuDe.getPreferredSize().height));
        panel.add(panelTieuDe);

        // --- Phần các mục menu ---

        panel.add(taoDuongKe());
        // [1. Trang chủ]
        nutTrangChu = taoMucMenu("Dashboard", "\uD83C\uDFE0", "trangChuNV");
        panel.add(nutTrangChu);
        panel.add(taoDuongKe());

        // [2. Mở ca]
        nutMoCa = taoMucMenu("Mở ca", "\u23F3", "moCa");
        panel.add(nutMoCa);
        panel.add(taoDuongKe());

        // [3. Kết ca]
        nutKetCa = taoMucMenu("Kết ca", "\u23F0", "ketCa");
        panel.add(nutKetCa);

        // Gạch chân
        panel.add(taoDuongKe());

        // [4. Bán vé mới]
        nutBanVe = taoMucMenu("Bán vé mới", "\uD83C\uDFAB", "banVeMoi"); // 🎫 (Sử dụng code emoji chuẩn)
        panel.add(nutBanVe);
        panel.add(taoDuongKe());

        // [5. Đổi vé]
        nutDoiVe = taoMucMenu("Đổi vé", "\u21C4", "doiVe");
        panel.add(nutDoiVe);
        panel.add(taoDuongKe());

        // [5.1. Trả vé]
        nutTraVe = taoMucMenu("Trả vé", "\uD83D\uDD19", "traVe"); // 🔙
        panel.add(nutTraVe);
        panel.add(taoDuongKe());


        // [6. Tra cứu vé]
        nutTraCuuVe = taoMucMenu("Tra cứu vé", "\uD83D\uDD0D", "traCuuVe");
        panel.add(nutTraCuuVe);
        panel.add(taoDuongKe());

        // [7. Tra cứu hóa đơn]
        nutTraCuuHD = taoMucMenu("Tra cứu Hóa đơn", "\uD83D\uDCCB", "traCuuHD");
        panel.add(nutTraCuuHD);
        panel.add(taoDuongKe());


        panel.add(Box.createVerticalGlue());

        // --- THÔNG TIN NV (Lấy từ CaLamViec) ---
        panel.add(taoPanelThongTinNV());

        // Nút Đăng xuất
        nutDangXuat = taoMucMenu("Đăng xuất", "\uD83D\uDEAA", "dangXuat");
        panel.add(nutDangXuat);

        return panel;
    }

    /**
     * Tạo panel hiển thị thông tin nhân viên đang đăng nhập.
     */
    private JPanel taoPanelThongTinNV() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Tên nhân viên
        JLabel nhanTenNV = new JLabel("<html><b>" + tenNVHienThi + "</b></html>");
        nhanTenNV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nhanTenNV.setForeground(Color.WHITE);
        nhanTenNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Mã nhân viên
        JLabel nhanMaNV = new JLabel("ID: " + maNVHienThi);
        nhanMaNV.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nhanMaNV.setForeground(Color.decode("#E0E0E0"));
        nhanMaNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(nhanTenNV);
        panel.add(nhanMaNV);
        panel.add(taoDuongKe());

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }


    /**
     * Phương thức tạo nút menu
     */
    private JButton taoMucMenu(String vanBan, String kyHieu, String tenCard) {
        JButton nut = new JButton(vanBan);

        String htmlText = "<html>" +
                "<span style='white-space: nowrap;'>" + // Áp dụng nowrap cho toàn bộ nội dung
                "<span style='font-family:\"Segoe UI Emoji\"; font-size:15pt;'>" + kyHieu + "</span>" +
                "&nbsp;&nbsp;&nbsp;" +
                "<span style='font-family:\"Segoe UI\", Arial; font-size: 12pt; font-weight: bold;'>" + vanBan + "</span>" +
                "</span>" +
                "</html>";
        nut.setText(htmlText);

        nut.setForeground(Color.WHITE);
        nut.setBackground(MAU_CHINH);
        nut.setFocusPainted(false);
        nut.setHorizontalAlignment(SwingConstants.LEFT);

        nut.setBorder(new EmptyBorder(10, 15, 10, 15));
        nut.setOpaque(true);

        int chieuCaoCoDinh = 45;
        final int CHIEU_RONG_MENU = 240;

        // 1. Buộc nút phải có kích thước Ưu tiên và Tối thiểu bằng chiều rộng menu
        Dimension kichThuocBuoc = new Dimension(CHIEU_RONG_MENU, chieuCaoCoDinh);

        // Thiết lập kích thước Ưu tiên (rất quan trọng cho BoxLayout)
        nut.setPreferredSize(kichThuocBuoc);

        // Thiết lập kích thước Tối thiểu
        nut.setMinimumSize(kichThuocBuoc);

        nut.setMaximumSize(new Dimension(Integer.MAX_VALUE, chieuCaoCoDinh));
        // Đăng ký nút vào Map và Listener
        nutMenu.put(tenCard, nut);

        // Xử lý hiệu ứng hover/màu sắc
        nut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (nut.getBackground().equals(MAU_CHINH)) {
                    nut.setBackground(MAU_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (nut.getBackground().equals(MAU_HOVER)) {
                    nut.setBackground(MAU_CHINH);
                }
            }
        });
        return nut;
    }

    /**
     * Tạo gạch chân giữa các nhóm chức năng
     */
    private JSeparator taoDuongKe() {
        JSeparator duongKe = new JSeparator(SwingConstants.HORIZONTAL);
        duongKe.setForeground(new Color(255, 255, 255, 70));
        duongKe.setBackground(MAU_CHINH);
        duongKe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return duongKe;
    }


    // KHU VỰC CONTENT PANEL & CARDLAYOUT
    /**
     * Khởi tạo Panel chứa CardLayout và thêm các màn hình
     */
    private void khoiTaoPanelNoiDung() {
        boCucCard = new CardLayout();
        panelNoiDung = new JPanel(boCucCard);

        // Thêm các màn hình (Cần đảm bảo các lớp Panel này tồn tại)
        panelNoiDung.add(new ManHinhTrangChuNVBanVe(), "trangChuNV");
        // Giả định các Panel sau tồn tại
        panelNoiDung.add(new ManHinhMoCa(), "moCa");
        panelNoiDung.add(new ManHinhKetCa(), "ketCa");

//        ManHinhBanVe panelBanVe = new ManHinhBanVe();
//        panelBanVe.setName("banVeMoi");
        panelNoiDung.add(new ManHinhBanVe(), "banVeMoi");

        panelNoiDung.add(new ManHinhDoiVe(), "doiVe");
        panelNoiDung.add(new ManHinhTraVe(), "traVe");
        panelNoiDung.add(new ManHinhTraCuuVe(), "traCuuVe");
        panelNoiDung.add(new ManHinhTraCuuHoaDon(), "traCuuHD");

        add(panelNoiDung, BorderLayout.CENTER);
    }

    /**
     * Thiết lập Action Listener cho tất cả các nút menu
     */
    private void dangKiSuKien() {
        for (JButton button : nutMenu.values()) {
            button.addActionListener(this);
        }
    }

    /**
     * Chuyển đổi màn hình trong CardLayout và highlight nút menu tương ứng
     */
    public void chuyenManHinh(String tenCard) {
        boCucCard.show(panelNoiDung, tenCard);
        danhDauNutDangChon(nutMenu.get(tenCard));
    }

    /**
     * Đổi màu nền của nút menu đang được chọn
     */
    private void danhDauNutDangChon(JButton nutHoatDong) {
        for (JButton nut : nutMenu.values()) {
            if (nut != null) {
                nut.setBackground(MAU_CHINH);
            }
        }
        if (nutHoatDong != null) {
            nutHoatDong.setBackground(MAU_DUOC_CHON);
        }
    }

    /**
     * Thêm hoặc cập nhật một JPanel vào CardLayout.
     */
    public void themHoacCapNhatCard(JPanel panelMoi, String tenCard) {
        Component thanhPhanCu = null;

        for (Component comp : panelNoiDung.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(tenCard)) {
                thanhPhanCu = comp;
                break;
            }
        }
        if (thanhPhanCu != null) {
            panelNoiDung.remove(thanhPhanCu);
        }
        panelMoi.setName(tenCard);
        panelNoiDung.add(panelMoi, tenCard);

        panelNoiDung.revalidate();
        panelNoiDung.repaint();
    }


    // XỬ LÝ SỰ KIỆN CHUNG
    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();

        // Tìm tên card tương ứng với nút được click
        String tenCard = nutMenu.entrySet().stream()
                .filter(entry -> entry.getValue() == nguon)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if ("dangXuat".equals(tenCard)) {
            int xacNhan = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (xacNhan == JOptionPane.YES_OPTION) {
                // Xóa phiên làm việc trước khi đóng (rất quan trọng)
                CaLamViec.getInstance().ketThucCa();
                this.dispose();
                // Mở lại màn hình đăng nhập nếu cần
                // new ManHinhDangNhap().setVisible(true);
            }
            return;
        }

        if (tenCard != null) {
            chuyenManHinh(tenCard);
        }
    }

    // =================================================================================
    // MAIN

    public static void main(String[] args) {
        // --- CHUẨN BỊ MOCKUP TRƯỚC KHI CHẠY ---
        // Giả lập việc đăng nhập để CaLamViec có dữ liệu
        try {
            // Giả lập đối tượng NhanVien đã đăng nhập
            NhanVien nvMock = new NhanVien("NVBV0001", "Trần Đức Nam");
            CaLamViec.getInstance().batDauCa(nvMock);
        } catch (Exception e) {
            System.err.println("Lỗi MOCKUP NhanVien/CaLamViec: " + e.getMessage());
            // Bỏ qua lỗi nếu lớp NhanVien/CaLamViec chưa được định nghĩa
        }

        SwingUtilities.invokeLater(() -> {
            new BanVeDashboard();
        });
    }

    public Component layCardTheoTen(String tenCard) {
        for (Component comp : panelNoiDung.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(tenCard)) {
                return comp;
            }
        }
        return null;
    }
}