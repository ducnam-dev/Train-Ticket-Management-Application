package view;

import com.toedter.calendar.JDateChooser;
import dao.ChuyenTauDao;
import dao.TauDAO;
import dao.TuyenDao;
import database.ConnectDB;
import entity.ChuyenTau;
import entity.Tau;
import entity.Tuyen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class ManHinhThemChuyenTauDonGian extends JFrame {

    private JTextField txtMaChuyenTau;
    private JComboBox<Tuyen> cbTuyen;
    private JComboBox<Tau> cbTau;
    private JDateChooser dateNgayDi;
    private JTextField txtGioDi;
    private JButton btnLuu;

    // DAO
    private TuyenDao tuyenDao = new TuyenDao();
    private TauDAO tauDao = new TauDAO();
    private ChuyenTauDao chuyenTauDao = new ChuyenTauDao();

    public ManHinhThemChuyenTauDonGian() {
        setTitle("Thêm Chuyến Tàu (Thủ Công)");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Kết nối CSDL ngay khi mở
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        khoiTaoGiaoDien();
        taiDuLieuVaoComboBox();
    }

    private void khoiTaoGiaoDien() {
        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Mã Chuyến Tàu
        mainPanel.add(new JLabel("Mã Chuyến Tàu:"));
        txtMaChuyenTau = new JTextField();
        mainPanel.add(txtMaChuyenTau);

        // 2. Chọn Tuyến
        mainPanel.add(new JLabel("Chọn Tuyến:"));
        cbTuyen = new JComboBox<>();
        // Hiển thị tên tuyến thay vì mã object
        cbTuyen.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tuyen) setText(((Tuyen) value).getTenTuyen());
                return this;
            }
        });
        mainPanel.add(cbTuyen);

        // 3. Chọn Tàu
        mainPanel.add(new JLabel("Chọn Tàu:"));
        cbTau = new JComboBox<>();
        // Hiển thị số hiệu tàu
        cbTau.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tau) setText(((Tau) value).getSoHieu());
                return this;
            }
        });
        mainPanel.add(cbTau);

        // 4. Ngày Đi
        mainPanel.add(new JLabel("Ngày Khởi Hành:"));
        dateNgayDi = new JDateChooser();
        dateNgayDi.setDateFormatString("dd/MM/yyyy");
        mainPanel.add(dateNgayDi);

        // 5. Giờ Đi
        mainPanel.add(new JLabel("Giờ (HH:mm):"));
        txtGioDi = new JTextField("08:00");
        mainPanel.add(txtGioDi);

        // 6. Nút Lưu
        mainPanel.add(new JLabel("")); // Placeholder
        btnLuu = new JButton("LƯU CHUYẾN TÀU");
        btnLuu.setBackground(new Color(0, 153, 76));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Arial", Font.BOLD, 14));
        btnLuu.addActionListener(e -> xuLyLuu());
        mainPanel.add(btnLuu);

        add(mainPanel);
    }

    private void taiDuLieuVaoComboBox() {
        try {
            // Tải Tuyến
            List<Tuyen> dsTuyen = tuyenDao.layTatCaTuyen();
            for (Tuyen t : dsTuyen) cbTuyen.addItem(t);

            // Tải Tàu
            List<Tau> dsTau = tauDao.layTatCa();
            for (Tau t : dsTau) cbTau.addItem(t);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void xuLyLuu() {
        try {
            // 1. Lấy dữ liệu
            String maChuyen = txtMaChuyenTau.getText().trim();
            Tuyen tuyenChon = (Tuyen) cbTuyen.getSelectedItem();
            Tau tauChon = (Tau) cbTau.getSelectedItem();
            java.util.Date ngayChon = dateNgayDi.getDate();
            String gioChonStr = txtGioDi.getText().trim();

            // 2. Kiểm tra rỗng
            if (maChuyen.isEmpty() || tuyenChon == null || tauChon == null || ngayChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            // 3. Chuyển đổi dữ liệu
            LocalDate ngayKhoiHanh = ngayChon.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if(gioChonStr.length() == 5) gioChonStr += ":00"; // Fix format HH:mm -> HH:mm:ss
            LocalTime gioKhoiHanh = LocalTime.parse(gioChonStr);

            // 4. Tạo đối tượng ChuyenTau (Dùng constructor đơn giản hoặc set từng cái)
            ChuyenTau ct = new ChuyenTau();
            ct.setMaChuyenTau(maChuyen);
            ct.setTuyen(tuyenChon); // Quan trọng
            ct.setTau(tauChon);     // Quan trọng
            ct.setNgayKhoiHanh(ngayKhoiHanh);
            ct.setGioKhoiHanh(gioKhoiHanh);
            // Các trường GaDi, GaDen, NV, v.v.. có thể để null lúc này vì CSDL đã chuẩn hóa

            // 5. Gọi DAO lưu
            if (chuyenTauDao.addChuyenTau(ct)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công chuyến: " + maChuyen);
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ManHinhThemChuyenTauDonGian().setVisible(true);
        });
    }
}