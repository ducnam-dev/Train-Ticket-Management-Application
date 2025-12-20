package gui.Popup;

import com.toedter.calendar.JDateChooser;
import dao.KhuyenMaiDAO; // Import DAO
import entity.KhuyenMai; // Import Entity
import gui.Panel.ManHinhQuanLyKhuyenMai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Lớp này tạo giao diện Popup để Tạo/Sửa Khuyến Mãi.
 * Tích hợp DAO để xử lý nghiệp vụ.
 */
public class PopupTaoKhuyenMai extends JDialog implements ActionListener {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final SimpleDateFormat DATE_FORMAT_SQL = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("###,###,##0");


    // Khai báo các component chính
    private JTextField txtMaKM;
    private JTextField txtTenKM;
    private JDateChooser dateChooserBatDau;
    private JDateChooser dateChooserKetThuc;

    // Loại Giảm Giá (PHAN_TRAM_GIA/CO_DINH) được xác định từ spinnerPhanTram/spinnerTienGiam
    private JSpinner spinnerPhanTram; // Lưu giá trị 0.0 -> 1.0 (0% -> 100%)
    private JSpinner spinnerTienGiam; // Lưu giá trị tiền cố định

    // Khu vực điều kiện (DKApDung: MIN_GIA/MIN_SL/NONE)
    private JComboBox<String> cbDieuKien;
    private JTextField txtGiaTriDK; // Dùng để nhập Giá trị cho MIN_GIA hoặc MIN_SL

    private JTextArea txtAreaMoTa; // Giữ nguyên để người dùng nhập mô tả chi tiết, nhưng KHÔNG LƯU vào CSDL
    private JButton btnLuu, btnHuy;

    // Tham chiếu DAO và Panel
    private KhuyenMaiDAO khuyenMaiDAO;
    private ManHinhQuanLyKhuyenMai parentPanel;
    private String currentMaKM;

    // Tên trường trong DB dùng chung cho cả Form và Validation
    private static final String LOAI_DK_MIN_GIA = "MIN_GIA";
    private static final String LOAI_DK_MIN_SL = "MIN_SL";
    private static final String LOAI_DK_NONE = "NONE";

    private static final String LOAI_GIAM_PHAN_TRAM = "PHAN_TRAM_GIA";
    private static final String LOAI_GIAM_CO_DINH = "CO_DINH";


    public PopupTaoKhuyenMai(JFrame parent, ManHinhQuanLyKhuyenMai parentPanel, String maKM) {
        super(parent, true);
        this.parentPanel = parentPanel;
        this.currentMaKM = maKM;
        this.khuyenMaiDAO = new KhuyenMaiDAO(); // Khởi tạo DAO

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
            loadDataForEdit(maKM);
        }

        // Thêm Listener cho các Spinner để chỉ cho phép chọn một loại giảm
        addListenerToSpinners();
    }

    private void addListenerToSpinners() {
        spinnerPhanTram.addChangeListener(e -> {
            if ((Double) spinnerPhanTram.getValue() > 0.0 && (Integer) spinnerTienGiam.getValue() > 0) {
                spinnerTienGiam.setValue(0);
            }
        });

        spinnerTienGiam.addChangeListener(e -> {
            if ((Integer) spinnerTienGiam.getValue() > 0 && (Double) spinnerPhanTram.getValue() > 0.0) {
                spinnerPhanTram.setValue(0.0);
            }
        });
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

        // --- Cột 3 & 4: Giá trị giảm ---
        gbc.weightx = 0; gbc.gridwidth = 1; gbc.insets = new Insets(5, 20, 5, 5); // Lề trái cho cột mới

        // Phần Trăm Giảm (Loại KM: PHAN_TRAM_GIA)
        gbc.gridx = 2; gbc.gridy = 0; fieldsPanel.add(new JLabel("Giảm (%):"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; gbc.insets = new Insets(5, 5, 5, 5);
        // Thay đổi SpinnerNumberModel: Max 100%, Step 1%
        spinnerPhanTram = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        fieldsPanel.add(spinnerPhanTram, gbc);

        // Tiền Giảm Trừ (Loại KM: CO_DINH)
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0; gbc.insets = new Insets(5, 20, 5, 5);
        fieldsPanel.add(new JLabel("Giảm (VND):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1.0; gbc.insets = new Insets(5, 5, 5, 5);
        spinnerTienGiam = new JSpinner(new SpinnerNumberModel(0, 0, 10000000, 10000));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerTienGiam, "###,###,##0");
        spinnerTienGiam.setEditor(editor);
        fieldsPanel.add(spinnerTienGiam, gbc);

        // Ô trống
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.insets = new Insets(5, 5, 5, 5);
        fieldsPanel.add(new JLabel(""), gbc);


        // --- Khu vực Điều kiện Áp Dụng (MIN_GIA / MIN_SL) ---
        JPanel dkPanel = createDieuKienPanel();
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(15, 5, 5, 5);
        fieldsPanel.add(dkPanel, gbc);

        // Mô Tả
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH; gbc.insets = new Insets(5, 5, 5, 5);
        fieldsPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4; gbc.gridheight = 2;
        txtAreaMoTa = new JTextArea(5, 20);
        txtAreaMoTa.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        fieldsPanel.add(new JScrollPane(txtAreaMoTa), gbc);


        panel.add(fieldsPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Tạo panel chứa các trường cho Điều kiện Khuyến Mãi (DKApDung: MIN_GIA/MIN_SL/NONE)
     */
    private JPanel createDieuKienPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Điều kiện áp dụng"));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("Loại điều kiện:"));
        cbDieuKien = new JComboBox<>(new String[]{
                "Không có điều kiện",
                "Hóa đơn tối thiểu (VND)",
                "Số lượng vé tối thiểu"
        });
        cbDieuKien.addActionListener(this);
        panel.add(cbDieuKien);

        panel.add(new JLabel("Giá trị ĐK:"));
        txtGiaTriDK = new JTextField(15);
        txtGiaTriDK.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(txtGiaTriDK);

        // Khởi tạo trạng thái ban đầu
        txtGiaTriDK.setEnabled(false);
        txtGiaTriDK.setText("");

        return panel;
    }

    /**
     * Tạo panel chứa các nút Lưu và Hủy
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panel.setOpaque(false);

        btnLuu = new JButton(currentMaKM == null ? "✅ Lưu Khuyến Mãi" : "💾 Cập Nhật");
        btnLuu.setFont(FONT_BOLD_14);
        btnLuu.addActionListener(this);

        btnHuy = new JButton("❌ Hủy");
        btnHuy.setFont(FONT_BOLD_14);
        btnHuy.addActionListener(this);

        panel.add(btnLuu);
        panel.add(btnHuy);

        return panel;
    }

    /**
     * Đổ dữ liệu từ CSDL vào form khi Sửa
     */
    private void loadDataForEdit(String maKM) {
        KhuyenMai km = khuyenMaiDAO.layKhuyenMaiTheoMa(maKM);
        if (km == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy Khuyến Mãi cần sửa: " + maKM, "Lỗi", JOptionPane.ERROR_MESSAGE);
            lamMoiForm();
            return;
        }

        txtMaKM.setText(km.getMaKM());
        txtTenKM.setText(km.getTenKM());

        // Ngày
        dateChooserBatDau.setDate(Date.from(km.getNgayBD().atZone(ZoneId.systemDefault()).toInstant()));
        dateChooserKetThuc.setDate(Date.from(km.getNgayKT().atZone(ZoneId.systemDefault()).toInstant()));

        // Loại giảm giá
        double giamGia = km.getGiaTriGiam().doubleValue();
        if (LOAI_GIAM_PHAN_TRAM.equals(km.getLoaiKM())) {
            spinnerPhanTram.setValue((int) giamGia); // Lấy giá trị %
            spinnerTienGiam.setValue(0);
        } else if (LOAI_GIAM_CO_DINH.equals(km.getLoaiKM())) {
            spinnerPhanTram.setValue(0);
            spinnerTienGiam.setValue((int) giamGia);
        }

        // Điều kiện áp dụng
        if (LOAI_DK_MIN_GIA.equals(km.getDkApDung())) {
            cbDieuKien.setSelectedIndex(1); // Hóa đơn tối thiểu
            txtGiaTriDK.setText(VND_FORMAT.format(km.getGiaTriDK()));
            txtGiaTriDK.setEnabled(true);
        } else if (LOAI_DK_MIN_SL.equals(km.getDkApDung())) {
            cbDieuKien.setSelectedIndex(2); // Số lượng vé tối thiểu
            txtGiaTriDK.setText(km.getGiaTriDK().intValue() + "");
            txtGiaTriDK.setEnabled(true);
        } else {
            cbDieuKien.setSelectedIndex(0); // Không có điều kiện
            txtGiaTriDK.setText("");
            txtGiaTriDK.setEnabled(false);
        }

        txtAreaMoTa.setText(km.getTenKM()); // (Giả định: Dùng TenKM làm mô tả tạm thời nếu không có cột mô tả riêng)
    }

    private void lamMoiForm() {
        txtMaKM.setText(khuyenMaiDAO.khoiTaoMaKMMoi()); // Cần tạo hàm generateNewMaKM() trong DAO
        txtTenKM.setText("");
        dateChooserBatDau.setDate(null);
        dateChooserKetThuc.setDate(null);
        spinnerPhanTram.setValue(0);
        spinnerTienGiam.setValue(0);
        cbDieuKien.setSelectedIndex(0);
        txtGiaTriDK.setText("");
        txtGiaTriDK.setEnabled(false);
        txtAreaMoTa.setText("");
    }

    // Tạo KhuyenMai Entity từ dữ liệu Form (Sau khi đã Validate)
    private KhuyenMai createKhuyenMaiFromForm() {
        String maKM = txtMaKM.getText();
        String tenKM = txtTenKM.getText().trim();
        Date ngayBDDate = dateChooserBatDau.getDate();
        Date ngayKTDate = dateChooserKetThuc.getDate();

        // 1. Loại Giảm Giá & Giá trị
        int phanTram = (Integer) spinnerPhanTram.getValue();
        int tienGiam = (Integer) spinnerTienGiam.getValue();

        String loaiKM;
        BigDecimal giaTriGiam;

        if (phanTram > 0) {
            loaiKM = LOAI_GIAM_PHAN_TRAM;
            giaTriGiam = new BigDecimal(phanTram);
        } else { // tienGiam > 0
            loaiKM = LOAI_GIAM_CO_DINH;
            giaTriGiam = new BigDecimal(tienGiam);
        }

        // 2. Điều kiện Áp Dụng & Giá trị DK
        String dkApDung;
        BigDecimal giaTriDK = null;
        String dkValueStr = txtGiaTriDK.getText().trim().replace(",", "");

        int dkIndex = cbDieuKien.getSelectedIndex();
        if (dkIndex == 1) { // Hóa đơn tối thiểu (MIN_GIA)
            dkApDung = LOAI_DK_MIN_GIA;
            try {
                giaTriDK = new BigDecimal(dkValueStr);
            } catch (NumberFormatException e) { /* Đã xử lý ở validate */ }
        } else if (dkIndex == 2) { // Số lượng vé tối thiểu (MIN_SL)
            dkApDung = LOAI_DK_MIN_SL;
            try {
                giaTriDK = new BigDecimal(dkValueStr);
            } catch (NumberFormatException e) { /* Đã xử lý ở validate */ }
        } else { // Không có điều kiện
            dkApDung = LOAI_DK_NONE;
        }

        // Chuyển đổi Date sang LocalDateTime
        LocalDateTime ngayBD = ngayBDDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime ngayKT = ngayKTDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().withHour(23).withMinute(59).withSecond(59);

        // Xác định trạng thái ban đầu
        String trangThai = ngayBD.isAfter(LocalDateTime.now()) ? "KHONG_HOAT_DONG" : "HOAT_DONG";

        KhuyenMai km = new KhuyenMai();
        km.setMaKM(maKM);
        km.setTenKM(tenKM);
        km.setLoaiKM(loaiKM);
        km.setGiaTriGiam(giaTriGiam);
        km.setDkApDung(dkApDung);
        km.setGiaTriDK(giaTriDK);
        km.setNgayBD(ngayBD);
        km.setNgayKT(ngayKT);
        km.setTrangThai(trangThai);
        // Lưu ý: Trường mô tả (txtAreaMoTa) hiện không có cột tương ứng trong CSDL

        return km;
    }


    private boolean validateAndGetFormData() {
        String tenKM = txtTenKM.getText().trim();
        Date ngayBD = dateChooserBatDau.getDate();
        Date ngayKT = dateChooserKetThuc.getDate();
        int phanTram = (Integer) spinnerPhanTram.getValue();
        int tienGiam = (Integer) spinnerTienGiam.getValue();
        int dkIndex = cbDieuKien.getSelectedIndex();
        String dkValueStr = txtGiaTriDK.getText().trim().replace(",", "");

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
            JOptionPane.showMessageDialog(this, "Chỉ được chọn GIẢM THEO PHẦN TRĂM hoặc GIẢM THEO SỐ TIỀN.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (phanTram == 0 && tienGiam == 0) {
            JOptionPane.showMessageDialog(this, "Phải chọn mức giảm giá.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra Điều kiện áp dụng
        if (dkIndex != 0) {
            if (dkValueStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Giá trị cho Điều kiện.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtGiaTriDK.requestFocus();
                return false;
            }
            try {
                BigDecimal dkValue = new BigDecimal(dkValueStr);
                if (dkValue.doubleValue() <= 0) {
                    JOptionPane.showMessageDialog(this, "Giá trị Điều kiện phải lớn hơn 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    txtGiaTriDK.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Giá trị Điều kiện phải là số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtGiaTriDK.requestFocus();
                return false;
            }
        }

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
        else if (src == cbDieuKien) {
            // Bật/Tắt ô nhập Giá trị ĐK
            int dkIndex = cbDieuKien.getSelectedIndex();
            boolean isEnabled = dkIndex != 0;
            txtGiaTriDK.setEnabled(isEnabled);
            if (!isEnabled) {
                txtGiaTriDK.setText("");
            } else if (dkIndex == 1) {
                txtGiaTriDK.setText(VND_FORMAT.format(0)); // Format tiền
            } else if (dkIndex == 2) {
                txtGiaTriDK.setText("0"); // Format số lượng
            }
        }
    }

    public void handleThemKhuyenMai() {
        if (!validateAndGetFormData()) return;

        KhuyenMai newKm = createKhuyenMaiFromForm();

        if (khuyenMaiDAO.themKhuyenMai(newKm)) {
            JOptionPane.showMessageDialog(this, "Tạo Khuyến Mãi [" + newKm.getMaKM() + "] thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            parentPanel.loadDataToTable();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Tạo Khuyến Mãi thất bại. Vui lòng kiểm tra lại Mã KM hoặc kết nối CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleCapNhatKhuyenMai() {
        if (!validateAndGetFormData()) return;

        KhuyenMai kmToUpdate = createKhuyenMaiFromForm();

        // Luôn giữ MaKM cũ khi cập nhật
        kmToUpdate.setMaKM(currentMaKM);

        if (khuyenMaiDAO.suaKhuyenMai(kmToUpdate)) {
            JOptionPane.showMessageDialog(this, "Cập Nhật Khuyến Mãi [" + currentMaKM + "] thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            parentPanel.loadDataToTable();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Cập Nhật Khuyến Mãi [" + currentMaKM + "] thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

}