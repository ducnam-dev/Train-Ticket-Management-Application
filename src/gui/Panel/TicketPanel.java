// File: gui.Panel.TicketPanel.java
package gui.Panel;

import entity.*; // Cần đảm bảo các lớp entity được import đúng
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

public class TicketPanel extends JPanel {

    private Ve ve;
    private static final Font FONT_MONOSPACE = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);

    // Constructor nhận đối tượng Ve
    public TicketPanel(Ve ve) {
        this.ve = ve;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.WHITE);

        // Chia panel thành 2 cột chính
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 10, 0));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainContent.add(createTicketInfoPanel());
        mainContent.add(createQRCodePanel());

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createTicketInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Helper để tạo các JLabel căn lề trái
        panel.add(createTitleLabel("Thông tin hành trình"));
        panel.add(createLine("Ga đi - Ga đến:", getHanhTrinh()));
        panel.add(createLine("Tàu/Train:", getMaTau()));
        panel.add(createLine("Ngày đi/Date:", getNgayKhoiHanh()));
        panel.add(createLine("Giờ đi/Time:", getGioKhoiHanh()));
        panel.add(createLine("Toa/Coach:", getMaToa()));
        panel.add(createLine("Chỗ/Seat:", getSoCho() + " (" + getLoaiGhe() + ")"));

        panel.add(Box.createVerticalStrut(15));

        panel.add(createTitleLabel("Thông tin hành khách"));
        panel.add(createLine("Họ tên/Full Name:", getHoTenKhachHang()));
        panel.add(createLine("Giấy tờ/Passport:", getCCCD()));
        panel.add(createLine("Loại vé/Ticket:", getTenLoaiVe()));

        panel.add(Box.createVerticalStrut(15));

        panel.add(createTitleLabel("Giá vé/Price: " + formatVnd(ve.getGia())));
        panel.add(createLineNote("(Giá vé trên đã có bảo hiểm, dịch vụ đi kèm và thuế GTGT)"));

        // Dùng Glue để đẩy tất cả lên trên
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createQRCodePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY)); // Đường kẻ dọc

        JLabel qrLabel = new JLabel();
        qrLabel.setPreferredSize(new Dimension(180, 180));
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER); // Căn giữa ảnh

        // **SỬ DỤNG LINK ẢNH QR GIẢ LẬP**
        String qrImageUrl = "D:/06_Study/Stuby IUH/Hoc ki 5/Phat trien ung dung/DU AN/Train-Ticket-Management-Application/Ảnh test chương trình/QR vé tàu.jpg";        ;
        try {
            // Tải ảnh từ URL
//            URL url = new URL(qrImageUrl);
            ImageIcon icon = new ImageIcon(qrImageUrl);

            // Ép kích thước ảnh về 180x180 (Nếu ảnh gốc không phải 180x180)
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            qrLabel.setIcon(new ImageIcon(scaledImg));

        } catch (Exception e) {
            // Trường hợp lỗi tải ảnh (mất mạng hoặc link hỏng), hiển thị placeholder text
            qrLabel.setText("Lỗi tải QR Code");
            qrLabel.setForeground(Color.RED);
            qrLabel.setBackground(Color.LIGHT_GRAY);
            qrLabel.setOpaque(true);
            System.err.println("Lỗi tải ảnh QR Code: " + e.getMessage());
        }


        panel.add(qrLabel, BorderLayout.NORTH);
        // Thông tin dưới Mã QR
        JPanel footerInfo = new JPanel(new GridLayout(3, 1));
        footerInfo.setOpaque(false);
        footerInfo.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Mã đặt chỗ (Lấy từ trường nào đó của Ve/Hóa đơn - giả định là ID vé)
        footerInfo.add(createLine("Mã vé:", ve.getId()));
        footerInfo.add(createLine("Đại lý bán vé:", "VNPay App")); // Giả lập

        panel.add(footerInfo, BorderLayout.CENTER);

        return panel;
    }

    // ===================================================
    // III. GETTERS & HELPERS
    // ===================================================

    private JLabel createLine(String label, String value) {
        JLabel lbl = new JLabel("<html><b>" + label + "</b> " + value + "</html>");
        lbl.setFont(FONT_NORMAL);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel createTitleLabel(String text) {
        JLabel lbl = new JLabel("<html><b>" + text + "</b></html>");
        lbl.setFont(FONT_BOLD);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel createLineNote(String note) {
        JLabel lbl = new JLabel("<html><i>" + note + "</i></html>");
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private String getHanhTrinh() {
        ChuyenTau ct = ve.getChuyenTauChiTiet();
        if (ct == null || ct.getGaDi() == null || ct.getGaDen() == null) return "N/A";
        return ct.getGaDi().getTenGa() + " - " + ct.getGaDen().getTenGa();
    }

    private String getMaTau() {
        ChuyenTau ct = ve.getChuyenTauChiTiet();
        return (ct != null) ? ct.getMaTau() : "N/A";
    }

    private String getNgayKhoiHanh() {
        ChuyenTau ct = ve.getChuyenTauChiTiet();
        return (ct != null && ct.getNgayKhoiHanh() != null) ? ct.getNgayKhoiHanh().toString() : "N/A";
    }

    private String getGioKhoiHanh() {
        ChuyenTau ct = ve.getChuyenTauChiTiet();
        return (ct != null && ct.getGioKhoiHanh() != null) ? ct.getGioKhoiHanh().toString() : "N/A";
    }

    private String getMaToa() {
        ChoDat cd = ve.getChoDatChiTiet();
        return (cd != null) ? cd.getMaToa() : "N/A";
    }

    private String getSoCho() {
        ChoDat cd = ve.getChoDatChiTiet();
        return (cd != null) ? cd.getSoCho() : "N/A";
    }

    // Giả định loại ghế được lấy từ ChoDat hoặc LoaiChoDat
    private String getLoaiGhe() {
        ChoDat cd = ve.getChoDatChiTiet();
        return (cd != null) ? cd.getMaCho() : "Ghế mềm điều hòa";
    }

    private String getHoTenKhachHang() {
        KhachHang kh = ve.getKhachHangChiTiet();
        return (kh != null) ? kh.getHoTen() : "N/A";
    }

    private String getCCCD() {
        KhachHang kh = ve.getKhachHangChiTiet();
        return (kh != null) ? kh.getSoCCCD() : "N/A";
    }

    // Giả định bạn có thông tin chi tiết Loại vé trong Entity Ve
    private String getTenLoaiVe() {
        return "Người lớn"; // Hoặc truy xuất từ ve.getMaLoaiVe()
    }

    private String formatVnd(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        nf.setMaximumFractionDigits(0);
        return nf.format(amount);
    }
}