package gui.Panel;

import control.CaLamViec;
import dao.*;
import database.ConnectDB;
import entity.*;
import entity.lopEnum.TrangThaiChuyenTau;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

public class ManHinhQuanLyChuyenTau extends JPanel {

    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);

    // Tuyến Components
    private JTable tbTuyen;
    private DefaultTableModel modelTuyen;
    private JTextField txtMaTuyen;
    private JTextField txtTenTuyen;
    private JComboBox<Ga> cbGaDau;
    private JComboBox<Ga> cbGaCuoi;
    private JComboBox<Tau> cbChonTau;

    private JButton btnThemTuyen, btnSuaTuyen, btnXoaTrangTuyen;

    // Ga Trong Tuyến Components
    private JTable tbGaTrongTuyen;
    private DefaultTableModel modelGa;
    private JComboBox<Ga> cbMaGa;
    private JTextField txtThuTuGa;
    private JTextField txtKhoangCach;
    private JTextField txtThoiGianDi; // Nhập số phút
    private JTextField txtThoiGianDung; // Nhập số phút
    private JButton btnThemGa, btnSuaGa, btnXoaGa, btnLamMoiGa;

    // Lịch trình lặp Components
    private JDateChooser dateChooserBatDau;
    private JDateChooser dateChooserKetThuc;
    private JButton btnTaoLichTrinh;
    private JTextField txtGioKhoiHanhChinh;

    // DAO
    private final TuyenDao tuyenDao;
    private final GaTrongTuyenDao gaTrongTuyenDao;
    private final GaDao gaDao;
    private final ChuyenTauDao chuyenTauDao;
    private final TauDAO tauDao;

    public ManHinhQuanLyChuyenTau() {
        try {
            tuyenDao = new TuyenDao();
            gaTrongTuyenDao = new GaTrongTuyenDao();
            gaDao = new GaDao();
            chuyenTauDao = new ChuyenTauDao();
            tauDao = new TauDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + e.getMessage(), "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Lỗi khởi tạo DAO", e);
        }

        khoiTaoGiaoDien();
        try {
            taiDuLieuTuyen();
            loadDuLieuGaDocLap();
            loadDuLieuTau();
        } catch (SQLException e) {
            hienThiThongBaoLoi("Lỗi khởi tạo dữ liệu ban đầu: " + e.getMessage());
        }
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel tieuDe = new JLabel("QUẢN LÝ TUYẾN & TẠO LỊCH TRÌNH TỰ ĐỘNG");
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tieuDe.setForeground(new Color(0, 102, 204));
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        add(tieuDe, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(taoPanelTuyen());
        centerPanel.add(taoPanelGaTrongTuyen());

        add(centerPanel, BorderLayout.CENTER);

        // Panel Lịch trình nằm ở dưới cùng (South) và to hơn
        add(taoPanelTaoLichTrinh(), BorderLayout.SOUTH);
    }

    // =========================================================================
    // 1. PANEL QUẢN LÝ TUYẾN
    // =========================================================================
    private JPanel taoPanelTuyen() {
        JPanel panelTuyen = new JPanel(new BorderLayout(5, 5));
        panelTuyen.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "1. Danh sách Tuyến Tàu"));
        panelTuyen.setBackground(Color.WHITE);

        panelTuyen.add(taoFormTuyen(), BorderLayout.NORTH);

        String[] cotTuyen = {"Mã Tuyến", "Tên Tuyến", "Ga Đầu", "Ga Cuối"};
        modelTuyen = new DefaultTableModel(cotTuyen, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tbTuyen = new JTable(modelTuyen);
        tbTuyen.setFont(FONT_PLAIN_14);
        tbTuyen.setRowHeight(25);
        tbTuyen.getTableHeader().setFont(FONT_BOLD_14);

        tbTuyen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiChiTietTuyen();
            }
        });
        JScrollPane scrollTuyen = new JScrollPane(tbTuyen);
        panelTuyen.add(scrollTuyen, BorderLayout.CENTER);

        return panelTuyen;
    }

    private JPanel taoFormTuyen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1: Mã Tuyến + Tên Tuyến
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Mã Tuyến:"), gbc);
        gbc.gridx = 1; txtMaTuyen = new JTextField(10); panel.add(txtMaTuyen, gbc);

        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("  Tên Tuyến:"), gbc);
        gbc.gridx = 3; txtTenTuyen = new JTextField(15); panel.add(txtTenTuyen, gbc);

        // Hàng 2: Ga Đầu + Ga Cuối
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Ga Đầu:"), gbc);
        gbc.gridx = 1; cbGaDau = new JComboBox<>(); panel.add(cbGaDau, gbc);

        gbc.gridx = 2; gbc.gridy = 1; panel.add(new JLabel("  Ga Cuối:"), gbc);
        gbc.gridx = 3; cbGaCuoi = new JComboBox<>(); panel.add(cbGaCuoi, gbc);

        // Nút bấm
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNut.setOpaque(false);
        btnThemTuyen = new JButton("Thêm");
        btnSuaTuyen = new JButton("Sửa");
        btnXoaTrangTuyen = new JButton("Mới");
        panelNut.add(btnThemTuyen); panelNut.add(btnSuaTuyen); panelNut.add(btnXoaTrangTuyen);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; panel.add(panelNut, gbc);

        btnThemTuyen.addActionListener(e -> themTuyen());
        btnXoaTrangTuyen.addActionListener(e -> xoaTrangFormTuyen());

        return panel;
    }

    // =========================================================================
    // 2. PANEL GA TRONG TUYẾN
    // =========================================================================
    private JPanel taoPanelGaTrongTuyen() {
        JPanel panelGa = new JPanel(new BorderLayout(5, 5));
        panelGa.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "2. Cấu hình Ga dừng đỗ"));
        panelGa.setBackground(Color.WHITE);

        panelGa.add(taoPanelNhapLieuGa(), BorderLayout.NORTH);

        String[] cotGa = {"Mã Ga", "Thứ tự", "KC (km)", "TG Đi (phút)", "TG Dừng (phút)"};
        modelGa = new DefaultTableModel(cotGa, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tbGaTrongTuyen = new JTable(modelGa);
        tbGaTrongTuyen.setFont(FONT_PLAIN_14);
        tbGaTrongTuyen.setRowHeight(25);
        tbGaTrongTuyen.getTableHeader().setFont(FONT_BOLD_14);

        tbGaTrongTuyen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dienThongTinGaVaoForm();
            }
        });
        JScrollPane scrollGa = new JScrollPane(tbGaTrongTuyen);
        panelGa.add(scrollGa, BorderLayout.CENTER);

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNut.setOpaque(false);
        btnThemGa = new JButton("Thêm Ga");
        btnSuaGa = new JButton("Sửa Ga");
        btnXoaGa = new JButton("Xóa Ga");
        btnLamMoiGa = new JButton("Làm Mới");
        panelNut.add(btnThemGa); panelNut.add(btnSuaGa); panelNut.add(btnXoaGa); panelNut.add(btnLamMoiGa);
        panelGa.add(panelNut, BorderLayout.SOUTH);

        // Events
        btnThemGa.addActionListener(e -> themGaTrongTuyen());
        btnSuaGa.addActionListener(e -> suaGaTrongTuyen());
        btnXoaGa.addActionListener(e -> xoaGaTrongTuyen());
        btnLamMoiGa.addActionListener(e -> xoaTrangFormGa());

        return panelGa;
    }

    private JPanel taoPanelNhapLieuGa() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 10); // Tăng lề phải để tách cột
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1: Mã Ga + Thứ tự Ga
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Mã Ga:"), gbc);
        gbc.gridx = 1; cbMaGa = new JComboBox<>(); panel.add(cbMaGa, gbc);

        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("Thứ tự:"), gbc);
        gbc.gridx = 3; txtThuTuGa = new JTextField(5); panel.add(txtThuTuGa, gbc);

        // Hàng 2: Khoảng cách + Thời gian dừng
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("KC (km):"), gbc);
        gbc.gridx = 1; txtKhoangCach = new JTextField(10); panel.add(txtKhoangCach, gbc);

        gbc.gridx = 2; gbc.gridy = 1; panel.add(new JLabel("Dừng (phút):"), gbc);
        gbc.gridx = 3; txtThoiGianDung = new JTextField(5); panel.add(txtThoiGianDung, gbc);

        // Hàng 3: Thời gian đi đến ga kế (Dàn hàng ngang 1 mình hoặc ghép nếu cần)
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("TG Đi kế:"), gbc);
        gbc.gridx = 1; txtThoiGianDi = new JTextField(10); panel.add(txtThoiGianDi, gbc);

        // JLabel đơn vị cho rõ ràng
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(new JLabel("(phút đi đến ga tiếp theo)"), gbc);

        return panel;
    }

    // =========================================================================
    // 3. PANEL TẠO LỊCH TRÌNH (TO HƠN VÀ ĐẦY ĐỦ CHỨC NĂNG)
    // =========================================================================
    private JPanel taoPanelTaoLichTrinh() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(230, 240, 255));
        panel.setBorder(new CompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
                        "3. KHỞI TẠO LỊCH TRÌNH CHẠY TÀU (BATCH GENERATION)",
                        TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, FONT_BOLD_14, new Color(0, 102, 204)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- DÒNG 1: CHỌN TÀU & GIỜ KHỞI HÀNH ---

        // 1. Label Chọn Tàu
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblTau = new JLabel("Chọn Tàu Thực Hiện:");
        lblTau.setFont(FONT_BOLD_14);
        panel.add(lblTau, gbc);

        // 2. ComboBox Tàu (THÊM MỚI)
        gbc.gridx = 1; gbc.gridy = 0;
        cbChonTau = new JComboBox<>();
        cbChonTau.setFont(FONT_PLAIN_14);
        cbChonTau.setPreferredSize(new Dimension(200, 30));
        panel.add(cbChonTau, gbc);

        // 3. Label Giờ
        gbc.gridx = 2; gbc.gridy = 0;
        JLabel lblGio = new JLabel("Giờ Khởi Hành:");
        lblGio.setFont(FONT_BOLD_14);
        panel.add(lblGio, gbc);

        // 4. Text Giờ
        gbc.gridx = 3; gbc.gridy = 0;
        txtGioKhoiHanhChinh = new JTextField("06:00", 10);
        txtGioKhoiHanhChinh.setFont(FONT_PLAIN_14);
        panel.add(txtGioKhoiHanhChinh, gbc);

        // --- DÒNG 2: NGÀY BẮT ĐẦU & KẾT THÚC ---

        // 5. Label Từ Ngày
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblTuNgay = new JLabel("Từ Ngày:");
        lblTuNgay.setFont(FONT_BOLD_14);
        panel.add(lblTuNgay, gbc);

        // 6. DateChooser Từ Ngày
        gbc.gridx = 1; gbc.gridy = 1;
        dateChooserBatDau = new JDateChooser();
        dateChooserBatDau.setDateFormatString("dd/MM/yyyy");
        dateChooserBatDau.setFont(FONT_PLAIN_14);
        panel.add(dateChooserBatDau, gbc);

        // 7. Label Đến Ngày
        gbc.gridx = 2; gbc.gridy = 1;
        JLabel lblDenNgay = new JLabel("Đến Ngày:");
        lblDenNgay.setFont(FONT_BOLD_14);
        panel.add(lblDenNgay, gbc);

        // 8. DateChooser Đến Ngày
        gbc.gridx = 3; gbc.gridy = 1;
        dateChooserKetThuc = new JDateChooser();
        dateChooserKetThuc.setDateFormatString("dd/MM/yyyy");
        dateChooserKetThuc.setFont(FONT_PLAIN_14);
        panel.add(dateChooserKetThuc, gbc);

        // --- DÒNG 3: NÚT BẤM ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; // Span 4 cột
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        btnTaoLichTrinh = new JButton("TIẾN HÀNH TẠO LỊCH TRÌNH");
        btnTaoLichTrinh.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnTaoLichTrinh.setBackground(new Color(0, 153, 76));
        btnTaoLichTrinh.setForeground(Color.WHITE);
        btnTaoLichTrinh.setPreferredSize(new Dimension(300, 45));
        btnTaoLichTrinh.setFocusPainted(false);
        btnTaoLichTrinh.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnTaoLichTrinh.addActionListener(e -> taoLichTrinhHangLoat());

        panel.add(Box.createRigidArea(new Dimension(0, 15)), gbc); // Spacer
        panel.add(btnTaoLichTrinh, gbc);

        return panel;
    }
    // =========================================================================
    // LOGIC TẠO LỊCH TRÌNH HÀNG LOẠT (COMPLETED)
    // =========================================================================

    // Trong ManHinhCauHinhTuyen.java

    private void taoLichTrinhHangLoat() {
        // 1. Kiểm tra đầu vào
        String maTuyen = txtMaTuyen.getText();
        if (txtMaTuyen.isEditable() || maTuyen.isEmpty()) {
            hienThiThongBaoLoi("Vui lòng chọn một Tuyến từ bảng bên trái.");
            return;
        }

        java.util.Date startDate = dateChooserBatDau.getDate();
        java.util.Date endDate = dateChooserKetThuc.getDate();
        String gioBatDauStr = txtGioKhoiHanhChinh.getText().trim();

        if (startDate == null || endDate == null) {
            hienThiThongBaoLoi("Vui lòng chọn đầy đủ ngày bắt đầu và kết thúc.");
            return;
        }
        if (startDate.after(endDate)) {
            hienThiThongBaoLoi("Ngày bắt đầu phải trước ngày kết thúc.");
            return;
        }

        LocalTime gioBatDauCoDinh;
        try {
            if(gioBatDauStr.length() == 5) gioBatDauStr += ":00";
            gioBatDauCoDinh = LocalTime.parse(gioBatDauStr);
        } catch (Exception e) {
            hienThiThongBaoLoi("Giờ khởi hành không hợp lệ. Định dạng chuẩn: HH:mm (VD: 08:00)");
            return;
        }

        // 2. Thực hiện tạo lịch trình
        try {
            // [QUAN TRỌNG] Lấy đối tượng Tuyến đầy đủ từ CSDL
            Tuyen tuyenObj = tuyenDao.layTuyenTheoMa(maTuyen);
            if (tuyenObj == null) {
                hienThiThongBaoLoi("Không tìm thấy thông tin Tuyến trong CSDL.");
                return;
            }

            List<GaTrongTuyen> danhSachGa = gaTrongTuyenDao.layGaTrongTuyenTheoMa(maTuyen);
            if (danhSachGa.size() < 2) {
                hienThiThongBaoLoi("Tuyến này chưa cấu hình đủ Ga (cần ít nhất 2 ga).");
                return;
            }
            // KIỂM TRA TÀU
            Tau tauDuocChon = (Tau) cbChonTau.getSelectedItem();
            if (tauDuocChon == null) {
                hienThiThongBaoLoi("Vui lòng chọn Tàu để thực hiện lịch trình.");
                return;
            }

            int soLuongChuyenTaoMoi = 0;

            // [QUAN TRỌNG] Tạo đối tượng Tàu (Entity Tau)
            // Lưu ý: Mã tàu này PHẢI tồn tại trong bảng Tau của CSDL (ví dụ: 'TAUX' trong script của bạn)
            // Tạm thời hardcode 'TAUX' hoặc lấy từ giao diện nếu có

            entity.Tau tauObj = tauDuocChon;

            LocalDate currentDay = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDay = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            while (!currentDay.isAfter(endDay)) {
                // Truyền đối tượng Tuyen và Tau vào hàm con
                int count = taoLichTrinhNgay(tuyenObj, tauObj, currentDay, gioBatDauCoDinh, danhSachGa);
                soLuongChuyenTaoMoi += count;
                currentDay = currentDay.plusDays(1);
            }

            this.setCursor(Cursor.getDefaultCursor());

            JOptionPane.showMessageDialog(this,
                    "Hoàn tất!\nĐã tạo thành công " + soLuongChuyenTaoMoi + " chuyến tàu khả thi.\n" +
                            "Tuyến: " + maTuyen + "\n" +
                            "Giai đoạn: " + startDate + " - " + endDate,
                    "Kết quả Tạo Lịch Trình",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            this.setCursor(Cursor.getDefaultCursor());
            hienThiThongBaoLoi("Lỗi CSDL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Logic tạo chuyến tàu cho 1 ngày cụ thể
     */
// Trong ManHinhCauHinhTuyen.java

    /**
     * Logic tạo chuyến tàu cho 1 ngày cụ thể
     * SỬA ĐỔI: Nhận tham số là Object (Tuyen, Tau) thay vì String
     */
    private int taoLichTrinhNgay(Tuyen tuyenObj, entity.Tau tauObj, LocalDate ngayKhoiHanh, LocalTime gioBatDau, List<GaTrongTuyen> danhSachGa) throws SQLException {
        int soLuongTauDuocTao = 0;

        // Map: Mã Ga -> Thời điểm Tàu có mặt tại đó
        Map<String, LocalDateTime> masterSchedule = new HashMap<>();
        LocalDateTime thoiDiemHienTai = LocalDateTime.of(ngayKhoiHanh, gioBatDau);

        // 1. Tính toán Master Schedule
        masterSchedule.put(danhSachGa.get(0).getMaGa(), thoiDiemHienTai);

        for (int i = 0; i < danhSachGa.size() - 1; i++) {
            GaTrongTuyen gaHienTai = danhSachGa.get(i);
            GaTrongTuyen gaKeTiep = danhSachGa.get(i + 1);

            int tgChayPhut = gaHienTai.getThoiGianDiDenGaTiepTheo();
            thoiDiemHienTai = thoiDiemHienTai.plusMinutes(tgChayPhut);
            masterSchedule.put(gaKeTiep.getMaGa() + "_DEN", thoiDiemHienTai);

            if (i + 1 < danhSachGa.size() - 1) {
                int tgDungPhut = gaKeTiep.getThoiGianDung();
                thoiDiemHienTai = thoiDiemHienTai.plusMinutes(tgDungPhut);
                masterSchedule.put(gaKeTiep.getMaGa(), thoiDiemHienTai);
            } else {
                masterSchedule.put(gaKeTiep.getMaGa(), thoiDiemHienTai);
            }
        }

        // 2. Tạo các cặp Chuyến Tàu
        for (int i = 0; i < danhSachGa.size(); i++) {
            for (int j = i + 1; j < danhSachGa.size(); j++) {
                GaTrongTuyen gttDi = danhSachGa.get(i);
                GaTrongTuyen gttDen = danhSachGa.get(j);

                LocalDateTime thoiDiemDi = masterSchedule.get(gttDi.getMaGa());

                LocalDateTime thoiDiemDen;
                if (j == danhSachGa.size() - 1) {
                    thoiDiemDen = masterSchedule.get(gttDen.getMaGa());
                } else {
                    thoiDiemDen = masterSchedule.get(gttDen.getMaGa() + "_DEN");
                }

                // Tạo Mã Chuyến Tàu
                String maChuyenTau = String.format("%s_%s_%s%s",
                        tuyenObj.getMaTuyen(),
                        thoiDiemDi.format(DateTimeFormatter.ofPattern("yyMMdd")),
                        gttDi.getMaGa(), gttDen.getMaGa());

                Ga gaDi = new Ga(gttDi.getMaGa());
                Ga gaDen = new Ga(gttDen.getMaGa());

                //lấy mã nhân viên
                NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
                if (nv == null) {
                    hienThiThongBaoLoi("Lỗi phiên làm việc: Không tìm thấy thông tin Nhân viên đăng nhập.");
                }


                // [QUAN TRỌNG] Tạo Entity ChuyenTau với đầy đủ đối tượng Tuyen và Tau
                ChuyenTau ct = new ChuyenTau(
                        maChuyenTau,
                        tuyenObj,           // <-- GÁN TUYẾN (Không được null)
                        tauObj.getSoHieu(), // Mã tàu (String)
                        thoiDiemDi.toLocalDate(),
                        thoiDiemDi.toLocalTime(),
                        gaDi,
                        gaDen,
                        tauObj,             // <-- GÁN TÀU (Không được null)
                        thoiDiemDen.toLocalDate(),
                        thoiDiemDen.toLocalTime(),
                        nv,                 // Nhân viên tạo
                        TrangThaiChuyenTau.DANG_CHO // Dùng Enum đúng
                );

                try {
                    chuyenTauDao.themChuyenTauNangCao(ct);
                    soLuongTauDuocTao++;
                } catch (SQLException ex) {
                    System.err.println("Lỗi thêm: " + ex.getMessage());
                }
            }
        }
        return soLuongTauDuocTao;
    }
    // =========================================================================
    // CÁC HÀM HỖ TRỢ & DAO (LOGIC CŨ)
    // =========================================================================

    private void hienThiChiTietTuyen() {
        int selectedRow = tbTuyen.getSelectedRow();
        if (selectedRow != -1) {
            String maTuyen = (String) modelTuyen.getValueAt(selectedRow, 0);
            String tenTuyen = (String) modelTuyen.getValueAt(selectedRow, 1);
            String gaDauMa = (String) modelTuyen.getValueAt(selectedRow, 2);
            String gaCuoiMa = (String) modelTuyen.getValueAt(selectedRow, 3);

            txtMaTuyen.setText(maTuyen);
            txtTenTuyen.setText(tenTuyen);
            txtMaTuyen.setEditable(false);

            chonGaTrongComboBox(cbGaDau, gaDauMa);
            chonGaTrongComboBox(cbGaCuoi, gaCuoiMa);

            taiDuLieuGaTrongTuyen(maTuyen);
            xoaTrangFormGa();
        }
    }

    private void taiDuLieuGaTrongTuyen(String maTuyen) {
        modelGa.setRowCount(0);
        try {
            List<GaTrongTuyen> danhSachGa = gaTrongTuyenDao.layGaTrongTuyenTheoMa(maTuyen);
            for (GaTrongTuyen g : danhSachGa) {
                modelGa.addRow(new Object[]{
                        g.getMaGa(),
                        g.getThuTuGa(),
                        g.getKhoangCachTichLuy(),
                        g.getThoiGianDiDenGaTiepTheo(),
                        g.getThoiGianDung()
                });
            }
        } catch (SQLException e) {
            hienThiThongBaoLoi("Lỗi tải dữ liệu Ga trong Tuyến: " + e.getMessage());
        }
    }


    private void dienThongTinGaVaoForm() {
        int selectedRow = tbGaTrongTuyen.getSelectedRow();
        if (selectedRow != -1) {
            String maGa = (String) modelGa.getValueAt(selectedRow, 0);
            chonGaTrongComboBox(cbMaGa, maGa);
            txtThuTuGa.setText(modelGa.getValueAt(selectedRow, 1).toString());
            txtKhoangCach.setText(modelGa.getValueAt(selectedRow, 2).toString());
            txtThoiGianDi.setText(modelGa.getValueAt(selectedRow, 3).toString());
            txtThoiGianDung.setText(modelGa.getValueAt(selectedRow, 4).toString());
            cbMaGa.setEnabled(false);
        }
    }

    private void xoaTrangFormGa() {
        cbMaGa.setSelectedIndex(0);
        txtThuTuGa.setText("");
        txtKhoangCach.setText("");
        txtThoiGianDi.setText("");
        txtThoiGianDung.setText("");
        cbMaGa.setEnabled(true);
        tbGaTrongTuyen.clearSelection();
    }

    private void loadDuLieuGaDocLap() throws SQLException {
        Vector<Ga> danhSachGa = gaDao.layDanhSachGa();
        cbGaDau.removeAllItems();
        cbGaCuoi.removeAllItems();
        cbMaGa.removeAllItems();
        for (Ga ga : danhSachGa) {
            cbGaDau.addItem(ga);
            cbGaCuoi.addItem(ga);
            cbMaGa.addItem(ga);
        }
        GaRenderer renderer = new GaRenderer();
        cbGaDau.setRenderer(renderer);
        cbGaCuoi.setRenderer(renderer);
        cbMaGa.setRenderer(renderer);
    }
    private void loadDuLieuTau() {
        cbChonTau.removeAllItems();
        try {
            // Giả sử TauDao có hàm layTatCaTau() trả về List<Tau>
            // Hoặc bạn có thể dùng select * from Tau trong TauDao
            List<Tau> danhSachTau = tauDao.layTatCa();

            for (Tau t : danhSachTau) {
                // Chỉ thêm tàu đang hoạt động (tuỳ logic của bạn)
                if (!"Bảo trì".equals(t.getTrangThai())) {
                    cbChonTau.addItem(t);
                }
            }

            cbChonTau.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Tau) {
                        Tau t = (Tau) value;
                        setText(t.getSoHieu() + " (" + t.getTrangThai() + ")");
                    }
                    return this;
                }
            });

        } catch (Exception e) {
            hienThiThongBaoLoi("Không tải được danh sách tàu: " + e.getMessage());
        }
    }

    private void taiDuLieuTuyen() {
        modelTuyen.setRowCount(0);
        try {
            List<Tuyen> danhSachTuyen = tuyenDao.layTatCaTuyen();
            for (Tuyen t : danhSachTuyen) {
                modelTuyen.addRow(new Object[]{t.getMaTuyen(), t.getTenTuyen(), t.getGaDau(), t.getGaCuoi()});
            }
        } catch (SQLException e) {
            hienThiThongBaoLoi("Lỗi tải dữ liệu Tuyến: " + e.getMessage());
        }
    }

    private void themTuyen() { /* Logic thêm tuyến vào DB */ }
    private void xoaTrangFormTuyen() {
        txtMaTuyen.setText(""); txtTenTuyen.setText(""); txtMaTuyen.setEditable(true);
        modelGa.setRowCount(0);
    }

    private void themGaTrongTuyen() {
    }
    private void suaGaTrongTuyen() {  }
    private void xoaGaTrongTuyen() { }

    private void chonGaTrongComboBox(JComboBox<Ga> cb, String maGa) {
        for(int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).getMaGa().equals(maGa)) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }

    private class GaRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Ga) {
                setText(((Ga) value).getTenGa() + " (" + ((Ga) value).getMaGa() + ")");
            }
            return this;
        }
    }

    private void hienThiThongBaoLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {

        NhanVien nvTest = new NhanVien();
        nvTest.setMaNV("NVQL0001");
        nvTest.setHoTen("Nguyễn Văn Test");

        CaLamViec.getInstance().batDauCa(nvTest);
        System.out.println("Thiết lập phiên Nhân viên: " + nvTest.getMaNV());

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Test Giao Diện Quản Lý Chuyến Tàu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);

            ManHinhQuanLyChuyenTau panel = new ManHinhQuanLyChuyenTau();
            frame.add(panel);
            frame.setVisible(true);
        });
    }
}