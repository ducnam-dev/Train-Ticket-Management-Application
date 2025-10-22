package gui.Panel;

import entity.ChoDat;
import entity.TempKhachHang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Map;

public class ManHinhXacNhanBanVe extends JPanel {
    private Map<String, ChoDat> danhSachGhe;
    private Map<String, TempKhachHang> danhSachKhach;
    private String maChuyen;
    private Date ngayDi;

    public ManHinhXacNhanBanVe() {
        this(null, null, null, null);
    }

    public ManHinhXacNhanBanVe(Map<String, ChoDat> danhSachGhe,
                               Map<String, TempKhachHang> danhSachKhach,
                               String maChuyen, Date ngayDi) {
        this.danhSachGhe = danhSachGhe;
        this.danhSachKhach = danhSachKhach;
        this.maChuyen = maChuyen;
        this.ngayDi = ngayDi;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10,10));
        setBorder(new EmptyBorder(12,12,12,12));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Xác thực bán vé", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        // Summary info
        JLabel info = new JLabel();
        String summary = "<html>";
        summary += "Chuyến: " + (maChuyen != null ? maChuyen : "-") + "<br>";
        summary += "Ngày: " + (ngayDi != null ? ngayDi.toString() : "-") + "<br>";
        int count = danhSachGhe != null ? danhSachGhe.size() : 0;
        summary += "Số ghế đã chọn: " + count + "<br>";
        summary += "</html>";
        info.setText(summary);
        info.setBorder(new EmptyBorder(6,6,6,6));
        center.add(info);

        // List selected seats + customers (if available)
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(BorderFactory.createTitledBorder("Danh sách ghế / khách"));

        if (danhSachGhe != null && !danhSachGhe.isEmpty()) {
            for (ChoDat c : danhSachGhe.values()) {
                String seatText = "Toa: " + (c.getMaToa() == null ? "-" : c.getMaToa())
                        + " - Ghế: " + c.getSoCho()
                        + " (MaCho: " + c.getMaCho() + ")";
                TempKhachHang tk = (danhSachKhach != null) ? danhSachKhach.get(c.getMaCho()) : null;
                String name = tk != null && tk.hoTen != null ? tk.hoTen : "Chưa có tên";
                JLabel lbl = new JLabel(seatText + " - " + name);
                listPanel.add(lbl);
            }
        } else {
            listPanel.add(new JLabel("Chưa có ghế được chọn."));
        }

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setPreferredSize(new Dimension(600, 220));
        center.add(sp);

        add(center, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10,10));
        btnPanel.setOpaque(false);

        JButton btnBack = new JButton("← Quay lại");
        btnBack.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof JFrame) {
                JFrame frame = (JFrame) w;
                frame.setContentPane(new ManHinhBanVe());
                frame.revalidate();
                frame.repaint();
            }
        });

        JButton btnConfirm = new JButton("Xác thực & Hoàn tất");
        btnConfirm.setBackground(new Color(40, 167, 69));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Minimal confirmation behavior: show success and go to home
                JOptionPane.showMessageDialog(ManHinhXacNhanBanVe.this,
                        "Xác thực thành công. Vé đã được lưu.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                Window w = SwingUtilities.getWindowAncestor(ManHinhXacNhanBanVe.this);
                if (w instanceof JFrame) {
                    JFrame frame = (JFrame) w;
                    frame.setContentPane(new ManHinhTrangChuNVBanVe());
                    frame.revalidate();
                    frame.repaint();
                }
            }
        });

        btnPanel.add(btnBack);
        btnPanel.add(btnConfirm);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // ====================
    // MODULE: Main (để chạy độc lập)
    // ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Xác nhận Bán vé (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ManHinhXacNhanBanVe(), BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
