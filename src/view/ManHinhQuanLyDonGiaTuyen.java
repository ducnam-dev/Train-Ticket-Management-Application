package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ManHinhQuanLyDonGiaTuyen extends JPanel {
    private JTable tableTuyen;
    private DefaultTableModel tableModel;
    private JTextField txtMaTuyen, txtTenTuyen, txtDonGia;
    private JButton btnCapNhat;

    public ManHinhQuanLyDonGiaTuyen() {
        // Sử dụng BorderLayout với khoảng cách 15px giữa các thành phần
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Tạo khoảng cách với viền cửa sổ

        // 1. Tiêu đề phía trên
        JLabel lblHeader = new JLabel("QUẢN LÝ ĐƠN GIÁ THEO TUYẾN", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(new Color(41, 128, 185)); // Màu xanh đậm chuyên nghiệp
        add(lblHeader, BorderLayout.NORTH);

        // 2. Khởi tạo Bảng (Bên Trái)
        khoiTaoBang();
        JScrollPane scrollPane = new JScrollPane(tableTuyen);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Panel Nhập liệu (Bên Phải - Cố định kích thước)
        JPanel panelDong = taoPanelNhapLieu();
        add(panelDong, BorderLayout.EAST);

        // Sự kiện click bảng
        tableTuyen.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableTuyen.getSelectedRow();
                if (row >= 0) {
                    txtMaTuyen.setText(tableModel.getValueAt(row, 0).toString());
                    txtTenTuyen.setText(tableModel.getValueAt(row, 1).toString());
                    txtDonGia.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });

        btnCapNhat.addActionListener(e -> capNhatDonGia());

        taiDuLieu();
    }

    private void khoiTaoBang() {
        String[] columns = {"Mã Tuyến", "Tên Tuyến", "Đơn giá (VNĐ/KM)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableTuyen = new JTable(tableModel);
        tableTuyen.setRowHeight(30); // Tăng chiều cao hàng cho dễ nhìn
        tableTuyen.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Căn giữa dữ liệu trong cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableTuyen.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableTuyen.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Cố định độ rộng cột Mã Tuyến
        tableTuyen.getColumnModel().getColumn(0).setPreferredWidth(80);
        tableTuyen.getColumnModel().getColumn(0).setMaxWidth(100);
    }

    private JPanel taoPanelNhapLieu() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        // Cố định chiều rộng panel là 320px
        panel.setPreferredSize(new Dimension(320, 0));

        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridLayout(4, 1, 10, 10));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin chi tiết"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        txtMaTuyen = new JTextField();
        txtMaTuyen.setEditable(false);
        txtMaTuyen.setBackground(new Color(236, 240, 241));

        txtTenTuyen = new JTextField();
        txtTenTuyen.setEditable(false);
        txtTenTuyen.setBackground(new Color(236, 240, 241));

        txtDonGia = new JTextField();
        txtDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Group các Label và Field
        panelForm.add(createFieldGroup("Mã Tuyến:", txtMaTuyen));
        panelForm.add(createFieldGroup("Tên Tuyến:", txtTenTuyen));
        panelForm.add(createFieldGroup("Đơn giá/KM (VNĐ):", txtDonGia));

        // Nút bấm
        btnCapNhat = new JButton("LƯU THAY ĐỔI");
        btnCapNhat.setPreferredSize(new Dimension(0, 45));
        btnCapNhat.setBackground(new Color(46, 204, 113));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCapNhat.setFocusPainted(false);

        panel.add(panelForm, BorderLayout.NORTH);
        panel.add(btnCapNhat, BorderLayout.SOUTH);

        return panel;
    }

    // Hàm phụ trợ tạo nhóm Label và TextField
    private JPanel createFieldGroup(String labelText, JTextField textField) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(label, BorderLayout.NORTH);
        p.add(textField, BorderLayout.CENTER);
        return p;
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"SE1", "Tàu Thống Nhất Bắc Nam", 1000});
        tableModel.addRow(new Object[]{"SE2", "Tàu siêu tốc Nam Bắc", 1200});
        tableModel.addRow(new Object[]{"SPT2", "Tàu Sài Gòn - Phan Thiết", 1500});
    }

    private void capNhatDonGia() {
        String ma = txtMaTuyen.getText();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tuyến từ bảng!");
            return;
        }
        try {
            int giaMoi = Integer.parseInt(txtDonGia.getText().trim());
            if (giaMoi < 0) throw new NumberFormatException();

            // Logic cập nhật vào CSDL ở đây
            JOptionPane.showMessageDialog(this, "Đã cập nhật đơn giá cho tuyến " + ma + " thành công!");
            taiDuLieu();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Đơn giá phải là số nguyên dương!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
}