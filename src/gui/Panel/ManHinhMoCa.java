package gui.Panel;
// java

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ManHinhMoCa: Panel khai báo tiền đầu ca làm việc.
 * Tương tự cấu trúc cơ bản của ManHinhKetCa, chỉ tập trung vào nhập liệu tiền mặt và tổng kết.
 */
public class ManHinhMoCa extends JPanel {

    // UI components (fields)
    private JTextField txtNhanVien;
    private JTextField txtThoiGian;
    private JTextField txtTongTienMatDauCa;
    private JTextField txtTongTienCKDauCa;
    private JTextField txtTongTienQuyDauCa;

    // Fields cho khu vực nhập chi tiết tiền mặt
    private Map<Integer, JTextField> denominationFields = new LinkedHashMap<>();

    // Constants
    private static final NumberFormat CURRENCY_FORMAT = new DecimalFormat("#,##0 VNĐ");
    private static final int[] MENH_GIA_LON_DEN_NHO = {500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000, 1000, 500};

    public ManHinhMoCa() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245));

        // Khởi tạo denominationFields trước khi tạo UI
        khoiTaoDenominationFields();

        add(taoNoiDungChinh(), BorderLayout.CENTER);

        // Nạp dữ liệu giả lập (nhân viên, thời gian, số tờ ban đầu)
        napDuLieuGiaLap();
    }

    // ======= UI Builders =======

    private JPanel taoNoiDungChinh() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 242, 245));

        // Tạo panel chứa nội dung chính để center
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.setPreferredSize(new Dimension(450, 600)); // Kích thước cố định tương đối

        // Thêm tiêu đề và thông tin nhân viên
        contentPanel.add(createPanelTieuDeThongTin(), BorderLayout.NORTH);

        // Thêm khu vực nhập liệu chi tiết tiền mặt và tổng kết
        contentPanel.add(createKhuVucNhapLieu(), BorderLayout.CENTER);

        // Căn giữa contentPanel trong mainPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(contentPanel, gbc);

        return mainPanel;
    }

    private JPanel createPanelTieuDeThongTin() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel titleLabel = new JLabel("Khai báo tiền đầu ca");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        infoPanel.setOpaque(false);

        JLabel nvLabel = new JLabel("Nhân viên:");
        nvLabel.setFont(nvLabel.getFont().deriveFont(Font.BOLD, 14f));
        infoPanel.add(nvLabel);

        txtNhanVien = new JTextField(15);
        txtNhanVien.setEditable(false);
        txtNhanVien.setBorder(null);
        txtNhanVien.setFont(txtNhanVien.getFont().deriveFont(Font.BOLD, 14f));
        txtNhanVien.setForeground(new Color(0, 123, 255));
        infoPanel.add(txtNhanVien);

        txtThoiGian = new JTextField(10);
        txtThoiGian.setEditable(false);
        txtThoiGian.setBorder(null);
        txtThoiGian.setFont(txtThoiGian.getFont().deriveFont(Font.PLAIN, 14f));
        infoPanel.add(txtThoiGian);

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(new JSeparator(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createKhuVucNhapLieu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // 1. Khu vực nhập chi tiết tiền mặt
        JPanel menhGiaPanel = createChiTietMenhGiaPanel();
        panel.add(menhGiaPanel);

        panel.add(Box.createVerticalStrut(15));

        // 2. Khu vực Tổng kết
        JPanel summaryPanel = createKhuVucTongKet();
        panel.add(summaryPanel);

        panel.add(Box.createVerticalStrut(10));

        // 3. Nút hành động
        panel.add(createNutMoCaPanel());

        return panel;
    }

    private JPanel createChiTietMenhGiaPanel() {
        JPanel panel = new JPanel(new GridLayout(MENH_GIA_LON_DEN_NHO.length, 2, 5, 10));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setOpaque(false);

        for (int menhGia : MENH_GIA_LON_DEN_NHO) {
            JLabel label = new JLabel(String.format("Số tờ mệnh giá %s đ", CURRENCY_FORMAT.format(menhGia).replace(" VNĐ", "")));
            panel.add(label);

            JTextField soToField = denominationFields.get(menhGia);
            panel.add(soToField);
        }

        return panel;
    }

    private JPanel createKhuVucTongKet() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 15));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setOpaque(false);

        // 1. Tổng mặt tiền đầu ca
        JLabel matLabel = new JLabel("Tổng mặt tiền đầu ca");
        matLabel.setFont(matLabel.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(matLabel);
        txtTongTienMatDauCa = createSummaryField(false, null);
        panel.add(txtTongTienMatDauCa);

        // 2. Tiền trong TK đầu ca
        JLabel tkLabel = new JLabel("Tiền trong TK đầu ca");
        tkLabel.setFont(tkLabel.getFont().deriveFont(Font.BOLD, 14f));
        tkLabel.setForeground(Color.BLUE);
        panel.add(tkLabel);
        txtTongTienCKDauCa = createSummaryField(true, null); // Có thể chỉnh sửa tiền chuyển khoản
        panel.add(txtTongTienCKDauCa);

        // 3. Tiền đầu ca (Tổng)
        JLabel tongLabel = new JLabel("Tiền đầu ca");
        tongLabel.setFont(tongLabel.getFont().deriveFont(Font.BOLD, 16f));
        tongLabel.setForeground(Color.RED);
        panel.add(tongLabel);
        txtTongTienQuyDauCa = createSummaryField(false, Color.RED);
        txtTongTienQuyDauCa.setBackground(new Color(220, 220, 220));
        panel.add(txtTongTienQuyDauCa);

        return panel;
    }

    private JTextField createSummaryField(boolean editable, Color fgColor) {
        JTextField field = new JTextField("0 VNĐ", 10);
        field.setEditable(editable);
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setFont(field.getFont().deriveFont(Font.BOLD, 14f));
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        if (fgColor != null) {
            field.setForeground(fgColor);
        }
        return field;
    }

    private JPanel createNutMoCaPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        JButton btnMoCa = new JButton(" Mở ca");
        btnMoCa.setFont(btnMoCa.getFont().deriveFont(Font.BOLD, 16f));
        btnMoCa.setPreferredSize(new Dimension(150, 40));
        btnMoCa.setBackground(new Color(40, 167, 69)); // Màu xanh lá
        btnMoCa.setForeground(Color.WHITE);


        btnMoCa = new JButton("Mở ca");
        styleNutChinh(btnMoCa);
//        btnMoCa.addActionListener(e -> timKiemChuyenTau());

        btnMoCa.setHorizontalTextPosition(SwingConstants.LEFT);
        btnMoCa.addActionListener(e -> xuLyMoCa());

        panel.add(btnMoCa);
        return panel;
    }
    private void styleNutChinh(JButton btn) {
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(100, 25));
    }

    // ======= Helpers / Logic =======

    private void khoiTaoDenominationFields() {
        // Thứ tự quan trọng theo MENH_GIA_LON_DEN_NHO
        for (int mg : MENH_GIA_LON_DEN_NHO) {
            JTextField field = new JTextField(5);
            field.setHorizontalAlignment(JTextField.RIGHT);
            field.setText("0");
            field.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // Listener để tính toán lại tổng khi số lượng tờ thay đổi
            ActionListener listener = e -> tinhTongTien();
            field.addActionListener(listener);
            field.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    tinhTongTien();
                }
            });

            denominationFields.put(mg, field);
        }
    }

    private long parseTextField(JTextField field) {
        if (field == null || field.getText().isEmpty()) return 0;
        try {
            // Xóa tất cả ký tự không phải số (bao gồm dấu phẩy, VNĐ)
            String text = field.getText().replaceAll("[^\\d]", "");
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void tinhTongTien() {
        long tongTienMat = 0;

        for (Map.Entry<Integer, JTextField> entry : denominationFields.entrySet()) {
            int menhGia = entry.getKey();
            JTextField soToField = entry.getValue();
            long soTo;
            try {
                soTo = Long.parseLong(soToField.getText().replaceAll("[^\\d]", ""));
            } catch (NumberFormatException e) {
                soTo = 0;
                soToField.setText("0"); // Đặt lại 0 nếu nhập không hợp lệ
            }
            tongTienMat += menhGia * soTo;
        }

        long tongTienChuyenKhoan = parseTextField(txtTongTienCKDauCa);
        long tongQuyDauCa = tongTienMat + tongTienChuyenKhoan;

        // Cập nhật các trường tổng kết
        txtTongTienMatDauCa.setText(CURRENCY_FORMAT.format(tongTienMat));
        txtTongTienQuyDauCa.setText(CURRENCY_FORMAT.format(tongQuyDauCa));
    }

    // ======= Data / Actions =======

    private void napDuLieuGiaLap() {
        // Thông tin nhân viên và thời gian
        txtNhanVien.setText("Trần Đức Nam");
        txtThoiGian.setText("31/09/2025 09:50");

        // Dữ liệu tiền mặt (dựa trên hình ảnh)
        denominationFields.get(500000).setText("0");
        denominationFields.get(200000).setText("0");
        denominationFields.get(100000).setText("2");
        denominationFields.get(50000).setText("20");
        denominationFields.get(20000).setText("50");
        denominationFields.get(10000).setText("20");
        denominationFields.get(5000).setText("10");
        denominationFields.get(2000).setText("10");
        denominationFields.get(1000).setText("20");
        denominationFields.get(500).setText("10");

        // Tiền chuyển khoản (giả lập)
        long tienCK = 1000000L;
        txtTongTienCKDauCa.setText(CURRENCY_FORMAT.format(tienCK));

        // Tính toán tổng tiền ban đầu
        tinhTongTien();
    }

    private void xuLyMoCa() {
        long tongQuyDauCa = parseTextField(txtTongTienQuyDauCa);

        if (tongQuyDauCa <= 0) {
            JOptionPane.showMessageDialog(this, "Tổng tiền đầu ca phải lớn hơn 0 để mở ca.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận mở ca với tổng quỹ đầu ca là " + txtTongTienQuyDauCa.getText() + "?",
                "Xác nhận Mở ca",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Logic lưu trữ dữ liệu mở ca vào CSDL (nhân viên, thời gian, chi tiết tiền mặt, tổng tiền)
            JOptionPane.showMessageDialog(this, "Mở ca thành công! Ca làm việc đã bắt đầu.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            // Chuyển sang màn hình Bán vé
            // Ví dụ: SwingUtilities.getWindowAncestor(this).dispose(); // Đóng frame
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Mở ca (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ManHinhMoCa(), BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}