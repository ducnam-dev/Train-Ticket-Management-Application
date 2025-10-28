package gui.Panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Map;

/**
 * ManHinhCapNhatCongThucGia - Lớp tạo giao diện
 * Cập nhật các hệ số tính giá vé theo cấu trúc ảnh người dùng cung cấp.
 * Chức năng: Tìm, Cập nhật hệ số, và hiển thị kết quả giá sau khi cập nhật.
 */
public class ManHinhQuanLyGiaVe extends JPanel implements ActionListener {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("###,###,##0");
    private static final DecimalFormat HS_FORMAT = new DecimalFormat("0.00"); // Định dạng cho hệ số

    // =================================================================================
    // KHAI BÁO COMPONENT
    // =================================================================================

    // Khu vực 1: Tìm chuyến tàu
    private JTextField txtMaTauTim, txtTenTauTim;
    private JButton btnTimTau;

    // Khu vực Cập nhật Công thức Giá
    private JTextField txtTenTauCapNhat, txtGiaVeKM;

    // Hệ số vé
    private Map<String, JTextField> heSoFields = new Hashtable<>();
    private JButton btnXacNhan;

    // Khu vực 2: Thông tin giá vé sau cập nhật (Sử dụng 2 bảng giả định)
    private JTable tableGiaCu, tableGiaMoi;
    private DefaultTableModel modelGiaCu, modelGiaMoi;


    public ManHinhQuanLyGiaVe() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Tiêu đề ---
        JLabel title = new JLabel("Cập nhật Công thức Tính Giá Vé");
        title.setFont(FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        add(title, BorderLayout.NORTH);

        // --- Khu vực chính (Chia 2 cột chính) ---
        JPanel mainArea = new JPanel(new BorderLayout(15, 0));
        mainArea.setOpaque(false);

        // --- Cột trái: Tìm kiếm + Bảng 1 ---
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setOpaque(false);

        leftColumn.add(createKhuVucTimChuyen()); // Khu vực 1
        leftColumn.add(Box.createRigidArea(new Dimension(0, 15)));
        leftColumn.add(createKhuVucGiaSauCapNhat("Thông tin giá vé cũ", modelGiaCu, tableGiaCu)); // Bảng 2.1 (Cũ)

        // --- Cột phải: Cập nhật + Bảng 2 ---
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setOpaque(false);

        rightColumn.add(createKhuVucCapNhatCongThuc()); // Khu vực Cập nhật
        rightColumn.add(Box.createRigidArea(new Dimension(0, 15)));
        rightColumn.add(createKhuVucGiaSauCapNhat("Thông tin giá vé sau cập nhật", modelGiaMoi, tableGiaMoi)); // Bảng 2.2 (Mới)

        // --- Split Pane cho 2 cột ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftColumn, rightColumn);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(10);
        splitPane.setOpaque(false);

        mainArea.add(splitPane, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // Load dữ liệu mẫu
        loadInitialData();
    }

    // =================================================================================
    // UI BUILDERS (Tạo các khu vực giao diện)
    // =================================================================================

    /**
     * Khu vực 1: Tìm chuyến tàu
     */
    private JPanel createKhuVucTimChuyen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED),
                "Tìm chuyến tàu",
                TitledBorder.LEFT, TitledBorder.TOP,
                FONT_BOLD_14, Color.RED));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã tàu
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; panel.add(new JLabel("Mã tàu :"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; txtMaTauTim = new JTextField(15); panel.add(txtMaTauTim, gbc);

        // Tên tàu
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; panel.add(new JLabel("Tên tàu :"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; txtTenTauTim = new JTextField("SE1", 15); panel.add(txtTenTauTim, gbc);

        // Nút Tìm
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        btnTimTau = new JButton("Tìm");
        btnTimTau.setBackground(Color.RED);
        btnTimTau.setForeground(Color.WHITE);
        btnTimTau.addActionListener(this);
        panel.add(btnTimTau, gbc);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    /**
     * Khu vực Cập nhật Công thức Giá
     */
    private JPanel createKhuVucCapNhatCongThuc() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Cập nhật công thức tính giá",
                TitledBorder.LEFT, TitledBorder.TOP,
                FONT_BOLD_14, PRIMARY_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Cột 1 & 2: Giá KM và Hệ số toa ---

        // Tên tàu
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; panel.add(new JLabel("Tên tàu :"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; txtTenTauCapNhat = new JTextField("SE1", 8); txtTenTauCapNhat.setEditable(false); panel.add(txtTenTauCapNhat, gbc);

        // Giá vé theo km
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; panel.add(new JLabel("Giá vé theo km :"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtGiaVeKM = new JTextField("1.500 VND", 8);
        heSoFields.put("GiaVeKM", txtGiaVeKM);
        panel.add(txtGiaVeKM, gbc);

        // Hệ số ghế ngồi
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; panel.add(new JLabel("Hệ số ghế ngồi :"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        JTextField txtHSGheNgoi = new JTextField("1", 8);
        heSoFields.put("HSGheNgoi", txtHSGheNgoi);
        panel.add(txtHSGheNgoi, gbc);

        // Hệ số giường nằm
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; panel.add(new JLabel("Hệ số giường nằm :"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        JTextField txtHSGiuongNam = new JTextField("1.5", 8);
        heSoFields.put("HSGiuongNam", txtHSGiuongNam);
        panel.add(txtHSGiuongNam, gbc);

        // --- Cột 3 & 4: Hệ số loại khách ---
        gbc.weightx = 0; gbc.gridwidth = 1; gbc.insets = new Insets(5, 20, 5, 5);

        // Hệ số trẻ em
        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("Hệ số trẻ em :"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; gbc.insets = new Insets(5, 5, 5, 5);
        JTextField txtHSTreEm = new JTextField("0.8", 8);
        heSoFields.put("HSTreEm", txtHSTreEm);
        panel.add(txtHSTreEm, gbc);

        // Hệ số cao tuổi
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0; gbc.insets = new Insets(5, 20, 5, 5);
        panel.add(new JLabel("Hệ số cao tuổi :"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1.0; gbc.insets = new Insets(5, 5, 5, 5);
        JTextField txtHSCaoTuoi = new JTextField("0.75", 8);
        heSoFields.put("HSCaoTuoi", txtHSCaoTuoi);
        panel.add(txtHSCaoTuoi, gbc);

        // Hệ số sinh viên
        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0; gbc.insets = new Insets(5, 20, 5, 5);
        panel.add(new JLabel("Hệ số sinh viên :"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 1.0; gbc.insets = new Insets(5, 5, 5, 5);
        JTextField txtHSSinhVien = new JTextField("0.9", 8);
        heSoFields.put("HSSinhVien", txtHSSinhVien);
        panel.add(txtHSSinhVien, gbc);

        // Hệ số người lớn
        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0; gbc.insets = new Insets(5, 20, 5, 5);
        panel.add(new JLabel("Hệ số người lớn :"), gbc);
        gbc.gridx = 3; gbc.gridy = 3; gbc.weightx = 1.0; gbc.insets = new Insets(5, 5, 5, 5);
        JTextField txtHSNguoiLon = new JTextField("1", 8);
        heSoFields.put("HSNguoiLon", txtHSNguoiLon);
        panel.add(txtHSNguoiLon, gbc);


        // Nút Xác nhận
        gbc.gridx = 3; gbc.gridy = 4; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setBackground(Color.RED);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.addActionListener(this);
        panel.add(btnXacNhan, gbc);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    /**
     * Khu vực 2: Bảng hiển thị giá vé (Cũ và Mới)
     */
    private JPanel createKhuVucGiaSauCapNhat(String title, DefaultTableModel model, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                title,
                TitledBorder.LEFT, TitledBorder.TOP,
                FONT_BOLD_14, Color.BLACK));

        String[] columnNames = {"Tên tàu", "Loại ghế", "Đối tượng", "Giá vé"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);

        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setFont(FONT_BOLD_14);
        table.getTableHeader().setFont(FONT_BOLD_14);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Gán lại model và table cho biến lớp để dễ truy cập sau này
        if (title.contains("cũ")) {
            this.modelGiaCu = model;
            this.tableGiaCu = table;
        } else {
            this.modelGiaMoi = model;
            this.tableGiaMoi = table;
        }

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Thiết lập chiều cao cứng để hai bảng hiển thị ngang nhau
        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 300));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        return panel;
    }

    // =================================================================================
    // LOGIC XỬ LÝ DỮ LIỆU & SỰ KIỆN
    // =================================================================================

    /**
     * [Giả định] Tải dữ liệu giá vé mẫu ban đầu
     */
    private void loadInitialData() {
        // Tải dữ liệu giả định cho bảng Giá cũ
        Object[][] dataCu = {
                {"SE1", "Ghế ngồi", "Trẻ em", "567.000"},
                {"SE1", "Ghế ngồi", "Người lớn", "834.000"},
                {"SE1", "Giường nằm", "Sinh Viên", "788.000"}
        };
        for (Object[] row : dataCu) {
            modelGiaCu.addRow(row);
        }

        // Tải dữ liệu giả định cho bảng Giá mới (Mặc định dùng giá mẫu từ ảnh)
        Object[][] dataMoi = {
                {"SE1", "Ghế ngồi", "Trẻ em", "667.000"},
                {"SE1", "Ghế ngồi", "Người lớn", "994.000"},
                {"SE1", "Giường nằm", "Sinh Viên", "878.000"}
        };
        for (Object[] row : dataMoi) {
            modelGiaMoi.addRow(row);
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút Tìm hoặc Xác nhận
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTimTau) {
            handleTimTau();
        } else if (e.getSource() == btnXacNhan) {
            handleCapNhatGia();
        }
    }

    private void handleTimTau() {
        String tenTau = txtTenTauTim.getText().trim();
        if (tenTau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên tàu để tìm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // [Logic DAO]: Tìm và đổ dữ liệu hệ số hiện tại lên form
        // Giả lập đổ dữ liệu hệ số (và giá cũ)
        JOptionPane.showMessageDialog(this, "Đã tìm thấy tàu " + tenTau + ". Vui lòng kiểm tra và cập nhật hệ số.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        txtTenTauCapNhat.setText(tenTau);
        // loadDataToTable(); // Tải lại 2 bảng giá
    }

    private void handleCapNhatGia() {
        if (!validateHeSoFields()) return;

        // [Logic DAO]: Gọi DAO.capNhatHeSo(...)
        // Sau khi cập nhật, gọi DAO để tính toán lại các giá vé mẫu và hiển thị lên bảng Giá Mới

        JOptionPane.showMessageDialog(this, "Đã cập nhật công thức tính giá vé cho tàu [" + txtTenTauCapNhat.getText() + "].", "Thành công", JOptionPane.INFORMATION_MESSAGE);

        // Giả lập hiển thị giá mới (thực tế cần tính toán từ các hệ số trên form)
        // modelGiaMoi.setRowCount(0); // Cần load lại giá mới
    }

    /**
     * Kiểm tra tính hợp lệ của các trường hệ số
     */
    private boolean validateHeSoFields() {
        // Kiểm tra Giá vé KM
        String giaVeKMStr = txtGiaVeKM.getText().replaceAll("[^\\d\\.]", "");
        if (giaVeKMStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá vé theo km không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtGiaVeKM.requestFocus();
            return false;
        }

        // Kiểm tra tất cả hệ số phải là số dương
        for (Map.Entry<String, JTextField> entry : heSoFields.entrySet()) {
            if (entry.getKey().equals("GiaVeKM")) continue; // Bỏ qua giá KM (đã check ở trên)

            JTextField field = entry.getValue();
            String value = field.getText().trim();
            try {
                double hs = Double.parseDouble(value);
                if (hs <= 0.0) {
                    JOptionPane.showMessageDialog(this, "Hệ số [" + entry.getKey() + "] phải là số dương.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    field.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Hệ số [" + entry.getKey() + "] phải là một giá trị số hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                field.requestFocus();
                return false;
            }
        }
        return true;
    }


    /**
     * Phương thức main để chạy độc lập
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Kiểm tra Màn hình Cập nhật Công thức Giá");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.add(new ManHinhQuanLyGiaVe(), BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}