package gui.Panel;

import dao.ChoDatDAO;
import dao.ChuyenTauDao;
import dao.GaDao;
import dao.ToaDAO;
import entity.*;
import gui.MainFrame.BanVeDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.foreign.Linker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * ManHinhBanVe - Phiên bản đã sắp xếp thành module để quan sát.
 * Mỗi vùng (UI Builders, Data Actions, Helpers, Event Handlers, State) được
 * tách rõ ràng bằng comment tiếng Việt.
 */
public class ManHinhBanVe extends JPanel implements MouseListener, ActionListener {

    // ====================
    // MODULE: Trạng thái & dữ liệu (State)
    // ====================
    private JPanel pnlToa;
    private JPanel pnlSoDoGhe;
    private JComboBox<Ga> cbGaDi;
    private JComboBox<Ga> cbGaDen;
    private JTextField dateField;

    private JLabel lblTongSoKhach;
    private JTextField txtNguoiCaoTuoi;
    private JTextField txtNguoiLon;
    private JTextField txtTreCon;
    private JTextField txtSinhVien;

    private JTable tableChuyenTau;
    private DefaultTableModel tableModel;

    private JPanel pnlDanhSachGheDaCho;

    private Date date;
    private List<ChuyenTau> ketQua = new ArrayList<>();
    private String maChuyenTauHienTai = null;

    private JButton lastSelectedToaButton = null;
    private String maToaHienTai = null;

    // Map tạm thời: MaChoDat -> TempKhachHang
    private Map<String, TempKhachHang> danhSachKhachHang = new HashMap<>();

    // Map số lượng yêu cầu theo loại
    private Map<String, Integer> soLuongYeuCau = new HashMap<>();

    // Map toàn bộ ChoDat của toa hiện tại để tra cứu nhanh
    private Map<String, ChoDat> tatCaChoDatToaHienTai = new HashMap<>();

    // Danh sách ghế đang chọn: MaCho -> ChoDat
    private Map<String, ChoDat> danhSachGheDaChon = new LinkedHashMap<>();

    // Map nút ghế: MaCho -> JButton (để cập nhật trạng thái nút khi hủy chọn)
    private Map<String, JButton> seatButtonsMap = new HashMap<>();

    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private JScrollPane thongTinKhachScrollPane;

    // DAO
    private ChoDatDAO choDatDao = new ChoDatDAO();

    // Mã loại vé (hằng)
    private static final String MA_VE_NL = "VT01";
    private static final String MA_VE_TE = "VT02";
    private static final String MA_VE_NCT = "VT03";
    private static final String MA_VE_SV = "VT04";
    private JButton cancelButton, nextButton;
    private JButton btnTimChuyen;
    private Color contentPanel;

    // ====================
    // MODULE: Constructor + Layout chính
    // ====================
    public ManHinhBanVe() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245));

        add(taoPanelTieuDe(), BorderLayout.NORTH);
        add(taoNoiDungChinh(), BorderLayout.CENTER);
    }

    // ====================
    // MODULE: UI BUILDERS (các phương thức tạo vùng giao diện)
    // ====================
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 50));
        panel.setBorder(new EmptyBorder(0, 10, 0, 10));

        JLabel titleLabel = new JLabel("Bán vé");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        panel.add(titleLabel, BorderLayout.WEST);

        JLabel idLabel = new JLabel("ID: QL200001");
        panel.add(idLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel taoNoiDungChinh() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 0));
        mainPanel.setBackground(Color.WHITE);

        JPanel contentLeftPanel = new JPanel();
        contentLeftPanel.setLayout(new BoxLayout(contentLeftPanel, BoxLayout.Y_AXIS));
        contentLeftPanel.setOpaque(false);
        contentLeftPanel.setBorder(new EmptyBorder(0, 0, 0, 5));

        // Các khu vực con (được tách modular)
        contentLeftPanel.add(createKhuVucTimKiem());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucDanhSachChuyenTau());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucChonLoaiKhach());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucChonViTriGhe());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucTongTien());
        contentLeftPanel.add(Box.createVerticalGlue());

        JScrollPane leftScrollPane = new JScrollPane(contentLeftPanel);
        leftScrollPane.setBorder(null);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(true);
        leftContainer.add(leftScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = createKhuVucThongTinKhach();
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftContainer, rightPanel);
        split.setResizeWeight(0.75);
        split.setOneTouchExpandable(true);
        split.setDividerSize(6);

        mainPanel.add(split, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createKhuVucTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
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

        if (danhSachGa.size() > 1) {
            cbGaDen.setSelectedIndex(3);
        }

        panel.add(new JLabel("Ngày đi"));
        dateField = new JTextField("10/11/2025", 8);
        dateField.setPreferredSize(new Dimension(80, 25));
        panel.add(dateField);

        btnTimChuyen = new JButton("Tìm chuyến");
        styleNutChinh(btnTimChuyen);
        btnTimChuyen.addActionListener(this);
        panel.add(btnTimChuyen);

        datCanhKhuVuc(panel);
        return panel;
    }

    private JScrollPane createKhuVucDanhSachChuyenTau() {
        String[] columnNames = {"Tên Chuyến", "Ngày đi", "Giờ đi"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tableChuyenTau = new JTable(tableModel);
        tableChuyenTau.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(tableChuyenTau);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        scrollPane.setMaximumSize(new Dimension(1200, 100));
        return scrollPane;
    }

    private JPanel createKhuVucChonLoaiKhach() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Chọn toa và ghế");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        topRow.add(new JLabel("Tổng số khách:"));
        lblTongSoKhach = new JLabel("0");
        lblTongSoKhach.setFont(lblTongSoKhach.getFont().deriveFont(Font.BOLD, 14f));
        lblTongSoKhach.setForeground(new Color(220, 53, 69));
        topRow.add(lblTongSoKhach);

        JPanel loaiKhachSpinBoxPanel = new JPanel();
        loaiKhachSpinBoxPanel.setLayout(new BoxLayout(loaiKhachSpinBoxPanel, BoxLayout.Y_AXIS));
        loaiKhachSpinBoxPanel.setOpaque(false);
        loaiKhachSpinBoxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNguoiLon = new JTextField(1);
        txtTreCon = new JTextField(1);
        txtNguoiCaoTuoi = new JTextField(1);
        txtSinhVien = new JTextField(1);

        loaiKhachSpinBoxPanel.add(createSpinBoxPanel("Người lớn (11-59 tuổi)", "1", null, txtNguoiLon));
        loaiKhachSpinBoxPanel.add(createSpinBoxPanel("Trẻ em (6-10 tuổi)", "0", "-25%", txtTreCon));
        loaiKhachSpinBoxPanel.add(createSpinBoxPanel("Người cao tuổi (> 60 tuổi)", "0", "-15%", txtNguoiCaoTuoi));
        loaiKhachSpinBoxPanel.add(createSpinBoxPanel("Sinh viên (Thẻ SV)", "0", "-10%", txtSinhVien));

        topRow.add(Box.createHorizontalStrut(20));
        topRow.add(loaiKhachSpinBoxPanel);
        panel.add(topRow);

        pnlToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        pnlToa.setOpaque(false);
        pnlToa.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlToa.add(new JLabel("Chọn toa:"));
        panel.add(pnlToa);
        panel.setMaximumSize(new Dimension(1200, 100));

        capNhatSoLuongYeuCau();
        datCanhKhuVuc(panel);
        return panel;
    }

    private JPanel createKhuVucChonViTriGhe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Chọn vị trí của ghế");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        pnlSoDoGhe = new JPanel();
        pnlSoDoGhe.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane soDoScrollPane = new JScrollPane(pnlSoDoGhe);
        soDoScrollPane.setBorder(BorderFactory.createEmptyBorder());
        soDoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        soDoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        soDoScrollPane.setPreferredSize(new Dimension(100, 150));
        soDoScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        panel.add(soDoScrollPane);
        panel.add(Box.createVerticalStrut(10));

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setOpaque(false);
        legendPanel.add(taoMucChuGiai(Color.LIGHT_GRAY, "Chỗ trống"));
        legendPanel.add(taoMucChuGiai(Color.BLACK, "Đã đặt"));
        legendPanel.add(taoMucChuGiai(new Color(0, 123, 255), "Đang chọn"));
        legendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(legendPanel);
        panel.add(Box.createVerticalStrut(5));

        pnlDanhSachGheDaCho = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlDanhSachGheDaCho.setOpaque(false);
        pnlDanhSachGheDaCho.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlDanhSachGheDaCho.add(new JLabel("Ghế đã chọn:"));
        capNhatDanhSachGheDaChonUI();
        panel.add(pnlDanhSachGheDaCho);

        datCanhKhuVuc(panel);
        return panel;
    }

    private JPanel createKhuVucTongTien() {
        JPanel fullSummary = new JPanel(new BorderLayout());
        fullSummary.setBackground(Color.white);
        fullSummary.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        legendPanel.setOpaque(false);
        legendPanel.add(taoMucChuGiai(Color.LIGHT_GRAY, "Chỗ trống"));
        legendPanel.add(taoMucChuGiai(Color.BLACK, "Không trống"));
        legendPanel.add(taoMucChuGiai(new Color(0, 123, 255), "Đang chọn"));
        fullSummary.add(legendPanel, BorderLayout.WEST);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(new JLabel("Đã chọn: X/Y"));

        JLabel totalLabel = new JLabel("Tổng tiền vé: 0 VNĐ");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        totalLabel.setForeground(new Color(255, 165, 0));
        summaryPanel.add(totalLabel);

        fullSummary.add(summaryPanel, BorderLayout.EAST);
        datCanhKhuVuc(fullSummary);
        return fullSummary;
    }

    private JPanel createKhuVucThongTinKhach() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Thông tin khách hàng"));

        JPanel infoScrollPanel = new JPanel();
        infoScrollPanel.setLayout(new BoxLayout(infoScrollPanel, BoxLayout.Y_AXIS));
        infoScrollPanel.setOpaque(false);
        infoScrollPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        infoScrollPanel.add(new JLabel("Chọn ghế để thêm thông tin."));
        infoScrollPanel.add(Box.createVerticalGlue());
        infoScrollPanel.setPreferredSize(new Dimension(400, 300));

        thongTinKhachScrollPane = new JScrollPane(infoScrollPanel);
        thongTinKhachScrollPane.setBorder(null);
        thongTinKhachScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(thongTinKhachScrollPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::capNhatThongTinKhachUI);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        cancelButton = new JButton("< Hủy");
        cancelButton.setPreferredSize(new Dimension(80, 40));
        cancelButton.setFont(cancelButton.getFont().deriveFont(Font.BOLD, 14f));
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);

        nextButton = new JButton("Tiếp theo >");
        nextButton.setPreferredSize(new Dimension(120, 40));
        nextButton.setFont(nextButton.getFont().deriveFont(Font.BOLD, 14f));
        nextButton.setBackground(new Color(0, 123, 255));
        nextButton.setForeground(Color.WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(nextButton);
        //Đăng ký sự kiện
        cancelButton.addActionListener(this);
        nextButton.addActionListener(this);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Add these helper methods and the listener attachments into `ManHinhBanVe` class.

    // --- Helper: reset state/UI ---
    private void resetAllData() {
        danhSachGheDaChon.clear();
        danhSachKhachHang.clear();
        tatCaChoDatToaHienTai.clear();
        seatButtonsMap.clear();

        maChuyenTauHienTai = null;
        maToaHienTai = null;
        lastSelectedToaButton = null;

        SwingUtilities.invokeLater(() -> {
            capNhatDanhSachGheDaChonUI();

            if (pnlToa != null) {
                pnlToa.removeAll();
                pnlToa.add(new JLabel("Chọn toa:"));
                pnlToa.revalidate();
                pnlToa.repaint();
            }

            if (pnlSoDoGhe != null) {
                pnlSoDoGhe.removeAll();
                pnlSoDoGhe.add(new JLabel("Chưa có sơ đồ ghế."));
                pnlSoDoGhe.revalidate();
                pnlSoDoGhe.repaint();
            }

            capNhatThongTinKhachUI();
        });
    }

    // --- Helper: switch main frame to another panel ---
    private void switchToPanel(Component panel) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof JFrame) {
            JFrame frame = (JFrame) w;
            frame.getContentPane().removeAll();
            frame.getContentPane().add(panel, BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể chuyển màn hình: top-level window không phải JFrame.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Small helper to show instantiation error ---
    private void showInstantiationError(String className, Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Không thể mở " + className + ": " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // ====================
    // MODULE: UI Helper Methods (style / small components)
    // ====================
    private void datCanhKhuVuc(JPanel panel) {
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
    }

    private void styleNutChinh(JButton btn) {
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(100, 25));
    }

    private JButton taoNutToa(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(String.format("<html><center>%s</center></html>", text.replace("\n", "<br>")));
        btn.setPreferredSize(new Dimension(110, 60));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
        btn.setBackground(bgColor != null ? bgColor : Color.LIGHT_GRAY);
        btn.setForeground(fgColor != null ? fgColor : Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel taoMucChuGiai(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        JLabel square = new JLabel();
        square.setPreferredSize(new Dimension(15, 15));
        square.setOpaque(true);
        square.setBackground(color);
        square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(square);
        panel.add(new JLabel(text));
        return panel;
    }

    // ====================
    // MODULE: SpinBox helpers
    // ====================
    private JPanel createSpinBoxPanel(String labelText, String initialValue, String discountText, JTextField targetField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel labelDiscountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelDiscountPanel.setOpaque(false);

        JLabel mainLabel = new JLabel(labelText);
        mainLabel.setFont(mainLabel.getFont().deriveFont(Font.BOLD, 14f));
        labelDiscountPanel.add(mainLabel);

        if (discountText != null) {
            JLabel discountLabel = new JLabel(discountText);
            discountLabel.setForeground(Color.RED);
            discountLabel.setFont(discountLabel.getFont().deriveFont(Font.BOLD, 12f));
            labelDiscountPanel.add(discountLabel);
        }
        panel.add(labelDiscountPanel, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        controlPanel.setOpaque(false);

        JButton btnMinus = new JButton("−");
        btnMinus.setPreferredSize(new Dimension(30, 30));
        btnMinus.setMargin(new Insets(0, 0, 0, 0));
        styleSpinButton(btnMinus);

        targetField.setText(initialValue);
        targetField.setHorizontalAlignment(JTextField.CENTER);
        targetField.setPreferredSize(new Dimension(40, 30));
        targetField.setMaximumSize(new Dimension(40, 30));
        targetField.setEditable(false);

        JButton btnPlus = new JButton("+");
        btnPlus.setPreferredSize(new Dimension(30, 30));
        btnPlus.setMargin(new Insets(0, 0, 0, 0));
        styleSpinButton(btnPlus);

        btnPlus.addActionListener(e -> changeQuantity(targetField, 1));
        btnMinus.addActionListener(e -> changeQuantity(targetField, -1));

        controlPanel.add(btnMinus);
        controlPanel.add(targetField);
        controlPanel.add(btnPlus);

        panel.add(controlPanel, BorderLayout.EAST);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return panel;
    }

    private void styleSpinButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    private int parseTextFieldToInt(JTextField field) {
        try {
            if (field.getText().trim().isEmpty()) return 0;
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void changeQuantity(JTextField field, int delta) {
        int currentValue = parseTextFieldToInt(field);
        int newValue = currentValue + delta;
        if (newValue < 0) newValue = 0;
        field.setText(String.valueOf(newValue));
        capNhatSoLuongYeuCau();
    }

    // ====================
    // MODULE: Logic tính toán & cập nhật trạng thái
    // ====================
    private void capNhatSoLuongYeuCau() {
        int nguoiCaoTuoi = parseTextFieldToInt(txtNguoiCaoTuoi);
        int nguoiLon = parseTextFieldToInt(txtNguoiLon);
        int treCon = parseTextFieldToInt(txtTreCon);
        int sinhVien = parseTextFieldToInt(txtSinhVien);

        int tongSoKhachMoi = nguoiCaoTuoi + nguoiLon + treCon + sinhVien;

        if (lblTongSoKhach != null) {
            lblTongSoKhach.setText(String.valueOf(tongSoKhachMoi));
        }

        soLuongYeuCau.clear();
        soLuongYeuCau.put("NguoiCaoTuoi", nguoiCaoTuoi);
        soLuongYeuCau.put("NguoiLon", nguoiLon);
        soLuongYeuCau.put("TreCon", treCon);
        soLuongYeuCau.put("SinhVien", sinhVien);

        if (danhSachGheDaChon.size() > tongSoKhachMoi) {
            JOptionPane.showMessageDialog(this,
                    "Số lượng ghế đã chọn (" + danhSachGheDaChon.size() + ") vượt quá Tổng số khách mới (" + tongSoKhachMoi + "). Vui lòng hủy chọn bớt.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String[] getLoaiVeOptions() {
        return new String[] {
                getTenLoaiVeHienThi(MA_VE_NL),
                getTenLoaiVeHienThi(MA_VE_TE),
                getTenLoaiVeHienThi(MA_VE_NCT),
                getTenLoaiVeHienThi(MA_VE_SV)
        };
    }

    private String getTenLoaiVeHienThi(String maLoaiVe) {
        return switch (maLoaiVe) {
            case "VT01" -> "Người lớn (VT01)";
            case "VT02" -> "Trẻ em (VT02)";
            case "VT03" -> "Người cao tuổi (VT03)";
            case "VT04" -> "Sinh viên (VT04)";
            default -> "Người lớn (VT01)";
        };
    }

    private String getMaLoaiVeFromHienThi(String tenHienThi) {
        if (tenHienThi.contains("(VT01)")) return "VT01";
        if (tenHienThi.contains("(VT02)")) return "VT02";
        if (tenHienThi.contains("(VT03)")) return "VT03";
        if (tenHienThi.contains("(VT04)")) return "VT04";
        return "VT01";
    }

    private Vector<String> taoDanhSachLoaiVeUuTien() {
        Vector<String> dsMaVe = new Vector<>();
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_NCT, soLuongYeuCau.getOrDefault("NguoiCaoTuoi", 0));
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_TE, soLuongYeuCau.getOrDefault("TreCon", 0));
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_SV, soLuongYeuCau.getOrDefault("SinhVien", 0));
        themLoaiVeVaoDanhSach(dsMaVe, MA_VE_NL, soLuongYeuCau.getOrDefault("NguoiLon", 0));
        return dsMaVe;
    }

    private void themLoaiVeVaoDanhSach(Vector<String> ds, String maVe, int soLuong) {
        for (int i = 0; i < soLuong; i++) ds.add(maVe);
    }

    // ====================
    // MODULE: Data / Actions (tìm chuyến, nạp dữ liệu)
    // ====================
    private void timKiemChuyenTau() {
        Ga gaDiSelected = (Ga) cbGaDi.getSelectedItem();
        Ga gaDenSelected = (Ga) cbGaDen.getSelectedItem();

        if (gaDiSelected == null || gaDenSelected == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn ga đi và ga đến.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (gaDiSelected.getMaGa().equals(gaDenSelected.getMaGa())) {
            JOptionPane.showMessageDialog(null, "Ga đi và Ga đến không được trùng nhau.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maGaDi = gaDiSelected.getMaGa();
        String maGaDen = gaDenSelected.getMaGa();
        String ngayDiString = dateField.getText();
        String ngayDiSQL;
        try {
            date = INPUT_DATE_FORMAT.parse(ngayDiString);
            ngayDiSQL = SQL_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Ngày đi không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ChuyenTauDao dao = new ChuyenTauDao();
        ketQua = dao.timChuyenTau(maGaDi, maGaDen, ngayDiSQL);

        if (ketQua == null || ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy chuyến tàu nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }

        napDuLieuLenBang(ketQua);
    }

    private void napDuLieuLenBang(List<ChuyenTau> danhSach) {
        DefaultTableModel model = (DefaultTableModel) tableChuyenTau.getModel();
        model.setRowCount(0);
        if (danhSach == null) return;
        for (ChuyenTau ct : danhSach) {
            Object[] rowData = {ct.getMaChuyenTau(), ct.getNgayKhoiHanh(), ct.getGioKhoiHanh()};
            model.addRow(rowData);
        }
    }

    private void hienThiDanhSachToaTau(String maTau) {
        List<Toa> danhSachToa = new ArrayList<>();
        try {
            ToaDAO toaTauDAO = new ToaDAO();
            danhSachToa = toaTauDAO.getDanhSachToaByMaTau(maTau);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách toa tàu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        napNutToa(danhSachToa);
    }

    public void napNutToa(List<Toa> danhSachToa) {
        pnlToa.removeAll();
        pnlToa.add(new JLabel("Chọn toa:"));

        for (Toa toa : danhSachToa) {
            String soThuTuToa = laySoThuTuToa(toa.getMaToa());
            String text = "Toa " + soThuTuToa + "\n" + toa.getLoaiToa();
            JButton btnToa = taoNutToa(text, null, null);
            btnToa.addActionListener(e -> xuLyChonToa(btnToa, toa.getMaToa()));
            pnlToa.add(btnToa);
        }
        pnlToa.revalidate();
        pnlToa.repaint();
    }

    // ====================
    // MODULE: XỬ LÝ CHỌN TOA / VẼ SƠ ĐỒ GHẾ
    // ====================
    private void xuLyChonToa(JButton currentButton, String maToa) {
        maToaHienTai = maToa;

        // Đổi màu nút
        if (lastSelectedToaButton != null) {
            lastSelectedToaButton.setBackground(Color.LIGHT_GRAY);
            lastSelectedToaButton.setForeground(Color.BLACK);
        }
        currentButton.setBackground(new Color(0, 123, 255));
        currentButton.setForeground(Color.WHITE);
        lastSelectedToaButton = currentButton;

        if (this.maChuyenTauHienTai == null || this.maChuyenTauHienTai.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn chuyến tàu trước.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Chưa chọn chuyến tàu."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
            return;
        }

        //
        // Giải thích: ở đây không gọi danhSachGheDaChon.clear() để giữ
        // trạng thái ghế người dùng đã chọn khi chuyển giữa các toa
        // của cùng một chuyến (nếu đó là hành vi mong muốn).
        // Nếu muốn reset khi đổi toa, hãy mở comment để clear.
        //
        System.out.println("Đã chọn Toa: " + maToa + ". Tiến hành tải sơ đồ ghế." );

        List<ChoDat> danhSachChoDat = choDatDao.getDanhSachChoDatByMaToaVaTrangThai(
                maToa,
                maChuyenTauHienTai
        );

        if (danhSachChoDat.isEmpty()) {
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Toa này chưa có dữ liệu chỗ đặt."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
        } else {
            veSoDoGhe(danhSachChoDat);
        }
    }

    private String laySoThuTuToa(String maToa) {
        if (maToa != null && maToa.contains("-")) {
            return maToa.substring(maToa.lastIndexOf('-') + 1);
        }
        return maToa;
    }

    private static final Dimension SQUARE_SEAT_SIZE = new Dimension(60, 30);

    private void veSoDoGhe(List<ChoDat> danhSachChoDat) {
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();
        tatCaChoDatToaHienTai.clear();

        pnlSoDoGhe.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlSoDoGhe.setOpaque(true);
        pnlSoDoGhe.setBackground(Color.WHITE);
        pnlSoDoGhe.setBorder(new EmptyBorder(10, 10, 10, 10));

        int rows = 4;
        int columns = (int) Math.ceil((double) danhSachChoDat.size() / rows);
        JPanel gridContainer = new JPanel(new GridLayout(rows, columns, 5, 5));
        gridContainer.setOpaque(false);

        for (ChoDat cho : danhSachChoDat) {
            JButton btnCho = new JButton(cho.getSoCho());
            btnCho.setPreferredSize(SQUARE_SEAT_SIZE);
            btnCho.setMinimumSize(SQUARE_SEAT_SIZE);
            btnCho.setMaximumSize(SQUARE_SEAT_SIZE);
            btnCho.setFont(btnCho.getFont().deriveFont(Font.BOLD, 12f));

            boolean isSelected = danhSachGheDaChon.containsKey(cho.getMaCho());
            boolean isBooked = cho.isDaDat();
            tatCaChoDatToaHienTai.put(cho.getMaCho(), cho);

            if (isSelected) {
                btnCho.setBackground(new Color(0, 123, 255));
                btnCho.setForeground(Color.WHITE);
                btnCho.setEnabled(true);
                btnCho.setToolTipText("Ghế đang được chọn");
            } else if (isBooked) {
                btnCho.setBackground(Color.BLACK);
                btnCho.setForeground(Color.WHITE);
                btnCho.setEnabled(false);
                btnCho.setToolTipText("Ghế đã được bán");
            } else {
                btnCho.setBackground(Color.LIGHT_GRAY);
                btnCho.setForeground(Color.BLACK);
                btnCho.setEnabled(true);
            }

            if (btnCho.isEnabled()) {
                btnCho.addActionListener(e -> xuLyChonGhe(btnCho, cho));
            }

            seatButtonsMap.put(cho.getMaCho(), btnCho);
            gridContainer.add(btnCho);
        }

        pnlSoDoGhe.add(gridContainer);
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }

    // ====================
    // MODULE: Chọn/Hủy ghế, cập nhật UI danh sách & form khách
    // ====================
    private void xuLyChonGhe(JButton btnCho, ChoDat cho) {
        String maCho = cho.getMaCho();

        int tongSoKhachYeuCau = parseTextFieldToInt(txtNguoiCaoTuoi) +
                parseTextFieldToInt(txtNguoiLon) +
                parseTextFieldToInt(txtTreCon) +
                parseTextFieldToInt(txtSinhVien);

        if (danhSachGheDaChon.containsKey(maCho)) {
            danhSachGheDaChon.remove(maCho);
            btnCho.setBackground(Color.LIGHT_GRAY);
            btnCho.setForeground(Color.BLACK);
        } else {
            if (danhSachGheDaChon.size() >= tongSoKhachYeuCau) {
                JOptionPane.showMessageDialog(this,
                        "Đã chọn đủ " + tongSoKhachYeuCau + " ghế. Vui lòng thay đổi số lượng khách hoặc hủy chọn ghế cũ trước.",
                        "Giới hạn chọn", JOptionPane.WARNING_MESSAGE);
                return;
            }
            danhSachGheDaChon.put(maCho, cho);
            btnCho.setBackground(new Color(0, 123, 255));
            btnCho.setForeground(Color.WHITE);

            TempKhachHang tempKhach = new TempKhachHang(cho);
            danhSachKhachHang.put(maCho, tempKhach);
            // LƯU Ý: đã thêm vào cả danhSachGheDaChon trước đó
        }

        capNhatDanhSachGheDaChonUI();
        capNhatThongTinKhachUI();
    }

    private void xuLyHuyChonGhe(String maCho) {
        danhSachGheDaChon.remove(maCho);
        JButton btnCho = seatButtonsMap.get(maCho);
        if (btnCho != null) {
            btnCho.setBackground(Color.LIGHT_GRAY);
            btnCho.setForeground(Color.BLACK);
        }
        capNhatDanhSachGheDaChonUI();
        capNhatThongTinKhachUI();
    }

    private void capNhatDanhSachGheDaChonUI() {
        pnlDanhSachGheDaCho.removeAll();
        pnlDanhSachGheDaCho.add(new JLabel("Ghế đã chọn (" + danhSachGheDaChon.size() + "):"));
        for (ChoDat cho : danhSachGheDaChon.values()) {
            String soThuTuToa = laySoThuTuToa(cho.getMaToa());
            String soCho = cho.getSoCho();
            JButton btnGhe = taoNutGheDaChon(cho.getMaCho(), soThuTuToa, soCho);
            pnlDanhSachGheDaCho.add(btnGhe);
        }
        pnlDanhSachGheDaCho.revalidate();
        pnlDanhSachGheDaCho.repaint();
    }

    private JButton taoNutGheDaChon(String maGhe, String soThuTuToa, String soCho) {
        String text = "Chỗ " + soCho + ", Toa " + soThuTuToa;
        JButton btn = new JButton(text);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 25));
        btn.addActionListener(e -> xuLyHuyChonGhe(maGhe));
        return btn;
    }

    private JPanel createKhachPanel(TempKhachHang tempKhach) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String soCho = tempKhach.choDat.getSoCho();
        String soThuTuToa = laySoThuTuToa(tempKhach.choDat.getMaToa());
        String loaiKhachHienThi = getTenLoaiVeHienThi(tempKhach.maLoaiVe);
        String gia = "Giá vé sẽ được tính";

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setOpaque(false);
        JLabel maGheLabel = new JLabel("Ghế: " + soCho + " / Toa: " + soThuTuToa);
        maGheLabel.setFont(maGheLabel.getFont().deriveFont(Font.BOLD));
        leftHeader.add(maGheLabel);

        JComboBox<String> cbLoaiKhach = new JComboBox<>(getLoaiVeOptions());
        cbLoaiKhach.setSelectedItem(loaiKhachHienThi);
        cbLoaiKhach.setPreferredSize(new Dimension(120, 25));
        cbLoaiKhach.setMaximumSize(new Dimension(120, 25));
        cbLoaiKhach.addActionListener(e -> {
            String maMoi = getMaLoaiVeFromHienThi((String) cbLoaiKhach.getSelectedItem());
            tempKhach.maLoaiVe = maMoi;
        });
        leftHeader.add(cbLoaiKhach);
        headerRow.add(leftHeader, BorderLayout.WEST);

        JLabel giaLabel = new JLabel(gia);
        giaLabel.setForeground(Color.BLUE);
        headerRow.add(giaLabel, BorderLayout.EAST);

        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(headerRow);

        JPanel detailGrid = new JPanel(new GridLayout(2, 4, 10, 5));
        detailGrid.setOpaque(false);
        detailGrid.setBorder(new EmptyBorder(5, 0, 5, 0));

        detailGrid.add(new JLabel("Họ và tên*"));
        JTextField hoTenField = new JTextField(tempKhach.hoTen, 10);
        detailGrid.add(hoTenField);
        detailGrid.add(new JLabel("Tuổi"));
        JTextField tuoiField = new JTextField(String.valueOf(tempKhach.tuoi > 0 ? tempKhach.tuoi : ""), 3);
        detailGrid.add(tuoiField);

        detailGrid.add(new JLabel("Số điện thoại"));
        JTextField sdtField = new JTextField(tempKhach.sdt, 10);
        detailGrid.add(sdtField);
        detailGrid.add(new JLabel("CCCD*"));
        JTextField cccdField = new JTextField(tempKhach.cccd, 10);
        detailGrid.add(cccdField);

        panel.add(detailGrid);

        hoTenField.addFocusListener(new java.awt.event.FocusAdapter() { public void focusLost(java.awt.event.FocusEvent evt) { tempKhach.hoTen = hoTenField.getText(); }});
        cccdField.addFocusListener(new java.awt.event.FocusAdapter() { public void focusLost(java.awt.event.FocusEvent evt) { tempKhach.cccd = cccdField.getText(); }});
        sdtField.addFocusListener(new java.awt.event.FocusAdapter() { public void focusLost(java.awt.event.FocusEvent evt) { tempKhach.sdt = sdtField.getText(); }});
        tuoiField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                try {
                    tempKhach.tuoi = Integer.parseInt(tuoiField.getText().trim());
                } catch (Exception e) {
                    tempKhach.tuoi = 0;
                }
            }
        });

        hoTenField.setMaximumSize(hoTenField.getPreferredSize());
        tuoiField.setMaximumSize(tuoiField.getPreferredSize());
        sdtField.setMaximumSize(sdtField.getPreferredSize());
        cccdField.setMaximumSize(cccdField.getPreferredSize());

        return panel;
    }

    private void capNhatThongTinKhachUI() {
        if (thongTinKhachScrollPane == null) {
            System.out.println("Lỗi: thongTinKhachScrollPane chưa được khởi tạo.");
            return;
        }
        JPanel infoScrollPanel = (JPanel) thongTinKhachScrollPane.getViewport().getView();
        infoScrollPanel.removeAll();

        List<TempKhachHang> danhSachTemp = new ArrayList<>(danhSachKhachHang.values());

        if (danhSachTemp.isEmpty()) {
            infoScrollPanel.add(new JLabel("Chưa có ghế nào được chọn."));
            infoScrollPanel.add(Box.createVerticalGlue());
            infoScrollPanel.revalidate();
            infoScrollPanel.repaint();
            return;
        }

        Vector<String> dsMaVeUuTien = taoDanhSachLoaiVeUuTien();
        int soFormHienThi = danhSachTemp.size();

        for (int i = 0; i < soFormHienThi; i++) {
            TempKhachHang tempKhach = danhSachTemp.get(i);
            if (i < dsMaVeUuTien.size()) {
                tempKhach.maLoaiVe = dsMaVeUuTien.get(i);
            }
            JPanel khachPanel = createKhachPanel(tempKhach);
            infoScrollPanel.add(khachPanel);
        }

        infoScrollPanel.add(Box.createVerticalGlue());
        infoScrollPanel.revalidate();
        infoScrollPanel.repaint();
    }

    // ====================
    // MODULE: Event handlers (Mouse)
    // ====================
    @Override
    public void mouseClicked(MouseEvent e) {
        int selectedRow = tableChuyenTau.getSelectedRow();
        if (selectedRow != -1 && ketQua != null && selectedRow < ketQua.size()) {
            String maTau = ketQua.get(selectedRow).getMaTau();
            String maChuyenTauMoi = ketQua.get(selectedRow).getMaChuyenTau();

            // Nếu chuyển sang chuyến tàu khác -> reset toàn bộ trạng thái liên quan
            if (!maChuyenTauMoi.equals(maChuyenTauHienTai)) {
                danhSachGheDaChon.clear();
                danhSachKhachHang.clear();

                lastSelectedToaButton = null;
                maToaHienTai = null;
                seatButtonsMap.clear();
                tatCaChoDatToaHienTai.clear();

                capNhatDanhSachGheDaChonUI();
                pnlToa.removeAll();
                pnlToa.add(new JLabel("Chọn toa: (Đang tải...)"));
                pnlToa.revalidate();
                pnlToa.repaint();

                pnlSoDoGhe.removeAll();
                pnlSoDoGhe.add(new JLabel("Vui lòng chọn Toa."));
                pnlSoDoGhe.revalidate();
                pnlSoDoGhe.repaint();
            }

            maChuyenTauHienTai = maChuyenTauMoi;
            hienThiDanhSachToaTau(maTau);
        }
    }

    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }



    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src == btnTimChuyen){
            timKiemChuyenTau();
        }

        else if (src == cancelButton) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn hủy toàn bộ dữ liệu và quay về Trang chủ?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            resetAllData();

            // Try to navigate to an existing TrangChuPanel if present; otherwise show a simple placeholder.
            // --- LOGIC CHUYỂN PANEL MỚI ---
            // 1. Lấy cửa sổ cha (JFrame/BanVeDashboard)
            Window w = SwingUtilities.getWindowAncestor(this);

            if (w instanceof BanVeDashboard) {
                BanVeDashboard dashboard = (BanVeDashboard) w;

                // 2. Chuẩn bị dữ liệu và khởi tạo Panel xác nhận
                // ManHinhXacNhanBanVe cần có constructor phù hợp
                ManHinhTrangChuNVBanVe confirmPanel = new ManHinhTrangChuNVBanVe(

                );

                // 3. Thay thế panel cũ bằng panel mới và chuyển card
                // * Cần có phương thức public trong BanVeDashboard để thực hiện việc này.
                dashboard.addOrUpdateCard(confirmPanel, "trangChu");
                dashboard.switchToCard("trangChu");
                //4. Đổi màu nút
//                dashboard.highlightButton("trangChu");

            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể tìm thấy cửa sổ Dashboard. Vui lòng chạy ứng dụng từ BanVeDashboard.",
                        "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
            }

        } else if (src == nextButton) {
            int required = parseTextFieldToInt(txtNguoiCaoTuoi)
                    + parseTextFieldToInt(txtNguoiLon)
                    + parseTextFieldToInt(txtTreCon)
                    + parseTextFieldToInt(txtSinhVien);

            if (required <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn số lượng khách hợp lệ.",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (danhSachGheDaChon.size() != required) {
                JOptionPane.showMessageDialog(this,
                        "Số ghế đã chọn (" + danhSachGheDaChon.size() + ") không khớp với Tổng số khách (" + required + ").",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // --- LOGIC CHUYỂN PANEL MỚI ---
            // 1. Lấy cửa sổ cha (JFrame/BanVeDashboard)
            Window w = SwingUtilities.getWindowAncestor(this);

            if (w instanceof BanVeDashboard) {
                BanVeDashboard dashboard = (BanVeDashboard) w;

                // 2. Chuẩn bị dữ liệu và khởi tạo Panel xác nhận
                // ManHinhXacNhanBanVe cần có constructor phù hợp
                ManHinhXacNhanBanVe confirmPanel = new ManHinhXacNhanBanVe(
                        danhSachGheDaChon,
                        danhSachKhachHang,
                        maChuyenTauHienTai,
                        date
                );

                // 3. Thay thế panel cũ bằng panel mới và chuyển card
                // * Cần có phương thức public trong BanVeDashboard để thực hiện việc này.
                dashboard.addOrUpdateCard(confirmPanel, "xacNhanBanVe");
                dashboard.switchToCard("xacNhanBanVe");

            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể tìm thấy cửa sổ Dashboard. Vui lòng chạy ứng dụng từ BanVeDashboard.",
                        "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // ====================
    // MODULE: Main (để chạy độc lập)
    // ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Bán vé (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ManHinhBanVe(), BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
