package gui.Panel;

import dao.ChuyenTauDao;
import dao.GaDao;
import dao.ToaDAO;
import entity.ChuyenTau;
import entity.Ga;
import entity.Toa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * ManHinhBanVe: Panel bán vé - tách các phần UI và logic rõ ràng,
 * mỗi khu vực có 1 phương thức tạo, helper methods gom nhóm.
 */
public class ManHinhBanVe extends JPanel implements MouseListener {

    // UI components (fields)
    private JPanel toaPanel;
    private JComboBox<Ga> cbGaDi;
    private JComboBox<Ga> cbGaDen;
    private JTextField dateField;
    private JTable tableChuyenTau;
    private DefaultTableModel tableModel;


    // Data
    private Date date;
    private List<ChuyenTau> ketQua = new ArrayList<>();
    private String maChuyenTauHienTai = null; // Biến lưu trữ Mã Chuyến tàu đang được chọn

    // State
    private JButton lastSelectedToaButton = null;

    // Constants
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public ManHinhBanVe() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    // ======= UI Builders =======

    private JPanel createHeaderPanel() {
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

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 0));
        mainPanel.setBackground(new Color(240, 242, 245));

        JPanel contentLeftPanel = new JPanel();
        contentLeftPanel.setLayout(new BoxLayout(contentLeftPanel, BoxLayout.Y_AXIS));
        contentLeftPanel.setOpaque(false);
        contentLeftPanel.setBorder(new EmptyBorder(0, 0, 0, 5));

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
        leftContainer.setOpaque(false);
        leftContainer.setPreferredSize(new Dimension(650, 0));
        leftContainer.add(leftScrollPane, BorderLayout.NORTH);
        leftContainer.add(Box.createVerticalGlue(), BorderLayout.CENTER);

        mainPanel.add(leftContainer, BorderLayout.WEST);
        mainPanel.add(createKhuVucThongTinKhach(), BorderLayout.CENTER);

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

        JButton searchButton = new JButton("Tìm chuyến");
        stylePrimaryButton(searchButton);
        searchButton.addActionListener(e -> timKiemChuyenTau());
        panel.add(searchButton);

        setAreaAlignment(panel);
        return panel;
    }

    private JScrollPane createKhuVucDanhSachChuyenTau() {
        String[] columnNames = {"Tên Chuyến", "Ngày đi", "Giờ đi"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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
        topRow.add(new JLabel("Số khách:"));
        topRow.add(new JTextField("3", 3));

        JPanel loaiKhachInfo = new JPanel(new GridLayout(4, 2, 5, 5));
        loaiKhachInfo.setOpaque(false);
        loaiKhachInfo.add(new JLabel("Người cao tuổi (từ 60 tuổi) -25%"));
        loaiKhachInfo.add(new JTextField("1", 3));
        loaiKhachInfo.add(new JLabel("Người lớn (từ 11 đến 59 tuổi)"));
        loaiKhachInfo.add(new JTextField("2", 3));
        JLabel treConLabel = new JLabel("Trẻ con (từ dưới 10 tuổi) -20%");
        treConLabel.setForeground(Color.RED);
        loaiKhachInfo.add(treConLabel);
        loaiKhachInfo.add(new JTextField("0", 3));
        JLabel sinhVienLabel = new JLabel("Sinh viên -10%");
        sinhVienLabel.setForeground(Color.RED);
        loaiKhachInfo.add(sinhVienLabel);
        loaiKhachInfo.add(new JTextField("0", 3));

        topRow.add(Box.createHorizontalStrut(20));
        topRow.add(loaiKhachInfo);
        panel.add(topRow);

        toaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        toaPanel.setOpaque(false);
        toaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        toaPanel.add(new JLabel("Chọn toa:"));
        panel.add(toaPanel);

        setAreaAlignment(panel);
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

        JPanel seatMapPanel = createSeatMapSimplified();
        seatMapPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(seatMapPanel);
        panel.add(Box.createVerticalStrut(10));

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pricePanel.setOpaque(false);
        pricePanel.add(new JButton("Ghế 7-Toa 3: 800.000"));
        pricePanel.add(new JButton("Ghế 10-Toa 3: 800.000"));

        JButton discountBtn = new JButton("-25%");
        discountBtn.setForeground(Color.RED);
        discountBtn.setBackground(new Color(224, 224, 224));
        pricePanel.add(discountBtn);

        pricePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(pricePanel);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem(Color.GRAY.brighter(), "Chỗ trống"));
        legendPanel.add(createLegendItem(Color.BLACK, "Không trống"));
        legendPanel.add(createLegendItem(new Color(0, 123, 255), "Đang chọn"));
        panel.add(legendPanel);

        setAreaAlignment(panel);
        return panel;
    }

    private JPanel createSeatMapSimplified() {
        JPanel seatPanel = new JPanel(new GridLayout(4, 7, 5, 5));
        seatPanel.setOpaque(false);
        seatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] rows = {"Tầng 3", "Tầng 2", "Tầng 1"};
        String[][] seats = {
                {"5 6", "11 12", "17 18", "23 24", "29 30", "35 36"},
                {"3 4", "9 10", "15 16", "21 22", "27 28", "33 34"},
                {"1 2", "7 8", "13 14", "19 20", "25 26", "31 32"}
        };
        String[] khoang = {"Cửa", "Khoang 1", "Khoang 2", "Khoang 3", "Khoang 4", "Khoang 5", "Khoang 6"};

        for (int i = 0; i < seats.length; i++) {
            seatPanel.add(new JLabel(rows[i], SwingConstants.RIGHT));
            for (int j = 0; j < seats[i].length; j++) {
                JButton seat = createSeatButton(seats[i][j]);
                if ("21 22".equals(seats[i][j]) || "19 20".equals(seats[i][j])) {
                    seat.setBackground(Color.RED);
                    seat.setForeground(Color.WHITE);
                } else if ("1 2".equals(seats[i][j]) || "3 4".equals(seats[i][j])) {
                    seat.setBackground(new Color(0, 123, 255));
                    seat.setForeground(Color.WHITE);
                }
                seatPanel.add(seat);
            }
        }

        for (String label : khoang) {
            JLabel kLabel = new JLabel(label);
            kLabel.setFont(kLabel.getFont().deriveFont(Font.BOLD, 11f));
            kLabel.setHorizontalAlignment(SwingConstants.CENTER);
            if ("Cửa".equals(label)) {
                kLabel.setForeground(new Color(0, 123, 255));
            }
            seatPanel.add(kLabel);
        }

        return seatPanel;
    }

    private JPanel createKhuVucTongTien() {
        JPanel fullSummary = new JPanel(new BorderLayout());
        fullSummary.setBackground(Color.WHITE);
        fullSummary.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem(Color.GRAY.brighter(), "Chỗ trống"));
        legendPanel.add(createLegendItem(Color.BLACK, "Không trống"));
        legendPanel.add(createLegendItem(new Color(0, 123, 255), "Đang chọn"));
        fullSummary.add(legendPanel, BorderLayout.WEST);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(new JLabel("Đã chọn: 3/3"));

        JLabel totalLabel = new JLabel("Tổng tiền vé: 2.200.000 VNĐ");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        totalLabel.setForeground(new Color(255, 165, 0));
        summaryPanel.add(totalLabel);

        fullSummary.add(summaryPanel, BorderLayout.EAST);
        setAreaAlignment(fullSummary);
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

        infoScrollPanel.add(createKhachPanel("A03-G07", "Người lớn 1", "800.000 VNĐ", "Bảo Duy", "20", "01234xxxxxx", "00111XXXXXX"));
        infoScrollPanel.add(createKhachPanel("A01-G10", "Người lớn 2", "800.000 VNĐ", "Bảo Duy", "20", "09999xxxxxx", "00222XXXXXX"));
        infoScrollPanel.add(createKhachPanel("A03-G8", "Người cao tuổi", "900.000 VNĐ", "Việt Hùng", "70", "05678xxxxxx", "00666XXXXXX"));
        infoScrollPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(infoScrollPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        JButton cancelButton = new JButton("< Hủy");
        cancelButton.setPreferredSize(new Dimension(80, 40));
        cancelButton.setFont(cancelButton.getFont().deriveFont(Font.BOLD, 14f));
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);

        JButton nextButton = new JButton("Tiếp theo >");
        nextButton.setPreferredSize(new Dimension(100, 40));
        nextButton.setFont(nextButton.getFont().deriveFont(Font.BOLD, 14f));
        nextButton.setBackground(new Color(0, 123, 255));
        nextButton.setForeground(Color.WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createKhachPanel(String maGhe, String loaiKhach, String gia, String hoTen, String tuoi, String sdt, String cccd) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setOpaque(false);
        JLabel maGheLabel = new JLabel(maGhe);
        maGheLabel.setFont(maGheLabel.getFont().deriveFont(Font.BOLD));
        leftHeader.add(maGheLabel);
        JLabel loaiKhachLabel = new JLabel(loaiKhach);
        loaiKhachLabel.setFont(loaiKhachLabel.getFont().deriveFont(Font.BOLD));
        leftHeader.add(loaiKhachLabel);

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
        JTextField hoTenField = new JTextField(hoTen, 10);
        detailGrid.add(hoTenField);
        detailGrid.add(new JLabel("Tuổi"));
        JTextField tuoiField = new JTextField(tuoi, 3);
        detailGrid.add(tuoiField);

        detailGrid.add(new JLabel("Số điện thoại"));
        JTextField sdtField = new JTextField(sdt, 10);
        detailGrid.add(sdtField);
        detailGrid.add(new JLabel("CCCD*"));
        JTextField cccdField = new JTextField(cccd, 10);
        detailGrid.add(cccdField);

        panel.add(detailGrid);

        hoTenField.setMaximumSize(hoTenField.getPreferredSize());
        tuoiField.setMaximumSize(tuoiField.getPreferredSize());
        sdtField.setMaximumSize(sdtField.getPreferredSize());
        cccdField.setMaximumSize(cccdField.getPreferredSize());

        return panel;
    }

    // ======= Helpers / Styles =======

    private void setAreaAlignment(JPanel panel) {
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(100, 25));
    }

    private JButton createToaButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(String.format("<html><center>%s</center></html>", text.replace("\n", "<br>")));
        btn.setPreferredSize(new Dimension(110, 60));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
        btn.setBackground(bgColor != null ? bgColor : Color.LIGHT_GRAY);
        btn.setForeground(fgColor != null ? fgColor : Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton createSeatButton(String text) {
        JButton button = new JButton("<html><center>" + text.replace(" ", "<br>") + "</center></html>");
        button.setPreferredSize(new Dimension(50, 40));
        button.setMargin(new Insets(1, 1, 1, 1));
        button.setBackground(Color.GRAY.brighter());
        button.setForeground(Color.BLACK);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 10f));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createLegendItem(Color color, String text) {
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

    private void customizeTextField(JTextComponent field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    // ======= Data / Actions =======

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

        String ngayDiString = dateField.getText();
        String ngayDiSQL;
        try {
            date = INPUT_DATE_FORMAT.parse(ngayDiString);
            ngayDiSQL = SQL_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Ngày đi không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maGaDi = gaDiSelected.getMaGa();
        String maGaDen = gaDenSelected.getMaGa();

        ChuyenTauDao dao = new ChuyenTauDao();
        ketQua = dao.timChuyenTau(maGaDi, maGaDen, ngayDiSQL);

        if (ketQua == null || ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy chuyến tàu nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }

        loadDuLieuLenBang(ketQua);
    }

    private void loadDuLieuLenBang(List<ChuyenTau> danhSach) {
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
            for (Toa toa : danhSachToa) {
                System.out.println("Toa: " + toa.getMaToa() + ", Loại: " + toa.getLoaiToa());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách toa tàu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        loadToaButtons(danhSachToa);
    }

    public void loadToaButtons(List<Toa> danhSachToa) {
        toaPanel.removeAll();
        toaPanel.add(new JLabel("Chọn toa:"));

        for (Toa toa : danhSachToa) {
            String soThuTuToa = getSoThuTuToa(toa.getMaToa());
            String text = "Toa " + soThuTuToa + "\n" + toa.getLoaiToa();
            JButton btnToa = createToaButton(text, null, null);
            btnToa.addActionListener(e -> xuLyChonToa(btnToa, toa.getMaToa()));
            toaPanel.add(btnToa);
        }

        toaPanel.revalidate();
        toaPanel.repaint();
    }

    private void xuLyChonToa(JButton currentButton, String maToa) {
        if (lastSelectedToaButton != null) {
            lastSelectedToaButton.setBackground(Color.LIGHT_GRAY);
            lastSelectedToaButton.setForeground(Color.BLACK);
        }

        currentButton.setBackground(new Color(0, 123, 255));
        currentButton.setForeground(Color.WHITE);
        lastSelectedToaButton = currentButton;

        System.out.println("Đã chọn Toa: " + maToa + ". Tiến hành tải sơ đồ ghế.");
    }

    private String getSoThuTuToa(String maToa) {
        if (maToa != null && maToa.contains("-")) {
            return maToa.substring(maToa.lastIndexOf('-') + 1);
        }
        return maToa;
    }

    // ======= MouseListener =======

    @Override
    public void mouseClicked(MouseEvent e) {
        int selectedRow = tableChuyenTau.getSelectedRow();
        if (selectedRow != -1 && ketQua != null && selectedRow < ketQua.size()) {
            String maTau = ketQua.get(selectedRow).getMaTau();
            hienThiDanhSachToaTau(maTau);
        }
    }

    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }

    // ======= Main for standalone testing =======

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