package view;

import dao.ToaDAO;
import entity.Toa;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManHinhQuanLyToa extends JPanel {
    private ToaDAO toaDAO = new ToaDAO();
    private JTable tblToa;
    private DefaultTableModel modelToa;
    private JTextField txtMaToa, txtLoaiToa, txtHeSo;
    private JButton btnCapNhat;

    public ManHinhQuanLyToa() {
        setLayout(new BorderLayout(10, 10));

        // --- PHẦN BẢNG (Bên trái) ---
        String[] columns = {"Mã Toa", "Tên Toa", "Loại Toa", "Hệ Số Giá"};
        modelToa = new DefaultTableModel(columns, 0);
        tblToa = new JTable(modelToa);

        tblToa.getSelectionModel().addListSelectionListener(e -> {
            int row = tblToa.getSelectedRow();
            if (row != -1) hienThiChiTiet(row);
        });

        // --- PHẦN FORM (Bên phải) ---
        JPanel pnlEdit = new JPanel(new GridLayout(5, 2, 5, 5));
        pnlEdit.setBorder(BorderFactory.createTitledBorder("Điều chỉnh Toa"));

        pnlEdit.add(new JLabel("Mã Toa:"));
        txtMaToa = new JTextField(); txtMaToa.setEditable(false);
        pnlEdit.add(txtMaToa);

        pnlEdit.add(new JLabel("Loại Toa:"));
        txtLoaiToa = new JTextField(); txtLoaiToa.setEditable(false);
        pnlEdit.add(txtLoaiToa);

        pnlEdit.add(new JLabel("Hệ số giá:"));
        txtHeSo = new JTextField();
        pnlEdit.add(txtHeSo);

        btnCapNhat = new JButton("Lưu hệ số");
        btnCapNhat.addActionListener(e -> xuLyCapNhat());

        add(new JScrollPane(tblToa), BorderLayout.CENTER);

        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.add(pnlEdit, BorderLayout.NORTH);
        pnlRight.add(btnCapNhat, BorderLayout.SOUTH);
        pnlRight.setPreferredSize(new Dimension(250, 0));
        add(pnlRight, BorderLayout.EAST);

        loadData();
    }

    private void loadData() {
        modelToa.setRowCount(0);
        // Lấy danh sách toa từ DAO (đảm bảo ToaDAO đã có cột heSoToa)
        List<Toa> list = toaDAO.getAllToa();
        for (Toa t : list) {
            modelToa.addRow(new Object[]{t.getMaToa(), t.getMaToa(), t.getLoaiToa(), t.getHeSoToa()});
        }
    }

    private void hienThiChiTiet(int row) {
        txtMaToa.setText(modelToa.getValueAt(row, 0).toString());
        txtLoaiToa.setText(modelToa.getValueAt(row, 2).toString());
        txtHeSo.setText(modelToa.getValueAt(row, 3).toString());
    }

    private void xuLyCapNhat() {
        try {
            String maToa = txtMaToa.getText();
            double heSoMoi = Double.parseDouble(txtHeSo.getText());

            // Ràng buộc an toàn
            if (heSoMoi < 1.0 || heSoMoi > 5.0) {
                JOptionPane.showMessageDialog(this, "Hệ số phải từ 1.0 đến 5.0");
                return;
            }

            if (toaDAO.capNhatHeSoToa(maToa, heSoMoi)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Hệ số phải là số thực (VD: 1.25)");
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Quản Lý Toa");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        frame.add(new ManHinhQuanLyToa());
        frame.setVisible(true);
    }
}