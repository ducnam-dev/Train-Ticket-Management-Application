package gui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class BanVeUI extends JFrame {

    public BanVeUI() {
        setTitle("Bán vé");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Cấu hình BorderLayout cho JFrame
        setLayout(new BorderLayout(5, 5)); // Khoảng cách giữa các thành phần

        // 1. Panel Menu (WEST)
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        // 2. Panel Nội dung Chính và Thông tin Khách hàng (CENTER)
        JPanel contentPanel = createMainContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Thêm Padding cho JFrame
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        pack(); // Điều chỉnh kích thước cửa sổ dựa trên các thành phần
        setLocationRelativeTo(null); // Đặt cửa sổ ra giữa màn hình
    }

    // --- Phương thức tạo Menu Panel (BorderLayout.WEST) ---
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(150, 600)); // Kích thước menu cố định
        panel.setBackground(new Color(30, 144, 255)); // Màu xanh

        // Thêm logo và các nút menu
        // ... (ví dụ: Trang chủ, Mở ca, Bán vé, Đổi vé...)
        panel.add(createMenuItem("Trang chủ", true));
        panel.add(createMenuItem("Mở ca", false));
        panel.add(createMenuItem("Bán vé", true)); // Nút đang chọn
        panel.add(createMenuItem("Đổi vé", false));
        
        // Thêm khoảng trống và Đăng xuất
        panel.add(Box.createVerticalGlue());
        panel.add(createMenuItem("Đăng xuất", false));

        return panel;
    }

    private JButton createMenuItem(String text, boolean isSelected) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setBackground(isSelected ? Color.WHITE : new Color(30, 144, 255));
        button.setForeground(isSelected ? Color.BLACK : Color.WHITE);
        button.setBorder(new EmptyBorder(10, 10, 10, 10));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }

    // --- Phương thức tạo Panel Nội dung Chính (CENTER) ---
    private JPanel createMainContentPanel() {
        // Sử dụng GridBagLayout để chia thành 2 cột: Trái (Nội dung) và Phải (Thông tin khách)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding cho các thành phần

        // 2.1. Panel Nội dung (Trái) - Chứa 4 khu vực 1-4
        JPanel contentLeftPanel = new JPanel();
        contentLeftPanel.setLayout(new BoxLayout(contentLeftPanel, BoxLayout.Y_AXIS));

        contentLeftPanel.add(createKhuVucTimKiem()); // Khu vực 1
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucDanhSachChuyenTau()); // Khu vực 2
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucChonLoaiKhach()); // Khu vực 3
        contentLeftPanel.add(Box.createVerticalStrut(10));
        contentLeftPanel.add(createKhuVucChonViTriGhe()); // Khu vực 4
        contentLeftPanel.add(Box.createVerticalGlue());
        
        // Khu vực 5 (Tổng tiền) sẽ được đặt ở cuối contentLeftPanel hoặc tách riêng
        JPanel khuVucTongTien = createKhuVucTongTien(); 
        contentLeftPanel.add(khuVucTongTien);


        // 2.2. Panel Thông tin Khách (Phải - Khu vực 6)
        JPanel infoRightPanel = createKhuVucThongTinKhach();

        // Thêm vào mainPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6; // Cột trái chiếm 60% chiều rộng
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(contentLeftPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4; // Cột phải chiếm 40% chiều rộng
        mainPanel.add(infoRightPanel, gbc);

        return mainPanel;
    }

    // --- Phương thức tạo từng Khu vực nhỏ ---

    // Khu vực 1: Tìm kiếm chuyến tàu
    private JPanel createKhuVucTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm chuyến tàu"));
        panel.add(new JLabel("Ga đi"));
        panel.add(new JComboBox<>(new String[]{"Sài Gòn"}));
        panel.add(new JLabel("Ga đến"));
        panel.add(new JComboBox<>(new String[]{"Hà Nội"}));
        panel.add(new JLabel("Ngày đi"));
        panel.add(new JTextField("30/9/2025", 8));
        panel.add(new JButton("Tìm chuyến"));
        return panel;
    }

    // Khu vực 2: Danh sách chuyến tàu
    private JPanel createKhuVucDanhSachChuyenTau() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách chuyến tàu"));

        String[] columnNames = {"Mã tàu", "Ghế trống", "Giờ đi"};
        Object[][] data = {
            {"SE1", "80/120", "18:00"},
            {"TN1", "90/100", "6:00"}
        };
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }
    
    // Khu vực 3: Chọn loại khách
    private JPanel createKhuVucChonLoaiKhach() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Chọn toa và ghế"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Dòng 1: Số khách
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Số khách:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(new JTextField("3", 3), gbc);
        
        // Các loại khách và chiết khấu (GridBagLayout chi tiết hơn)
        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("Người lớn (từ 60 tuổi) -25%"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; panel.add(new JTextField("1", 3), gbc);

        gbc.gridx = 2; gbc.gridy = 1; panel.add(new JLabel("Người lớn (từ 11 đến 59 tuổi)"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; panel.add(new JTextField("2", 3), gbc);

        gbc.gridx = 2; gbc.gridy = 2; panel.add(new JLabel("Trẻ con (từ dưới 10 tuổi) -20%"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; panel.add(new JTextField("0", 3), gbc);
        
        gbc.gridx = 2; gbc.gridy = 3; panel.add(new JLabel("Sinh viên -10%"), gbc);
        gbc.gridx = 3; gbc.gridy = 3; panel.add(new JTextField("0", 3), gbc);
        
        // Dòng chọn toa (FlowLayout hoặc GridBagLayout)
        JPanel toaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toaPanel.add(new JLabel("Chọn toa:"));
        toaPanel.add(new JButton("Toa 5 Ghế mềm"));
        toaPanel.add(new JButton("Toa 4 Ghế mềm"));
        toaPanel.add(new JButton("Toa 3 Giường nằm khoảng 6"));
        // ... Thêm các toa khác

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; panel.add(toaPanel, gbc);

        return panel;
    }
    
    // Khu vực 4: Chọn vị trí ghế (Sẽ phức tạp nhất, dùng JPanel và GridBagLayout/GridLayout)
    private JPanel createKhuVucChonViTriGhe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Chọn vị trí của ghế"));

        // Panel để hiển thị sơ đồ ghế (GridBagLayout cho căn chỉnh tốt)
        JPanel seatMapPanel = new JPanel(new GridBagLayout());
        // ... (Đây là phần phức tạp nhất, cần tạo từng JButton/JLabel cho từng ghế và xếp chúng theo ma trận)
        // Ví dụ: Tạo các button với màu sắc khác nhau (trống, đã chọn, không trống)
        
        seatMapPanel.add(new JLabel("Sơ đồ ghế...")); // Placeholder

        panel.add(seatMapPanel, BorderLayout.NORTH);
        
        // Panel giá vé dưới sơ đồ
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pricePanel.add(new JButton("Ghế 7-Toa 3: 800.000"));
        pricePanel.add(new JButton("Ghế 10-Toa 3: 800.000"));
        pricePanel.add(new JButton("Ghế 8-Toa 3: 600.000 -25%"));
        panel.add(pricePanel, BorderLayout.CENTER);
        
        // Chú thích
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.add(new JLabel("Trống: "));
        legendPanel.add(new JLabel("Không trống: "));
        legendPanel.add(new JLabel("Đang chọn: "));
        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Khu vực 5: Tổng tiền và nút điều hướng
    private JPanel createKhuVucTongTien() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Căn phải
        panel.add(new JLabel("Đã chọn: 3/3"));
        JLabel totalLabel = new JLabel("Tổng tiền vé: 2.200.000 VND");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(totalLabel);
        return panel;
    }

    // Khu vực 6: Thông tin khách hàng
    private JPanel createKhuVucThongTinKhach() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

        // Tải thông tin khách (dùng GridBagLayout để căn đều các trường)
        panel.add(createKhachPanel("A03-607", "Người lớn 1 (từ 11-59 tuổi)", "Bảo Duy", "20", "0123xxxx", "0011xxxx"));
        panel.add(createKhachPanel("A03-603", "Người lớn 2 (từ 11-59 tuổi)", "Bảo Duy", "20", "0999xxxx", "0022xxxx"));
        panel.add(createKhachPanel("A03-68", "Người cao tuổi (từ 60 tuổi)", "Việt Hùng", "70", "0567xxxx", "0066xxxx"));
        
        // Nút Hủy và Tiếp theo
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(Color.RED);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(80, 40));
        
        JButton nextButton = new JButton("Tiếp theo >");
        nextButton.setBackground(Color.ORANGE);
        nextButton.setForeground(Color.WHITE);
        nextButton.setPreferredSize(new Dimension(100, 40));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(nextButton);
        
        panel.add(Box.createVerticalGlue()); // Đẩy các panel khách lên trên
        panel.add(buttonPanel);

        return panel;
    }

    // Hàm phụ trợ để tạo panel thông tin khách hàng chi tiết
    private JPanel createKhachPanel(String maGhe, String loaiKhach, String hoTen, String tuoi, String sdt, String cccd) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Dòng 1: Mã ghế và Loại khách
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel(maGhe + ":"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; 
        JLabel loaiKhachLabel = new JLabel(loaiKhach);
        loaiKhachLabel.setFont(loaiKhachLabel.getFont().deriveFont(Font.BOLD));
        panel.add(loaiKhachLabel, gbc);
        gbc.gridwidth = 1;

        // Dòng 2: Giá
        gbc.gridx = 0; gbc.gridy = 1; JLabel gia = new JLabel("800.000 VND"); gia.setForeground(Color.BLUE); panel.add(gia, gbc);
        
        // Dòng 3: Chi tiết
        gbc.gridy = 2;
        gbc.gridx = 0; panel.add(new JLabel("Họ và tên*"), gbc);
        gbc.gridx = 1; panel.add(new JTextField(hoTen, 10), gbc);
        gbc.gridx = 2; panel.add(new JLabel("Tuổi"), gbc);
        gbc.gridx = 3; panel.add(new JTextField(tuoi, 3), gbc);
        
        gbc.gridy = 3;
        gbc.gridx = 0; panel.add(new JLabel("Số điện thoại"), gbc);
        gbc.gridx = 1; panel.add(new JTextField(sdt, 10), gbc);
        gbc.gridx = 2; panel.add(new JLabel("CCCD*"), gbc);
        gbc.gridx = 3; panel.add(new JTextField(cccd, 10), gbc);
        
        return panel;
    }


    public static void main(String[] args) {
        // Đảm bảo Swing chạy trên EDT
        SwingUtilities.invokeLater(() -> {
            new BanVeUI().setVisible(true);
        });
    }
}