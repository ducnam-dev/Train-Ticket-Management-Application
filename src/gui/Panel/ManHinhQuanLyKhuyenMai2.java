//ManHinhQuanLyKhuyenMai2

package gui.Panel;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import gui.Popup.PopupTaoKhuyenMai; // Import lớp Popup mới

/**
 * Lớp này tạo giao diện Quản lý Khuyến Mãi (Màn hình chính).
 * Chỉ hiển thị bảng và các nút điều khiển chính.
 */
public class ManHinhQuanLyKhuyenMai2 extends JPanel implements ActionListener {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("###,###,##0");


    // Khai báo các component chính trên màn hình quản lý
    private JTable table;
    private DefaultTableModel tableModel;

    // Các nút chức năng
    private JButton btnThem, btnSua, btnKetThuc, btnGiaHan, btnLamMoi;

    // Các trường dữ liệu sẽ được sử dụng cho chức năng Sửa/Kết thúc/Gia hạn
    private JTextField txtMaKM; // Giữ lại để lưu Mã KM được chọn từ bảng

    // Gán tham chiếu của JFrame/JDialog cha để Popup có thể lấy
    private JFrame parentFrame;

    public ManHinhQuanLyKhuyenMai2() {
        // Tìm JFrame cha (nếu có)
        SwingUtilities.invokeLater(() -> {
            Container parent = getTopLevelAncestor();
            if (parent instanceof JFrame) {
                parentFrame = (JFrame) parent;
            } else if (parent instanceof JDialog) {
                parentFrame = (JFrame) ((JDialog) parent).getParent();
            }
        });


        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Tiêu đề ---
        JLabel title = new JLabel("Quản lý Khuyến Mãi");
        title.setFont(FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        add(title, BorderLayout.NORTH);

        // --- Khu vực chính (Nút và Bảng) ---
        JPanel mainArea = new JPanel();
        mainArea.setLayout(new BoxLayout(mainArea, BoxLayout.Y_AXIS));
        mainArea.setOpaque(false);

        // Cần một JTextField ẩn để lưu Mã KM được chọn từ bảng
        txtMaKM = new JTextField();
        txtMaKM.setVisible(false);
        this.add(txtMaKM);

        // 1. Panel Nút chức năng
        JPanel buttonPanel = createButtonPanel();
        mainArea.add(buttonPanel);

        // Khoảng cách
        mainArea.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Bảng dữ liệu
        JPanel tablePanel = createTablePanel();
        mainArea.add(tablePanel);

        add(mainArea, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        loadDataToTable();
        lamMoiTrangThaiChon();
    }

    /**
     * Tạo panel chứa các nút chức năng (Tạo, Sửa, Kết thúc, Gia hạn)
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setOpaque(false);

        // Thay đổi: Nút "Tạo Khuyến Mãi" sẽ mở popup
        btnThem = new JButton("➕ Tạo Khuyến Mãi");
        btnSua = new JButton("📝 Cập Nhật");
        btnKetThuc = new JButton("⛔ Kết Thúc KM");
        btnGiaHan = new JButton("⏳ Gia Hạn KM");
        btnLamMoi = new JButton("🔄 Làm Mới");

        // Đặt màu cho nút chính (Tạo mới)
        btnThem.setBackground(PRIMARY_COLOR);
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnKetThuc.addActionListener(this);
        btnGiaHan.addActionListener(this);
        btnLamMoi.addActionListener(this);

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnKetThuc);
        buttonPanel.add(btnGiaHan);
        buttonPanel.add(btnLamMoi);

        return buttonPanel;
    }


    /**
     * Tạo panel chứa bảng hiển thị danh sách khuyến mãi
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));

        // Tên cột: Bao gồm các cột chính của KhuyenMai
        String[] columnNames = {"Mã KM", "Tên KM", "Bắt đầu", "Kết thúc", "Loại", "Giảm (%)", "Giảm (VND)", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setFont(FONT_PLAIN_14);
        table.getTableHeader().setFont(FONT_BOLD_14);
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        // Thêm sự kiện click chuột để lưu MaKM được chọn và bật nút
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    fillFormFromTable(row); // fillFormFromTable giờ chỉ cập nhật trạng thái
                }
            }
        });


        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * [Giả định] Đổ dữ liệu mẫu lên bảng
     */
    public void loadDataToTable() {
        // ... (Giữ nguyên logic loadDataToTable)
        tableModel.setRowCount(0); // Xóa dữ liệu cũ

        Object[][] data = {
                {"KM001", "Trẻ em 1/6", "2026-06-01", "2026-06-01", "VE_DON", 30.0, 0, "HoatDong"},
                {"KM002", "Mua 4 vé -10%", "2025-10-01", "2026-01-31", "HOA_DON", 10.0, 0, "HoatDong"},
                {"KM003", "Giảm 50k / 500k", "2025-01-01", "2026-12-31", "HOA_DON", 0.0, 50000, "HoatDong"},
                {"KM004", "Hè giảm 20%", "2025-06-01", "2025-08-31", "HOA_DON", 20.0, 0, "DaKetThuc"}
        };

        for (Object[] row : data) {
            // Định dạng lại các giá trị số và ngày cho hiển thị trên bảng
            Object[] newRow = row.clone();
            newRow[5] = row[5] + "%";
            newRow[6] = VND_FORMAT.format(row[6]);
            tableModel.addRow(newRow);
        }
        lamMoiTrangThaiChon();
    }

    /**
     * Cập nhật trạng thái nút khi click vào một hàng trên bảng.
     */
    private void fillFormFromTable(int row) {
        String maKM = tableModel.getValueAt(row, 0).toString();
        String trangThai = tableModel.getValueAt(row, 7).toString();

        txtMaKM.setText(maKM);

        // Kích hoạt các nút Sửa/Kết thúc/Gia hạn
        btnSua.setEnabled(true);
        btnKetThuc.setEnabled("HoatDong".equals(trangThai));
        btnGiaHan.setEnabled(true);
    }

    /**
     * Thiết lập trạng thái ban đầu/sau khi làm mới
     */
    private void lamMoiTrangThaiChon() {
        txtMaKM.setText("");
        btnSua.setEnabled(false);
        btnKetThuc.setEnabled(false);
        btnGiaHan.setEnabled(false);
        table.clearSelection();
    }

    // =================================================================================
    // LOGIC XỬ LÝ SỰ KIỆN (ActionListener)
    // =================================================================================

    // [Giả định] Hàm này không còn được dùng, nhưng giữ lại để tránh lỗi nếu bạn muốn dùng
    private String generateNewMaKM() {
        return "KM" + (int)(Math.random() * 9000 + 1000); // Mã giả định
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLamMoi) {
            lamMoiTrangThaiChon();
            loadDataToTable(); // Tải lại bảng
        }
        else if (src == btnThem) {
            // Mở Popup Tạo Khuyến Mãi
            PopupTaoKhuyenMai popup = new PopupTaoKhuyenMai(parentFrame, this, null);
            popup.setVisible(true);
        }
        else if (src == btnSua) {
            // Mở Popup Sửa Khuyến Mãi (cần load dữ liệu MaKM)
            String maKM = txtMaKM.getText();
            if (maKM.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Lấy dữ liệu đầy đủ của Khuyến Mãi (Giả định)
            // Object khuyenMai = KhuyenMaiDAO.getByID(maKM);

            // Lấy dữ liệu cơ bản từ bảng để truyền (Giả định)
            int row = table.getSelectedRow();
            if (row == -1) {
                // Xảy ra nếu người dùng click nút Sửa sau khi chọn rồi bỏ chọn
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Giả định: Lấy tạm dữ liệu row để truyền cho popup, thực tế nên gọi DAO
            String tenKM = tableModel.getValueAt(row, 1).toString();
            String ngayBD = tableModel.getValueAt(row, 2).toString();
            String ngayKT = tableModel.getValueAt(row, 3).toString();

            PopupTaoKhuyenMai popup = new PopupTaoKhuyenMai(parentFrame, this, maKM);
            // Giả định: Thiết lập dữ liệu cho popup để sửa
            // popup.setFormData(maKM, tenKM, ngayBD, ngayKT, ...);
            popup.setVisible(true);
        }
        else if (src == btnKetThuc) {
            handleKetThucKhuyenMai();
        }
        else if (src == btnGiaHan) {
            // Mở Popup Gia Hạn hoặc dùng dialog đơn giản
            handleGiaHanKhuyenMai();
        }
    }


    private void handleKetThucKhuyenMai() {
        String maKM = txtMaKM.getText();
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Kết Thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn KẾT THÚC Khuyến Mãi [" + maKM + "] ngay lập tức?",
                "Xác nhận Kết thúc", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // [Logic DAO]: Gọi DAO.ketThucKhuyenMai(MaKM, NgayHomNay)
            JOptionPane.showMessageDialog(this, "Đã Kết Thúc Khuyến Mãi [" + maKM + "].", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataToTable();
        }
    }

    private void handleGiaHanKhuyenMai() {
        String maKM = txtMaKM.getText();
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Gia Hạn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // **Trong thực tế: Mở một JDialog đơn giản để chọn Ngày Kết Thúc mới**

        // Giả định: Sử dụng JDateChooser tạm thời để lấy ngày mới
        JDateChooser newDateChooser = new JDateChooser();
        newDateChooser.setDateFormatString("dd/MM/yyyy");
        JPanel datePanel = new JPanel(new FlowLayout());
        datePanel.add(new JLabel("Chọn Ngày Kết Thúc mới:"));
        datePanel.add(newDateChooser);

        int result = JOptionPane.showConfirmDialog(this, datePanel, "Gia Hạn Khuyến Mãi [" + maKM + "]", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION && newDateChooser.getDate() != null) {
            Date ngayKetThucMoi = newDateChooser.getDate();
            // [Logic DAO]: Gọi DAO.giaHanKhuyenMai(MaKM, NgayKetThucMoi)
            JOptionPane.showMessageDialog(this, "Gia Hạn Khuyến Mãi [" + maKM + "] đến " + DATE_FORMAT.format(ngayKetThucMoi) + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataToTable();
        } else if (result == JOptionPane.OK_OPTION && newDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngày Kết Thúc mới.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Phương thức main để chạy độc lập
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Kiểm tra Màn hình Quản lý Khuyến Mãi");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);

            // Tạo một MainFrame giả định để chứa Panel
            JPanel mainFrame = new JPanel(new BorderLayout());
            mainFrame.add(new ManHinhQuanLyKhuyenMai2(), BorderLayout.CENTER);

            frame.setContentPane(mainFrame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}