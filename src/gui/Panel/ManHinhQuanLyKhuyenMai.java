package gui.Panel;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Lớp này tạo giao diện Quản lý Khuyến Mãi.
 * Chức năng: Tạo, Sửa, Kết thúc, Gia hạn KM.
 */
public class ManHinhQuanLyKhuyenMai extends JPanel implements ActionListener { // Implement ActionListener

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT (Dùng lại từ ManhinhQuanLyChuyenTau)
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("###,###,##0");


    // Khai báo các component
    private JTextField txtMaKM;
    private JTextField txtTenKM;
    private JDateChooser dateChooserBatDau;
    private JDateChooser dateChooserKetThuc;
    private JComboBox<String> cbLoaiApDung;
    private JSpinner spinnerPhanTram;
    private JSpinner spinnerTienGiam;
    private JTextArea txtAreaMoTa;
    private JTable table;
    private DefaultTableModel tableModel;

    // Các nút chức năng
    private JButton btnThem, btnSua, btnKetThuc, btnGiaHan, btnLamMoi, btnThemDK;


    public ManHinhQuanLyKhuyenMai() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Tiêu đề ---
        JLabel title = new JLabel("Quản lý Khuyến Mãi");
        title.setFont(FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        add(title, BorderLayout.NORTH);

        // --- Khu vực chính (Form và Bảng) ---
        JPanel mainArea = new JPanel();
        mainArea.setLayout(new BoxLayout(mainArea, BoxLayout.Y_AXIS));
        mainArea.setOpaque(false);

        // 1. Form nhập liệu và Điều kiện
        JPanel formPanel = createFormPanel();
        mainArea.add(formPanel);

        // Khoảng cách
        mainArea.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Bảng dữ liệu
        JPanel tablePanel = createTablePanel();
        mainArea.add(tablePanel);

        add(mainArea, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        loadDataToTable();
        lamMoiForm(); // Reset/Clear form
    }

    /**
     * Tạo panel chứa form nhập liệu chi tiết.
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Sử dụng GridBagLayout cho khu vực nhập liệu
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Cột 1 & 2: Thông tin cơ bản ---

        // Mã KM
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; fieldsPanel.add(new JLabel("Mã KM:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; txtMaKM = new JTextField(15); txtMaKM.setEditable(false); fieldsPanel.add(txtMaKM, gbc);

        // Tên KM
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; fieldsPanel.add(new JLabel("Tên KM:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; txtTenKM = new JTextField(15); fieldsPanel.add(txtTenKM, gbc);

        // Ngày Bắt Đầu
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; fieldsPanel.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; dateChooserBatDau = new JDateChooser(); dateChooserBatDau.setDateFormatString("dd/MM/yyyy"); fieldsPanel.add(dateChooserBatDau, gbc);

        // Ngày Kết Thúc
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; fieldsPanel.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; dateChooserKetThuc = new JDateChooser(); dateChooserKetThuc.setDateFormatString("dd/MM/yyyy"); fieldsPanel.add(dateChooserKetThuc, gbc);

        // Loại Áp Dụng (VE_DON / HOA_DON)
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; fieldsPanel.add(new JLabel("Áp dụng cho:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        cbLoaiApDung = new JComboBox<>(new String[]{"HOA_DON", "VE_DON"});
        fieldsPanel.add(cbLoaiApDung, gbc);

        // --- Cột 3 & 4: Giá trị giảm ---
        gbc.weightx = 0; gbc.gridwidth = 1; gbc.insets = new Insets(5, 20, 5, 5); // Lề trái cho cột mới

        // Phần Trăm Giảm
        gbc.gridx = 2; gbc.gridy = 0; fieldsPanel.add(new JLabel("Giảm (%):"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; gbc.insets = new Insets(5, 5, 5, 5);
        spinnerPhanTram = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.01));
        fieldsPanel.add(spinnerPhanTram, gbc);

        // Tiền Giảm Trừ
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0; gbc.insets = new Insets(5, 20, 5, 5);
        fieldsPanel.add(new JLabel("Giảm (VND):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1.0; gbc.insets = new Insets(5, 5, 5, 5);
        spinnerTienGiam = new JSpinner(new SpinnerNumberModel(0, 0, 10000000, 10000));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerTienGiam, "###,###,##0");
        spinnerTienGiam.setEditor(editor);
        fieldsPanel.add(spinnerTienGiam, gbc);

        // Mô Tả
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridheight = 2; gbc.weightx = 0; gbc.insets = new Insets(5, 20, 5, 5);
        fieldsPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; gbc.gridheight = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH; gbc.insets = new Insets(5, 5, 5, 5);
        txtAreaMoTa = new JTextArea(3, 20);
        txtAreaMoTa.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        fieldsPanel.add(new JScrollPane(txtAreaMoTa), gbc);

        // Quay lại gridheight = 1
        gbc.gridheight = 1;

        // --- Panel Điều kiện bổ sung (Giả định) ---
        JPanel dkPanel = createDieuKienPanel();
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(15, 5, 5, 5);
        fieldsPanel.add(dkPanel, gbc);


        panel.add(fieldsPanel, BorderLayout.CENTER);

        // --- Panel Nút chức năng ---
        JPanel buttonPanel = createButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height + 30));
        return panel;
    }

    /**
     * Tạo panel chứa các trường cho Điều kiện Khuyến Mãi (LoaiKhach, SoLuong,...)
     */
    private JPanel createDieuKienPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Điều kiện áp dụng bổ sung (DieuKienKhuyenMai)"));
        panel.setBackground(Color.WHITE);

        // Các trường giả định cho DieuKienKhuyenMai:

        // 1. Loại điều kiện
        panel.add(new JLabel("Điều kiện:"));
        JComboBox<String> cbLoaiDK = new JComboBox<>(new String[]{"LOAI_KHACH", "GA_DI", "GA_DEN", "SO_LUONG", "GIA_TRI_TT"});
        panel.add(cbLoaiDK);

        // 2. Giá trị đối chiếu
        panel.add(new JLabel("Giá trị:"));
        JTextField txtGiaTriDK = new JTextField(10);
        panel.add(txtGiaTriDK);

        // Nút Thêm Điều kiện
        btnThemDK = new JButton("Thêm điều kiện");
        btnThemDK.addActionListener(this); // Đăng ký sự kiện
        panel.add(btnThemDK);

        // Bảng nhỏ hiển thị các điều kiện đã thêm (cho giao diện)
        // ... (Cần một bảng nhỏ hoặc JList ở đây để hiển thị điều kiện đã nhập)

        return panel;
    }

    /**
     * Tạo panel chứa các nút chức năng (Tạo, Sửa, Kết thúc, Gia hạn)
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnThem = new JButton("Tạo Khuyến Mãi");
        btnSua = new JButton("Cập Nhật");
        btnKetThuc = new JButton("Kết Thúc KM");
        btnGiaHan = new JButton("Gia Hạn KM");
        btnLamMoi = new JButton("Làm Mới");

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

        // Thêm sự kiện click chuột để đổ dữ liệu lên form
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    fillFormFromTable(row);
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
        // Đây là logic giả định, trong thực tế cần truy vấn CSDL (Ví dụ: KhuyenMaiDAO.getAll())
        tableModel.setRowCount(0); // Xóa dữ liệu cũ

        Object[][] data = {
                {"KM001", "Trẻ em 1/6", "2026-06-01", "2026-06-01", "VE_DON", 30.0, 0, "HoatDong"},
                {"KM002", "Mua 4 vé -10%", "2025-10-01", "2026-01-31", "HOA_DON", 10.0, 0, "HoatDong"},
                {"KM003", "Giảm 50k / 500k", "2025-01-01", "2026-12-31", "HOA_DON", 0.0, 50000, "HoatDong"},
                {"KM004", "Hè giảm 20%", "2025-06-01", "2025-08-31", "HOA_DON", 20.0, 0, "DaKetThuc"}
        };

        for (Object[] row : data) {
            // Định dạng lại các giá trị số và ngày cho hiển thị trên bảng
            row[5] = row[5] + "%";
            row[6] = VND_FORMAT.format(row[6]);
            tableModel.addRow(row);
        }
    }

    /**
     * [Giả định] Đổ dữ liệu từ bảng lên form khi click
     */
    private void fillFormFromTable(int row) {
        try {
            txtMaKM.setText(tableModel.getValueAt(row, 0).toString());
            txtTenKM.setText(tableModel.getValueAt(row, 1).toString());

            // Chuyển đổi String sang Date cho JDateChooser
            dateChooserBatDau.setDate(DATE_FORMAT.parse(tableModel.getValueAt(row, 2).toString()));
            dateChooserKetThuc.setDate(DATE_FORMAT.parse(tableModel.getValueAt(row, 3).toString()));
            cbLoaiApDung.setSelectedItem(tableModel.getValueAt(row, 4).toString());

            // Xử lý Phần Trăm Giảm (Đưa về giá trị 0.xx)
            String phanTramStr = tableModel.getValueAt(row, 5).toString().replace("%", "");
            spinnerPhanTram.setValue(Double.parseDouble(phanTramStr) / 100.0);

            // Xử lý Tiền Giảm Trừ (Đưa về giá trị số nguyên)
            String tienGiamStr = tableModel.getValueAt(row, 6).toString().replaceAll("[^\\d]", "");
            spinnerTienGiam.setValue(Integer.parseInt(tienGiamStr));

            // Mô tả và Điều kiện cần được load từ CSDL sau khi có MaKM
            txtAreaMoTa.setText("Chi tiết KM: " + txtMaKM.getText()); // Giả định

            // Kích hoạt các nút Sửa/Kết thúc/Gia hạn
            btnSua.setEnabled(true);
            btnKetThuc.setEnabled("HoatDong".equals(tableModel.getValueAt(row, 7)));
            btnGiaHan.setEnabled(true);
            btnThem.setEnabled(false); // Không cho thêm khi đang sửa

            // Xóa chọn bảng
            table.clearSelection();


        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi đổ dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =================================================================================
    // LOGIC XỬ LÝ SỰ KIỆN (ActionListener)
    // =================================================================================

    /**
     * Lấy dữ liệu từ form và thực hiện kiểm tra cơ bản.
     * @return true nếu dữ liệu hợp lệ.
     */
    private boolean validateAndGetFormData() {
        String tenKM = txtTenKM.getText().trim();
        Date ngayBD = dateChooserBatDau.getDate();
        Date ngayKT = dateChooserKetThuc.getDate();
        double phanTram = (Double) spinnerPhanTram.getValue();
        int tienGiam = (Integer) spinnerTienGiam.getValue();

        if (tenKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên Khuyến Mãi không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenKM.requestFocus();
            return false;
        }
        if (ngayBD == null || ngayKT == null) {
            JOptionPane.showMessageDialog(this, "Ngày Bắt Đầu và Ngày Kết Thúc không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (ngayKT.before(ngayBD)) {
            JOptionPane.showMessageDialog(this, "Ngày Kết Thúc phải sau hoặc bằng Ngày Bắt Đầu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (phanTram > 0 && tienGiam > 0) {
            JOptionPane.showMessageDialog(this, "Chỉ được chọn GIẢM THEO PHẦN TRĂM hoặc GIẢM THEO SỐ TIỀN, không được chọn cả hai.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (phanTram == 0.0 && tienGiam == 0) {
            JOptionPane.showMessageDialog(this, "Phải chọn mức giảm giá.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Cần thêm logic kiểm tra trùng MaKM khi THÊM MỚI

        return true;
    }

    private void lamMoiForm() {
        txtMaKM.setText(generateNewMaKM()); // Tạo mã KM mới
        txtTenKM.setText("");
        dateChooserBatDau.setDate(null);
        dateChooserKetThuc.setDate(null);
        cbLoaiApDung.setSelectedIndex(0);
        spinnerPhanTram.setValue(0.0);
        spinnerTienGiam.setValue(0);
        txtAreaMoTa.setText("");

        // Kích hoạt/Vô hiệu hóa nút
        btnThem.setEnabled(true);
        btnSua.setEnabled(false);
        btnKetThuc.setEnabled(false);
        btnGiaHan.setEnabled(false);
        table.clearSelection();
    }

    private String generateNewMaKM() {
        // [Logic DAO]: Tìm mã KM lớn nhất và tăng lên 1
        return "KM" + (int)(Math.random() * 9000 + 1000); // Mã giả định
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLamMoi) {
            lamMoiForm();
            loadDataToTable(); // Tải lại bảng
        }
        else if (src == btnThem) {
            handleThemKhuyenMai();
        }
        else if (src == btnSua) {
            handleCapNhatKhuyenMai();
        }
        else if (src == btnKetThuc) {
            handleKetThucKhuyenMai();
        }
        else if (src == btnGiaHan) {
            handleGiaHanKhuyenMai();
        }
        else if (src == btnThemDK) {
            JOptionPane.showMessageDialog(this, "Logic Thêm Điều Kiện sẽ được thực hiện tại đây (cần bảng DieuKienKhuyenMai).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleThemKhuyenMai() {
        if (!validateAndGetFormData()) return;

        // [Logic DAO]: Gọi DAO.themKhuyenMai(...)
        JOptionPane.showMessageDialog(this, "Tạo Khuyến Mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        loadDataToTable();
        lamMoiForm();
    }

    private void handleCapNhatKhuyenMai() {
        if (txtMaKM.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Cập Nhật.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!validateAndGetFormData()) return;

        // [Logic DAO]: Gọi DAO.capNhatKhuyenMai(...)
        JOptionPane.showMessageDialog(this, "Cập Nhật Khuyến Mãi [" + txtMaKM.getText() + "] thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        loadDataToTable();
        lamMoiForm();
    }

    private void handleKetThucKhuyenMai() {
        if (txtMaKM.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Kết Thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn KẾT THÚC Khuyến Mãi [" + txtMaKM.getText() + "] ngay lập tức?",
                "Xác nhận Kết thúc", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // [Logic DAO]: Gọi DAO.ketThucKhuyenMai(MaKM, NgayHomNay)
            JOptionPane.showMessageDialog(this, "Đã Kết Thúc Khuyến Mãi [" + txtMaKM.getText() + "].", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataToTable();
            lamMoiForm();
        }
    }

    private void handleGiaHanKhuyenMai() {
        if (txtMaKM.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Gia Hạn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Date ngayKetThucMoi = dateChooserKetThuc.getDate();
        if (ngayKetThucMoi == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngày Kết Thúc mới cho Khuyến Mãi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // [Logic DAO]: Gọi DAO.giaHanKhuyenMai(MaKM, NgayKetThucMoi)
        JOptionPane.showMessageDialog(this, "Gia Hạn Khuyến Mãi [" + txtMaKM.getText() + "] đến " + DATE_FORMAT.format(ngayKetThucMoi) + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        loadDataToTable();
        lamMoiForm();
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
            mainFrame.add(new ManHinhQuanLyKhuyenMai(), BorderLayout.CENTER);

            frame.setContentPane(mainFrame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}