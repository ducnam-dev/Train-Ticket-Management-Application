package gui.Panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class ManHinhKetCa extends JPanel {

    // --- Components ---
    // Cột 1: Hệ thống tính (Read-only)
    private JTextField txtSysTienMat, txtSysChuyenKhoan, txtSysTongThu, txtSysTongChi, txtSysThucThu;

    // Cột 2 & 3: Thực tế (Input)
    private JTextField txtActTienMat, txtActChuyenKhoan;
    private JTextField txtChenhLech;
    private JTextArea txtGhiChu;

    private Map<Integer, JTextField> mapMenhGia = new LinkedHashMap<>();
    private static final int[] MENH_GIA = {500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000, 1000, 500};
    private static final DecimalFormat CURRENCY = new DecimalFormat("#,##0");

    public ManHinhKetCa() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Content: Chia 3 cột (Hệ thống | Đếm tiền | Đối soát)
        JPanel content = new JPanel(new GridLayout(1, 3, 10, 0));
        content.setOpaque(false);

        content.add(createPanelHeThong()); // Cột 1
        content.add(createPanelDemTien()); // Cột 2
        content.add(createPanelDoiSoat()); // Cột 3

        add(content, BorderLayout.CENTER);

        // Footer Action
        add(createFooter(), BorderLayout.SOUTH);

        // Load dữ liệu mẫu
        loadDuLieuMau();
    }

    // --- UI Sections ---

    private JPanel createHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel("TỔNG KẾT & BÀN GIAO CA");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(new Color(0, 102, 204));
        p.add(lbl, BorderLayout.WEST);
        p.add(new JSeparator(), BorderLayout.SOUTH);
        return p;
    }

    // Cột 1: Thông tin từ hệ thống (Lý thuyết)
    private JPanel createPanelHeThong() {
        JPanel p = createGroupPanel("Số liệu Hệ thống (Lý thuyết)");
        p.setLayout(new GridLayout(0, 1, 5, 5));

        p.add(new JLabel("Tổng thu tiền mặt:"));
        txtSysTienMat = createInfoField();
        p.add(txtSysTienMat);

        p.add(new JLabel("Tổng thu chuyển khoản/Thẻ:"));
        txtSysChuyenKhoan = createInfoField();
        p.add(txtSysChuyenKhoan);

        p.add(new JLabel("(-) Tổng chi tiền mặt:"));
        txtSysTongChi = createInfoField();
        p.add(txtSysTongChi);

        p.add(new JSeparator());

        p.add(new JLabel("TỔNG QUỸ CẦN CÓ (Kết quả):"));
        txtSysThucThu = createInfoField();
        txtSysThucThu.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtSysThucThu.setForeground(Color.BLUE);
        p.add(txtSysThucThu);

        // Spacer
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(p, BorderLayout.NORTH);
        return wrapper;
    }

    // Cột 2: Đếm tiền chi tiết
    private JScrollPane createPanelDemTien() {
        JPanel p = createGroupPanel("Kiểm kê tiền mặt (Thực tế)");
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        for (int mg : MENH_GIA) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
            p.add(new JLabel(CURRENCY.format(mg)), gbc);

            gbc.gridx = 1; gbc.weightx = 0.6;
            JTextField txt = new JTextField("0");
            txt.setHorizontalAlignment(JTextField.RIGHT);
            txt.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) { tinhChenhLech(); }
            });
            mapMenhGia.put(mg, txt);
            p.add(txt, gbc);
            row++;
        }

        // Tổng tiền mặt thực tế
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 2, 5);
        p.add(new JSeparator(), gbc);

        row++;
        gbc.gridy = row;
        p.add(new JLabel("Tổng tiền mặt thực đếm:"), gbc);

        row++;
        gbc.gridy = row;
        txtActTienMat = createInfoField(); // Read-only, tính từ các ô trên
        txtActTienMat.setFont(new Font("Segoe UI", Font.BOLD, 16));
        p.add(txtActTienMat, gbc);

        return new JScrollPane(p);
    }

    // Cột 3: Đối soát & Ghi chú
    private JPanel createPanelDoiSoat() {
        JPanel p = createGroupPanel("Đối soát & Giải trình");
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Nhập tiền CK thực tế
        gbc.gridy = 0;
        JLabel lblCK = new JLabel("Tiền trong TK Ngân hàng (Thực tế):");
        lblCK.setForeground(new Color(0, 100, 0)); // Dark Green
        p.add(lblCK, gbc);

        gbc.gridy = 1;
        txtActChuyenKhoan = new JTextField("0");
        txtActChuyenKhoan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtActChuyenKhoan.setHorizontalAlignment(JTextField.RIGHT);
        txtActChuyenKhoan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { tinhChenhLech(); }
        });
        p.add(txtActChuyenKhoan, gbc);

        // Hiển thị chênh lệch
        gbc.gridy = 2;
        p.add(new JLabel("CHÊNH LỆCH (Thực tế - Lý thuyết):"), gbc);

        gbc.gridy = 3;
        txtChenhLech = createInfoField();
        txtChenhLech.setFont(new Font("Segoe UI", Font.BOLD, 20));
        p.add(txtChenhLech, gbc);

        // Ghi chú
        gbc.gridy = 4;
        p.add(new JLabel("Ghi chú/Giải trình (Bắt buộc nếu lệch):"), gbc);

        gbc.gridy = 5; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        txtGhiChu = new JTextArea();
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.add(new JScrollPane(txtGhiChu), gbc);

        return p;
    }

    private JPanel createFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);
        JButton btn = new JButton("Kết thúc Ca & In báo cáo");
        btn.setPreferredSize(new Dimension(220, 50));
        btn.setBackground(new Color(220, 53, 69)); // Red color
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.addActionListener(e -> xuLyKetCa());
        p.add(btn);
        return p;
    }

    // --- Helpers ---
    private JPanel createGroupPanel(String title) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title));
        return p;
    }

    private JTextField createInfoField() {
        JTextField t = new JTextField("0");
        t.setEditable(false);
        t.setHorizontalAlignment(JTextField.RIGHT);
        t.setBackground(new Color(245, 245, 245));
        return t;
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s.replaceAll("[^0-9-]", "")); } // Chấp nhận số âm
        catch (Exception e) { return 0; }
    }

    // --- Logic Quan trọng ---

    private void tinhChenhLech() {
        // 1. Tính tổng tiền mặt thực tế từ các mệnh giá
        long tongMatAct = 0;
        for (Map.Entry<Integer, JTextField> entry : mapMenhGia.entrySet()) {
            tongMatAct += entry.getKey() * parseLong(entry.getValue().getText());
        }
        txtActTienMat.setText(CURRENCY.format(tongMatAct));

        // 2. Lấy tiền CK thực tế
        long tongCKAct = parseLong(txtActChuyenKhoan.getText());

        // 3. Lấy tổng lý thuyết (Hệ thống)
        long tongLyThuyet = parseLong(txtSysThucThu.getText());

        // 4. Tính chênh lệch = (Mặt Act + CK Act) - Lý thuyết
        long tongThucTe = tongMatAct + tongCKAct;
        long chenhLech = tongThucTe - tongLyThuyet;

        txtChenhLech.setText(CURRENCY.format(chenhLech));

        // Đổi màu để cảnh báo
        if (chenhLech == 0) {
            txtChenhLech.setForeground(new Color(40, 167, 69)); // Xanh
            txtGhiChu.setBackground(Color.WHITE);
        } else {
            txtChenhLech.setForeground(Color.RED); // Đỏ
            txtGhiChu.setBackground(new Color(255, 240, 240)); // Nền hồng nhạt nhắc nhở
        }
    }

    private void xuLyKetCa() {
        long chenhLech = parseLong(txtChenhLech.getText());
        String ghichu = txtGhiChu.getText().trim();

        if (chenhLech != 0 && ghichu.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Đang có chênh lệch tiền (" + txtChenhLech.getText() + ").\nVui lòng nhập lý do vào ô Ghi chú!",
                    "Yêu cầu bắt buộc", JOptionPane.ERROR_MESSAGE);
            txtGhiChu.requestFocus();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn kết thúc ca làm việc?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Kết ca thành công! Dữ liệu đã được lưu.");
            // Code lưu xuống DB ở đây
        }
    }

    private void loadDuLieuMau() {
        // Giả lập dữ liệu hệ thống tính được từ DB
        long sysMat = 5000000;
        long sysCK = 2000000;
        long sysChi = 500000;
        long sysTotal = sysMat + sysCK - sysChi;

        txtSysTienMat.setText(CURRENCY.format(sysMat));
        txtSysChuyenKhoan.setText(CURRENCY.format(sysCK));
        txtSysTongChi.setText(CURRENCY.format(sysChi));
        txtSysThucThu.setText(CURRENCY.format(sysTotal));

        // Giả lập người dùng nhập (để test tính năng chênh lệch)
        // Ví dụ: Người dùng nhập thiếu -> Lệch âm
        mapMenhGia.get(500000).setText("8"); // 4tr
        txtActChuyenKhoan.setText("2,000,000");

        tinhChenhLech(); // Gọi tính toán lần đầu
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Kết Ca");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new ManHinhKetCa());
        f.setSize(1200, 750);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}