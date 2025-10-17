package gui.Panel;

import dao.ChuyenTauDao;
import dao.GaDao;
import entity.ChuyenTau;
import entity.Ga;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent; // Quan trọng để tránh lỗi ClassNotFound

import java.awt.*;
import java.util.List;
import java.util.Vector;

import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Lớp ManHinhBanVe: Chỉ chứa nội dung chính của màn hình Bán vé
 * - Đã loại bỏ GridBagLayout, thay bằng BorderLayout và BoxLayout.
 */
public class ManHinhBanVe extends JPanel {

    private JComboBox<Ga> cbGaDi, cbGaDen;
    private JTextField dateField;
    private JTable tableChuyenTau;
    private DefaultTableModel tableModel;


    public ManHinhBanVe() {
        // Cấu hình BorderLayout cho Panel (dùng cho tiêu đề và nội dung chính)
        setLayout(new BorderLayout(5, 5));

        // Thêm Padding bên ngoài
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245)); // Màu nền xám nhạt
//        setBackground(Color.BLACK);
        // 1. Panel Header (Tiêu đề "Bán vé" và ID người dùng)
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 2. Panel Nội dung Chính (CENTER)
        JPanel contentPanel = createMainContentPanel();
        add(contentPanel, BorderLayout.CENTER); // Vị trí CENTER sẽ tự mở rộng
    }

    // --- Phương thức tạo Header Panel ---
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

    // --- Phương thức tạo Panel Nội dung Chính (CENTER) ---
    private JPanel createMainContentPanel() {
        // *** SỬ DỤNG BorderLayout để chia 2 cột Trái và Phải ***
        JPanel mainPanel = new JPanel(new BorderLayout(10, 0)); // 10px khoảng cách giữa 2 cột
        mainPanel.setBackground(new Color(240, 242, 245));
//        mainPanel.setBackground(new Color(0, 242, 0));

        // 2.1. Panel Nội dung Trái (WEST/CENTER - Sử dụng BoxLayout để xếp chồng)
        JPanel contentLeftPanel = new JPanel();
        contentLeftPanel.setLayout(new BoxLayout(contentLeftPanel, BoxLayout.Y_AXIS));
        contentLeftPanel.setOpaque(false);
        contentLeftPanel.setBorder(new EmptyBorder(0, 0, 0, 5)); // Padding nhỏ bên phải

        // Thêm các khu vực con
        contentLeftPanel.add(createKhuVucTimKiem());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucDanhSachChuyenTau());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucChonLoaiKhach());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucChonViTriGhe());
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucTongTien());

        // *** THÊM KEO ĐỂ ĐẨY NỘI DUNG LÊN TRÊN ***
        contentLeftPanel.add(Box.createVerticalGlue());

        // 2.2. Panel Thông tin Khách (Phải)
        JPanel infoRightPanel = createKhuVucThongTinKhach();

        // Thêm vào mainPanel: Cột Trái (WEST)
        // Dùng JScrollPane bọc contentLeftPanel để có thể cuộn nếu màn hình nhỏ
        JScrollPane leftScrollPane = new JScrollPane(contentLeftPanel);
        leftScrollPane.setBorder(null);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.getVerticalScrollBar().setUnitIncrement(16); // Tăng tốc độ cuộn

        // Để giới hạn chiều rộng của cột trái, ta wrap nó trong một Panel khác (tùy chọn, ở đây dùng BorderLayout.WEST)
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(false);
        // Thiết lập chiều rộng cố định (hoặc tối đa) cho container bên trái
        // Nếu không dùng GridBag, việc kiểm soát tỷ lệ/kích thước 2 cột sẽ khó hơn. 
        // Ta dùng PreferredSize cho bên Trái và để bên Phải chiếm hết phần còn lại (CENTER)
        leftContainer.setPreferredSize(new Dimension(650, 0)); // Giả định cố định 650px cho cột Trái

        leftContainer.add(leftScrollPane, BorderLayout.NORTH); // NORTH để đảm bảo nội dung căn trên
        leftContainer.add(Box.createVerticalGlue(), BorderLayout.CENTER); // Thêm keo vào giữa để đảm bảo NORTH hoạt động

        mainPanel.add(leftContainer, BorderLayout.WEST);

        // Thêm vào mainPanel: Cột Phải (CENTER) - Tự mở rộng
        mainPanel.add(infoRightPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    // Hàm phụ trợ giúp các khu vực con có Alignment X tốt hơn cho BoxLayout
    private void setAreaAlignment(JPanel panel) {
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
    }


    // --- KHU VỰC CON (Giữ lại nội dung và loại bỏ GridBagLayout trong các hàm phụ) ---

    // --- Khu vực 1: Tìm kiếm chuyến tàu ---
    private JPanel createKhuVucTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Tìm kiếm chuyến tàu");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        // 1. Khởi tạo đối tượng DAO để lấy dữ liệu
//        GaDao gaTauDAO = new GaDao();
        // 2. Gọi phương thức để lấy danh sách ga từ CSDL
        Vector<Ga> danhSachGa = new  GaDao().layDanhSachGa();
        // 3. Tạo JComboBox sử dụng dữ liệu vừa lấy được

        // Ga đi
        panel.add(new JLabel("Ga đi"));
        cbGaDi = new JComboBox<>(danhSachGa);
        panel.add(cbGaDi);

        // Ga đến
        panel.add(new JLabel("Ga đến"));
        cbGaDen = new JComboBox<>(danhSachGa);
        panel.add(cbGaDen);

         if (danhSachGa.size() > 1) {
             cbGaDen.setSelectedIndex(1);
         }


        panel.add(new JLabel("Ngày đi"));
        dateField = new JTextField("30/9/2025", 8);
        dateField.setPreferredSize(new Dimension(80, 25));
        panel.add(dateField);

        JButton searchButton = new JButton("Tìm chuyến");
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 25));
        panel.add(searchButton);

        // B. Thêm ActionListener cho nút Tìm chuyến
        searchButton.addActionListener(e -> timKiemChuyenTau());
        panel.add(searchButton);

        setAreaAlignment(panel);
        return panel;

    }

    // --- Khu vực 2: Danh sách chuyến tàu ---
//    private JPanel createKhuVucDanhSachChuyenTau() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBackground(Color.WHITE);
//
//        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Danh sách chuyến tàu");
//        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
//        panel.setBorder(title);
//
//        // ... JTable giữ nguyên ...
//        String[] columnNames = {"Mã tàu", "Ghế trống", "Giờ đi"};
//        Object[][] data = {
//                {"SE1", "80/120", "18:00"},
//                {"TN1", "90/100", "6:00"}
//        };
//        JTable table = new JTable(data, columnNames);
//        table.setFillsViewportHeight(true);
//        table.setRowHeight(30);
//        table.setFont(table.getFont().deriveFont(Font.PLAIN, 14f));
//        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
//
//        panel.add(new JScrollPane(table), BorderLayout.CENTER);
//
//        // Đặt kích thước cố định
//        panel.setPreferredSize(new Dimension(0, 150));
//        setAreaAlignment(panel); // Vẫn dùng Alignment để khớp với BoxLayout cha
//        return panel;
//    }

    private JScrollPane createKhuVucDanhSachChuyenTau() {
        // 1. Định nghĩa cột
        String[] columnNames = {"Tên Chuyến", "Ngày đi", "Giờ đi"};

        // 2. Khởi tạo tableModel (sử dụng biến thành viên)
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Không cho phép chỉnh sửa trực tiếp trên bảng
                return false;
            }
        };

        // 3. Khởi tạo tableChuyenTau (sử dụng biến thành viên)
        tableChuyenTau = new JTable(tableModel);

        // 4. Đặt JTable vào JScrollPane để có thanh cuộn
        JScrollPane scrollPane = new JScrollPane(tableChuyenTau);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        return scrollPane;
    }

    // --- Khu vực 3: Chọn toa và loại khách ---
    private JPanel createKhuVucChonLoaiKhach() {
        // *** THAY GridBagLayout bằng BoxLayout để xếp chồng và FlowLayout cho các dòng ***
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Chọn toa và ghế");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        // Dòng 1: Số khách và Chiết khấu
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Thêm padding dọc
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRow.add(new JLabel("Số khách:"));
        topRow.add(new JTextField("3", 3));

        // Chiết khấu (Sử dụng GridLayout để căn đều 2 cột)
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


        // Dòng 2: Chọn toa
        JPanel toaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        toaPanel.setOpaque(false);
        toaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        toaPanel.add(new JLabel("Chọn toa:"));

        JButton toa3 = createToaButton("Toa 3\nGiường nằm\nkhoang 6", new Color(0, 123, 255), Color.WHITE);

        toaPanel.add(createToaButton("Toa 5\nGhế mềm", null, null));
        toaPanel.add(createToaButton("Toa 4\nGhế mềm", null, null));
        toaPanel.add(toa3);
        toaPanel.add(createToaButton("Toa 2\nGiường nằm\nkhoang 6", null, null));
        toaPanel.add(createToaButton("Toa 1\nGiường nằm\nkhoang 6", null, null));

        panel.add(toaPanel);

        setAreaAlignment(panel);
        return panel;
    }

    // Hàm phụ trợ tạo nút toa (Giữ nguyên)
    private JButton createToaButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        button.setMargin(new Insets(5, 5, 5, 5));
        button.setBackground(bgColor != null ? bgColor : new Color(224, 224, 224));
        button.setForeground(fgColor != null ? fgColor : Color.BLACK);
        button.setFocusPainted(false);
        return button;
    }

    // --- Khu vực 4: Chọn vị trí ghế (Sử dụng GridLayout đơn giản cho Sơ đồ) ---
    private JPanel createKhuVucChonViTriGhe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Chọn vị trí của ghế");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        // Sơ đồ ghế (THAY GridBagLayout bằng GridLayout)
        JPanel seatMapPanel = createSeatMapSimplified();
        seatMapPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(seatMapPanel);
        panel.add(Box.createVerticalStrut(10));

        // Panel giá vé dưới sơ đồ
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

        // Chú thích
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem(Color.GRAY.brighter(), "Chỗ trống"));
        legendPanel.add(createLegendItem(Color.BLACK, "Không trống"));
        legendPanel.add(createLegendItem(new Color(0, 123, 255), "Đang chọn"));
        panel.add(legendPanel);

        setAreaAlignment(panel);
        return panel;
    }

    // Tạo sơ đồ ghế (Sử dụng GridLayout)
    private JPanel createSeatMapSimplified() {
        // Tổng cộng có 4 hàng (3 tầng + 1 hàng Khoang) và 7 cột (1 nhãn Tầng + 6 khoang)
        JPanel seatPanel = new JPanel(new GridLayout(4, 7, 5, 5));
        seatPanel.setOpaque(false);
        seatPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding cho sơ đồ ghế

        String[] rows = {"Tầng 3", "Tầng 2", "Tầng 1"};
        String[][] seats = {
                {"5 6", "11 12", "17 18", "23 24", "29 30", "35 36"},
                {"3 4", "9 10", "15 16", "21 22", "27 28", "33 34"},
                {"1 2", "7 8", "13 14", "19 20", "25 26", "31 32"}
        };
        String[] khoang = {"Cửa", "Khoang 1", "Khoang 2", "Khoang 3", "Khoang 4", "Khoang 5", "Khoang 6"};

        // Thêm các nút Ghế và nhãn Tầng
        for (int i = 0; i < seats.length; i++) {
            // Cột 0: Nhãn Tầng
            seatPanel.add(new JLabel(rows[i], SwingConstants.RIGHT));

            // Cột 1-6: Các nút Ghế
            for (int j = 0; j < seats[i].length; j++) {
                JButton seat = createSeatButton(seats[i][j]);

                // Trạng thái ghế mẫu:
                if (seats[i][j].equals("21 22") || seats[i][j].equals("19 20")) {
                    seat.setBackground(Color.RED);
                    seat.setForeground(Color.WHITE);
                } else if (seats[i][j].equals("1 2") || seats[i][j].equals("3 4")) {
                    seat.setBackground(new Color(0, 123, 255));
                    seat.setForeground(Color.WHITE);
                }
                seatPanel.add(seat);
            }
        }

        // Hàng cuối: Nhãn Khoang (Dùng FlowLayout để căn chỉnh các nhãn tốt hơn nếu cần)
        for (String label : khoang) {
            JLabel kLabel = new JLabel(label);
            kLabel.setFont(kLabel.getFont().deriveFont(Font.BOLD, 11f));
            kLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Xử lý nút Cửa (giả lập là JLabel)
            if (label.equals("Cửa")) {
                kLabel.setText("Cửa");
                kLabel.setForeground(new Color(0, 123, 255));
            }
            seatPanel.add(kLabel);
        }

        return seatPanel;
    }

    // Hàm phụ trợ tạo nút ghế (Giữ nguyên)
    private JButton createSeatButton(String text) {
        JButton button = new JButton("<html><center>" + text.replace(" ", "<br>") + "</center></html>");
        button.setPreferredSize(new Dimension(50, 40));
        button.setMargin(new Insets(1, 1, 1, 1));
        button.setBackground(Color.GRAY.brighter()); // Mặc định là trống
        button.setForeground(Color.BLACK);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 10f));
        button.setFocusPainted(false);
        return button;
    }

    // Hàm phụ trợ tạo chú thích (Giữ nguyên)
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

    // --- Khu vực 5: Tổng tiền và Summary (Giữ nguyên BorderLayout/FlowLayout) ---
    private JPanel createKhuVucTongTien() {
        JPanel fullSummary = new JPanel(new BorderLayout());
        fullSummary.setBackground(Color.WHITE);
        fullSummary.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Phần Chú thích (đẩy sang trái)
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem(Color.GRAY.brighter(), "Chỗ trống"));
        legendPanel.add(createLegendItem(Color.BLACK, "Không trống"));
        legendPanel.add(createLegendItem(new Color(0, 123, 255), "Đang chọn"));
        fullSummary.add(legendPanel, BorderLayout.WEST);

        // Phần Tổng kết (đẩy sang phải)
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);

        summaryPanel.add(new JLabel("Đã chọn: 3/3"));

        JLabel totalLabel = new JLabel("Tổng tiền vé: 2.200.000 VNĐ");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        totalLabel.setForeground(new Color(255, 165, 0)); // Màu cam
        summaryPanel.add(totalLabel);

        fullSummary.add(summaryPanel, BorderLayout.EAST);

        setAreaAlignment(fullSummary);
        return fullSummary;
    }

    // --- Khu vực 6: Thông tin khách hàng & Nút Hủy/Tiếp theo ---
    private JPanel createKhuVucThongTinKhach() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin khách hàng"));

        // Container cho thông tin khách (dùng JScrollPane)
        JPanel infoScrollPanel = new JPanel();
        infoScrollPanel.setLayout(new BoxLayout(infoScrollPanel, BoxLayout.Y_AXIS));
        infoScrollPanel.setOpaque(false);
        infoScrollPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Thêm các panel thông tin khách hàng
        infoScrollPanel.add(createKhachPanel("A03-G07", "Người lớn 1", "800.000 VNĐ", "Bảo Duy", "20", "01234xxxxxx", "00111XXXXXX"));
        infoScrollPanel.add(createKhachPanel("A01-G10", "Người lớn 2", "800.000 VNĐ", "Bảo Duy", "20", "09999xxxxxx", "00222XXXXXX"));
        infoScrollPanel.add(createKhachPanel("A03-G8", "Người cao tuổi", "900.000 VNĐ", "Việt Hùng", "70", "05678xxxxxx", "00666XXXXXX"));
        infoScrollPanel.add(Box.createVerticalGlue()); // Keo ở infoScrollPanel

        JScrollPane scrollPane = new JScrollPane(infoScrollPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- Panel Nút Hủy và Tiếp theo (SOUTH) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        JButton cancelButton = new JButton("< Hủy");
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(80, 40));
        cancelButton.setFont(cancelButton.getFont().deriveFont(Font.BOLD, 14f));

        JButton nextButton = new JButton("Tiếp theo >");
        nextButton.setBackground(new Color(0, 123, 255));
        nextButton.setForeground(Color.WHITE);
        nextButton.setPreferredSize(new Dimension(100, 40));
        nextButton.setFont(nextButton.getFont().deriveFont(Font.BOLD, 14f));

        buttonPanel.add(cancelButton);
        buttonPanel.add(nextButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Hàm phụ trợ để tạo panel thông tin khách hàng chi tiết
    private JPanel createKhachPanel(String maGhe, String loaiKhach, String gia, String hoTen, String tuoi, String sdt, String cccd) {
        // *** THAY GridBagLayout bằng BoxLayout (Y_AXIS) và FlowLayout ***
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Dòng 1: Mã ghế, Loại khách và Giá (Sử dụng FlowLayout hoặc BorderLayout)
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

        // Dòng 2 & 3: Chi tiết nhập liệu (Sử dụng GridLayout 2x4 để căn đều các field)
        JPanel detailGrid = new JPanel(new GridLayout(2, 4, 10, 5)); // 2 hàng, 4 cột (Label, Field, Label, Field)
        detailGrid.setOpaque(false);
        detailGrid.setBorder(new EmptyBorder(5, 0, 5, 0));

        // Dòng 2
        detailGrid.add(new JLabel("Họ và tên*"));
        JTextField hoTenField = new JTextField(hoTen, 10);
        detailGrid.add(hoTenField);
        detailGrid.add(new JLabel("Tuổi"));
        JTextField tuoiField = new JTextField(tuoi, 3);
        detailGrid.add(tuoiField);

        // Dòng 3
        detailGrid.add(new JLabel("Số điện thoại"));
        JTextField sdtField = new JTextField(sdt, 10);
        detailGrid.add(sdtField);
        detailGrid.add(new JLabel("CCCD*"));
        JTextField cccdField = new JTextField(cccd, 10);
        detailGrid.add(cccdField);

        panel.add(detailGrid);

        // Cần đảm bảo các JTextField có chiều rộng tối đa phù hợp (sử dụng BoxLayout)
        hoTenField.setMaximumSize(hoTenField.getPreferredSize());
        tuoiField.setMaximumSize(tuoiField.getPreferredSize());
        sdtField.setMaximumSize(sdtField.getPreferredSize());
        cccdField.setMaximumSize(cccdField.getPreferredSize());


        return panel;
    }

    // Hàm phụ trợ cho việc tùy chỉnh JTextComponent (giúp tránh lỗi NoClassDefFoundError: JTextComponent)
    private void customizeTextField(JTextComponent field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    /**
     * Hàm tải dữ liệu từ cơ sở dữ liệu
     *
     * @return Danh sách chuyến tàu cần tìm
     * Quy trình thực hiện (đã có lớp connectDB và ChuyenTauDao phù trách tìm trong csdl):
     * 1. Kết nối đến cơ sở dữ liệu
     * 2. Thực hiện truy vấn để lấy danh sách chuyến tàu dựa trên tiêu chí tìm kiếm
     * 3. Chuyển đổi kết quả truy vấn thành danh sách các đối tượng ChuyenTau
     * 4. Trả về danh sách chuyến tàu
     * @throws Exception nếu có lỗi trong quá trình truy xuất dữ liệu
     *                   <p>
     * Sau đó gọi một hàm để hiện thị những dữ liệu này lên bảng JTable trong khu vực Danh sách chuyến tàu.
     *
     */
//event tìm kiếm chuyến tàu
    private void timKiemChuyenTau() {
        // 1. Lấy dữ liệu từ giao diện
//        String gaDi = (String) cbGaDi.getSelectedItem();
//        String gaDen = (String) cbGaDen.getSelectedItem();
        Ga gaDiSelected = (Ga) cbGaDi.getSelectedItem();
        Ga gaDenSelected = (Ga) cbGaDen.getSelectedItem();
        String ngayDiString = dateField.getText(); // Ví dụ: "30/9/2025"

        // 2. Chuyển đổi định dạng ngày (RẤT QUAN TRỌNG)
        String ngayDiSQL = null;
        try {
            // Định dạng đầu vào (từ người dùng)
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            // Định dạng đầu ra (cho CSDL SQL Server)
            SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");

            java.util.Date date = inputFormat.parse(ngayDiString);
            ngayDiSQL = sqlFormat.format(date);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Ngày đi không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return; // Dừng nếu ngày nhập không đúng
        }
        String maGaDi = gaDiSelected.getMaGa();
        String maGaDen = gaDenSelected.getMaGa();
        System.out.println("Tìm kiếm theo " + ngayDiSQL + " từ " + maGaDi + " đến " + maGaDen);

        // 3. Thực hiện truy vấn CSDL
        ChuyenTauDao dao = new ChuyenTauDao(); // Khởi tạo DAO
        List<ChuyenTau> ketQua = dao.timChuyenTau(maGaDi, maGaDen, ngayDiSQL);

        // 4. Cập nhật kết quả lên giao diện
        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy chuyến tàu nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }

        //một JTable tên là 'tableChuyenTau' trên một Panel khác
        System.out.println("Đang tải dữ liệu lên bảng chuyến tàu...");
        loadDuLieuLenBang(ketQua);
    }
    // Phương thức này có thể nằm trong lớp ManHinhBanVe của bạn
    private void loadDuLieuLenBang(List<ChuyenTau> danhSach) {
        DefaultTableModel model = (DefaultTableModel) tableChuyenTau.getModel();
        // 1. Xóa dữ liệu cũ
        model.setRowCount(0);
        // 2. Thêm dữ liệu mới
        for (ChuyenTau ct : danhSach) {
            Object[] rowData = {
                    ct.getMaChuyenTau(),
                    ct.getNgayKhoiHanh(),
                    ct.getGioKhoiHanh(),
            };
            model.addRow(rowData);
            System.out.println("Đã thêm chuyến tàu: " + ct.getMaChuyenTau());
        }
    }
    /**
     * Phương thức Main để kiểm tra giao diện ManHinhBanVe một cách độc lập.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Bán vé (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Thêm ManHinhBanVe vào JFrame
            frame.add(new ManHinhBanVe(), BorderLayout.CENTER);

            frame.pack();
            frame.setSize(1200, 850); // Đặt kích thước để xem dễ hơn
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}