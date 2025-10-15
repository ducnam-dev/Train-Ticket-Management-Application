package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Lớp ManHinhBanVe: Chỉ chứa nội dung chính của màn hình Bán vé
 * (Tìm kiếm, Chọn ghế, Thông tin khách hàng), loại bỏ Menu Sidebar.
 */
public class ManHinhBanVe extends JPanel {

    public ManHinhBanVe() {
        // Cấu hình BorderLayout cho Panel (dùng cho tiêu đề và nội dung chính)
        setLayout(new BorderLayout(5, 5));

        // Thêm Padding bên ngoài
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245)); // Màu nền xám nhạt

        setBackground(new Color(240, 0, 0));
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
        // Sử dụng GridBagLayout để chia thành 2 cột: Trái và Phải
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding giữa 2 cột

        // 2.1. Panel Nội dung (Trái - Khu vực 1, 3, 4, 5)
        JPanel contentLeftPanel = new JPanel();
        contentLeftPanel.setLayout(new BoxLayout(contentLeftPanel, BoxLayout.Y_AXIS));
        contentLeftPanel.setOpaque(false);

        contentLeftPanel.add(createKhuVucTimKiem()); // Khu vực 1
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucDanhSachChuyenTau()); // Khu vực 2
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucChonLoaiKhach()); // Khu vực 3
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucChonViTriGhe()); // Khu vực 4
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucTongTien()); // Khu vực 5

        // ********* THAY ĐỔI: Thêm keo để đẩy mọi thứ lên trên và lấp đầy khoảng trống *********
        contentLeftPanel.add(Box.createVerticalGlue());


        // 2.2. Panel Thông tin Khách (Phải - Khu vực 6)
        JPanel infoRightPanel = createKhuVucThongTinKhach();

        // Thêm vào mainPanel (Cột Trái)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.55;
        gbc.weighty = 1.0;
        // *** SỬA ĐỔI QUAN TRỌNG: Fill chỉ theo chiều dọc, Căn chỉnh Top-Left ***
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(contentLeftPanel, gbc);

        // Thêm vào mainPanel (Cột Phải)
        gbc.gridx = 1;
        gbc.weightx = 0.45;
        // Cột phải vẫn cần fill BOTH để JScrollPane của nó lấp đầy
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER; // Hoặc để mặc định
        mainPanel.add(infoRightPanel, gbc);

        return mainPanel;
    }

    // --- Khu vực 1: Tìm kiếm chuyến tàu ---
    private JPanel createKhuVucTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Tìm kiếm chuyến tàu");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        panel.add(new JLabel("Ga đi"));
        panel.add(new JComboBox<>(new String[]{"Sài Gòn", "Hà Nội"}));
        panel.add(new JLabel("Ga đến"));
        panel.add(new JComboBox<>(new String[]{"Hà Nội", "Sài Gòn"}));

        panel.add(new JLabel("Ngày đi"));
        JTextField dateField = new JTextField("30/9/2025", 8);
        dateField.setPreferredSize(new Dimension(80, 25));
        panel.add(dateField);

        JButton searchButton = new JButton("Tìm chuyến");
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 25));
        panel.add(searchButton);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

        return panel;
    }

    // --- Khu vực 2: Danh sách chuyến tàu ---
    private JPanel createKhuVucDanhSachChuyenTau() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Danh sách chuyến tàu");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        String[] columnNames = {"Mã tàu", "Ghế trống", "Giờ đi"};
        Object[][] data = {
                {"SE1", "80/120", "18:00"},
                {"TN1", "90/100", "6:00"}
        };
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(table.getFont().deriveFont(Font.PLAIN, 14f));
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));

        // Đặt bảng vào JScrollPane để tiêu đề hiển thị
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // ********* THAY ĐỔI: Đặt kích thước cố định để các khu vực khác không bị lấn *********
        panel.setPreferredSize(new Dimension(0, 150));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); // Giới hạn kích thước tối đa theo chiều dọc

        return panel;
    }

    // --- Khu vực 3: Chọn toa và loại khách ---
    private JPanel createKhuVucChonLoaiKhach() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Chọn toa và ghế");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Dòng 1: Số khách và Chiết khấu
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topRow.setOpaque(false);
        topRow.add(new JLabel("Số khách:"));
        topRow.add(new JTextField("3", 3));

        // Chiết khấu (Mô phỏng 2 cột VBox)
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

        topRow.add(Box.createHorizontalStrut(20)); // Khoảng cách giữa Số khách và Chiết khấu
        topRow.add(loaiKhachInfo);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; panel.add(topRow, gbc);


        // Dòng 2: Chọn toa
        JPanel toaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        toaPanel.setOpaque(false);
        toaPanel.add(new JLabel("Chọn toa:"));

        JButton toa3 = createToaButton("Toa 3\nGiường nằm\nkhoang 6", new Color(0, 123, 255), Color.WHITE);

        toaPanel.add(createToaButton("Toa 5\nGhế mềm", null, null));
        toaPanel.add(createToaButton("Toa 4\nGhế mềm", null, null));
        toaPanel.add(toa3); // Nút đang chọn
        toaPanel.add(createToaButton("Toa 2\nGiường nằm\nkhoang 6", null, null));
        toaPanel.add(createToaButton("Toa 1\nGiường nằm\nkhoang 6", null, null));

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(toaPanel, gbc);

        return panel;
    }

    // Hàm phụ trợ tạo nút toa
    private JButton createToaButton(String text, Color bgColor, Color fgColor) {
        // Sử dụng HTML cho text đa dòng
        JButton button = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        button.setMargin(new Insets(5, 5, 5, 5));
        button.setBackground(bgColor != null ? bgColor : new Color(224, 224, 224));
        button.setForeground(fgColor != null ? fgColor : Color.BLACK);
        button.setFocusPainted(false);
        return button;
    }

    // --- Khu vực 4: Chọn vị trí ghế ---
    private JPanel createKhuVucChonViTriGhe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Chọn vị trí của ghế");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        // Sơ đồ ghế
        JPanel seatMapPanel = createSeatMap();
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

        return panel;
    }

    // Tạo sơ đồ ghế
    private JPanel createSeatMap() {
        JPanel seatPanel = new JPanel(new GridBagLayout());
        seatPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 4, 2, 4);
        gbc.anchor = GridBagConstraints.CENTER;

        String[] rows = {"Tầng 3", "Tầng 2", "Tầng 1"};
        String[][] seats = {
                {"5 6", "11 12", "17 18", "23 24", "29 30", "35 36"},
                {"3 4", "9 10", "15 16", "21 22", "27 28", "33 34"},
                {"1 2", "7 8", "13 14", "19 20", "25 26", "31 32"}
        };
        String[] khoang = {"khoang 1", "khoang 2", "khoang 3", "khoang 4", "khoang 5", "khoang 6"};

        // Thêm nhãn Tầng/Hàng (Cột 0)
        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            seatPanel.add(new JLabel(rows[i], SwingConstants.RIGHT), gbc);
        }

        // Thêm nút Cửa
        gbc.gridx = 0; gbc.gridy = rows.length; gbc.gridwidth = 1;
        JButton doorButton = createSeatButton("Cửa");
        doorButton.setBackground(new Color(0, 123, 255));
        doorButton.setForeground(Color.WHITE);
        seatPanel.add(doorButton, gbc);

        // Thêm các nút Ghế
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                JButton seat = createSeatButton(seats[i][j]);

                // Trạng thái ghế mẫu: Ghế đỏ/Không trống (21 22, 19 20)
                if (seats[i][j].equals("21 22") || seats[i][j].equals("19 20")) {
                    seat.setBackground(Color.RED);
                    seat.setForeground(Color.WHITE);
                }
                // Ghế đang chọn (1 2, 3 4)
                else if (seats[i][j].equals("1 2") || seats[i][j].equals("3 4")) {
                    seat.setBackground(new Color(0, 123, 255));
                    seat.setForeground(Color.WHITE);
                }

                gbc.gridx = j + 1;
                gbc.gridy = i;
                seatPanel.add(seat, gbc);
            }
        }

        // Thêm nhãn Khoang
        for (int i = 0; i < khoang.length; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = rows.length + 1;
            seatPanel.add(new JLabel(khoang[i]), gbc);
        }

        return seatPanel;
    }

    // Hàm phụ trợ tạo nút ghế
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

    // Hàm phụ trợ tạo chú thích
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

    // --- Khu vực 5: Tổng tiền và Summary ---
    private JPanel createKhuVucTongTien() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT); // Giúp BoxLayout hoạt động tốt hơn

        // Phần Chú thích (đẩy sang trái)
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem(Color.GRAY.brighter(), "Chỗ trống"));
        legendPanel.add(createLegendItem(Color.BLACK, "Không trống"));
        legendPanel.add(createLegendItem(new Color(0, 123, 255), "Đang chọn"));

        // Phần Tổng kết (đẩy sang phải)
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);

        summaryPanel.add(new JLabel("Đã chọn: 3/3"));

        JLabel totalLabel = new JLabel("Tổng tiền vé: 2.200.000 VNĐ");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        totalLabel.setForeground(new Color(255, 165, 0)); // Màu cam
        summaryPanel.add(totalLabel);

        JPanel fullSummary = new JPanel(new BorderLayout());
        fullSummary.setBackground(Color.WHITE);
        fullSummary.add(legendPanel, BorderLayout.WEST);
        fullSummary.add(summaryPanel, BorderLayout.EAST);
        fullSummary.setBorder(new EmptyBorder(5, 10, 5, 10)); // Padding bên trong

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
        infoScrollPanel.add(createKhachPanel("A03-G07", "Người lớn 1 (từ 11-59 tuổi)", "800.000 VNĐ", "Bảo Duy", "20", "01234xxxxxx", "00111XXXXXX"));
        infoScrollPanel.add(createKhachPanel("A01-G10", "Người lớn 2 (từ 11-59 tuổi)", "800.000 VNĐ", "Bảo Duy", "20", "09999xxxxxx", "00222XXXXXX"));
        infoScrollPanel.add(createKhachPanel("A03-G8", "Người cao tuổi (từ 60 tuổi)", "900.000 VNĐ", "Việt Hùng", "70", "05678xxxxxx", "00666XXXXXX"));
        // Thêm keo vào infoScrollPanel để các thông tin khách không dãn ra khi có thêm khoảng trống
        infoScrollPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(infoScrollPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- Panel Nút Hủy và Tiếp theo (SOUTH) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        JButton cancelButton = new JButton("< Hủy");
        cancelButton.setBackground(new Color(220, 53, 69)); // Đỏ
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(80, 40));
        cancelButton.setFont(cancelButton.getFont().deriveFont(Font.BOLD, 14f));

        JButton nextButton = new JButton("Tiếp theo >");
        nextButton.setBackground(new Color(0, 123, 255)); // Xanh
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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Mã ghế, Loại khách và Giá
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel maGheLabel = new JLabel(maGhe);
        maGheLabel.setFont(maGheLabel.getFont().deriveFont(Font.BOLD));
        panel.add(maGheLabel, gbc);

        gbc.gridx = 1;
        JLabel loaiKhachLabel = new JLabel(loaiKhach);
        loaiKhachLabel.setFont(loaiKhachLabel.getFont().deriveFont(Font.BOLD));
        panel.add(loaiKhachLabel, gbc);

        gbc.gridx = 2; gbc.weightx = 1.0;
        JLabel giaLabel = new JLabel(gia);
        giaLabel.setForeground(Color.BLUE);
        panel.add(giaLabel, gbc);
        gbc.weightx = 0;

        // Dòng 2 & 3: Chi tiết nhập liệu
        gbc.gridy = 1;
        gbc.gridx = 0; panel.add(new JLabel("Họ và tên*"), gbc);
        gbc.gridx = 1; panel.add(new JTextField(hoTen, 10), gbc);
        gbc.gridx = 2; panel.add(new JLabel("Tuổi"), gbc);
        gbc.gridx = 3; panel.add(new JTextField(tuoi, 3), gbc);

        gbc.gridy = 2;
        gbc.gridx = 0; panel.add(new JLabel("Số điện thoại"), gbc);
        gbc.gridx = 1; panel.add(new JTextField(sdt, 10), gbc);
        gbc.gridx = 2; panel.add(new JLabel("CCCD*"), gbc);
        gbc.gridx = 3; panel.add(new JTextField(cccd, 10), gbc);

        return panel;
    }

    /**
     * Phương thức Main để kiểm tra giao diện ManHinhBanVe một cách độc lập.
     */
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Panel Bán vé (Kiểm tra)");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setLayout(new BorderLayout());
//
//            // Thêm ManHinhBanVe vào JFrame
//            frame.add(new ManHinhBanVe(), BorderLayout.CENTER);
//
//            frame.pack();
//            frame.setSize(1200, 800); // Đặt kích thước để xem dễ hơn
//            frame.setLocationRelativeTo(null);
//            frame.setVisible(true);
//        });
//    }
}