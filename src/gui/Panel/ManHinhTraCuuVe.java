package gui.Panel;

import dao.VeDAO;
import entity.Ve;
import entity.KhachHang; // Entity chi tiết
import entity.ChuyenTau; // Entity chi tiết
import entity.ChoDat; // Entity chi tiết

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

/**
 * ManHinhTraCuuVe: Màn hình Tra cứu vé theo mẫu, sử dụng logic DAO đã sửa lỗi.
 */
public class ManHinhTraCuuVe extends JPanel implements ActionListener {

    // --- CÁC MÀU SẮC VÀ FONT ---
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);

    // --- Components VÙNG 1 ---
    private ButtonGroup searchGroup;
    private JRadioButton rbMaVe, rbHoTenSDT, rbHoTenCCCD;
    private JTextField txtTimKiem;
    private JLabel lblTieuDeNhap;
    private JButton btnTimKiem, btnXoaBoLoc;

    // --- Components VÙNG 2 ---
    private JTable tableKetQua;
    private DefaultTableModel tableModel;

    // --- DAO & STATE ---
    private VeDAO veDAO;

    // Các hằng cho chế độ tìm kiếm
    private static final String MODE_MA_VE = "MaVe";
    private static final String MODE_SDT = "HoTenSDT";
    private static final String MODE_CCCD = "HoTenCCCD";


    public ManHinhTraCuuVe() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(BG_COLOR);

        // --- KHỞI TẠO DAO (Sử dụng lớp VeDAO trực tiếp) ---
        try {
            veDAO = new dao.VeDAO();
        } catch (Exception e) {
            veDAO = null;
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: Không thể khởi tạo VeDAO.", "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            System.err.println("Lỗi khởi tạo DAO: " + e.getMessage());
        }

        // 1. Tiêu đề
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Nội dung chính (Tìm kiếm + Bảng)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        contentPanel.add(createKhuVucTimKiem()); // Vùng 1
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createKhuVucKetQua()); // Vùng 2

        add(contentPanel, BorderLayout.CENTER);


        xoaBoLoc(); // Đặt trạng thái ban đầu
    }

    // =========================================================================
    // I. UI BUILDERS
    // =========================================================================

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Tra cứu vé");
        title.setFont(FONT_TITLE);
        panel.add(title, BorderLayout.EAST);

        JLabel nvLabel = new JLabel("Xin chào nhân viên, Trần Nam Sơn");
        nvLabel.setFont(FONT_BOLD_14);
        panel.add(nvLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createKhuVucTimKiem() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // --- 1. Phương thức tra cứu (Tabs) ---
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        tabsPanel.setOpaque(false);
        tabsPanel.setBorder(new EmptyBorder(10, 15, 5, 15));

        searchGroup = new ButtonGroup();
        rbMaVe = createRadioButton("Mã vé", MODE_MA_VE, true);
        rbHoTenSDT = createRadioButton("Họ tên + Số điện thoại", MODE_SDT, false);
        rbHoTenCCCD = createRadioButton("Họ tên + CCCD", MODE_CCCD, false);

        tabsPanel.add(rbMaVe);
        tabsPanel.add(rbHoTenSDT);
        tabsPanel.add(rbHoTenCCCD);

        // --- 2. Input và Nút ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(0, 15, 15, 15));

        lblTieuDeNhap = new JLabel("Mã vé:");
        lblTieuDeNhap.setFont(FONT_PLAIN_14);
        inputPanel.add(lblTieuDeNhap);

        txtTimKiem = new JTextField("VSE1-T2-G10", 30);
        txtTimKiem.setFont(FONT_PLAIN_14);
        txtTimKiem.setPreferredSize(new Dimension(300, 35));
        inputPanel.add(txtTimKiem);

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(PRIMARY_COLOR);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(FONT_BOLD_14);
        inputPanel.add(btnTimKiem);

        btnXoaBoLoc = new JButton("Xóa bộ lọc");
        btnXoaBoLoc.setFont(FONT_BOLD_14);
        inputPanel.add(btnXoaBoLoc);

        panel.add(tabsPanel);
        panel.add(inputPanel);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

        return panel;
    }

    private JRadioButton createRadioButton(String text, String command, boolean selected) {
        JRadioButton rb = new JRadioButton(text);
        rb.setActionCommand(command);
        rb.setSelected(selected);
        rb.setOpaque(false);
        rb.setFont(FONT_PLAIN_14);
        rb.addActionListener(e -> capNhatLabelTimKiem(command));
        searchGroup.add(rb);
        return rb;
    }

    private void capNhatLabelTimKiem(String mode) {
        switch (mode) {
            case MODE_MA_VE:
                lblTieuDeNhap.setText("Mã vé:");
                txtTimKiem.setToolTipText("Nhập Mã vé");
                break;
            case MODE_SDT:
                lblTieuDeNhap.setText("Họ tên + SĐT:");
                txtTimKiem.setToolTipText("Nhập Họ tên hoặc Số điện thoại");
                break;
            case MODE_CCCD:
                lblTieuDeNhap.setText("Họ tên + CCCD:");
                txtTimKiem.setToolTipText("Nhập Họ tên hoặc Số CCCD");
                break;
        }
    }

    private JScrollPane createKhuVucKetQua() {
        String[] columnNames = {"STT", "Mã vé", "Tên hành khách", "Trạng thái", "Hành trình", "Loại toa", "Ghế/Giường", "Ngày khởi hành", "Giá vé", "Chi tiết"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column != 9; }
        };

        tableKetQua = new JTable(tableModel);
        tableKetQua.setRowHeight(28);
        tableKetQua.setFont(FONT_PLAIN_14);
        tableKetQua.getTableHeader().setFont(FONT_BOLD_14);
        tableKetQua.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableKetQua.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableKetQua.getColumnModel().getColumn(0).setMaxWidth(40);

        JScrollPane scrollPane = new JScrollPane(tableKetQua);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách vé"));
        return scrollPane;
    }

    // =========================================================================
    // II. LOGIC & CSDL
    // =========================================================================

    private void xoaBoLoc() {
        txtTimKiem.setText("");
        tableModel.setRowCount(0);
        rbMaVe.setSelected(true);
        capNhatLabelTimKiem(MODE_MA_VE);
    }

    /**
     * Phương thức thực hiện tìm kiếm vé dựa trên chế độ đã chọn.
     */
    private void timKiemVe() {
        if (veDAO == null) return;

        String mode = searchGroup.getSelection().getActionCommand();
        String searchTerm = txtTimKiem.getText().trim();

        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin tìm kiếm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Ve> ketQua = new Vector<>();

        // Logic tìm kiếm
        if (mode.equals(MODE_MA_VE)) {
            Ve veTimThay = veDAO.getChiTietVeChoTraVe(searchTerm, null);
            if (veTimThay != null) {
                ketQua.add(veTimThay);
            }
        }
        // LƯU Ý: Các mode tìm kiếm khác cần được triển khai trong VeDAO.timVeTheoKhachHang(hoTen, sdt, cccd)

        // 2. Hiển thị kết quả
        if (ketQua == null || ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            tableModel.setRowCount(0);
            return;
        }

        napDuLieuLenBang(ketQua);
    }

    /**
     * Đưa kết quả tìm kiếm lên JTable.
     */
    private void napDuLieuLenBang(List<Ve> danhSach) {
        tableModel.setRowCount(0);
        int stt = 1;

        for (Ve ve : danhSach) {
            // Lấy dữ liệu Entity đã tra cứu và gán vào Ve:
            KhachHang kh = ve.getKhachHangChiTiet();
            ChuyenTau ct = ve.getChuyenTauChiTiet();
            ChoDat cd = ve.getChoDatChiTiet();

            // --- Ánh xạ dữ liệu từ Entities con ---
            String tenKhach = kh != null ? kh.getHoTen() : ve.getKhachHang();
            String maVe = ve.getId();

            String hanhTrinh = (ct != null && ct.getGaDi() != null && ct.getGaDen() != null) ?
                    ct.getGaDi().getTenGa() + " - " + ct.getGaDen().getTenGa() : "N/A";
            String loaiToa = (cd != null) ? cd.getMaToa() : "N/A";
            String gheGiuong = (cd != null) ? cd.getSoCho() : "N/A";
            String ngayKhoiHanh = (ct != null && ct.getNgayKhoiHanh() != null && ct.getGioKhoiHanh() != null) ?
                    ct.getNgayKhoiHanh().toString() + " " + ct.getGioKhoiHanh().toString() : "N/A";

            // Lấy trạng thái thực tế (Cần Entity Ve.java có getter cho TrangThai)
            String trangThaiVe = "Đã bán"; // TODO: Lấy trạng thái thực tế từ Ve Entity

            Object[] rowData = {
                    stt++,
                    maVe,
                    tenKhach,
                    trangThaiVe,
                    hanhTrinh,
                    loaiToa,
                    gheGiuong,
                    ngayKhoiHanh,
                    String.format("%,.0f", ve.getGia()),
                    "👁️"
            };
            tableModel.addRow(rowData);
        }
    }


    // =========================================================================
    // III. EVENT HANDLERS
    // =========================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTimKiem) {
            timKiemVe();
        } else if (e.getSource() == btnXoaBoLoc) {
            xoaBoLoc();
        }
    }

    // =========================================================================
    // IV. MAIN TEST
    // =========================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Demo Màn hình Tra cứu vé");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(new ManHinhTraCuuVe(), BorderLayout.CENTER);

            JPanel mockMenu = new JPanel();
            mockMenu.setBackground(new Color(34, 137, 203));
            mockMenu.setPreferredSize(new Dimension(200, 0));

            mainPanel.add(mockMenu, BorderLayout.WEST);

            frame.setContentPane(mainPanel);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}