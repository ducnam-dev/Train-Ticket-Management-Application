package gui.MainFrame;

// Import các Panel cần hiển thị (Giả định các lớp này kế thừa từ JPanel)
import gui.Panel.*;
//import gui.Panel.ManHinhTraCuuVe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp này tạo MainFrame cho quyền Nhân viên Bán Vé, chứa Menu cố định và CardLayout.
 */
public class BanVeDashboard extends JFrame implements ActionListener {

    // =================================================================================
    // HẰNG SỐ VÀ KHAI BÁO
    // =================================================================================
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private final Color PRIMARY_COLOR = new Color(34, 137, 203); // Màu xanh nhạt hơn cho NV Quản Lý
    private final Color SELECTED_COLOR = new Color(74, 184, 237); // Màu xanh sáng hơn
    private final Color HOVER_COLOR = new Color(45, 150, 215);
    private final Map<String, JButton> menuButtons = new HashMap<>();

    // Các nút menu cần quản lý
    private JButton btnTrangChu, btnMoCa, btnKetCa, btnBanVe, btnDoiVe, btnTraCuuVe, btnTraCuuHD, btnDangXuat, btnTraVe;

    public BanVeDashboard() {
        setTitle("Hệ thống Bán Vé Tàu - Nhân viên Bán Vé");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Panel Menu bên trái
        JPanel navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        // 2. Panel nội dung (CardLayout)
        initContentPanel();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        switchToCard("trangChuNV");
        initEventHandlers();
        setVisible(true);
    }

    // =================================================================================
    // KHU VỰC MENU (NHÂN VIÊN BÁN VÉ)
    // =================================================================================

    /**
     * Tạo panel điều hướng bên trái cho Nhân viên Bán Vé.
     */
    private JPanel createNavPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PRIMARY_COLOR);
        panel.setPreferredSize(new Dimension(200, 0)); // Chiều rộng hẹp hơn
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Phần Header (Logo và ID) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoLabel = new JLabel("GA XE");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idLabel = new JLabel("NV BÁN VÉ");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(Color.WHITE);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        idLabel.setBorder(new EmptyBorder(5, 5, 20, 0));

        headerPanel.add(logoLabel);
        headerPanel.add(idLabel);
        headerPanel.setMaximumSize(headerPanel.getPreferredSize());
        panel.add(headerPanel);

        // --- Phần các mục menu ---

        // [1. Trang chủ]
        btnTrangChu = createNavItem("Trang chủ", "\uD83C\uDFE0", "trangChuNV"); // 🏠
        panel.add(btnTrangChu);

        // [2. Mở ca]
        btnMoCa = createNavItem("Mở ca", "\u23F3", "moCa"); // ⏳
        panel.add(btnMoCa);

        // [3. Kết ca]
        btnKetCa = createNavItem("Kết ca", "\u23F0", "ketCa"); // ⏱️
        panel.add(btnKetCa);

        // --- Separator ---
        panel.add(createSeparator());

        // [4. Bán vé mới]
        btnBanVe = createNavItem("Bán vé mới", "\uD83C", "banVeMoi"); // 🎫
        panel.add(btnBanVe);

        // [5. Đổi vé]
        btnDoiVe = createNavItem("Đổi vé", "\u21C4", "doiVe"); // ⇄
        panel.add(btnDoiVe);
        panel.add(createSeparator());

        // [5 1. Trả vé]
        btnTraVe = createNavItem("Trả vé", "\u21C4", "traVe"); // ⇄
        panel.add(btnTraVe);
        panel.add(createSeparator());
        // --- Separator ---


        // [6. Tra cứu vé]
        btnTraCuuVe = createNavItem("Tra cứu vé", "\uD83D\uDD0D", "traCuuVe"); // 🔍
        panel.add(btnTraCuuVe);

        // [7. Tra cứu hóa đơn]
        btnTraCuuHD = createNavItem("Tra cứu hóa đơn", "\uD83D\uDCCB", "traCuuHD"); // 📋
        panel.add(btnTraCuuHD);


        panel.add(Box.createVerticalGlue());

        // --- Nút Đăng xuất ---
        btnDangXuat = createNavItem("Đăng xuất", "\uD83D\uDEAA", "dangXuat"); // 🚪
        panel.add(btnDangXuat);

        return panel;
    }

    /**
     * Phương thức tạo nút menu
     */
    private JButton createNavItem(String text, String iconText, String cardName) {
        JButton button = new JButton(text);

        String htmlText = "<html>" +
                "<span style='font-family:\"Segoe UI Emoji\"; font-size:15pt;'>" + iconText + "</span>" +
                "&nbsp;&nbsp;&nbsp;" +
                "<span style='font-family:\"Segoe UI\", Arial; font-size: 12pt; font-weight: bold;'>" + text + "</span>" +
                "</html>";
        button.setText(htmlText);

        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 15, 10, 15)); // Căn lề trái hợp lý
        button.setOpaque(true);

        int fixedHeight = 45;
        Dimension itemSize = new Dimension(Integer.MAX_VALUE, fixedHeight);
        button.setMaximumSize(itemSize);

        // Đăng ký nút vào Map và Listener
        menuButtons.put(cardName, button);

        // Xử lý hiệu ứng hover/màu sắc
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setBackground(HOVER_COLOR);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(HOVER_COLOR)) {
                    button.setBackground(PRIMARY_COLOR);
                }
            }
        });
        return button;
    }

    /**
     * Tạo Separator giữa các nhóm chức năng
     */
    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(255, 255, 255, 70));
        separator.setBackground(PRIMARY_COLOR);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separator;
    }


    // =================================================================================
    // KHU VỰC CONTENT PANEL & CARDLAYOUT
    // =================================================================================

    /**
     * Khởi tạo Panel chứa CardLayout và thêm các màn hình
     */
    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Thêm các màn hình (Giả định tất cả đều là JPanel)
        contentPanel.add(new ManHinhTrangChuNVBanVe(), "trangChuNV");
        contentPanel.add(new ManHinhMoCa(), "moCa");
        contentPanel.add(new ManHinhKetCa(), "ketCa");


        ManHinhBanVe banVePanel = new ManHinhBanVe();
        banVePanel.setName("banVeMoi"); // <-- Đặt tên nội bộ (Component.name)
        contentPanel.add(banVePanel, "banVeMoi");


        contentPanel.add(new JPanel(), "doiVe");
        contentPanel.add(new ManHinhTraVe(), "traVe");
        contentPanel.add(new ManHinhTraCuuVe(), "traCuuVe");
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
        // Đặt tất cả các nút về màu ban đầu
        for (JButton button : menuButtons.values()) {
            if (button != null) {
                button.setBackground(PRIMARY_COLOR);
            }
        }
        // Highlight nút đang hoạt động
        if (active != null) {
            active.setBackground(SELECTED_COLOR);
        }
    }

    /**
     * Thêm hoặc cập nhật một JPanel vào CardLayout.
     * Phương thức này giúp thêm các panel được khởi tạo với dữ liệu động.
     * * @param newPanel Panel mới cần thêm.
     * @param cardName Tên card (String) tương ứng.
     */
    public void addOrUpdateCard(JPanel newPanel, String cardName) {
        // 1. Tìm và xóa panel cũ dựa trên tên card
        // Note: Vì CardLayout không có getConstraints() công khai,
        // ta phải xóa panel cũ bằng cách duyệt qua tất cả và sử dụng remove().

        // Tạo một Component để giữ tham chiếu đến panel cũ cần xóa
        Component oldComponent = null;

        // Duyệt qua tất cả các Component trong contentPanel
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                oldComponent = comp;
                break;
            }
        }

        // Nếu tìm thấy component cũ, hãy xóa nó
        if (oldComponent != null) {
            contentPanel.remove(oldComponent);
        }

        // 2. Thêm panel mới
        // Đặt tên cho component mới, giúp việc tìm kiếm/xóa sau này dễ dàng hơn
        newPanel.setName(cardName);
        contentPanel.add(newPanel, cardName);

        // 3. Cập nhật giao diện
        contentPanel.revalidate();
        contentPanel.repaint();
    }



    // =================================================================================
    // XỬ LÝ SỰ KIỆN CHUNG
    // =================================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // Tìm tên card tương ứng với nút được click
        String cardName = menuButtons.entrySet().stream()
                .filter(entry -> entry.getValue() == src)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

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
        try {
            // Thiết lập Look and Feel để làm đẹp hơn chương trình
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new BanVeDashboard();
        });
    }

    public Component getCardByName(String cardName) {
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                return comp;
            }
        }
        return null;
    }
}