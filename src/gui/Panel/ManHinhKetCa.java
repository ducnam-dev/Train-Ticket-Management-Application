// java
package gui.Panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ManHinhKetCa: Panel kết thúc ca làm việc, dựa trên cấu trúc của ManHinhBanVe.
 */
public class ManHinhKetCa extends JPanel {

    // UI components (fields)
    private JTextField txtNhanVien;
    private JTextField txtThoiGian;
    private JTextField txtTongTienMatDauCa;
    private JTextField txtTongTienCKDauCa;
    private JTextField txtTongTienQuyDauCa;
    private JTextField txtTongThuTienMat;
    private JTextField txtTongThuChuyenKhoan;
    private JTextField txtTongChi;
    private JTextField txtTongTienQuyCuoiCa;

    // NEW FIELDS for direct reference to avoid NullPointerException
    private JTextField txtTienMatThucTe;
    private JTextField txtTienLech;

    // Fields cho khu vực chi tiết tiền mặt (ghi đè số lượng)
    private Map<Integer, JTextField> denominationFields = new LinkedHashMap<>();

    // Constants
    private static final NumberFormat CURRENCY_FORMAT = new DecimalFormat("#,##0 VNĐ");
    private static final NumberFormat SIMPLE_NUMBER_FORMAT = new DecimalFormat("#,##0");

    public ManHinhKetCa() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245));

        // Bước 1: Khởi tạo denominationFields đầu tiên!
        khoiTaoDenominationFields();

        // Bước 2: Sau đó mới thêm các Panel truy cập đến fields này
        add(taoPanelTieuDe(), BorderLayout.NORTH);
        add(taoNoiDungChinh(), BorderLayout.CENTER);

        // Bước 3: Nạp dữ liệu giả lập cuối cùng
        napDuLieuGiaLap();
    }

    // ======= UI Builders =======

    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 50));
        panel.setBorder(new EmptyBorder(0, 10, 0, 10));

        JLabel titleLabel = new JLabel("Kết Ca");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        panel.add(titleLabel, BorderLayout.WEST);

        JLabel idLabel = new JLabel("ID: NV100001");
        panel.add(idLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel taoNoiDungChinh() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 0));
        mainPanel.setBackground(new Color(240, 242, 245));

        JPanel leftPanel = createKhuVucTongKetCa(); // Khu vực chính (Tổng kết)

        JPanel rightPanel = createKhuVucChiTietTienMat(); // Khu vực chi tiết (Đếm tiền)
        rightPanel.setPreferredSize(new Dimension(350, 0)); // Cố định chiều rộng cho khu vực đếm tiền

        // DÙNG JSPLITPANE để tự động co giãn như ManHinhBanVe
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setResizeWeight(0.65); // 65% trái, 35% phải
        split.setOneTouchExpandable(true);
        split.setDividerSize(6);

        mainPanel.add(split, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createKhuVucTongKetCa() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 0, 5));

        // 1. Thông tin ca làm việc
        panel.add(createThongTinCaPanel());
        panel.add(Box.createVerticalStrut(10));

        // 2. Tóm tắt Tiền Đầu Ca
        panel.add(createTongKetTienDauCaPanel());
        panel.add(Box.createVerticalStrut(10));

        // 3. Tóm tắt Thu/Chi trong Ca
        panel.add(createTongKetThuChiCaPanel());
        panel.add(Box.createVerticalStrut(10));

        // 4. Tổng Tiền Quỹ Cuối Ca (Kết quả)
        panel.add(createTongTienQuyCuoiCaPanel());
        panel.add(Box.createVerticalStrut(10));

        // Nút hành động
        panel.add(createNutKetCa());
        panel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Đóng gói vào một container để dùng trong JSplitPane
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private JPanel createThongTinCaPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Thông tin ca làm việc");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        panel.add(new JLabel("Nhân viên:"));
        txtNhanVien = new JTextField(15);
        txtNhanVien.setEditable(false);
        panel.add(txtNhanVien);

        panel.add(new JLabel("Thời gian:"));
        txtThoiGian = new JTextField(12);
        txtThoiGian.setEditable(false);
        panel.add(txtThoiGian);

        datCanhKhuVuc(panel);
        return panel;
    }

    private JPanel createTongKetTienDauCaPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Tiền đầu ca");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        panel.add(new JLabel("Tổng tiền mặt đầu ca:"));
        txtTongTienMatDauCa = new JTextField(10);
        txtTongTienMatDauCa.setEditable(false);
        txtTongTienMatDauCa.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(txtTongTienMatDauCa);

        panel.add(new JLabel("Tổng tiền chuyển khoản đầu ca:"));
        txtTongTienCKDauCa = new JTextField(10);
        txtTongTienCKDauCa.setEditable(false);
        txtTongTienCKDauCa.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(txtTongTienCKDauCa);

        panel.add(new JLabel("TỔNG QUỸ ĐẦU CA:"));
        txtTongTienQuyDauCa = new JTextField(10);
        txtTongTienQuyDauCa.setEditable(false);
        txtTongTienQuyDauCa.setHorizontalAlignment(JTextField.RIGHT);
        txtTongTienQuyDauCa.setBackground(new Color(220, 220, 220));
        txtTongTienQuyDauCa.setFont(txtTongTienQuyDauCa.getFont().deriveFont(Font.BOLD));
        panel.add(txtTongTienQuyDauCa);

        datCanhKhuVuc(panel);
        return panel;
    }

    private JPanel createTongKetThuChiCaPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Giao dịch trong ca");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        panel.add(new JLabel("Tổng thu tiền mặt:"));
        txtTongThuTienMat = new JTextField(10);
        txtTongThuTienMat.setEditable(false);
        txtTongThuTienMat.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(txtTongThuTienMat);

        panel.add(new JLabel("Tổng thu chuyển khoản:"));
        txtTongThuChuyenKhoan = new JTextField(10);
        txtTongThuChuyenKhoan.setEditable(false);
        txtTongThuChuyenKhoan.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(txtTongThuChuyenKhoan);

        panel.add(new JLabel("Tổng chi trong ca:"));
        txtTongChi = new JTextField(10);
        txtTongChi.setEditable(false);
        txtTongChi.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(txtTongChi);

        datCanhKhuVuc(panel);
        return panel;
    }

    private JPanel createTongTienQuyCuoiCaPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel totalLabel = new JLabel("TỔNG TIỀN QUỸ CUỐI CA (Lý thuyết):");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(totalLabel);

        txtTongTienQuyCuoiCa = new JTextField(12);
        txtTongTienQuyCuoiCa.setEditable(false);
        txtTongTienQuyCuoiCa.setHorizontalAlignment(JTextField.RIGHT);
        txtTongTienQuyCuoiCa.setFont(txtTongTienQuyCuoiCa.getFont().deriveFont(Font.BOLD, 16f));
        txtTongTienQuyCuoiCa.setForeground(new Color(0, 123, 255));
        txtTongTienQuyCuoiCa.setBackground(new Color(245, 245, 245));
        panel.add(txtTongTienQuyCuoiCa);

        datCanhKhuVuc(panel);
        return panel;
    }

    private JPanel createNutKetCa() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnKetCa = new JButton("Xác nhận Kết ca");
        btnKetCa.setPreferredSize(new Dimension(180, 50));
        btnKetCa.setFont(btnKetCa.getFont().deriveFont(Font.BOLD, 16f));
        btnKetCa.setBackground(new Color(40, 167, 69)); // Màu xanh lá
        btnKetCa.setForeground(Color.WHITE);
        btnKetCa.addActionListener(e -> xuLyKetCa());

        panel.add(btnKetCa);
        datCanhKhuVuc(panel);
        return panel;
    }

    // --- Khu vực đếm tiền mặt chi tiết ---

    private JPanel createKhuVucChiTietTienMat() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Tiền mặt cuối ca (Thực tế đếm)"));

        JPanel detailScrollPanel = new JPanel();
        detailScrollPanel.setLayout(new BoxLayout(detailScrollPanel, BoxLayout.Y_AXIS));
        detailScrollPanel.setOpaque(false);
        detailScrollPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Khởi tạo các trường cho các mệnh giá tiền
        int[] menhGia = {500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000, 1000, 500};
        for (int mg : menhGia) {
            detailScrollPanel.add(createMenhGiaPanel(mg));
        }

        detailScrollPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(detailScrollPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Footer: Tổng tiền mặt thực tế và Lệch
        JPanel footerPanel = createFooterChiTietTienMat();
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void khoiTaoDenominationFields() {
        // Thứ tự quan trọng để tạo giao diện đúng
        int[] menhGia = {500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000, 1000, 500};
        for (int mg : menhGia) {
            JTextField field = new JTextField(5);
            field.setHorizontalAlignment(JTextField.RIGHT);
            field.setText("0"); // Mặc định là 0 tờ
            // Thêm listener để tính toán lại tổng khi số lượng thay đổi
            ActionListener listener = e -> tinhTongTienMatThucTe();
            field.addActionListener(listener);
            field.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    tinhTongTienMatThucTe();
                }
            });
            denominationFields.put(mg, field);
        }
    }

    private JPanel createMenhGiaPanel(int menhGia) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel label = new JLabel(CURRENCY_FORMAT.format(menhGia) + " x");
        label.setPreferredSize(new Dimension(120, 25));
        panel.add(label);

        JTextField soToField = denominationFields.get(menhGia);
        panel.add(soToField);

        JLabel totalLabel = new JLabel("= 0 VNĐ");
        totalLabel.setPreferredSize(new Dimension(100, 25));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLabel.setName("Total_" + menhGia); // Dùng để update
        panel.add(totalLabel);

        // Thiết lập kích thước tối đa cho panel con
        datCanhKhuVuc(panel);
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, 35));

        return panel;
    }

    private JPanel createFooterChiTietTienMat() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Tổng tiền mặt thực tế
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        row1.setOpaque(false);
        row1.setAlignmentX(Component.RIGHT_ALIGNMENT);
        row1.add(new JLabel("Tổng tiền mặt thực tế:"));

        // Khởi tạo và gán cho field txtTienMatThucTe
        txtTienMatThucTe = new JTextField("0 VNĐ", 12);
        txtTienMatThucTe.setHorizontalAlignment(JTextField.RIGHT);
        txtTienMatThucTe.setEditable(false);
        txtTienMatThucTe.setFont(txtTienMatThucTe.getFont().deriveFont(Font.BOLD));
        row1.add(txtTienMatThucTe);
        footer.add(row1);

        // 2. Lệch
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        row2.setOpaque(false);
        row2.setAlignmentX(Component.RIGHT_ALIGNMENT);
        row2.add(new JLabel("Lệch (Thực tế - Lý thuyết):"));

        // Khởi tạo và gán cho field txtTienLech
        txtTienLech = new JTextField("0 VNĐ", 12);
        txtTienLech.setHorizontalAlignment(JTextField.RIGHT);
        txtTienLech.setEditable(false);
        txtTienLech.setFont(txtTienLech.getFont().deriveFont(Font.BOLD));
        txtTienLech.setForeground(Color.RED);
        row2.add(txtTienLech);
        footer.add(row2);

        return footer;
    }

    // ======= Helpers / Styles =======

    private void datCanhKhuVuc(JPanel panel) {
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
    }

    private long parseCurrency(JTextField field) {
        if (field == null || field.getText().isEmpty()) return 0;
        try {
            // Xóa tất cả ký tự không phải số
            String text = field.getText().replaceAll("[^\\d]", "");
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void tinhTongTienMatThucTe() {
        // Tránh NPE nếu gọi trước khi components được khởi tạo
        if (txtTongTienMatDauCa == null || txtTienMatThucTe == null) return;

        long tongTienMatDauCa = parseCurrency(txtTongTienMatDauCa);
        long tongThuTienMat = parseCurrency(txtTongThuTienMat);
        long tongChi = parseCurrency(txtTongChi);

        long tongTienMatLyThuyet = tongTienMatDauCa + tongThuTienMat - tongChi;

        long tongTienMatThucTeValue = 0;

        // Lặp qua các trường mệnh giá
        for (Map.Entry<Integer, JTextField> entry : denominationFields.entrySet()) {
            int menhGia = entry.getKey();
            JTextField soToField = entry.getValue();
            long soTo;
            try {
                soTo = Long.parseLong(soToField.getText().replaceAll("[^\\d]", ""));
            } catch (NumberFormatException e) {
                soTo = 0;
            }

            long tongMenhGia = menhGia * soTo;
            tongTienMatThucTeValue += tongMenhGia;

            // Cập nhật nhãn tổng tiền mệnh giá
            JPanel parent = (JPanel) soToField.getParent();
            if (parent != null) {
                for (Component comp : parent.getComponents()) {
                    if (comp instanceof JLabel && ("Total_" + menhGia).equals(comp.getName())) {
                        ((JLabel) comp).setText("= " + SIMPLE_NUMBER_FORMAT.format(tongMenhGia) + " VNĐ");
                        break;
                    }
                }
            }
        }

        // Cập nhật tổng tiền mặt thực tế (SỬ DỤNG FIELD ĐÃ KHAI BÁO)
        txtTienMatThucTe.setText(CURRENCY_FORMAT.format(tongTienMatThucTeValue));

        // Cập nhật tiền lệch (SỬ DỤNG FIELD ĐÃ KHAI BÁO)
        long tienLech = tongTienMatThucTeValue - tongTienMatLyThuyet;
        txtTienLech.setText(CURRENCY_FORMAT.format(tienLech));
        txtTienLech.setForeground(tienLech == 0 ? Color.BLACK : (tienLech > 0 ? new Color(40, 167, 69) : new Color(220, 53, 69)));
    }

    // ======= Data / Actions =======

    private void napDuLieuGiaLap() {
        // Thông tin ca
        txtNhanVien.setText("Trần Đức Nam");
        txtThoiGian.setText("31/09/2025 09:50 - 18:00");

        // Tiền đầu ca (Lấy từ màn hình Mở ca)
        long tienMatDauCa = 2495000L;
        long tienCKDauCa = 1000000L;
        txtTongTienMatDauCa.setText(CURRENCY_FORMAT.format(tienMatDauCa));
        txtTongTienCKDauCa.setText(CURRENCY_FORMAT.format(tienCKDauCa));
        txtTongTienQuyDauCa.setText(CURRENCY_FORMAT.format(tienMatDauCa + tienCKDauCa));

        // Thu/Chi trong ca (Giả lập)
        long tongThuMat = 12500000L;
        long tongThuCK = 8500000L;
        long tongChi = 500000L;
        txtTongThuTienMat.setText(CURRENCY_FORMAT.format(tongThuMat));
        txtTongThuChuyenKhoan.setText(CURRENCY_FORMAT.format(tongThuCK));
        txtTongChi.setText(CURRENCY_FORMAT.format(tongChi));

        // Tổng quỹ lý thuyết cuối ca
        long tongQuyCuoiCaLyThuyet = tienMatDauCa + tongThuMat + tienCKDauCa + tongThuCK - tongChi;
        txtTongTienQuyCuoiCa.setText(CURRENCY_FORMAT.format(tongQuyCuoiCaLyThuyet));

        // Thiết lập số lượng tờ tiền mặt giả lập cuối ca (Thực tế đếm)
        // Giả sử đếm thiếu 5.000 (1 tờ 5k)
        Map<Integer, Long> soToThucTe = new LinkedHashMap<>();
        soToThucTe.put(500000, 26L); // 13.000.000
        soToThucTe.put(200000, 10L); // 2.000.000
        soToThucTe.put(100000, 15L); // 1.500.000
        soToThucTe.put(50000, 10L); // 500.000
        soToThucTe.put(20000, 10L); // 200.000
        soToThucTe.put(10000, 15L); // 150.000
        soToThucTe.put(5000, 19L); // Thiếu 1 tờ so với đầu ca (Giả sử đầu ca 20 tờ)
        soToThucTe.put(2000, 20L); // 40.000
        soToThucTe.put(1000, 10L); // 10.000
        soToThucTe.put(500, 10L); // 5.000

        for (Map.Entry<Integer, Long> entry : soToThucTe.entrySet()) {
            if (denominationFields.containsKey(entry.getKey())) {
                denominationFields.get(entry.getKey()).setText(SIMPLE_NUMBER_FORMAT.format(entry.getValue()));
            }
        }

        // Tính toán lại tổng tiền mặt thực tế sau khi nap dữ liệu
        tinhTongTienMatThucTe();
    }

    private void xuLyKetCa() {
        // Tránh NPE nếu các field chưa được tạo
        if (txtTienMatThucTe == null || txtTongTienQuyCuoiCa == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Dữ liệu chưa được khởi tạo hoàn chỉnh.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long tongTienMatThucTeValue = parseCurrency(txtTienMatThucTe);
        long tongTienMatLyThuyet = parseCurrency(txtTongTienMatDauCa) + parseCurrency(txtTongThuTienMat) - parseCurrency(txtTongChi);
        long tienLech = tongTienMatThucTeValue - tongTienMatLyThuyet;

        String thongBao = "Xác nhận kết ca với các thông tin:\n"
                + "- Tổng tiền quỹ lý thuyết: " + txtTongTienQuyCuoiCa.getText() + "\n"
                + "- Tiền mặt thực tế: " + CURRENCY_FORMAT.format(tongTienMatThucTeValue) + "\n"
                + "- Tiền lệch: " + CURRENCY_FORMAT.format(tienLech);

        int confirm = JOptionPane.showConfirmDialog(this, thongBao, "Xác nhận Kết ca", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Logic lưu trữ dữ liệu kết ca (thông tin ca, số tờ, tổng tiền lệch, ...)
            JOptionPane.showMessageDialog(this, "Kết ca thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            // Sau khi kết ca thành công, chuyển đến màn hình đăng nhập hoặc tương tự
        }
    }

    private void styleNutChinh(JButton btn) {
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(100, 25));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Kết ca (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ManHinhKetCa(), BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}