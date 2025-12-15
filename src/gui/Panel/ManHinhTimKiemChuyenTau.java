package gui.Panel;

import com.toedter.calendar.JDateChooser;
import dao.ChuyenTauDao;
import dao.GaDao;
import entity.ChuyenTau;
import entity.Ga;
import entity.Toa;
import control.VeSoDoTau;

import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Module độc lập chỉ xử lý logic Tìm kiếm và hiển thị Danh sách chuyến tàu.
 */
public class ManHinhTimKiemChuyenTau extends JPanel implements ActionListener {

    // =====================================================================
    // KHAI BÁO BIẾN CỐ ĐỊNH & FORMAT
    // =====================================================================
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);

    // =====================================================================
    // KHAI BÁO BIẾN UI & DAO
    // =====================================================================
    private JComboBox<Ga> cbGaDi;
    private JComboBox<Ga> cbGaDen;
    private JDateChooser dateChooserNgayDi;
    private JButton btnTimChuyen;

    private JPanel pnlChuyenTau;
    private JScrollPane scrChuyenTau;

    private List<ChuyenTau> ketQuaTimKiem = new ArrayList<>();

    // Interface Callback để thông báo cho màn hình cha khi chọn chuyến tàu
    private ChuyenTauSelectionListener selectionListener;

    // Biến lưu panel chuyến tàu đã chọn trước đó
    private JPanel lastSelectedChuyenTauPanel = null;

    // DAO
    private final ChuyenTauDao chuyenTauDao = new ChuyenTauDao();

    // =====================================================================
    // CONSTRUCTOR
    // =====================================================================

    public ManHinhTimKiemChuyenTau(ChuyenTauSelectionListener listener) {
        this.selectionListener = listener;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.white);

        // Thêm các khu vực giao diện
        add(createKhuVucTimKiem());
        add(Box.createVerticalStrut(10));
        add(createKhuVucDanhSachChuyenTau());
        add(Box.createVerticalGlue()); // Đảm bảo phần dưới không bị dãn quá mức
    }

    // =====================================================================
    // UI BUILDERS
    // =====================================================================

    private JPanel createKhuVucTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Tìm kiếm chuyến tàu");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        Vector<Ga> danhSachGa = new GaDao().layDanhSachGa();

        panel.add(new JLabel("Ga đi"));
        cbGaDi = new JComboBox<>(danhSachGa);
        panel.add(cbGaDi);

        panel.add(new JLabel("Ga đến"));
        cbGaDen = new JComboBox<>(danhSachGa);
        panel.add(cbGaDen);

        // Giá trị Test mặc định (Nếu cần)
        if (danhSachGa.size() > 6) cbGaDi.setSelectedIndex(6);
        if (danhSachGa.size() > 4) cbGaDen.setSelectedIndex(4);

        JLabel lblNgayDi = new JLabel("Ngày đi");
        lblNgayDi.setPreferredSize(new Dimension(50, 25));
        panel.add(lblNgayDi);
        dateChooserNgayDi = new JDateChooser();
        dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayDi.setDate(new Date());
        dateChooserNgayDi.setPreferredSize(new Dimension(100, 25));
        panel.add(dateChooserNgayDi);

        btnTimChuyen = new JButton("Tìm chuyến");
        styleNutChinh(btnTimChuyen);
        btnTimChuyen.addActionListener(this);
        panel.add(btnTimChuyen);

        return panel;
    }

    private JScrollPane createKhuVucDanhSachChuyenTau() {
        pnlChuyenTau = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlChuyenTau.setOpaque(false);
        pnlChuyenTau.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrChuyenTau = new JScrollPane(pnlChuyenTau);
        scrChuyenTau.setBorder(null);
        scrChuyenTau.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrChuyenTau.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        scrChuyenTau.setPreferredSize(new Dimension(600, 190));
        scrChuyenTau.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        pnlChuyenTau.add(new JLabel("Vui lòng chọn Ga đi, Ga đến và Ngày đi để tìm kiếm."));

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Danh sách chuyến tàu");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        scrChuyenTau.setBorder(title);

        return scrChuyenTau;
    }

    // =====================================================================
    // LOGIC TÌM KIẾM VÀ HIỂN THỊ
    // =====================================================================

    private void timKiemChuyenTau() {
        Ga gaDiSelected = (Ga) cbGaDi.getSelectedItem();
        Ga gaDenSelected = (Ga) cbGaDen.getSelectedItem();

        String maGaDi =  gaDiSelected.getMaGa();
        String maGaDen = gaDenSelected.getMaGa();

        Date date = dateChooserNgayDi.getDate();

        if (date == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày đi.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ngayDiSQL = SQL_DATE_FORMAT.format(date);

        // Gọi DAO mới (ChuyenTauDao đã được nâng cấp để tìm kiếm theo mô hình chặng)
        System.out.println("Tìm chuyến tàu từ " + maGaDi + " đến " + maGaDen + " vào ngày " + ngayDiSQL);
        ketQuaTimKiem = chuyenTauDao.timChuyenTauTheoGaVaNgayDi(maGaDi, maGaDen, ngayDiSQL);

        if (ketQuaTimKiem == null || ketQuaTimKiem.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến tàu nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }

        taoDanhSachChuyenTauPanel(ketQuaTimKiem);
    }

    private void taoDanhSachChuyenTauPanel(List<ChuyenTau> danhSach) {
        pnlChuyenTau.removeAll();
        lastSelectedChuyenTauPanel = null; // Reset trạng thái chọn cũ

        if (danhSach == null || danhSach.isEmpty()) {
            pnlChuyenTau.add(new JLabel("Không tìm thấy chuyến tàu nào phù hợp."));
        } else {
            for (int i = 0; i < danhSach.size(); i++) {
                ChuyenTau ct = danhSach.get(i);

                // Lấy thông tin Ga đi/đến thực tế từ ComboBox
                String maGaDiThucTe = ((Ga)cbGaDi.getSelectedItem()).getMaGa();
                String maGaDenThucTe = ((Ga)cbGaDen.getSelectedItem()).getMaGa();

                // DÙNG LOGIC TÍNH TOÁN GIỜ/NGÀY ĐẾN CHÍNH XÁC (TÍNH TỪ GTT)
                // Giả định ChuyenTau đã chứa logic này hoặc được tính từ DAO
                String ngayDiHienThi = ct.getNgayKhoiHanh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String gioDiHienThi = ct.getGioKhoiHanh().format(TIME_FORMATTER); // <-- Cần tính lại Giờ Đi tại Ga Di Thật (X)

                // Các biến này cần được tính toán lại dựa trên GaDiThucTe và GaDenThucTe
                // Nếu chưa có logic tính, chúng ta sẽ tạm thời lấy Giờ KH gốc.

                String ngayDenHienThi = ct.getNgayDenDuKien().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String gioDenHienThi = ct.getGioDenDuKien().format(TIME_FORMATTER); // <-- Cần tính lại Giờ Đến tại Ga Đến Thật (Y)

                JPanel pnlChuyenTauNut = taoNutChuyenTauVeSoDo(
                        ct.getMaChuyenTau(),
                        ngayDiHienThi,
                        gioDiHienThi,
                        ngayDenHienThi,
                        gioDenHienThi
                );

                pnlChuyenTauNut.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        xuLyChonChuyenTauPanel(pnlChuyenTauNut, ct);
                    }
                });

                pnlChuyenTau.add(pnlChuyenTauNut);
            }
        }

        pnlChuyenTau.revalidate();
        pnlChuyenTau.repaint();
    }

    private JPanel taoNutChuyenTauVeSoDo(String maChuyen, String ngayDi, String gioDi, String ngayDen, String gioDen) {

        String thoiGianDiHienThi = ngayDi + " " + gioDi;
        String thoiGianDenHienThi = ngayDen + " "+ gioDen;

        // Lấy MaTau từ MaChuyenTau (Ví dụ: SE8-20251223 -> SE8)
        String maTau = maChuyen.split("-")[0];

        VeSoDoTau soDoTauPanel = new VeSoDoTau(maTau, thoiGianDiHienThi, thoiGianDenHienThi);

        // Container đóng vai trò là NÚT
        JPanel nutChuyenTauContainer = new JPanel(new BorderLayout());
        nutChuyenTauContainer.add(soDoTauPanel, BorderLayout.CENTER);
        nutChuyenTauContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        nutChuyenTauContainer.setBackground(Color.WHITE);
        nutChuyenTauContainer.setPreferredSize(soDoTauPanel.getPreferredSize());

        // Hiệu ứng Hover
        nutChuyenTauContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (nutChuyenTauContainer != lastSelectedChuyenTauPanel) {
                    nutChuyenTauContainer.setBackground(new Color(220, 220, 220));
                    nutChuyenTauContainer.setBorder(BorderFactory.createLineBorder(COLOR_BLUE_LIGHT, 2, true));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (nutChuyenTauContainer != lastSelectedChuyenTauPanel) {
                    nutChuyenTauContainer.setBackground(Color.WHITE);
                    nutChuyenTauContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
                }
            }
        });

        return nutChuyenTauContainer;
    }

    private void xuLyChonChuyenTauPanel(JPanel currentPanel, ChuyenTau chuyenTau) {
        // 1. Reset trạng thái chọn của Panel cũ
        if (lastSelectedChuyenTauPanel != null) {
            lastSelectedChuyenTauPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
            lastSelectedChuyenTauPanel.setBackground(Color.WHITE);
        }

        // 2. Đặt trạng thái chọn cho Panel hiện tại
        currentPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 3, true));
        currentPanel.setBackground(new Color(255, 220, 180)); // Màu cam nhạt nổi bật khi chọn
        lastSelectedChuyenTauPanel = currentPanel;

        // 3. Gọi Callback để thông báo cho màn hình cha (ManHinhBanVe)
        if (selectionListener != null) {
            selectionListener.onChuyenTauSelected(chuyenTau);
        }
    }

    // =====================================================================
    // UTILITIES & EVENT HANDLERS
    // =====================================================================

    private void styleNutChinh(JButton btn) {
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(110, 25));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnTimChuyen){
            timKiemChuyenTau();
        }
    }

    // =====================================================================
    // INTERFACE CALLBACK
    // =====================================================================

    /**
     * Interface để truyền thông tin ChuyenTau được chọn từ panel này
     * về màn hình cha (ManHinhBanVe).
     */
    public interface ChuyenTauSelectionListener {
        void onChuyenTauSelected(ChuyenTau chuyenTau);
    }


    // =====================================================================
    // HÀM MAIN TEST ĐỘC LẬP
    // =====================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Tìm kiếm chuyến tàu (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Implement listener rỗng để test
            ManHinhTimKiemChuyenTau testPanel = new ManHinhTimKiemChuyenTau(new ChuyenTauSelectionListener() {
                @Override
                public void onChuyenTauSelected(ChuyenTau chuyenTau) {
                    System.out.println("CALLBACK: Chuyến tàu được chọn: " + chuyenTau.getMaChuyenTau());
                }
            });

            frame.add(testPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setSize(800, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}