package view;

import dao.LoaiVeDAO;
import entity.LoaiVe;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManHinhQuanLyLoaiVe extends JPanel {

    private final LoaiVeDAO loaiVeDAO;
    private JTable tableLoaiVe;
    private DefaultTableModel tableModel;

    // Các thành phần nhập liệu
    private JTextField txtMaLoaiVe;
    private JTextField txtTenLoai;
    private JTextField txtMucGiaGiam;
    private JSpinner spinTuoiMin;
    private JSpinner spinTuoiMax;

    private JButton btnCapNhat;

    public ManHinhQuanLyLoaiVe() {
        loaiVeDAO = new LoaiVeDAO();
        setLayout(new BorderLayout(10, 10));

        // 1. Khởi tạo Bảng và Model
        khoiTaoBang();

        // 2. Khởi tạo Panel chi tiết
        JPanel panelChiTiet = taoPanelChiTiet();

        // 3. Bố cục chính
        JScrollPane scrollPane = new JScrollPane(tableLoaiVe);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        add(scrollPane, BorderLayout.CENTER);
        add(panelChiTiet, BorderLayout.EAST);

        // Tải dữ liệu ban đầu
        taiDuLieuVaoBang();
    }

    // --- Khởi tạo và Tải Dữ liệu ---

    private void khoiTaoBang() {
        String[] columns = {"Mã Vé", "Tên Loại", "Giảm Giá (%)", "Tuổi Min", "Tuổi Max"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        tableLoaiVe = new JTable(tableModel);

        // Bắt sự kiện chọn hàng
        tableLoaiVe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableLoaiVe.getSelectedRow();
                if (row >= 0) {
                    hienThiChiTiet(row);
                }
            }
        });
    }

    private void taiDuLieuVaoBang() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<LoaiVe> danhSach = loaiVeDAO.getAllLoaiVe();

        for (LoaiVe lv : danhSach) {

            // Tính toán phần trăm giảm (ví dụ: 0.75 -> 25%)
            double phanTramGiam = (1.0 - lv.getMucGiamGia()) * 100;

            // 1. LÀM TRÒN và ĐỊNH DẠNG THÀNH CHUỖI
            String phanTramGiamHienThi = String.format("%.0f%%", phanTramGiam);

            tableModel.addRow(new Object[]{
                    lv.getMaLoaiVe(),
                    lv.getTenLoai(),
                    phanTramGiamHienThi, // Dùng String thay vì Double
                    lv.getTuoiMin(),
                    lv.getTuoiMax()
            });
        }
    }

    // --- Panel Chi Tiết và Sự kiện ---

    private JPanel taoPanelChiTiet() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel panelForm = new JPanel(new GridLayout(6, 2, 5, 5));

        txtMaLoaiVe = new JTextField(10);
        txtMaLoaiVe.setEditable(false); // Mã không thể sửa
        txtTenLoai = new JTextField(10);
        txtMucGiaGiam = new JTextField(10);

        // Khởi tạo JSpinner cho tuổi Min/Max (Dùng SpinnerNumberModel)
        // Giá trị Max được đặt là 150 để tránh lỗi khởi tạo
        spinTuoiMin = new JSpinner(new SpinnerNumberModel(0, 0, 150, 1));
        spinTuoiMax = new JSpinner(new SpinnerNumberModel(100, 0, 150, 1));

        panelForm.add(new JLabel("Mã Loại Vé:"));
        panelForm.add(txtMaLoaiVe);
        panelForm.add(new JLabel("Tên Loại:"));
        panelForm.add(txtTenLoai);
        panelForm.add(new JLabel("Giảm Giá (Hệ số 0.0-1.0):"));
        panelForm.add(txtMucGiaGiam);
        panelForm.add(new JLabel("Tuổi Tối Thiểu:"));
        panelForm.add(spinTuoiMin);
        panelForm.add(new JLabel("Tuổi Tối Đa:"));
        panelForm.add(spinTuoiMax);

        // Panel Chức năng
        JPanel panelChucNang = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnCapNhat = new JButton("Cập Nhật");

        panelChucNang.add(btnCapNhat);

        panel.add(new JLabel("THÔNG TIN LOẠI VÉ", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(panelForm, BorderLayout.CENTER);
        panel.add(panelChucNang, BorderLayout.SOUTH);

        // Thêm Listener cho các nút
        // <<< SỬA ĐỔI: Thêm hàm thông báo thay cho logic cũ
        btnCapNhat.addActionListener(e -> capNhatLoaiVe());
        // KẾT THÚC SỬA ĐỔI >>>

        return panel;
    }

    private void hienThiChiTiet(int rowIndex) {
        txtMaLoaiVe.setText(tableModel.getValueAt(rowIndex, 0).toString());
        txtTenLoai.setText(tableModel.getValueAt(rowIndex, 1).toString());

        // Lấy giá trị chuỗi (ví dụ: "25%") và chuyển về hệ số (0.75)
        String giamGiaHienThi = tableModel.getValueAt(rowIndex, 2).toString();

        // Loại bỏ ký tự '%' và chuyển thành số thực
        double giamGiaPhanTram = Double.parseDouble(giamGiaHienThi.replace("%", ""));

        // Chuyển % giảm giá sang Hệ số (1.0 - Giảm / 100) để hiện thị trong JTextField
        double heSoGiam = 1.0 - (giamGiaPhanTram / 100.0);
        txtMucGiaGiam.setText(String.format("%.2f", heSoGiam)); // Hiện hệ số trong form

        // Thiết lập giá trị cho JSpinner
        // Cần phải kiểm tra giá trị Max của Spinner để tránh lỗi
        int tuoiMin = (int) tableModel.getValueAt(rowIndex, 3);
        int tuoiMax = (int) tableModel.getValueAt(rowIndex, 4);

        // Điều chỉnh giá trị tuổi Max nếu nó vượt quá giới hạn của Spinner (ví dụ: 150)
        int spinnerMaxLimit = 150; // Dựa trên khởi tạo SpinnerNumberModel
        if (tuoiMax > spinnerMaxLimit) {
            spinTuoiMax.setValue(spinnerMaxLimit); // Đặt về giới hạn Max của Spinner
        } else {
            spinTuoiMax.setValue(tuoiMax);
        }
        spinTuoiMin.setValue(tuoiMin);


        txtMaLoaiVe.setEditable(false);
        btnCapNhat.setEnabled(true);
    }

    private void lamMoiForm() {
        txtMaLoaiVe.setText("");
        txtTenLoai.setText("");
        txtMucGiaGiam.setText("");

        // Cập nhật giá trị JSpinner về mặc định an toàn
        spinTuoiMin.setValue(0);
        spinTuoiMax.setValue(100);

        txtMaLoaiVe.setEditable(false);
        btnCapNhat.setEnabled(false);
    }


    // --- Xử lý Chức năng (Controller logic) ---

    private LoaiVe layDuLieuTuForm() {
        try {
            String ma = txtMaLoaiVe.getText().trim();
            String ten = txtTenLoai.getText().trim();

            // Xử lý ô hệ số: Chuyển đổi dấu phẩy sang dấu chấm để Double.parseDouble không lỗi
            String heSoStr = txtMucGiaGiam.getText().trim().replace(",", ".");
            double heSo = Double.parseDouble(heSoStr);

            // Cách lấy giá trị Spinner an toàn hơn
            int min = Integer.parseInt(spinTuoiMin.getValue().toString());
            int max = Integer.parseInt(spinTuoiMax.getValue().toString());

            if (ma.isEmpty() || ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã và Tên loại vé không được trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (heSo < 0 || heSo > 1.0) {
                JOptionPane.showMessageDialog(this, "Hệ số giảm giá phải nằm trong khoảng [0.0, 1.0].\nVí dụ: 0.75 cho giảm 25%", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (min > max) {
                JOptionPane.showMessageDialog(this, "Tuổi tối thiểu không được lớn hơn tuổi tối đa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return new LoaiVe(ma, ten, heSo, min, max);

        } catch (NumberFormatException ex) {
            // Thông báo chi tiết hơn để người dùng biết họ nhập sai ô nào
            JOptionPane.showMessageDialog(this,
                    "Lỗi định dạng: Vui lòng kiểm tra lại ô 'Giảm giá'. \nHệ số phải là số thập phân (Ví dụ: 0.85)",
                    "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    // <<< ĐÃ XÓA PHƯƠNG THỨC themLoaiVe() >>>

    private void capNhatLoaiVe() {
        LoaiVe updatedLoaiVe = layDuLieuTuForm();
        if (updatedLoaiVe != null) {
            // Đảm bảo không thay đổi Mã Loại Vé
            updatedLoaiVe.setMaLoaiVe(txtMaLoaiVe.getText().trim());

            if (loaiVeDAO.updateLoaiVe(updatedLoaiVe)) {
                JOptionPane.showMessageDialog(this, "Cập nhật loại vé thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuVaoBang();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // <<< ĐÃ XÓA PHƯƠNG THỨC xoaLoaiVe() >>>

    // --- Hàm main test (Để bạn có thể chạy thử) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Loại Vé");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ManHinhQuanLyLoaiVe());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}