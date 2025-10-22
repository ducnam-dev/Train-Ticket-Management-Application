package gui.Panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ManHinhTrangChuNVBanVe extends JPanel {
    private static final String TEN_NHAN_VIEN = "Trần Đức Nam";
    private static final String LUONG_CO_BAN = "7.567.000";
    private static final int NGAY_NGHI_CON_LAI = 5;

    public ManHinhTrangChuNVBanVe() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.decode("#F5F5F5")); // Màu nền xám nhạt

        // Lấy ngày hiện tại và định dạng theo tiếng Việt
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'ngày' dd 'tháng' MM 'năm' yyyy", new Locale("vi", "VN"));
        String dateString = today.format(formatter);

        // =========================================================================
        // PHẦN TRÊN CÙNG (Chào, Ngày, Avatar)
        // =========================================================================
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        // 1. Chào nhân viên
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Xin chào nhân viên : ");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        JLabel nameLabel = new JLabel(TEN_NHAN_VIEN);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel helloNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        helloNamePanel.setOpaque(false);
        helloNamePanel.add(welcomeLabel);
        helloNamePanel.add(nameLabel);

        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(helloNamePanel);

        // 2. Ngày
        JLabel dateLabel = new JLabel(dateString, SwingConstants.RIGHT);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // 3. Avatar/Biểu tượng 'N'
        JPanel avatarPanel = createAvatarPanel(TEN_NHAN_VIEN.substring(0, 1).toUpperCase());

        topPanel.add(welcomePanel, BorderLayout.WEST);
        topPanel.add(dateLabel, BorderLayout.CENTER); // Căn ngày vào giữa
        topPanel.add(avatarPanel, BorderLayout.EAST);

        // =========================================================================
        // PHẦN GIỮA (Hình ảnh và Thông báo)
        // =========================================================================
        JPanel middlePanel = new JPanel(new GridBagLayout());
        middlePanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;

        // 1. Hình ảnh
        gbc.gridx = 0;
        gbc.gridy = 0;
        middlePanel.add(createImagePanel(), gbc);

        // 2. Thông báo
        gbc.gridx = 1;
        gbc.gridy = 0;
        middlePanel.add(createAnnouncementPanel(), gbc);

        // =========================================================================
        // PHẦN DƯỚI (Thông tin nhân viên)
        // =========================================================================
        JPanel bottomPanel = createEmployeeInfoPanel();

        // Thêm các panel vào ManHinhTrangChuNVBanVe
        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Tạo panel chứa biểu tượng chữ cái đầu (Avatar)
     */
    private JPanel createAvatarPanel(String initial) {
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.decode("#3F51B5")); // Màu xanh dương cho nền
                int diameter = Math.min(getWidth(), getHeight());
                g.fillOval(0, 0, diameter, diameter);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                FontMetrics fm = g.getFontMetrics();
                int x = (diameter - fm.stringWidth(initial)) / 2;
                int y = (diameter - fm.getHeight()) / 2 + fm.getAscent();
                g.drawString(initial, x, y);
            }
        };
        avatarPanel.setPreferredSize(new Dimension(50, 50));
        avatarPanel.setOpaque(false);
        return avatarPanel;
    }

    /**
     * Tạo panel chứa hình ảnh tàu
     */
    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 300));
        panel.setBackground(Color.WHITE);

        // Tải ảnh (Sử dụng URL giả định cho một hình ảnh tàu)
        // Trong ứng dụng thực tế, bạn sẽ dùng đường dẫn file hoặc resource.
        String imagePath = "/images/anh tau.jpg"; // Giả định đường dẫn trong project
        ImageIcon originalIcon;
        try {
            // Cố gắng tải hình ảnh từ URL hoặc Resource
            URL imageUrl = ManHinhTrangChuNVBanVe.class.getResource(imagePath);
            if (imageUrl == null) {
                // Nếu không tìm thấy resource, dùng ảnh placeholder/từ web (nếu có)
                // Hoặc chỉ hiển thị một label với nền trắng
                throw new Exception("Image not found, using placeholder.");
            }
            originalIcon = new ImageIcon(imageUrl);
        } catch (Exception e) {
            // Dùng ảnh placeholder màu xanh dương nếu không tìm thấy ảnh
            originalIcon = new ImageIcon(
                    new BufferedImage(450, 250, BufferedImage.TYPE_INT_RGB)
            );
            Graphics g = originalIcon.getImage().getGraphics();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, 450, 250);
            g.dispose();
        }

        Image image = originalIcon.getImage();
        // Giảm kích thước ảnh cho vừa với panel
        Image scaledImage = image.getScaledInstance(450, 250, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

        panel.add(imageLabel, BorderLayout.CENTER);

        // Bo tròn góc bằng cách đặt border (tùy chọn, không phải bo tròn thực sự)
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    /**
     * Tạo panel chứa Thông báo
     */
    private JPanel createAnnouncementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Thông báo");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // Dùng JTextPane để dễ dàng định dạng bullet point
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setFont(new Font("Arial", Font.PLAIN, 16));

        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet bulletSet = new SimpleAttributeSet();
        StyleConstants.setLeftIndent(bulletSet, 15);
        StyleConstants.setFirstLineIndent(bulletSet, -15);
        StyleConstants.setLineSpacing(bulletSet, 0.5f);

        String[] announcements = {
                "chương trình khuyến mãi 10% sẽ diễn ra từ 10/10/2025 - 25/10/2025",
                "nhân viên vui lòng cập nhật lại thông tin trên hệ thống"
        };

        try {
            for (String announcement : announcements) {
                doc.insertString(doc.getLength(), "• ", null);
                doc.insertString(doc.getLength(), announcement + "\n", bulletSet);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        panel.add(textPane, BorderLayout.CENTER);

        // Bo tròn góc bằng cách đặt border (tùy chọn, không phải bo tròn thực sự)
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    /**
     * Tạo panel chứa Thông tin nhân viên
     */
    private JPanel createEmployeeInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Thông tin nhân viên");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel infoContent = new JPanel(new GridBagLayout());
        infoContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0; // Dãn ngang cho cột bên trái

        // --- Dòng 1: Ngày nghỉ phép ---
        JLabel leaveDaysText = new JLabel("Số ngày nghỉ phép còn lại trong tháng :");
        leaveDaysText.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel leaveDaysValue = new JLabel(NGAY_NGHI_CON_LAI + " ngày");
        leaveDaysValue.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0; gbc.gridy = 0; infoContent.add(leaveDaysText, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; infoContent.add(leaveDaysValue, gbc);

        // --- Dòng 2: Kì lương ---
        JLabel salaryText = new JLabel("Kì lương");
        salaryText.setFont(new Font("Arial", Font.PLAIN, 18));

        // Dùng JLabel riêng để tạo khoảng trống ' : '
        JLabel separator = new JLabel(":");
        separator.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel salaryValue = new JLabel(LUONG_CO_BAN + " VND");
        salaryValue.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; infoContent.add(salaryText, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.CENTER; // Căn giữa dấu ":"
        // Tạo panel cho dấu ":" và giá trị lương để căn chỉnh tốt hơn
        JPanel salaryDetailPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        salaryDetailPanel.setOpaque(false);
        salaryDetailPanel.add(separator);
        salaryDetailPanel.add(salaryValue);

        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; infoContent.add(salaryDetailPanel, gbc);

        panel.add(infoContent, BorderLayout.CENTER);

        // Bo tròn góc bằng cách đặt border (tùy chọn, không phải bo tròn thực sự)
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    // ====================
    // MODULE: Main (để chạy độc lập)
    // ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Màn hình Trang chủ NV Bán vé");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Thêm panel đã code
            frame.add(new ManHinhTrangChuNVBanVe(), BorderLayout.CENTER);

            frame.setSize(1000, 750); // Điều chỉnh kích thước để gần giống ảnh
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}