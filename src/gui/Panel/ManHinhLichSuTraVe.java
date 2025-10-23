package gui.Panel;

import dao.VeDAO;
import dao.impl.VeDAOImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ManHinhLichSuTraVe: Hiển thị danh sách các vé đã được hủy/trả.
 */
public class ManHinhLichSuTraVe extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private VeDAO veDAO; // Khai báo DAO
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);

    public ManHinhLichSuTraVe() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        // Khởi tạo DAO
        try {
            veDAO = new VeDAOImpl();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối CSDL để xem lịch sử.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            veDAO = null;
        }

        // 1. Tiêu đề
        JLabel title = new JLabel("Lịch sử Trả vé/Hủy vé", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // 2. Bảng dữ liệu
        tableModel = new DefaultTableModel(new String[]{"Mã vé", "Thời gian hủy", "Người hủy", "Lý do", "Giá hoàn trả", "Trạng thái"}, 0);
        table = new JTable(tableModel);

        table.setFont(FONT_PLAIN_14);
        table.setRowHeight(28);
        table.getTableHeader().setFont(FONT_BOLD_14);

        // Tải dữ liệu THỰC TẾ
        loadLichSuTraVe();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách các giao dịch đã hủy"));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Nút Quay lại (Giữ nguyên)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("← Quay lại màn hình Trả vé");
        btnBack.setFont(FONT_BOLD_14);
        btnBack.setBackground(new Color(0, 120, 215));
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> switchToPreviousScreen());
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Tải dữ liệu lịch sử từ DAO và điền vào bảng.
     */
    private void loadLichSuTraVe() {
        tableModel.setRowCount(0); // Xóa dữ liệu giả lập
        if (veDAO == null) return;

        List<Object[]> lichSu = veDAO.getLichSuTraVe();
        for (Object[] row : lichSu) {
            tableModel.addRow(row);
        }
    }

    /**
     * Chuyển về màn hình Trả vé.
     */
    private void switchToPreviousScreen() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof JFrame) {
            JFrame frame = (JFrame) w;
            frame.setContentPane(new ManHinhTraVe());
            frame.revalidate();
            frame.repaint();
        }
    }
}