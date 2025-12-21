package view;

import dao.LoaiChoDatDAO;
import entity.LoaiChoDat;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ManHinhQuanLyLoaiChoDat extends JPanel {

    private final LoaiChoDatDAO loaiChoDAO;
    private JTable tableLoaiCho;
    private DefaultTableModel tableModel;

    private JTextField txtMaLoaiCho, txtTenLoaiCho, txtHeSo;
    private JButton btnCapNhat, btnLamMoi;

    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);

    public ManHinhQuanLyLoaiChoDat() {
        loaiChoDAO = new LoaiChoDatDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Tiêu đề ---
        JLabel lblTitle = new JLabel("QUẢN LÝ HỆ SỐ LOẠI CHỖ ĐẶT");
        lblTitle.setFont(FONT_HEADER);
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // --- Center: Bảng dữ liệu ---
        khoiTaoBang();
        JScrollPane scrollPane = new JScrollPane(tableLoaiCho);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Danh sách loại chỗ (Ghế/Giường)",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.ITALIC, 12)));
        add(scrollPane, BorderLayout.CENTER);

        // --- East: Panel Chi tiết ---
        add(taoPanelChiTiet(), BorderLayout.EAST);

        taiDuLieuVaoBang();
    }

    private void khoiTaoBang() {
        String[] columns = {"Mã Loại Chỗ", "Tên Loại Chỗ", "Hệ Số Giá"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableLoaiCho = new JTable(tableModel);
        tableLoaiCho.setRowHeight(30);
        tableLoaiCho.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        tableLoaiCho.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableLoaiCho.getSelectedRow();
                if (row >= 0) hienThiChiTiet(row);
            }
        });
    }

    private JPanel taoPanelChiTiet() {
        JPanel mainSidePanel = new JPanel(new BorderLayout());
        mainSidePanel.setPreferredSize(new Dimension(350, 0));
        mainSidePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR), "Cấu hình hệ số"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaLoaiCho = new JTextField();
        txtMaLoaiCho.setEditable(false);
        txtMaLoaiCho.setBackground(new Color(235, 235, 235));

        txtTenLoaiCho = new JTextField();
        txtHeSo = new JTextField();

        addFormField(form, "Mã Loại Chỗ:", txtMaLoaiCho, gbc, 0);
        addFormField(form, "Tên Loại Chỗ:", txtTenLoaiCho, gbc, 1);
        addFormField(form, "Hệ Số Nhân:", txtHeSo, gbc, 2);

        // Panel chứa nút
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnCapNhat = new JButton("Cập Nhật");
        btnCapNhat.setPreferredSize(new Dimension(100, 35));
        btnCapNhat.setBackground(PRIMARY_COLOR);
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setEnabled(false);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setPreferredSize(new Dimension(100, 35));

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnCapNhat);

        btnCapNhat.addActionListener(e -> capNhatHeSo());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        mainSidePanel.add(form, BorderLayout.NORTH);
        mainSidePanel.add(pnlButtons, BorderLayout.SOUTH);

        return mainSidePanel;
    }

    private void addFormField(JPanel pnl, String label, JComponent comp, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        pnl.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        comp.setPreferredSize(new Dimension(0, 30));
        pnl.add(comp, gbc);
    }

    private void taiDuLieuVaoBang() {
        tableModel.setRowCount(0);
        List<LoaiChoDat> ds = loaiChoDAO.getAllLoaiChoDat();
        for (LoaiChoDat l : ds) {
            tableModel.addRow(new Object[]{ l.getMaLoaiCho(), l.getTenLoaiCho(), l.getHeSo() });
        }
    }

    private void hienThiChiTiet(int row) {
        txtMaLoaiCho.setText(tableModel.getValueAt(row, 0).toString());
        txtTenLoaiCho.setText(tableModel.getValueAt(row, 1).toString());
        txtHeSo.setText(tableModel.getValueAt(row, 2).toString());
        btnCapNhat.setEnabled(true);
    }

    private void lamMoiForm() {
        txtMaLoaiCho.setText("");
        txtTenLoaiCho.setText("");
        txtHeSo.setText("");
        tableLoaiCho.clearSelection();
        btnCapNhat.setEnabled(false);
    }

    private void capNhatHeSo() {
        try {
            String ma = txtMaLoaiCho.getText();
            String ten = txtTenLoaiCho.getText();
            double heSo = Double.parseDouble(txtHeSo.getText().replace(",", "."));

            LoaiChoDat loai = new LoaiChoDat(ma, ten, heSo);
            if (loaiChoDAO.updateLoaiChoDat(loai)) {
                JOptionPane.showMessageDialog(this, "Cập nhật hệ số thành công!");
                taiDuLieuVaoBang();
                lamMoiForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hệ số phải là số thực (Ví dụ: 1.25)");
        }
    }

    //main
    public static void main(String[] args) {
        JFrame frame = new JFrame("Quản Lý Loại Chỗ Đặt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new ManHinhQuanLyLoaiChoDat());
        frame.setVisible(true);
    }
}