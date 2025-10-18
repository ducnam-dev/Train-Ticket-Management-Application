//package gui.Panel;
//
//import dao.NhanVien_DAO;
//import entity.NhanVien;
//import javax.swing.*;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class TraCuuNhanVien extends JPanel implements ActionListener {
//    private NhanVien_DAO dao;
//    private JTextField txtMaNv, txtTenNv, txtSoDienThoai;
//    private JComboBox<String> cbChucVu;
//    private JButton btnLamMoi;
//    private JTable table;
//    private DefaultTableModel tableModel;
//    private List<NhanVien> allNhanVien;
//	private JComponent title;
//	private JPanel inputPanel;
//	private JPanel maNvPanel;
//	private JLabel lblMaNv;
//	private JPanel tenNvPanel;
//	private JLabel lblTenNv;
//	private JPanel sdtPanel;
//	private JLabel lblSoDienThoai;
//	private JPanel chucVuPanel;
//	private JLabel lblChucVu;
//
//    public TraCuuNhanVien() {
//        dao = new NhanVien_DAO();
//        setLayout(new BorderLayout(10, 10));
//
//        // Tiêu đề
//        title = new JLabel("TRA CỨU NHÂN VIÊN", JLabel.CENTER);
//        title.setFont(new Font("Arial", Font.BOLD, 24));
//        title.setForeground(new Color(0, 102, 204));
//        add(title, BorderLayout.NORTH);
//
//        // Panel nhập liệu
//        inputPanel = new JPanel();
//        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
//        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Tìm kiếm nhân viên"));
//        inputPanel.setBackground(Color.WHITE);
//
//        // Mã nhân viên
//        maNvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
//        maNvPanel.setBackground(Color.WHITE);
//        lblMaNv = new JLabel("Mã nhân viên:");
//        lblMaNv.setFont(new Font("Arial", Font.PLAIN, 14));
//        lblMaNv.setPreferredSize(new Dimension(100, 30));
//        maNvPanel.add(lblMaNv);
//        txtMaNv = new JTextField(15);
//        txtMaNv.setFont(new Font("Arial", Font.PLAIN, 14));
//        maNvPanel.add(txtMaNv);
//        inputPanel.add(maNvPanel);
//
//        // Tên nhân viên
//        inputPanel.add(Box.createVerticalStrut(10));
//        tenNvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
//        tenNvPanel.setBackground(Color.WHITE);
//        lblTenNv = new JLabel("Tên nhân viên:");
//        lblTenNv.setFont(new Font("Arial", Font.PLAIN, 14));
//        lblTenNv.setPreferredSize(new Dimension(100, 30));
//        tenNvPanel.add(lblTenNv);
//        txtTenNv = new JTextField(15);
//        txtTenNv.setFont(new Font("Arial", Font.PLAIN, 14));
//        tenNvPanel.add(txtTenNv);
//        inputPanel.add(tenNvPanel);
//
//        // Số điện thoại
//        inputPanel.add(Box.createVerticalStrut(10));
//        sdtPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
//        sdtPanel.setBackground(Color.WHITE);
//        lblSoDienThoai = new JLabel("Số điện thoại:");
//        lblSoDienThoai.setFont(new Font("Arial", Font.PLAIN, 14));
//        lblSoDienThoai.setPreferredSize(new Dimension(100, 30));
//        sdtPanel.add(lblSoDienThoai);
//        txtSoDienThoai = new JTextField(15);
//        txtSoDienThoai.setFont(new Font("Arial", Font.PLAIN, 14));
//        sdtPanel.add(txtSoDienThoai);
//        inputPanel.add(sdtPanel);
//
//        // Chức vụ
//        inputPanel.add(Box.createVerticalStrut(10));
//        chucVuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
//        chucVuPanel.setBackground(Color.WHITE);
//        lblChucVu = new JLabel("Chức vụ:");
//        lblChucVu.setFont(new Font("Arial", Font.PLAIN, 14));
//        lblChucVu.setPreferredSize(new Dimension(100, 30));
//        chucVuPanel.add(lblChucVu);
//        cbChucVu = new JComboBox<>(new String[]{"Tất cả", "Phục vụ", "Pha chế", "Quản lý", "Thu ngân"});
//        cbChucVu.setFont(new Font("Arial", Font.PLAIN, 14));
//        cbChucVu.setPreferredSize(new Dimension(200, 30));
//        chucVuPanel.add(cbChucVu);
//        inputPanel.add(chucVuPanel);
//
//        // Nút làm mới
//        inputPanel.add(Box.createVerticalStrut(20));
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
//        buttonPanel.setBackground(Color.WHITE);
//        btnLamMoi = new JButton("Làm mới");
//        btnLamMoi.setBackground(new Color(102, 204, 102));
//        btnLamMoi.setFont(new Font("Arial", Font.BOLD, 14));
//        btnLamMoi.setPreferredSize(new Dimension(150, 35));
//        buttonPanel.add(btnLamMoi);
//        inputPanel.add(buttonPanel);
//
//        // Thêm inputPanel vào phía Tây
//        add(inputPanel, BorderLayout.WEST);
//
//        // Bảng hiển thị nhân viên
//        tableModel = new DefaultTableModel(new String[]{"Mã NV", "Họ Tên", "Chức Vụ", "SĐT"}, 0);
//        table = new JTable(tableModel);
//        loadTableData();
//        JScrollPane tableScrollPane = new JScrollPane(table);
//        tableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Danh sách nhân viên"));
//        add(tableScrollPane, BorderLayout.CENTER);
//
//        // Thêm DocumentListener cho các text field
//        DocumentListener searchListener = new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                filterTable();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                filterTable();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                filterTable();
//            }
//        };
//
//        txtMaNv.getDocument().addDocumentListener(searchListener);
//        txtTenNv.getDocument().addDocumentListener(searchListener);
//        txtSoDienThoai.getDocument().addDocumentListener(searchListener);
//
//        // Thêm ItemListener cho JComboBox
//        cbChucVu.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                if (e.getStateChange() == ItemEvent.SELECTED) {
//                    filterTable();
//                }
//            }
//        });
//
//        // Sự kiện cho nút Làm mới
//        btnLamMoi.addActionListener(this);
//    }
//
//    private void loadTableData() {
//        allNhanVien = dao.getAllNhanVien();
//        filterTable();
//    }
//
//    private void filterTable() {
//        String maNvFilter = txtMaNv.getText().trim().toLowerCase();
//        String tenNvFilter = txtTenNv.getText().trim().toLowerCase();
//        String sdtFilter = txtSoDienThoai.getText().trim().toLowerCase();
//        String chucVuFilter = cbChucVu.getSelectedItem().toString();
//
//        List<NhanVien> filteredList = allNhanVien.stream()
//                .filter(nv -> nv.getMaNv().toLowerCase().contains(maNvFilter))
//                .filter(nv -> nv.getHoTen().toLowerCase().contains(tenNvFilter))
//                .filter(nv -> nv.getSoDienThoai().toLowerCase().contains(sdtFilter))
//                .filter(nv -> chucVuFilter.equals("Tất cả") || nv.getChucVu().equals(chucVuFilter))
//                .collect(Collectors.toList());
//
//        tableModel.setRowCount(0);
//        for (NhanVien nv : filteredList) {
//            tableModel.addRow(new Object[]{nv.getMaNv(), nv.getHoTen(), nv.getChucVu(), nv.getSoDienThoai()});
//        }
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == btnLamMoi) {
//            xoaRong();
//        }
//    }
//
//    private void xoaRong() {
//        txtMaNv.setText("");
//        txtTenNv.setText("");
//        txtSoDienThoai.setText("");
//        cbChucVu.setSelectedIndex(0);
//        loadTableData();
//    }
//}