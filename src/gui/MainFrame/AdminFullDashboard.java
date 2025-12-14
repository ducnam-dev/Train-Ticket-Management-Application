package gui.MainFrame;

// Import các Panel cần hiển thị
import control.CaLamViec;
import entity.NhanVien;
import gui.Panel.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;

/**
 * Lớp này tạo MainFrame cho quyền Admin (full quyền),
 * kết hợp tất cả các chức năng Quản Lý và Bán Vé.
 */
public class AdminFullDashboard extends JFrame implements ActionListener {

    // =================================================================================
    // HẰNG SỐ VÀ KHAI BÁO
    // =================================================================================
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel navPanel;

    // Màu sắc
    private final Color MAU_CHINH = new Color(0, 123, 255);
    private final Color MAU_DUOC_CHON = new Color(74, 184, 237);
    private final Color MAU_HOVER = new Color(45, 150, 215);

    private final Map<String, JButton> menuButtons = new HashMap<>();
    private static final int CHIEU_RONG_MENU = 200; // Tăng chiều rộng để dễ nhìn
    private static final int ICON_SIZE = 20;

    // Khai báo TẤT CẢ các nút từ 2 Dashboard
    private JButton btnTrangChu, btnDangXuat;

    // Chức năng Nhân Viên Bán Vé
    private JButton btnMoCa, btnKetCa, btnBanVe, btnDoiVe, btnTraVe, btnTraCuuVe;

    // Chức năng Quản Lý
    private JButton btnQLChuyenTau, btnQLNV, btnQLKhuyenMai, btnQLGiaVe, btnThongKe, btnTraCuuHD;

    // Dữ liệu Nhân viên
    private String maNVHienThi = "N/A";
    private String tenNVHienThi = "Đang tải...";

    // Instance của ManHinhBanVe (nếu cần truy cập lại)
    public ManHinhBanVe manHinhBanVeInstance;


    public AdminFullDashboard() {
        setTitle("Hệ thống Quản lý Vé Tàu - ADMIN TỔNG");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        layThongTinNhanVien();

        // 1. Panel Menu bên trái
        navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        // 2. Panel nội dung (CardLayout)
        initContentPanel();
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Phóng to tối đa

        // Mặc định hiển thị màn hình Trang Chủ
        switchToCard("trangChuQL");

        // Thiết lập sự kiện cho các nút menu
        initEventHandlers();
        setVisible(true);
    }

    // =================================================================================
    // CÁC PHƯƠNG THỨC HELPER
    // =================================================================================

    private void layThongTinNhanVien() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv != null) {
            this.maNVHienThi = nv.getMaNV();
            this.tenNVHienThi = nv.getHoTen();
        } else {
            this.maNVHienThi = "ADMIN";
            this.tenNVHienThi = "Toàn Quyền";
        }
    }

    /**
     * Helper: Tải, điều chỉnh kích thước và trả về ImageIcon.
     */
    private ImageIcon createIcon(String path) {
        // Tải icon từ class path.
        // Cần đảm bảo các file icon tồn tại trong thư mục tương ứng
        // (ví dụ: /images/iconMenu/home.png)
        URL imageUrl = getClass().getResource(path);
        if (imageUrl == null) {
            System.err.println("Không tìm thấy tài nguyên icon: " + path);
            return null;
        }
        try {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải hoặc điều chỉnh icon: " + path + " - " + e.getMessage());
            return null;
        }
    }

    // =================================================================================
    // KHU VỰC MENU
    // =================================================================================

    private JPanel createNavPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setPreferredSize(new Dimension(CHIEU_RONG_MENU, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Phần Header (Logo và ID) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(MAU_CHINH);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoLabel = new JLabel("HỆ THỐNG GA XE");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        panel.add(headerPanel);

        // --- MỤC: CHỨC NĂNG CHUNG ---
        panel.add(taoTieuDeMenu("TỔNG QUAN"));
        btnTrangChu = createNavItem("Dashboard", "/images/iconMenu/home.png", "trangChuQL");
        panel.add(btnTrangChu);
        panel.add(taoDuongKe());

        // --- MỤC: CHỨC NĂNG BÁN VÉ/CA ---
        panel.add(taoTieuDeMenu("NHIỆM VỤ CA/VÉ"));

        btnMoCa = createNavItem("Mở ca", "/images/iconMenu/moca.png", "moCa");
        panel.add(btnMoCa);
        btnKetCa = createNavItem("Kết ca", "/images/iconMenu/ketca.png", "ketCa");
        panel.add(btnKetCa);
        panel.add(taoDuongKe());

        btnBanVe = createNavItem("Bán vé mới", "/images/iconMenu/banve.png", "banVeMoi");
        panel.add(btnBanVe);
        btnDoiVe = createNavItem("Đổi vé", "/images/iconMenu/doive.png", "doiVe");
        panel.add(btnDoiVe);
        btnTraVe = createNavItem("Trả vé", "/images/iconMenu/trave.png", "traVe");
        panel.add(btnTraVe);
        panel.add(taoDuongKe());

        btnTraCuuVe = createNavItem("Tra cứu vé", "/images/iconMenu/tracuu.png", "traCuuVe");
        panel.add(btnTraCuuVe);
        btnTraCuuHD = createNavItem("Tra cứu HĐ", "/images/iconMenu/tracuuhoadon.png", "traCuuHD");
        panel.add(btnTraCuuHD);
        panel.add(taoDuongKe());


        // --- MỤC: CHỨC NĂNG QUẢN LÝ ---
        panel.add(taoTieuDeMenu("QUẢN LÝ HỆ THỐNG"));

        btnQLChuyenTau = createNavItem("QL Chuyến tàu", "/images/iconMenu/chuyentau.png", "qlChuyenTau");
        panel.add(btnQLChuyenTau);

        btnQLNV = createNavItem("QL Tài khoản NV", "/images/iconMenu/nhanvien.png", "qlNhanVien");
        panel.add(btnQLNV);

        btnQLGiaVe = createNavItem("QL Giá vé", "/images/iconMenu/giave.png", "qlGiaVe");
        panel.add(btnQLGiaVe);

        btnQLKhuyenMai = createNavItem("QL Khuyến mãi", "/images/iconMenu/khuyenmai.png", "qlKhuyenMai");
        panel.add(btnQLKhuyenMai);
        panel.add(taoDuongKe());

        btnThongKe = createNavItem("Thống kê Báo cáo", "/images/iconMenu/thongke.png", "thongKe");
        panel.add(btnThongKe);
        panel.add(taoDuongKe());


        panel.add(Box.createVerticalGlue()); // Đẩy phần dưới cùng xuống

        // --- THÔNG TIN NV ---
        panel.add(taoPanelThongTinNV());

        // --- Nút Đăng xuất ---
        btnDangXuat = createNavItem("Đăng xuất", "/images/iconMenu/logout.png", "dangXuat");
        panel.add(btnDangXuat);

        return panel;
    }

    private Component taoTieuDeMenu(String tieuDe) {
        JLabel label = new JLabel(tieuDe);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.decode("#FFFFFF"));
        label.setBorder(new EmptyBorder(15, 10, 5, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel taoPanelThongTinNV() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel nhanTenNV = new JLabel("QUYỀN: **ADMIN**");
        nhanTenNV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nhanTenNV.setForeground(Color.WHITE);
        nhanTenNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanMaNV = new JLabel("ID: " + maNVHienThi + " (" + tenNVHienThi + ")");
        nhanMaNV.setFont(new Font("Segoe UI", Font.PLAIN, 11));
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
    private JButton createNavItem(String text, String iconPath, String cardName) {
        JButton button = new JButton(text);

        ImageIcon icon = createIcon(iconPath);
        if (icon != null) {
            button.setIcon(icon);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(10);
        }

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(MAU_CHINH);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setOpaque(true);

        int fixedHeight = 45;
        Dimension itemSize = new Dimension(CHIEU_RONG_MENU, fixedHeight);

        button.setPreferredSize(itemSize);
        button.setMinimumSize(itemSize);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, fixedHeight));

        menuButtons.put(cardName, button);

        // Xử lý hiệu ứng hover/màu sắc
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(MAU_CHINH)) {
                    button.setBackground(MAU_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(MAU_HOVER) && !menuButtons.get(cardName).getBackground().equals(MAU_DUOC_CHON)) {
                    button.setBackground(MAU_CHINH);
                }
            }
        });
        return button;
    }

    /**
     * Tạo gạch chân
     */
    private JSeparator taoDuongKe() {
        JSeparator duongKe = new JSeparator(SwingConstants.HORIZONTAL);
        duongKe.setForeground(new Color(255, 255, 255, 70));
        duongKe.setBackground(MAU_CHINH);
        duongKe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return duongKe;
    }


    // =================================================================================
    // KHU VỰC CONTENT PANEL & CARDLAYOUT
    // =================================================================================

    /**
     * Khởi tạo Panel chứa CardLayout với TẤT CẢ các Panel chức năng
     */
    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // --- Chức năng Quản Lý (từ QuanLyDashboard) ---
        contentPanel.add(new ManHinhDashboardQuanLy(), "trangChuQL"); // Đổi tên để tránh trùng với trang chủ NV
        contentPanel.add(new ManhinhQuanLyChuyenTau(), "qlChuyenTau");
        contentPanel.add(new ManHinhQuanLyNhanVien(), "qlNhanVien");
        contentPanel.add(new ManHinhQuanLyKhuyenMai2(), "qlKhuyenMai");
        contentPanel.add(new ManHinhQuanLyGiaVe(), "qlGiaVe");
        contentPanel.add(new JPanel(), "thongKe");

        // --- Chức năng Bán Vé (từ BanVeDashboard) ---
        // SỬ DỤNG LẠI ManHinhDashboardQuanLy làm trang chủ tổng
        contentPanel.add(new ManHinhMoCa(), "moCa");
        contentPanel.add(new ManHinhKetCa(), "ketCa");

        manHinhBanVeInstance = new ManHinhBanVe(); // Giữ instance để có thể tương tác nếu cần
        contentPanel.add(manHinhBanVeInstance, "banVeMoi");

        contentPanel.add(new ManHinhDoiVe(), "doiVe");
        contentPanel.add(new ManHinhTraVe(), "traVe");
        contentPanel.add(new ManHinhTraCuuVe(), "traCuuVe");
        // Tra Cứu Hóa Đơn dùng chung
        contentPanel.add(new ManHinhTraCuuHoaDon(), "traCuuHD");


        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Thiết lập Action Listener cho tất cả các nút menu
     */
    private void initEventHandlers() {
        for (JButton button : menuButtons.values()) {
            button.addActionListener(this);
        }
    }

    /**
     * Chuyển đổi màn hình trong CardLayout và highlight nút menu tương ứng
     */
    public void switchToCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
        highlightActiveButton(menuButtons.get(cardName));
    }

    /**
     * Đổi màu nền của nút menu đang được chọn
     */
    private void highlightActiveButton(JButton active) {
        for (JButton button : menuButtons.values()) {
            if (button != null) {
                button.setBackground(MAU_CHINH);
            }
        }
        if (active != null) {
            active.setBackground(MAU_DUOC_CHON);
        }
    }

    // =================================================================================
    // XỬ LÝ SỰ KIỆN CHUNG
    // =================================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        String cardName = null;

        // Tìm tên card dựa trên nút được click
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            if (entry.getValue() == src) {
                cardName = entry.getKey();
                break;
            }
        }

        if ("dangXuat".equals(cardName)) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất Admin?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                CaLamViec.getInstance().ketThucCa(); // Dù là Admin vẫn nên gọi hàm kết thúc ca
                this.dispose();
                // TODO: Thêm logic chuyển về màn hình đăng nhập
            }
            return;
        }

        // Thêm xử lý đặc biệt cho Mở Ca/Kết Ca nếu cần
        if ("moCa".equals(cardName)) {
            // Logic riêng cho Mở ca
        }
        if ("ketCa".equals(cardName)) {
            // Logic riêng cho Kết ca
        }

        if (cardName != null) {
            switchToCard(cardName);
        }
    }

    // =================================================================================
    // MAIN
    // =================================================================================
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e){
            // Dùng giao diện mặc định nếu Nimbus không khả dụng
        }

        try {
            // MOCKUP NhanVien Admin
            NhanVien nvMock = new NhanVien("ADMIN001", "Admin Tổng", "0999888777");
            CaLamViec.getInstance().batDauCa(nvMock); // Giả lập ca làm việc đã bắt đầu
        } catch (Exception e) {
            System.err.println("Lỗi MOCKUP NhanVien/CaLamViec: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new AdminFullDashboard();
        });
    }
}