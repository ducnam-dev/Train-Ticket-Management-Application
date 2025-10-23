package gui.Panel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.JComboBox;
import javax.swing.*;
/**
 *
 * @author laptoptt
 */
public class ManHinhTraCuuHoaDon extends javax.swing.JPanel {
    private JComboBox<String> ComboBox_DanhMucTimKiem, comboBox_Month, comboBox_Year;
    private javax.swing.JTable Table_DanhSachHoaDon;
    private JButton btnFilter, btn_TraCuu;
    private javax.swing.JLabel lblKetQuaTimKiem, lblDanhMucTimKiem, lblNhapNam, lblNhapThang, lblNhapThongTin, lblThongBaoKetQua, lblThongTinTraCuu, lblTieuDeLocTG;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txt_NhapThongTin;
    /**
     * Creates new form ManHinhTraCuuHoaDon
     */
    public ManHinhTraCuuHoaDon() {
        // 1. Lời gọi bắt buộc của NetBeans (phải ở đầu tiên)
        initComponents();

        // 2. Gọi các hàm helper để "dọn dẹp" giao diện
        customizeUIComponents();
        customizeTable();
    }

    /**
     * Tùy chỉnh các component giao diện chung (không phải bảng)
     */
    private void customizeUIComponents() {
        // 1. Điều chỉnh kích thước JComboBox (Tùy bạn, có thể thêm các tùy chỉnh khác vào đây)
        Dimension newSize = new Dimension(163, 50);

        // Đã cập nhật tên biến:
        comboBox_Year.setPreferredSize(newSize);
        comboBox_Year.setMinimumSize(newSize);
        comboBox_Year.setMaximumSize(newSize);

        // ... Thêm các tùy chỉnh cho ComboBox_DanhMucTimKiem, comboBox_Month... nếu muốn ...
    }

    /**
     * Hàm tổng hợp, gọi tất cả các tùy chỉnh cho JTable
     */
    private void customizeTable() {
        styleTableHeader();
        setupTableColumns();
    }

    /**
     * Chỉ chịu trách nhiệm tùy chỉnh Header của Bảng
     */
    private void styleTableHeader() {
        // Đã cập nhật tên biến:
        JTableHeader header = Table_DanhSachHoaDon.getTableHeader();

        // Tắt viền
        header.setBorder(BorderFactory.createEmptyBorder());
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setBorder(BorderFactory.createEmptyBorder());

        // Màu nền, font chữ
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 14));

        // Căn giữa TIÊU ĐỀ cho TẤT CẢ các cột
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Chỉ chịu trách nhiệm tùy chỉnh các Cột, Hàng, và Cell Renderer
     */
    private void setupTableColumns() {
        // 1. Căn lề TOP mặc định cho tất cả các ô
        // Đã cập nhật tên biến:
        DefaultTableCellRenderer defaultRenderer = (DefaultTableCellRenderer) Table_DanhSachHoaDon.getDefaultRenderer(Object.class);
        defaultRenderer.setVerticalAlignment(SwingConstants.TOP);

        // Lấy model cột để tùy chỉnh
        // Đã cập nhật tên biến:
        TableColumnModel columnModel = Table_DanhSachHoaDon.getColumnModel();

        // 2. Cài đặt chiều rộng cột
        columnModel.getColumn(0).setPreferredWidth(150); // Cột 0 - Mã hóa đơn
        columnModel.getColumn(1).setPreferredWidth(200); // Cột 1 - Khách hàng

        // 3. Renderer căn giữa DỮ LIỆU
        DefaultTableCellRenderer centerDataRenderer = new DefaultTableCellRenderer();
        centerDataRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerDataRenderer.setVerticalAlignment(SwingConstants.TOP); // Giữ lại căn lề trên

        // Áp dụng cho các cột 2, 3, 4, 5
        columnModel.getColumn(2).setCellRenderer(centerDataRenderer); // Cột Tuyến
        columnModel.getColumn(3).setCellRenderer(centerDataRenderer); // Cột Số lượng vé
        columnModel.getColumn(4).setCellRenderer(centerDataRenderer); // Cột Ngày khởi hành
        columnModel.getColumn(5).setCellRenderer(centerDataRenderer); // Cột Tổng tiền

        // 4. Renderer kiểu "link" cho cột cuối (cột 6)
        DefaultTableCellRenderer linkRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    setText("<html><u>" + value.toString() + "</u></html>");
                }
                setForeground(Color.BLUE);
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        // Đã cập nhật tên biến:
        Table_DanhSachHoaDon.getColumnModel().getColumn(6).setCellRenderer(linkRenderer);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        lblTieuDeLocTG = new JLabel();
        lblNhapThang = new JLabel();
        ComboBox_DanhMucTimKiem = new JComboBox<>();
        lblThongTinTraCuu = new JLabel();
        txt_NhapThongTin = new JTextField();
        btn_TraCuu = new JButton();
        lblNhapThongTin = new JLabel();
        lblDanhMucTimKiem = new JLabel();
        comboBox_Month = new JComboBox<>();
        lblNhapNam = new JLabel();
        lblKetQuaTimKiem = new JLabel();
        lblThongBaoKetQua = new JLabel() {
            {
                // Tắt chế độ vẽ nền chữ nhật mặc định
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. VẼ NỀN BO TRÒN:
                // Nó sẽ tự động lấy màu background (xanh) bạn đã set
                g2.setColor(getBackground());

                // Bạn có thể thay đổi số 15, 15 để bo tròn nhiều hay ít
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();

                // 2. VẼ CHỮ LÊN TRÊN:
                // Gọi hàm gốc để vẽ chữ (text) lên trên
                super.paintComponent(g);
            }
        };
        jScrollPane1 = new JScrollPane();
        Table_DanhSachHoaDon = new JTable();
        btnFilter = new JButton();
        comboBox_Year = new JComboBox<>();

        lblTieuDeLocTG.setFont(new Font("Helvetica Neue", 0, 28)); // NOI18N
        lblTieuDeLocTG.setIcon(new ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/calendar.png")); // NOI18N
        lblTieuDeLocTG.setText("Tìm kiếm theo thời gian");

        lblNhapThang.setFont(new Font("Helvetica Neue", 1, 16)); // NOI18N
        lblNhapThang.setText("Tháng ");

        ComboBox_DanhMucTimKiem.setBackground(new Color(220, 222, 221));
        ComboBox_DanhMucTimKiem.setFont(new Font("Helvetica Neue", 0, 18)); // NOI18N
        ComboBox_DanhMucTimKiem.setForeground(new Color(102, 102, 102));
        ComboBox_DanhMucTimKiem.setModel(new DefaultComboBoxModel<>(new String[] { "Số điện thoại", "Số CCCD", "Mã hóa đơn" }));
        ComboBox_DanhMucTimKiem.setMinimumSize(new Dimension(124, 100));
        ComboBox_DanhMucTimKiem.setPreferredSize(new Dimension(133, 50));
        ComboBox_DanhMucTimKiem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ComboBox_DanhMucTimKiemActionPerformed(evt);
            }
        });

        lblThongTinTraCuu.setFont(new Font("Helvetica Neue", 1, 18)); // NOI18N
        lblThongTinTraCuu.setText("Thông tin tra cứu ");

        txt_NhapThongTin.setBackground(new Color(220, 222, 221));
        txt_NhapThongTin.setFont(new Font("Helvetica Neue", 0, 18)); // NOI18N
        txt_NhapThongTin.setForeground(new Color(102, 102, 102));
        txt_NhapThongTin.setText("VD : 0123456789");
        txt_NhapThongTin.setMinimumSize(new Dimension(124, 40));
        txt_NhapThongTin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                txt_NhapThongTinActionPerformed(evt);
            }
        });

        btn_TraCuu.setBackground(new Color(0, 102, 255));
        btn_TraCuu.setFont(new Font("Helvetica Neue", 0, 16)); // NOI18N
        btn_TraCuu.setForeground(new Color(255, 255, 255));
        btn_TraCuu.setText("Tra cứu");
        btn_TraCuu.setBorderPainted(false);
        btn_TraCuu.setOpaque(true);
        btn_TraCuu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_TraCuuActionPerformed(evt);
            }
        });

        lblNhapThongTin.setFont(new Font("Helvetica Neue", 0, 28)); // NOI18N
        lblNhapThongTin.setIcon(new ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/research.png")); // NOI18N
        lblNhapThongTin.setText("Nhập thông tin tra cứu hóa đơn");

        lblDanhMucTimKiem.setFont(new Font("Helvetica Neue", 1, 18)); // NOI18N
        lblDanhMucTimKiem.setText("Tìm kiếm theo ");

        comboBox_Month.setBackground(new Color(220, 222, 221));
        comboBox_Month.setFont(new Font("Helvetica Neue", 0, 18)); // NOI18N
        comboBox_Month.setModel(new DefaultComboBoxModel<>(new String[] { "<html><br><html>", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", " " }));
        comboBox_Month.setMinimumSize(new Dimension(72, 50));
        comboBox_Month.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                comboBox_MonthActionPerformed(evt);
            }
        });

        lblNhapNam.setFont(new Font("Helvetica Neue", 1, 16)); // NOI18N
        lblNhapNam.setText("Năm");

        lblKetQuaTimKiem.setFont(new Font("Apple Braille", 0, 26)); // NOI18N
        lblKetQuaTimKiem.setIcon(new ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/to-do-list.png")); // NOI18N
        lblKetQuaTimKiem.setText("Kết quả tìm kiếm");

        lblThongBaoKetQua.setBackground(new Color(0, 204, 102));
        lblThongBaoKetQua.setFont(new Font("Helvetica Neue", 1, 14)); // NOI18N
        lblThongBaoKetQua.setForeground(new Color(255, 255, 255));
        lblThongBaoKetQua.setHorizontalAlignment(SwingConstants.CENTER);
        lblThongBaoKetQua.setText("2 hóa đơn");

        Table_DanhSachHoaDon.setFont(new Font("Helvetica Neue", 0, 16)); // NOI18N
        Table_DanhSachHoaDon.setModel(new DefaultTableModel(
                new Object [][] {
                        {"HD2109202400010005", "<html>Nguyễn Văn Nam<br>0123456789</html>", "Sài Gòn - Hà Nội", "3", "<html>21-09-2024<br>08:30<html>", "2.420.000đ", "Xem"},
                        {"HD1901202400080436", "<html>Nguyễn Văn Nam<br>0123456789</html>", "Sài Gòn - Đà Nẵng", "2", "<html>10-01-2024<br>17:25</html>", "1.850.000đ", "Xem"},
                        {null, null, null, null, "", null, null}
                },
                new String [] {
                        "Mã hóa đơn", "Khách hàng", "Tuyến", "Số lượng vé", "Ngày khởi hành", "Tổng tiền", "Tùy chọn"
                }
        ) {
            Class[] types = new Class [] {
                    String.class, String.class, String.class, String.class, Object.class, String.class, Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        Table_DanhSachHoaDon.setRowHeight(60);
        jScrollPane1.setViewportView(Table_DanhSachHoaDon);

        btnFilter.setBackground(new Color(0, 102, 255));
        btnFilter.setFont(new Font("Helvetica Neue", 0, 16)); // NOI18N
        btnFilter.setForeground(new Color(255, 255, 255));
        btnFilter.setText("Lọc");
        btnFilter.setBorderPainted(false);
        btnFilter.setOpaque(true);
        btnFilter.setPreferredSize(new Dimension(33, 50));
        btnFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        comboBox_Year.setBackground(new Color(220, 222, 221));
        comboBox_Year.setFont(new Font("Helvetica Neue", 0, 18)); // NOI18N
        comboBox_Year.setModel(new DefaultComboBoxModel<>(new String[] { "<html><br></html>", "2020", "2021", "2022", "2023", "2024", "2025" }));
        comboBox_Year.setMinimumSize(new Dimension(72, 50));
        comboBox_Year.setPreferredSize(new Dimension(74, 50));
        comboBox_Year.setSize(new Dimension(20, 20));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(63, 63, 63)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(ComboBox_DanhMucTimKiem, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(comboBox_Month, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE)
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addGap(10, 10, 10)
                                                                                                .addComponent(lblDanhMucTimKiem)))
                                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                                .addGap(58, 58, 58)
                                                                                                                .addComponent(comboBox_Year, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE)
                                                                                                                .addGap(18, 18, 18)
                                                                                                                .addComponent(btnFilter, GroupLayout.PREFERRED_SIZE, 210, GroupLayout.PREFERRED_SIZE))
                                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                                .addGap(59, 59, 59)
                                                                                                                .addComponent(txt_NhapThongTin, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE)))
                                                                                                .addGap(29, 29, 29)
                                                                                                .addComponent(btn_TraCuu, GroupLayout.PREFERRED_SIZE, 210, GroupLayout.PREFERRED_SIZE))
                                                                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                .addComponent(lblThongTinTraCuu)
                                                                                                .addGap(545, 545, 545))))
                                                                        .addComponent(lblNhapThongTin)
                                                                        .addComponent(lblTieuDeLocTG)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(lblKetQuaTimKiem)
                                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(lblThongBaoKetQua, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE))))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(73, 73, 73)
                                                                .addComponent(lblNhapThang)
                                                                .addGap(225, 225, 225)
                                                                .addComponent(lblNhapNam)))
                                                .addGap(0, 38, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jScrollPane1)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(lblNhapThongTin)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblDanhMucTimKiem)
                                        .addComponent(lblThongTinTraCuu))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txt_NhapThongTin, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                                        .addComponent(btn_TraCuu, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ComboBox_DanhMucTimKiem, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addGap(27, 27, 27)
                                .addComponent(lblTieuDeLocTG)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblNhapThang)
                                        .addComponent(lblNhapNam))
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnFilter, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comboBox_Year, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comboBox_Month, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                                .addGap(72, 72, 72)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblKetQuaTimKiem)
                                        .addComponent(lblThongBaoKetQua, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
    }// </editor-fold>

    private void ComboBox_DanhMucTimKiemActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btn_TraCuuActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void comboBox_MonthActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void txt_NhapThongTinActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    // End of variables declaration
    public static void main(String[] args) {
        // 1. Tạo một cửa sổ mới
        javax.swing.JFrame frame = new javax.swing.JFrame("Test Màn Hình Tra Cứu");

        // 2. Tạo một bản sao của JPanel (chính là class này)
        ManHinhTraCuuHoaDon panel = new ManHinhTraCuuHoaDon();

        // 3. Thêm panel vào làm nội dung cho cửa sổ
        frame.setContentPane(panel);

        // 4. Thiết lập hành động mặc định khi bấm nút X (tắt chương trình)
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        // 5. Tự động điều chỉnh kích thước cửa sổ vừa với nội dung panel
        frame.pack();

        // 6. Tùy chọn: Đặt cửa sổ ra giữa màn hình
        frame.setLocationRelativeTo(null);

        // 7. Hiển thị cửa sổ
        frame.setVisible(true);
    }

}

