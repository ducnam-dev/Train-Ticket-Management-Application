package gui.Panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

import dao.VeDAO;
import entity.Ve;

/**
 * ManHinhTraVe: Tái hiện giao diện Trả vé theo mẫu, tích hợp logic tìm kiếm/hủy vé.
 */
public class ManHinhTraVe extends JPanel {

    // --- CÁC MÀU SẮC VÀ FONT ---
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color COLOR_RED = new Color(231, 76, 60);
    private static final Color COLOR_BLUE_LIGHT = new Color(74, 184, 237);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);

    // --- Components ---
    // Đã xóa btnLichSuTraVe khỏi khai báo
    private JButton btnTimKiem, btnHuyBo, btnXacNhan;
    private JComboBox<String> cbTimKiemTheo;
    private JTextField txtMaVeHoacSDT;

    private JLabel lblTenKHValue, lblSDTValue, lblTuyenDuongValue, lblToaValue, lblThoiGianValue, lblSoGheValue, lblGiaGocValue, lblTienHoanTraValue;
    private JComboBox<String> cbLyDoTraVe;

    // --- DAO & STATE ---
    private VeDAO veDAO;
    private Ve veHienTai;

    public ManHinhTraVe() {
        // --- 1. SETUP ---
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(BG_COLOR);

        // --- 2. KHỞI TẠO DAO ---
        try {
            veDAO = new VeDAO();
        } catch (Exception e) {
            System.err.println("Không thể khởi tạo VeDAOImpl: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Lỗi hệ thống: Không thể kết nối với CSDL hoặc khởi tạo DAO.",
                    "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        // 3. THÊM CÁC PANEL
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);

        // 4. THÊM SỰ KIỆN
        initEventHandlers();

        // Đặt trạng thái ban đầu
        xoaTrangThongTin();
    }

    // =========================================================================
    // I. UI BUILDERS
    // =========================================================================

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Trả vé");
        title.setFont(FONT_TITLE);
        panel.add(title, BorderLayout.WEST);

        JLabel nvLabel = new JLabel("Trần Nam Sơn");
        nvLabel.setFont(FONT_BOLD_14);
        panel.add(nvLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createSearchPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createTicketInfoPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createReasonAndButtonPanel());
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // --- VÙNG 1: TÌM KIẾM ---
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder("Tìm kiếm thông tin vé", PRIMARY_COLOR));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchRow.setOpaque(false);

        // 1. Nút Lịch sử Trả vé (ĐÃ XÓA)

        // 2. Tìm kiếm theo
        searchRow.add(new JLabel("Tìm kiếm theo:"));
        cbTimKiemTheo = new JComboBox<>(new String[]{"Mã vé", "Số điện thoại"});
        cbTimKiemTheo.setFont(FONT_PLAIN_14);
        searchRow.add(cbTimKiemTheo);

        // 3. Input
        txtMaVeHoacSDT = new JTextField(15);
        txtMaVeHoacSDT.setFont(FONT_PLAIN_14);
        searchRow.add(txtMaVeHoacSDT);

        // 4. Nút Tìm kiếm
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(COLOR_BLUE_LIGHT);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(FONT_BOLD_14);
        btnTimKiem.setPreferredSize(new Dimension(100, 30));
        searchRow.add(btnTimKiem);

        panel.removeAll();
        panel.add(searchRow);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    // --- VÙNG 2: THÔNG TIN VÉ ---
    private JPanel createTicketInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder("Thông tin vé", PRIMARY_COLOR));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoGrid = new JPanel(new GridLayout(4, 4, 20, 10));
        infoGrid.setBorder(new EmptyBorder(10, 15, 15, 15));
        infoGrid.setOpaque(false);

        lblTenKHValue = new JLabel(); addLabelAndValue(infoGrid, "Họ tên khách hàng", lblTenKHValue, "---");
        lblSDTValue = new JLabel(); addLabelAndValue(infoGrid, "Số điện thoại", lblSDTValue, "---");
        lblTuyenDuongValue = new JLabel(); addLabelAndValue(infoGrid, "Tuyến đường", lblTuyenDuongValue, "---");
        lblToaValue = new JLabel(); addLabelAndValue(infoGrid, "Toa", lblToaValue, "---");
        lblThoiGianValue = new JLabel(); addLabelAndValue(infoGrid, "Thời gian khởi hành", lblThoiGianValue, "---");
        lblSoGheValue = new JLabel(); addLabelAndValue(infoGrid, "Số ghế", lblSoGheValue, "---");

        lblGiaGocValue = new JLabel(); addLabelAndValue(infoGrid, "Giá vé gốc", lblGiaGocValue, "---", Color.BLACK, FONT_BOLD_14);
        lblTienHoanTraValue = new JLabel(); addLabelAndValue(infoGrid, "Số tiền hoàn trả", lblTienHoanTraValue, "---", COLOR_RED, FONT_BOLD_14);

        panel.add(infoGrid, BorderLayout.CENTER);
        return panel;
    }

    private void addLabelAndValue(JPanel grid, String labelText, JLabel valueLabel, String initialValue) {
        addLabelAndValue(grid, labelText, valueLabel, initialValue, Color.BLACK, FONT_PLAIN_14);
    }

    private void addLabelAndValue(JPanel grid, String labelText, JLabel valueLabel, String initialValue, Color valueColor, Font valueFont) {
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_PLAIN_14);
        grid.add(label);

        valueLabel.setText(initialValue);
        valueLabel.setFont(valueFont);
        valueLabel.setForeground(valueColor);
        grid.add(valueLabel);
    }

    private JPanel createReasonAndButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel reasonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        reasonPanel.setBackground(Color.WHITE);
        reasonPanel.setBorder(createStyledBorder("Lý do trả vé", PRIMARY_COLOR));

        reasonPanel.add(new JLabel("Chọn lý do trả vé"));
        cbLyDoTraVe = new JComboBox<>(new String[]{"Chọn lý do trả vé", "Khách hàng thay đổi kế hoạch", "Lỗi nhập liệu", "Chuyến tàu bị hủy"});
        cbLyDoTraVe.setFont(FONT_PLAIN_14);
        reasonPanel.add(cbLyDoTraVe);

        panel.add(reasonPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        btnHuyBo = new JButton("Hủy bỏ");
        btnHuyBo.setBackground(COLOR_RED);
        btnHuyBo.setForeground(Color.WHITE);
        btnHuyBo.setFont(FONT_BOLD_14);

        btnXacNhan = new JButton("Xác nhận trả vé");
        btnXacNhan.setBackground(PRIMARY_COLOR);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(FONT_BOLD_14);

        buttonPanel.add(btnHuyBo);
        buttonPanel.add(btnXacNhan);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private TitledBorder createStyledBorder(String title, Color color) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONT_BOLD_14,
                color
        );
    }

    // =========================================================================
    // II. LOGIC & EVENTS
    // =========================================================================

    private void initEventHandlers() {
        btnTimKiem.addActionListener(e -> xuLyTimKiemVe());
        btnXacNhan.addActionListener(e -> xuLyHuyVe());
        btnHuyBo.addActionListener(e -> xoaTrangThongTin());
        // Đã xóa btnLichSuTraVe.addActionListener(e -> xuLyChuyenManHinhLichSu());
    }

    private void xuLyTimKiemVe() {
        if (veDAO == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: VeDAO chưa được khởi tạo. Vui lòng kiểm tra kết nối CSDL.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String searchBy = (String) cbTimKiemTheo.getSelectedItem();
        String searchText = txtMaVeHoacSDT.getText().trim();

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã vé hoặc Số điện thoại để tìm kiếm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maVe = "Mã vé".equals(searchBy) ? searchText : null;
        String sdt = "Số điện thoại".equals(searchBy) ? searchText : null;

        veHienTai = veDAO.getChiTietVeChoTraVe(maVe, sdt);

        if (veHienTai != null) {
            hienThiThongTinVe(veHienTai);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin vé phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            xoaTrangThongTin();
        }
    }

    private void xuLyHuyVe() {
        if (veHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm kiếm vé trước khi xác nhận trả vé.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String lyDo = (String) cbLyDoTraVe.getSelectedItem();
        if (lyDo == null || lyDo.equals("Chọn lý do trả vé")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lý do trả vé.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận hủy vé " + veHienTai.getId() + " và hoàn trả " + lblTienHoanTraValue.getText() + "?",
                "Xác nhận Trả vé", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Giả sử getId() trả về Mã vé (String)
            if (veDAO.huyVe(veHienTai.getId())) { // SỬ DỤNG veHienTai.getId() (String)
                JOptionPane.showMessageDialog(this, "Trả vé thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                xoaTrangThongTin();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái vé.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
// à thì ra là thông tin giả lập thôi, không lấy từ DAO
    private void hienThiThongTinVe(Ve ve) {
        lblTenKHValue.setText(ve.getKhachHang() != null ? ve.getKhachHang() : "N/A");
        lblSDTValue.setText("----"); // Giả lập dữ liệu SĐT (cần lấy từ DAO)
        lblTuyenDuongValue.setText("-----"); // Giả lập (cần lấy từ DAO)
        lblToaValue.setText("--"); // Giả lập
        lblThoiGianValue.setText("----"); // Giả lập
        lblSoGheValue.setText("--"); // Giả lập
        lblGiaGocValue.setText(String.format("%,.0f VNĐ", ve.getGia()));

        double tienHoanTra = ve.getGia() * 0.9;
        lblTienHoanTraValue.setText(String.format("%,.0f VNĐ", tienHoanTra));

        btnXacNhan.setEnabled(true);
        cbLyDoTraVe.setEnabled(true);
    }

    private void xoaTrangThongTin() {
        lblTenKHValue.setText("---");
        lblSDTValue.setText("---");
        lblTuyenDuongValue.setText("---");
        lblToaValue.setText("---");
        lblThoiGianValue.setText("---");
        lblSoGheValue.setText("---");
        lblGiaGocValue.setText("---");
        lblTienHoanTraValue.setText("---");

        cbLyDoTraVe.setSelectedIndex(0);
        txtMaVeHoacSDT.setText("");

        veHienTai = null;
        btnXacNhan.setEnabled(false);
        cbLyDoTraVe.setEnabled(false);
    }

    // =========================================================================
    // III. MAIN TEST
    // =========================================================================

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Demo Màn hình Trả vé");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(new ManHinhTraVe(), BorderLayout.CENTER);

            JPanel mockMenu = new JPanel();
            mockMenu.setBackground(new Color(34, 137, 203));
            mockMenu.setPreferredSize(new Dimension(200, 0));

            JLabel lblTraVe = new JLabel("<html><b style='color: white;'>▶ Trả vé</b></html>");
            lblTraVe.setBorder(new EmptyBorder(10, 10, 10, 10));
            lblTraVe.setBackground(new Color(74, 184, 237));
            lblTraVe.setOpaque(true);
            lblTraVe.setMaximumSize(new Dimension(200, 40));
            lblTraVe.setAlignmentX(Component.LEFT_ALIGNMENT);

            mockMenu.setLayout(new BoxLayout(mockMenu, BoxLayout.Y_AXIS));
            mockMenu.add(Box.createVerticalStrut(200));
            mockMenu.add(lblTraVe);
            mockMenu.add(Box.createVerticalGlue());

            mainPanel.add(mockMenu, BorderLayout.WEST);

            frame.setContentPane(mainPanel);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}