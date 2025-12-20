package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Giả sử bạn đã có lớp entity.Tuyen


public class ManHinhQuanLyDonGiaTuyen extends JPanel {
    private JTable tableTuyen;
    private DefaultTableModel tableModel;
    private JTextField txtMaTuyen, txtTenTuyen, txtDonGia;
    private JButton btnCapNhat;

    public ManHinhQuanLyDonGiaTuyen() {
        setLayout(new BorderLayout(10, 10));

        // 1. Khởi tạo Bảng
        String[] columns = {"Mã Tuyến", "Tên Tuyến", "Đơn giá (VNĐ/KM)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableTuyen = new JTable(tableModel);

        // 2. Panel Nhập liệu (Phía Đông)
        JPanel panelChiTiet = new JPanel(new BorderLayout(5, 5));
        panelChiTiet.setBorder(BorderFactory.createTitledBorder("Cấu hình Đơn giá"));

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        txtMaTuyen = new JTextField(10); txtMaTuyen.setEditable(false);
        txtTenTuyen = new JTextField(10); txtTenTuyen.setEditable(false);
        txtDonGia = new JTextField(10);

        form.add(new JLabel("Mã Tuyến:")); form.add(txtMaTuyen);
        form.add(new JLabel("Tên Tuyến:")); form.add(txtTenTuyen);
        form.add(new JLabel("Đơn giá/KM:")); form.add(txtDonGia);

        btnCapNhat = new JButton("Lưu thay đổi");
        panelChiTiet.add(form, BorderLayout.CENTER);
        panelChiTiet.add(btnCapNhat, BorderLayout.SOUTH);

        // 3. Layout chính
        add(new JScrollPane(tableTuyen), BorderLayout.CENTER);
        add(panelChiTiet, BorderLayout.EAST);

        // Sự kiện
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

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        // Ở đây bạn gọi TuyenDAO để lấy danh sách
        // Ví dụ dữ liệu mẫu:
        tableModel.addRow(new Object[]{"SE1", "Tàu Thống Nhất Bắc Nam", 1000});
        tableModel.addRow(new Object[]{"SE2", "Tàu siêu tốc Nam Bắc", 1200});
        tableModel.addRow(new Object[]{"SPT2", "Tàu Sài Gòn - Phan Thiết", 1500});
    }

    private void capNhatDonGia() {
        String ma = txtMaTuyen.getText();
        try {
            int giaMoi = Integer.parseInt(txtDonGia.getText().trim());
            // Gọi DAO: tuyenDAO.updateDonGia(ma, giaMoi);
            JOptionPane.showMessageDialog(this, "Đã cập nhật đơn giá cho tuyến " + ma);
            taiDuLieu();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên cho đơn giá.");
        }
    }
}
