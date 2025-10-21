// java
package gui.Panel;

import dao.ChoDatDAO;
import dao.ChuyenTauDao;
import dao.GaDao;
import dao.ToaDAO;
import entity.ChoDat;
import entity.ChuyenTau;
import entity.Ga;
import entity.Toa;
import entity.lopEnum.TrangThaiChoDat;

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
    private JPanel pnlToa;
    private JPanel pnlSoDoGhe;
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

    private JPanel pnlDanhSachGheDaCho;

    // THÊM: Danh sách Mã Ghế đang được chọn (chờ mua)
    private List<String> danhSachMaGheDaChon = new ArrayList<>();
    // THÊM: Map để theo dõi trạng thái button (MaCho -> Button)
    private java.util.Map<String, JButton> seatButtonsMap = new java.util.HashMap<>();

    // Constants
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public ManHinhBanVe() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245));

        add(taoPanelTieuDe(), BorderLayout.NORTH);
        add(taoNoiDungChinh(), BorderLayout.CENTER);
    }

    // ======= UI Builders =======

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

        // Không cố định preferred size ở đây -> để split pane quản lý
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(true);
        leftContainer.add(leftScrollPane, BorderLayout.NORTH);
        leftContainer.add(Box.createVerticalGlue(), BorderLayout.LINE_START);

        JPanel rightPanel = createKhuVucThongTinKhach();

        // DÙNG JSPLITPANE để tự động co giãn
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftContainer, rightPanel);
        split.setResizeWeight(0.75); // tỷ lệ khi resize 8/2
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

        JButton searchButton = new JButton("Tìm chuyến");
        styleNutChinh(searchButton);
        searchButton.addActionListener(e -> timKiemChuyenTau());
        panel.add(searchButton);

        datCanhKhuVuc(panel);
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

        pnlToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        pnlToa.setOpaque(false);
        pnlToa.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlToa.add(new JLabel("Chọn toa:"));
        panel.add(pnlToa);
        panel.setMaximumSize(new Dimension(1200, 100));

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

        // Bọc sơ đồ ghế trong JScrollPane nếu số lượng ghế lớn,
        // nhưng ở đây tôi dùng JPanel đơn giản cho gọn.
        JScrollPane soDoScrollPane = new JScrollPane(pnlSoDoGhe);
        soDoScrollPane.setBorder(BorderFactory.createEmptyBorder());
        soDoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        soDoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        soDoScrollPane.setPreferredSize(new Dimension(100, 150)); // Đặt kích thước cho scrollpane
        soDoScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        panel.add(soDoScrollPane);
        panel.add(Box.createVerticalStrut(10));


        // 1. Panel Chú Giải (Legend)
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setOpaque(false);
        legendPanel.add(taoMucChuGiai(Color.GRAY.brighter(), "Chỗ trống"));
        legendPanel.add(taoMucChuGiai(Color.BLACK, "Đã đặt"));
        legendPanel.add(taoMucChuGiai(Color.GREEN, "Đang chọn"));
        legendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(legendPanel);
        panel.add(Box.createVerticalStrut(5));


        // 3. Panel Ghế Đã Chọn (Thay thế nút giả lập) //Dùng để sử lý sự kiện trong tương lai
        pnlDanhSachGheDaCho = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlDanhSachGheDaCho.setOpaque(false);
        pnlDanhSachGheDaCho.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlDanhSachGheDaCho.add(new JLabel("Ghế đã chọn:"));


        panel.add(pnlDanhSachGheDaCho);

        datCanhKhuVuc(panel);
        return panel;
    }



    // Kích thước cố định cho nút ghế (ví dụ: 50x50)


    // Hàm tạo nút giả lập ghế đã chọn
    private JButton taoNutGheDaChon(String maGhe) {
        JButton btn = new JButton(maGhe); // Chỉ hiển thị mã ghế
        btn.setBackground(new Color(0, 123, 255)); // Đổi màu nền cho đồng nhất với đang chọn
        btn.setForeground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 25));

        // THÊM: Logic để hủy chọn khi click vào nút này
        btn.addActionListener(e -> xuLyHuyChonGhe(maGhe));

        return btn;
    }


    private JPanel createKhuVucTongTien() {
        JPanel fullSummary = new JPanel(new BorderLayout());
        fullSummary.setBackground(Color.white);//white
        fullSummary.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        legendPanel.setOpaque(false);
        legendPanel.add(taoMucChuGiai(Color.GRAY.brighter(), "Chỗ trống"));
        legendPanel.add(taoMucChuGiai(Color.BLACK, "Không trống"));
        legendPanel.add(taoMucChuGiai(new Color(0, 123, 255), "Đang chọn"));
        fullSummary.add(legendPanel, BorderLayout.WEST);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(new JLabel("Đã chọn: 3/3"));

        JLabel totalLabel = new JLabel("Tổng tiền vé: 2.200.000 VNĐ");
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

        private JButton taoNutGhe(String text) {
        JButton button = new JButton("<html><center>" + text.replace(" ", "<br>") + "</center></html>");
        button.setPreferredSize(new Dimension(50, 40));
        button.setMargin(new Insets(1, 1, 1, 1));
        button.setBackground(Color.GRAY.brighter());
        button.setForeground(Color.BLACK);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 10f));
        button.setFocusPainted(false);
        return button;
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

    private void tuChinhTextField(JTextComponent field) {
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

    /**
     * Hiển thị danh sách toa tàu cho chuyến tàu được chọn
     * @param maTau
     */
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
        napNutToa(danhSachToa);
    }

    /**
     * Nạp nút toa vào panel
     * @param danhSachToa
     */
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

    /**
     * Xử lý khi chọn toa
     * @param currentButton
     * @param maToa
     */
    private ChoDatDAO choDatDao = new ChoDatDAO();

    private void xuLyChonToa(JButton currentButton, String maToa) {

        danhSachMaGheDaChon.clear();
        seatButtonsMap.clear(); // Xóa map các button cũ
        capNhatDanhSachGheDaChonUI(); // Cập nhật UI


        if (lastSelectedToaButton != null) {
            lastSelectedToaButton.setBackground(Color.LIGHT_GRAY);
            lastSelectedToaButton.setForeground(Color.BLACK);
        }
        currentButton.setBackground(new Color(0, 123, 255));
        currentButton.setForeground(Color.WHITE);
        lastSelectedToaButton = currentButton;

        if (this.maChuyenTauHienTai == null || this.maChuyenTauHienTai.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn chuyến tàu trước.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            // Dọn dẹp sơ đồ cũ
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Chưa chọn chuyến tàu."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
            return;
        }

        System.out.println("Đã chọn Toa: " + maToa + maChuyenTauHienTai + ". Tiến hành tải sơ đồ ghế." );

        List<ChoDat> danhSachChoDat = choDatDao.getDanhSachChoDatByMaToaVaTrangThai(
                maToa,
                maChuyenTauHienTai
        );

        //Xử lý và hiện thị
        if (danhSachChoDat.isEmpty()) {
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Toa này chưa có dữ liệu chỗ đặt."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
        } else {
            // 🔑 Kích hoạt phương thức vẽ sơ đồ ghế đã sửa ở trên
            veSoDoGhe(danhSachChoDat);
        }
    }

//    private JPanel veSoDoGheGiườngNằm(List<ChoDat> danhSachChoDat) {
//        // ... (logic khởi tạo seatPanel và các mảng Tầng/Khoang)
//
//        // Tạo Map để tra cứu dữ liệu nhanh chóng
//        Map<String, ChoDat> choDatMap = new HashMap<>();
//        for (ChoDat cho : danhSachChoDat) { choDatMap.put(cho.getSoCho(), cho); }
//
//        // Lặp qua cấu trúc UI cố định (Tầng/Khoang)
//        // ... (logic lặp)
//
//        // Trong vòng lặp, khi lấy được ChoDat choHienTai:
//        // ...
//        if (choHienTai != null) {
//            // Kiểm tra enum BAO_TRI: ưu tiên cao nhất
//            if (choHienTai.getTrangThai() == TrangThaiChoDat.BAO_TRI) {
//                // Màu XÁM, Vô hiệu hóa
//            }
//            // Kiểm tra trạng thái đặt vé: ưu tiên thứ hai
//            else if (choHienTai.isDaDat()) {
//                // Màu ĐỎ, Vô hiệu hóa
//            } else {
//                // Còn Trống: Xanh lá, Kích hoạt (enabled)
//                // Gắn ActionListener gọi xuLyDatGhe(...)
//            }
//        }
//        // ... (logic thêm button vào seatPanel)
//
//        return seatPanel;
//    }

    /**
     * Lấy số thứ tự toa từ mã toa
     * @param maToa
     * @return
     */
    private String laySoThuTuToa(String maToa) {
        if (maToa != null && maToa.contains("-")) {
            return maToa.substring(maToa.lastIndexOf('-') + 1);
        }
        return maToa;
    }

    // Kích thước cố định cho nút ghế
    private static final Dimension SQUARE_SEAT_SIZE = new Dimension(60, 30);
    /**
     * Vẽ sơ đồ ghế dựa trên danh sách chỗ đặt.
     * Sử dụng GridLayout bên trong một JPanel, sau đó JPanel này được thêm vào
     * pnlSoDoGhe với FlowLayout để đảm bảo các nút giữ nguyên kích thước và kích hoạt cuộn.
     * @param danhSachChoDat List<ChoDat> danh sách chỗ đặt của toa được chọn.
     */
    private void veSoDoGhe(List<ChoDat> danhSachChoDat) {
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();

        // 1. Thiết lập pnlSoDoGhe (Container chính, sử dụng FlowLayout để bọc Grid)
        pnlSoDoGhe.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Căn trái, không có khoảng cách ngoài
        pnlSoDoGhe.setOpaque(true);
        pnlSoDoGhe.setBackground(Color.WHITE); // Đổi về màu trắng để dễ nhìn
        pnlSoDoGhe.setBorder(new EmptyBorder(10, 10, 10, 10));


        // 2. Thiết lập Grid Container (Vẽ Sơ đồ)
        int rows = 4;
        int columns = (int) Math.ceil((double) danhSachChoDat.size() / rows);


        // Grid Container: chứa tất cả các nút ghế
        JPanel gridContainer = new JPanel(new GridLayout(rows, columns, 5, 5));
        gridContainer.setOpaque(false);

        // 3. Logic Chính: Lặp qua danh sách và áp dụng kiểu dáng
        for (ChoDat cho : danhSachChoDat) {
            JButton btnCho = new JButton(cho.getSoCho());

            // Thiết lập kích thước HÌNH VUÔNG CỐ ĐỊNH (40x40) và Font nhỏ
            btnCho.setPreferredSize(SQUARE_SEAT_SIZE);
            btnCho.setMinimumSize(SQUARE_SEAT_SIZE);
            btnCho.setMaximumSize(SQUARE_SEAT_SIZE);
            btnCho.setFont(btnCho.getFont().deriveFont(Font.BOLD, 12f));
            // 1. Kiểm tra trạng thái
            if (cho.getTrangThai() == TrangThaiChoDat.DA_SU_DUNG) {
                // Trường hợp 1: ĐÃ ĐẶT
                btnCho.setBackground(Color.BLACK);
                btnCho.setForeground(Color.WHITE);
                btnCho.setEnabled(false);
                btnCho.setToolTipText("Ghế đã được đặt");
            } else if (cho.getTrangThai() == TrangThaiChoDat.DA_HUY) {
                // Trường hợp 1b: BẢO TRÌ (Thêm logic này nếu có)
                btnCho.setBackground(Color.BLACK);
                btnCho.setForeground(Color.WHITE);
                btnCho.setEnabled(false);
                btnCho.setToolTipText("Ghế đang bảo trì");
            }
            else {
                // Trường hợp 2: CÒN TRỐNG
                btnCho.setBackground(Color.LIGHT_GRAY); // Màu xám nhạt cho trống
                btnCho.setForeground(Color.BLACK);
                btnCho.setEnabled(true);

                // Gắn sự kiện để xử lý việc đặt vé (chọn ghế)
                btnCho.addActionListener(e -> {
                    xuLyChonGhe(btnCho, cho.getMaCho());
                });
            }
            seatButtonsMap.put(cho.getMaCho(), btnCho);
            // Thêm nút vào Grid Container
            gridContainer.add(btnCho);
        }

        // 4. Thêm Grid Container vào Panel chính (pnlSoDoGhe)
        pnlSoDoGhe.add(gridContainer);

        // Thao tác cuối
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }

    /**
     * Logic chính để thêm/bỏ một ghế khỏi danh sách chọn.
     * @param btnCho Nút ghế được click.
     * @param maCho Mã chỗ đặt tương ứng.
     */
    private void xuLyChonGhe(JButton btnCho, String maCho) {
        if (danhSachMaGheDaChon.contains(maCho)) {
            // Trường hợp 1: Ghế đã được chọn -> HỦY CHỌN
            danhSachMaGheDaChon.remove(maCho);
            btnCho.setBackground(Color.LIGHT_GRAY);
            btnCho.setForeground(Color.BLACK);
            System.out.println("Đã hủy chọn ghế: " + maCho);
        } else {
            // Trường hợp 2: Ghế chưa được chọn -> CHỌN
            danhSachMaGheDaChon.add(maCho);
            btnCho.setBackground(new Color(0, 123, 255)); // Màu Đang chọn
            btnCho.setForeground(Color.WHITE);
            System.out.println("Đã chọn ghế: " + maCho);
        }

        // Cập nhật UI của danh sách ghế đã chọn
        capNhatDanhSachGheDaChonUI();
    }

    /**
     * Logic xử lý khi click vào nút ghế trên danh sách đã chọn để hủy chọn.
     * @param maCho Mã chỗ đặt cần hủy.
     */
    private void xuLyHuyChonGhe(String maCho) {
        // Hủy chọn trong danh sách
        danhSachMaGheDaChon.remove(maCho);

        // Cập nhật trạng thái nút trên sơ đồ ghế
        JButton btnCho = seatButtonsMap.get(maCho);
        if (btnCho != null) {
            btnCho.setBackground(Color.LIGHT_GRAY);
            btnCho.setForeground(Color.BLACK);
        }

        // Cập nhật UI danh sách đã chọn
        capNhatDanhSachGheDaChonUI();
        System.out.println("Đã hủy chọn ghế: " + maCho + " từ danh sách.");
    }

    /**
     * Xây dựng lại nội dung của selectedSeatsPanel dựa trên danh sách ghế đã chọn.
     */
    private void capNhatDanhSachGheDaChonUI() {
        pnlDanhSachGheDaCho.removeAll();
        pnlDanhSachGheDaCho.add(new JLabel("Ghế đã chọn:"));

        for (String maCho : danhSachMaGheDaChon) {
            JButton btnGhe = taoNutGheDaChon(maCho); // Gọi phương thức tạo nút
            pnlDanhSachGheDaCho.add(btnGhe);
        }

        pnlDanhSachGheDaCho.revalidate();
        pnlDanhSachGheDaCho.repaint();
    }

    // ======= MouseListener =======

    @Override
    public void mouseClicked(MouseEvent e) {
        int selectedRow = tableChuyenTau.getSelectedRow();
        if (selectedRow != -1 && ketQua != null && selectedRow < ketQua.size()) {
            String maTau = ketQua.get(selectedRow).getMaTau();
            maChuyenTauHienTai = ketQua.get(selectedRow).getMaChuyenTau();
            System.out.println("Chuyến tàu được chọn: " + maChuyenTauHienTai + " (Mã Tàu: " + maTau + ")");
            // Hiển thị danh sách toa tàu cho chuyến tàu được chọn
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