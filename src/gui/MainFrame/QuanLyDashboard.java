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
 * Lớp này tạo MainFrame cho quyền Quản Lý, chứa Menu cố định và CardLayout để chuyển màn hình.
 */
public class QuanLyDashboard extends JFrame implements ActionListener {

    // =================================================================================
    // HẰNG SỐ VÀ KHAI BÁO
    // =================================================================================
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel navPanel;

    // Màu sắc theo phong cách BanVeDashboard
    private final Color MAU_CHINH = new Color(34, 137, 203);
    private final Color MAU_DUOC_CHON = new Color(74, 184, 237);
    private final Color MAU_HOVER = new Color(45, 150, 215);

    private final Map<String, JButton> menuButtons = new HashMap<>();
    private static final int CHIEU_RONG_MENU = 180;
    private static final int ICON_SIZE = 20; // Kích thước icon

    // Các nút menu cần quản lý
    private JButton btnTrangChu, btnQLChuyenTau, btnQLNV, btnQLKhuyenMai, btnDangXuat;
    private JButton btnQLGiaVe, btnTraCuuHD, btnThongKe;

    // Dữ liệu Nhân viên
    private String maNVHienThi = "N/A";
    private String tenNVHienThi = "Đang tải...";


    public QuanLyDashboard() {
        setTitle("Hệ thống Quản lý Vé Tàu");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //3. Lấy thông tin nhân viên đang đăng nhập
        layThongTinNhanVien();

        // 1. Panel Menu bên trái
        navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        // 2. Panel nội dung (CardLayout)
        initContentPanel();
        setExtendedState(JFrame.MAXIMIZED_BOTH);



        // Mặc định hiển thị màn hình Trang Chủ
        switchToCard("trangChu");

        // Thiết lập sự kiện cho các nút menu
        initEventHandlers();
        setVisible(true);
    }

    /**
     * Helper: Tải, điều chỉnh kích thước và trả về ImageIcon.
     * @param path Đường dẫn tương đối từ gốc classpath (VD: "/images/home.png")
     * @return ImageIcon đã resize, hoặc null nếu lỗi.
     */
    private ImageIcon createIcon(String path) {
        URL imageUrl = getClass().getResource(path);
        if (imageUrl == null) {
            System.err.println("Không tìm thấy tài nguyên icon: " + path);
            return null;
        }
        try {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image image = originalIcon.getImage();
            // Điều chỉnh kích thước
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

    /**
     * Tạo panel điều hướng bên trái.
     */
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

        JLabel logoLabel = new JLabel("GA XE");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(logoLabel);


        headerPanel.add(Box.createVerticalStrut(10));
        panel.add(headerPanel);

        // --- Phần các mục menu ---

        panel.add(taoDuongKe());
        // [1. Trang chủ]
        btnTrangChu = createNavItem("Dashboard", "/images/home.png", "trangChu");
        panel.add(btnTrangChu);
        panel.add(taoDuongKe());

        // [2. Quản lý chuyến tàu]
        btnQLChuyenTau = createNavItem("QL chuyến tàu", "/images/chuyentau.png", "qlChuyenTau");
        panel.add(btnQLChuyenTau);
        panel.add(taoDuongKe());

        // [3. Quản lý tài khoản NV]
        btnQLNV = createNavItem("QL tài khoản NV", "/images/nhanvien.png", "qlNhanVien");
        panel.add(btnQLNV);
        panel.add(taoDuongKe());

        // [4. Quản lý giá vé]
        btnQLGiaVe = createNavItem("QL giá vé", "/images/giave.png", "qlGiaVe");
        panel.add(btnQLGiaVe);
        panel.add(taoDuongKe());

        // [5. Quản lý khuyến mãi]
        btnQLKhuyenMai = createNavItem("QL khuyến mãi", "/images/khuyenmai.png", "qlKhuyenMai");
        panel.add(btnQLKhuyenMai);
        panel.add(taoDuongKe());

        // [6. Tra cứu hóa đơn]
        btnTraCuuHD = createNavItem("Tra cứu hóa đơn", "/images/tracuuhoadon.png", "traCuuHD");
        panel.add(btnTraCuuHD);
        panel.add(taoDuongKe());

        // [7. Thống kê báo cáo]
        btnThongKe = createNavItem("Thống kê báo cáo", "/images/thongke.png", "thongKe");
        panel.add(btnThongKe);
        panel.add(taoDuongKe());

        panel.add(Box.createVerticalGlue());

        // --- THÔNG TIN NV ---
        panel.add(taoPanelThongTinNV());

        // --- Nút Đăng xuất ---
        btnDangXuat = createNavItem("Đăng xuất", "/images/logout.png", "dangXuat");
        panel.add(btnDangXuat);

        return panel;
    }

    private void layThongTinNhanVien() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv != null) {
            this.maNVHienThi = nv.getMaNV();
            this.tenNVHienThi = nv.getHoTen();
        } else {
            this.maNVHienThi = "Lỗi Phiên";
            this.tenNVHienThi = "Không tìm thấy";
        }
    }


    private JPanel taoPanelThongTinNV() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding bên trong panel

        JLabel nhanTenNV = new JLabel("NV: " + tenNVHienThi);
        nhanTenNV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nhanTenNV.setForeground(Color.WHITE);
        nhanTenNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanMaNV = new JLabel("ID: " + maNVHienThi);
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
     * Phương thức tạo nút menu sử dụng ImageIcon (Không dùng HTML)
     */
    private JButton createNavItem(String text, String iconPath, String cardName) {
        JButton button = new JButton(text);

        // Tải icon
        ImageIcon icon = createIcon(iconPath);
        if (icon != null) {
            button.setIcon(icon);
            // Đặt vị trí của icon (Icon ở trái, Text ở phải)
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(10); // Khoảng cách giữa icon và text
        }

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(MAU_CHINH);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT); // Căn lề trái

        // Thêm padding cho nội dung
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setOpaque(true);

        int fixedHeight = 45;
        Dimension itemSize = new Dimension(CHIEU_RONG_MENU, fixedHeight);

        // Thiết lập kích thước
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
                // Chỉ quay lại màu chính nếu không phải là nút đang được chọn
                if (button.getBackground().equals(MAU_HOVER) && !menuButtons.get(cardName).getBackground().equals(MAU_DUOC_CHON)) {
                    button.setBackground(MAU_CHINH);
                }
            }
        });
        return button;
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


    // =================================================================================
    // KHU VỰC CONTENT PANEL & CARDLAYOUT
    // =================================================================================

    /**
     * Khởi tạo Panel chứa CardLayout
     */
    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Thêm các màn hình quản lý vào CardLayout
        contentPanel.add(new ManHinhDashboardQuanLy(), "trangChu");
        contentPanel.add(new ManhinhQuanLyChuyenTau(), "qlChuyenTau");
        contentPanel.add(new ManHinhQuanLyNhanVien(), "qlNhanVien");
        contentPanel.add(new ManHinhQuanLyKhuyenMai2(), "qlKhuyenMai");
        contentPanel.add(new ManHinhTraCuuHoaDon(), "traCuuHD");
        contentPanel.add(new ManHinhQuanLyGiaVe(), "qlGiaVe");
        contentPanel.add(new JPanel(), "thongKe");


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
        // Đặt tất cả các nút về màu ban đầu
        for (JButton button : menuButtons.values()) {
            if (button != null) {
                button.setBackground(MAU_CHINH);
            }
        }
        // Highlight nút đang hoạt động
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
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
            }
            return;
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
            // Dùng giao diện mặc định
        }

        try {
            NhanVien nvMock = new NhanVien("NVQL0001", "Trần Đức Nam");
            CaLamViec.getInstance().batDauCa(nvMock);
        } catch (Exception e) {
            System.err.println("Lỗi MOCKUP NhanVien/CaLamViec: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new QuanLyDashboard();
        });
    }
}