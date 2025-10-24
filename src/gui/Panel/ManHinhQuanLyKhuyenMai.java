package gui.Panel;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

/**
 * Lớp này tạo giao diện Quản lý Khuyến Mãi.
 * Chức năng: Tạo, Sửa, Kết thúc, Gia hạn KM.
 */
public class ManHinhQuanLyKhuyenMai extends JPanel {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT (Dùng lại từ ManhinhQuanLyChuyenTau)
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);

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
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; txtMaKM = new JTextField(15); fieldsPanel.add(txtMaKM, gbc);

        // Tên KM
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; fieldsPanel.add(new JLabel("Tên KM:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; txtTenKM = new JTextField(15); fieldsPanel.add(txtTenKM, gbc);

        // Ngày Bắt Đầu
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; fieldsPanel.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; dateChooserBatDau = new JDateChooser(); fieldsPanel.add(dateChooserBatDau, gbc);

        // Ngày Kết Thúc
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; fieldsPanel.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; dateChooserKetThuc = new JDateChooser(); fieldsPanel.add(dateChooserKetThuc, gbc);

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
     * Giả định đây là khu vực để nhập dữ liệu cho bảng DieuKienKhuyenMai.
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
        JButton btnThemDK = new JButton("Thêm điều kiện");
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

        JButton btnThem = new JButton("Tạo Khuyến Mãi");
        JButton btnSua = new JButton("Cập Nhật");
        JButton btnKetThuc = new JButton("Kết Thúc KM");
        JButton btnGiaHan = new JButton("Gia Hạn KM");
        JButton btnLamMoi = new JButton("Làm Mới");

        // Thiết lập sự kiện
        btnThem.addActionListener(e -> { JOptionPane.showMessageDialog(this, "Thêm KM..."); /* Logic DAO */ });
        btnSua.addActionListener(e -> { JOptionPane.showMessageDialog(this, "Sửa KM..."); /* Logic DAO */ });
        btnKetThuc.addActionListener(e -> {
            // Logic kết thúc: Cập nhật TrangThai = 'DaKetThuc' và NgayKetThuc = Hôm nay
            JOptionPane.showMessageDialog(this, "Kết thúc KM...");
        });
        btnGiaHan.addActionListener(e -> {
            // Logic gia hạn: Cập nhật NgayKetThuc = Ngày mới từ DateChooser
            JOptionPane.showMessageDialog(this, "Gia hạn KM...");
        });

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
    private void loadDataToTable() {
        // Đây là logic giả định, trong thực tế cần truy vấn CSDL
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Object[][] data = {
                {"KM_TE_1_6", "Trẻ em 1/6", "2026-06-01", "2026-06-01", "VE_DON", "30.0%", "0", "HoatDong"},
                {"KM_4VE", "Mua 4 vé -10%", "2025-10-01", "2026-01-31", "HOA_DON", "10.0%", "0", "HoatDong"},
                {"KM_50K_500", "Giảm 50k / 500k", "2025-01-01", "2026-12-31", "HOA_DON", "0.0%", "50,000", "HoatDong"},
                {"KM_HE_20", "Hè giảm 20%", "2025-06-01", "2025-08-31", "HOA_DON", "20.0%", "0", "DaKetThuc"}
        };

        for (Object[] row : data) {
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
            // Cần logic phức tạp hơn để chuyển từ String sang Date cho JDateChooser
            // dateChooserBatDau.setDate(df.parse(tableModel.getValueAt(row, 2).toString()));
            // dateChooserKetThuc.setDate(df.parse(tableModel.getValueAt(row, 3).toString()));
            cbLoaiApDung.setSelectedItem(tableModel.getValueAt(row, 4).toString());

            // Xử lý giá trị số
            String phanTramStr = tableModel.getValueAt(row, 5).toString().replace("%", "");
            spinnerPhanTram.setValue(Double.parseDouble(phanTramStr) / 100.0);

            String tienGiamStr = tableModel.getValueAt(row, 6).toString().replace(",", "");
            spinnerTienGiam.setValue(Integer.parseInt(tienGiamStr));

            // TrangThai và Mô tả cần được load từ CSDL sau khi có MaKM

        } catch (Exception e) {
            System.err.println("Lỗi đổ dữ liệu lên form: " + e.getMessage());
            // e.printStackTrace();
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
            mainFrame.add(new ManHinhQuanLyKhuyenMai(), BorderLayout.CENTER);

            frame.setContentPane(mainFrame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}