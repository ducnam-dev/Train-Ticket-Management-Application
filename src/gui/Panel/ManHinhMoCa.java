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

public class ManHinhMoCa extends JPanel {

    // Components
    private JTextField txtNhanVien;
    private JTextField txtThoiGian;
    private JTextField txtTongTienMat; // Tổng tiền mặt từ các mệnh giá
    private JTextField txtTienNganHang; // Nhập tay số dư đầu ca
    private JTextField txtTongQuyDauCa; // Tổng cộng
    private JTextArea txtGhiChu;

    // Map lưu các ô nhập số lượng tờ tiền
    private Map<Integer, JTextField> mapMenhGia = new LinkedHashMap<>();
    private static final int[] MENH_GIA = {500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000, 1000, 500};
    private static final DecimalFormat CURRENCY = new DecimalFormat("#,##0");

    public ManHinhMoCa() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Center (Chia làm 2 bên: Đếm tiền & Tổng kết)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        centerPanel.add(createPanelDemTien());
        centerPanel.add(createPanelTongKet());

        add(centerPanel, BorderLayout.CENTER);

        // 3. Footer (Nút bấm)
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Load dữ liệu giả lập
        loadDuLieuMau();
    }

    private JPanel createHeaderPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lblTitle = new JLabel("KHAI BÁO ĐẦU CA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));

        JPanel infoP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoP.setOpaque(false);

        txtNhanVien = new JTextField("NV001 - Nguyễn Văn A");
        txtNhanVien.setEditable(false);
        txtThoiGian = new JTextField("16/12/2025 07:00");
        txtThoiGian.setEditable(false);

        infoP.add(new JLabel("Nhân viên:"));
        infoP.add(txtNhanVien);
        infoP.add(Box.createHorizontalStrut(10));
        infoP.add(new JLabel("Thời gian:"));
        infoP.add(txtThoiGian);

        p.add(lblTitle, BorderLayout.WEST);
        p.add(infoP, BorderLayout.EAST);
        p.add(new JSeparator(), BorderLayout.SOUTH);
        return p;
    }

    private JScrollPane createPanelDemTien() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Chi tiết tiền mặt trong két"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        for (int mg : MENH_GIA) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
            JLabel lbl = new JLabel(CURRENCY.format(mg) + " đ");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            p.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.6;
            JTextField txtSl = new JTextField("0", 10);
            txtSl.setHorizontalAlignment(JTextField.RIGHT);
            // Sự kiện nhập số -> tính lại tổng
            txtSl.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) { tinhToan(); }
            });
            mapMenhGia.put(mg, txtSl);
            p.add(txtSl, gbc);

            row++;
        }

        // Đẩy content lên trên
        gbc.gridy = row; gbc.weighty = 1.0;
        p.add(new JLabel(), gbc);

        return new JScrollPane(p);
    }

    private JPanel createPanelTongKet() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Tổng hợp & Ghi chú"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Tổng tiền mặt
        gbc.gridy = 0;
        p.add(new JLabel("Tổng tiền mặt (Tự tính):"), gbc);
        gbc.gridy = 1;
        txtTongTienMat = createField(false);
        p.add(txtTongTienMat, gbc);

        // Tiền ngân hàng
        gbc.gridy = 2;
        p.add(new JLabel("Số dư Tài khoản/Ngân hàng (Khai báo):"), gbc);
        gbc.gridy = 3;
        txtTienNganHang = createField(true);
        txtTienNganHang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { tinhToan(); }
        });
        p.add(txtTienNganHang, gbc);

        // Tổng quỹ
        gbc.gridy = 4;
        JLabel lblTong = new JLabel("TỔNG QUỸ ĐẦU CA:");
        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTong.setForeground(Color.RED);
        p.add(lblTong, gbc);

        gbc.gridy = 5;
        txtTongQuyDauCa = createField(false);
        txtTongQuyDauCa.setFont(new Font("Segoe UI", Font.BOLD, 20));
        txtTongQuyDauCa.setForeground(Color.RED);
        p.add(txtTongQuyDauCa, gbc);

        // Ghi chú
        gbc.gridy = 6;
        p.add(new JLabel("Ghi chú mở ca (nếu có):"), gbc);
        gbc.gridy = 7; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        txtGhiChu = new JTextArea();
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.add(new JScrollPane(txtGhiChu), gbc);

        return p;
    }

    private JPanel createFooterPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);
        JButton btnLuu = new JButton("Xác nhận Mở Ca");
        btnLuu.setBackground(new Color(40, 167, 69));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLuu.setPreferredSize(new Dimension(200, 45));

        btnLuu.addActionListener(e -> xuLyMoCa());
        p.add(btnLuu);
        return p;
    }

    private JTextField createField(boolean editable) {
        JTextField t = new JTextField("0");
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setHorizontalAlignment(JTextField.RIGHT);
        t.setEditable(editable);
        return t;
    }

    // Logic tính toán
    private void tinhToan() {
        long tongMat = 0;
        for (Map.Entry<Integer, JTextField> entry : mapMenhGia.entrySet()) {
            tongMat += entry.getKey() * parseLong(entry.getValue().getText());
        }
        long tienNH = parseLong(txtTienNganHang.getText());

        txtTongTienMat.setText(CURRENCY.format(tongMat));
        txtTongQuyDauCa.setText(CURRENCY.format(tongMat + tienNH));
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s.replaceAll("[^0-9]", "")); }
        catch (Exception e) { return 0; }
    }

    private void xuLyMoCa() {
        if (parseLong(txtTongQuyDauCa.getText()) == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền đầu ca!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Mở ca thành công!\nTổng quỹ: " + txtTongQuyDauCa.getText());
    }

    private void loadDuLieuMau() {
        mapMenhGia.get(100000).setText("5");
        mapMenhGia.get(50000).setText("10");
        txtTienNganHang.setText("1,000,000");
        tinhToan();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Mở Ca");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new ManHinhMoCa());
        f.setSize(1000, 700);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}