package gui.MainFrame;

import control.XuLyNhanVien;
import control.XuLyTaiKhoan;
import entity.NhanVien;
import entity.TaiKhoan;
import control.CaLamViec;
//import gui.MainFrame.ManHinhDashboardQuanLy; // Giả định lớp này tồn tại

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Lớp ManHinhDangNhap: Tạo giao diện màn hình Đăng nhập
 */
public class ManHinhDangNhap extends JFrame implements ActionListener {

    private static final String LOGO_PATH = "src/images/logo-train.png";

    private static final String TRAIN_IMAGE_PATH = "src/images/anh tau.jpg";

    // Kích thước mong muốn của ảnh logo và ảnh tàu
    private static final int LOGO_SIZE = 80;
    private static final int TRAIN_IMAGE_WIDTH = 600;

    private JButton btnDangNhap;
    private JTextField txtTaiKhoan;
    private JPasswordField pssMatKhau;

    public ManHinhDangNhap() {
        setTitle("Đăng nhập Hệ thống Quản lý Bán vé Tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Sử dụng BorderLayout cho JFrame chính
        setLayout(new BorderLayout());

        // 1. Thêm Panel Ảnh Đoàn Tàu (Bên trái - WEST)
        JPanel trainImagePanel = createTrainImagePanel();
        add(trainImagePanel, BorderLayout.WEST);

        // 2. Thêm Panel Form Đăng nhập (Trung tâm - CENTER)
        JPanel loginCenterPanel = createLoginCenterPanel(); // btnDangNhap được khởi tạo trong createLoginFormPanel()
        add(loginCenterPanel, BorderLayout.CENTER);

        loginCenterPanel.setBackground(Color.WHITE);

        // ==========================================================
        // THÊM CHỨC NĂNG NHẤN ENTER ĐỂ ĐĂNG NHẬP
        // ==========================================================
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null) {
            rootPane.setDefaultButton(btnDangNhap); // Chỉ định nút mặc định
        }

        setVisible(true);
    }

    // ... (Các phương thức createTrainImagePanel, createLoginCenterPanel, customizeTextField, createScaledImageLabel giữ nguyên)

    private JPanel createTrainImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(TRAIN_IMAGE_WIDTH, this.getHeight()));

        panel.setBackground(new Color(41, 128, 185));

        JLabel trainLabel = createScaledImageLabel(TRAIN_IMAGE_PATH, TRAIN_IMAGE_WIDTH, 550, "TRAIN IMAGE HERE");

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);

        centerContainer.add(Box.createVerticalGlue());
        trainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(trainLabel);
        centerContainer.add(Box.createVerticalGlue());

        panel.add(centerContainer, BorderLayout.CENTER);

        return panel;
    }

    // --- Panel Bên Phải: Form Đăng Nhập ---
    private JPanel createLoginCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JPanel loginPanel = createLoginFormPanel();

        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(loginPanel);
        centerPanel.add(Box.createVerticalGlue());

        return centerPanel;
    }

    private JPanel createLoginFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        panel.setPreferredSize(new Dimension(400, 450));
        panel.setMaximumSize(new Dimension(400, 450));

        // --- 1. Logo và Tiêu đề ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        headerPanel.setOpaque(false);

        JLabel logoLabel = createScaledImageLabel(LOGO_PATH, LOGO_SIZE, LOGO_SIZE, "LOGO HERE");

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));

        headerPanel.add(logoLabel);
        headerPanel.add(titleLabel);

        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(headerPanel);

        panel.add(Box.createVerticalStrut(30));

        // --- 2. Tên đăng nhập ---
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(usernameLabel);

        panel.add(Box.createVerticalStrut(5));

        txtTaiKhoan = new JTextField(); // Đặt giá trị mặc định để dễ test
        customizeTextField(txtTaiKhoan);
        panel.add(txtTaiKhoan);

        panel.add(Box.createVerticalStrut(15));

        // --- 3. Mật khẩu ---
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passwordLabel);

        panel.add(Box.createVerticalStrut(5));

        pssMatKhau = new JPasswordField(); // Đặt giá trị mặc định để dễ test
        customizeTextField(pssMatKhau);
        panel.add(pssMatKhau);

        panel.add(Box.createVerticalStrut(30));

        // --- 4. Nút Đăng nhập ---
        btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        btnDangNhap.setBackground(new Color(0, 123, 255));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 16));
        btnDangNhap.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDangNhap.addActionListener(this); // Gán ActionListener

        // --- 5. Nút Quên mật khẩu ---
        JButton forgotPasswordButton = new JButton("Quên mật khẩu");
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setForeground(new Color(0, 123, 255));
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPasswordButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Thêm các thành phần vào Panel
        panel.add(btnDangNhap);
        panel.add(Box.createVerticalStrut(10));
        panel.add(forgotPasswordButton);

        return panel;
    }

    /**
     * Tùy chỉnh JTextComponent
     */
    private void customizeTextField(JTextComponent field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Tải và tạo JLabel chứa ảnh, điều chỉnh kích thước
     */
    private JLabel createScaledImageLabel(String path, int width, int height, String fallbackText) {
        JLabel imageLabel = new JLabel();
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Lỗi: Không tìm thấy file ảnh tại đường dẫn: " + path);
                imageLabel.setText(fallbackText);
                return imageLabel;
            }

            ImageIcon originalIcon = new ImageIcon(path);
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));

        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh: " + e.getMessage());
            e.printStackTrace();
            imageLabel.setText(fallbackText);
        }
        return imageLabel;
    }


    /**
     * Phương thức Main để chạy màn hình đăng nhập.
     */
    public static void main(String[] args) {
        // Cần đảm bảo rằng lớp ConnectDB đã được gọi và kết nối đã được thiết lập
        // database.ConnectDB.getInstance().connect(); // Ví dụ gọi kết nối
        SwingUtilities.invokeLater(ManHinhDangNhap::new);
    }

    // ==============================================================================
    // LOGIC XỬ LÝ SỰ KIỆN ĐĂNG NHẬP
    // ==============================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnDangNhap) {
            String tenDangNhap = txtTaiKhoan.getText().trim();
            String matKhau = new String(pssMatKhau.getPassword()).trim();

            // Xử lý trường hợp nhập thiếu dữ liệu
            if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên đăng nhập và Mật khẩu.", "Lỗi đăng nhập", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Bước 2: Gọi hàm xác thực từ lớp Control
            TaiKhoan taiKhoan = XuLyTaiKhoan.authenticate(tenDangNhap, matKhau);


            if (taiKhoan != null) {
                // Xác thực thành công:
                String maNV = taiKhoan.getMaNV();

                // --- BƯỚC 2: LẤY VÀ LƯU THÔNG TIN NHÂN VIÊN ---
                NhanVien nhanVien = null;
                try {
                    // Gọi lớp xử lý để lấy thông tin NhanVien dựa trên MaNV
                    // (Giả định: XuLyNhanVien.getNhanVienByMaNV(maNV) trả về đối tượng NhanVien)
                    nhanVien = XuLyNhanVien.layThongTinNhanVienChoCaLamViec(maNV);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin nhân viên: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                    return; // Ngừng nếu không lấy được thông tin NV
                }

                if (nhanVien == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin nhân viên cho tài khoản này.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // BƯỚC 3: BẮT ĐẦU CA LÀM VIỆC VÀ LƯU SESSION => sẽ lấy để dùng sau
                CaLamViec.getInstance().batDauCa(nhanVien);


                // Phân quyền dựa trên MaNV chuẩn hóa:
                if (maNV.startsWith("NVQL")) {
                    // Mở màn hình quản lý (Ví dụ: NVQL0001, NVQL0002)
                    new QuanLyDashboard().setVisible(true);
                } else if (maNV.startsWith("NVBV")) {
                    // Mở màn hình bán vé (Ví dụ: NVBV0001)
                    new BanVeDashboard().setVisible(true);
                } else {
                    // Mặc định hoặc lỗi phân quyền
                    JOptionPane.showMessageDialog(this, "Tài khoản không có quyền truy cập.", "Lỗi phân quyền", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                this.dispose(); // Đóng màn hình đăng nhập
            } else {
                // Xác thực thất bại (Thông báo lỗi đã được xử lý trong XuLyTaiKhoan)
                // Hoặc hiển thị thông báo chung:
                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng.", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}