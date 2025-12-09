package gui.Popup;

import com.toedter.calendar.JDateChooser;
import gui.Panel.ManHinhQuanLyKhuyenMai;
import gui.Panel.ManHinhQuanLyKhuyenMai2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Lớp này tạo giao diện Popup để Tạo/Sửa Khuyến Mãi.
 */
public class PopupTaoKhuyenMai extends JDialog implements ActionListener {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
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

    // Các nút chức năng
    private JButton btnLuu, btnHuy, btnThemDK;

    // Tham chiếu đến màn hình quản lý để gọi loadDataToTable() sau khi thao tác
    private ManHinhQuanLyKhuyenMai2 parentPanel;
    private String currentMaKM; // Lưu MaKM nếu là chế độ Sửa (null nếu là Tạo mới)


    public PopupTaoKhuyenMai(JFrame parent, ManHinhQuanLyKhuyenMai2 parentPanel, String maKM) {
        super(parent, true); // true: modal dialog (chặn tương tác với cửa sổ chính)
        this.parentPanel = parentPanel;
        this.currentMaKM = maKM;

        setTitle(maKM == null ? "Tạo Khuyến Mãi Mới" : "Cập Nhật Khuyến Mãi: " + maKM);
        setSize(750, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBackground(BG_COLOR);
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Tiêu đề Dialog ---
        JLabel title = new JLabel(maKM == null ? "Tạo Khuyến Mãi" : "Cập Nhật Khuyến Mãi");
        title.setFont(FONT_TITLE);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        contentPane.add(title, BorderLayout.NORTH);

        // --- Khu vực chính (Form) ---
        JPanel formArea = createFormPanel();
        contentPane.add(formArea, BorderLayout.CENTER);

        // --- Panel Nút Lưu/Hủy ---
        JPanel actionPanel = createActionPanel();
        contentPane.add(actionPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);

        // Khởi tạo trạng thái form
        if (maKM == null) {
            lamMoiForm(); // Chế độ Tạo mới
        } else {
            // Chế độ Cập nhật: Gọi hàm load dữ liệu KM theo MaKM
            loadDataForEdit(maKM);
        }
    }

    /**
     * Tạo panel chứa form nhập liệu chi tiết. (Giống với form cũ)
     */
    private JPanel createFormPanel() {
        // ... (Giữ nguyên form nhập liệu chi tiết từ code cũ)
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
        panel.add(new JLabel("Điều kiện:"));
        JComboBox<String> cbLoaiDK = new JComboBox<>(new String[]{"LOAI_KHACH", "GA_DI", "GA_DEN", "SO_LUONG", "GIA_TRI_TT"});
        panel.add(cbLoaiDK);

        panel.add(new JLabel("Giá trị:"));
        JTextField txtGiaTriDK = new JTextField(10);
        panel.add(txtGiaTriDK);

        // Nút Thêm Điều kiện
        btnThemDK = new JButton("Thêm điều kiện");
        btnThemDK.addActionListener(this);
        panel.add(btnThemDK);

        // Bảng nhỏ hiển thị các điều kiện đã thêm (cho giao diện)
        // ... (Cần một bảng nhỏ hoặc JList ở đây để hiển thị điều kiện đã nhập)

        return panel;
    }

    /**
     * Tạo panel chứa các nút Lưu và Hủy
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panel.setOpaque(false);

        btnLuu = new JButton(currentMaKM == null ? "✅ Lưu Khuyến Mãi" : "💾 Cập Nhật");
        btnLuu.addActionListener(this);

        btnHuy = new JButton("❌ Hủy");
        btnHuy.addActionListener(this);

        panel.add(btnLuu);
        panel.add(btnHuy);

        return panel;
    }

    /**
     * [Giả định] Đổ dữ liệu từ CSDL vào form khi Sửa
     */
    private void loadDataForEdit(String maKM) {
        // [Logic DAO]: Gọi DAO.getKhuyenMaiByID(maKM) để lấy dữ liệu

        // Giả định đổ dữ liệu mẫu
        txtMaKM.setText(maKM);
        txtTenKM.setText("Khuyến mãi Đã Sửa (" + maKM + ")");
        try {
            dateChooserBatDau.setDate(DATE_FORMAT.parse("2025-11-01"));
            dateChooserKetThuc.setDate(DATE_FORMAT.parse("2025-12-31"));
        } catch (Exception e) {
            // bỏ qua
        }
        cbLoaiApDung.setSelectedItem("HOA_DON");
        spinnerPhanTram.setValue(0.15);
        spinnerTienGiam.setValue(0);
        txtAreaMoTa.setText("Chi tiết khuyến mãi cần cập nhật. Mã: " + maKM);

        // Cần thêm logic load DieuKienKhuyenMai
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
        // Cần thêm logic làm mới các trường DieuKienKhuyenMai
    }

    private String generateNewMaKM() {
        // [Logic DAO]: Tìm mã KM lớn nhất và tăng lên 1
        return "KM" + (int)(Math.random() * 9000 + 1000); // Mã giả định
    }

    // Giữ nguyên hàm kiểm tra hợp lệ
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
        // ... (Cần thêm logic kiểm tra trùng MaKM khi THÊM MỚI)

        return true;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnHuy) {
            dispose(); // Đóng Popup
        }
        else if (src == btnLuu) {
            if (currentMaKM == null) {
                handleThemKhuyenMai(); // Chế độ Tạo mới
            } else {
                handleCapNhatKhuyenMai(); // Chế độ Cập nhật
            }
        }
        else if (src == btnThemDK) {
            JOptionPane.showMessageDialog(this, "Logic Thêm Điều Kiện sẽ được thực hiện tại đây.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void handleThemKhuyenMai() {
        if (!validateAndGetFormData()) return;

        // [Logic DAO]: Gọi DAO.themKhuyenMai(...)
        JOptionPane.showMessageDialog(this, "Tạo Khuyến Mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        parentPanel.loadDataToTable(); // Tải lại dữ liệu ở màn hình chính
        dispose(); // Đóng Popup
    }

    public void handleCapNhatKhuyenMai() {
        if (!validateAndGetFormData()) return;

        // [Logic DAO]: Gọi DAO.capNhatKhuyenMai(...)
        JOptionPane.showMessageDialog(this, "Cập Nhật Khuyến Mãi [" + currentMaKM + "] thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        parentPanel.loadDataToTable(); // Tải lại dữ liệu ở màn hình chính
        dispose(); // Đóng Popup
    }
}