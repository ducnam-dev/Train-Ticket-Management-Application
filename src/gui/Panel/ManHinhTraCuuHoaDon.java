package gui.Panel; // <-- Make sure this package name is correct

// AWT Imports (Graphics, Layouts, Events)
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Point; // Added for evt.getPoint()
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Swing Imports (Components & Utilities)
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException; // Added for LookAndFeel catch

// Swing Border Imports
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

// Swing Table Imports
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

// Ghi chú: Đã xóa "import javax.swing.*;"

/**
 *
 * @author TranSon-Code
 */
public class ManHinhTraCuuHoaDon extends JPanel {


    // Variables declaration - do not modify
    private JButton btnFilter, btn_LamMoi, btn_TraCuu, btn_Xoa;
    private JComboBox<String> ComboBox_DanhMucTimKiem, comboBox_Month, comboBox_Year;
    private JLabel btnKetQuaTimKiem, lblDanhMucTimKiem, lblNhapNam, lblNhapThang, lblNhapThongTin, lblThongBaoKetQua, lblThongTinTraCuu, lblTieuDeLocTG;
    private JScrollPane jScrollPane1;
    private JTable Table_DanhSachHoaDon;
    private JTextField txt_NhapThongTin;



    // End of variables declaration
    /**
     * Creates new form ManHinhTraCuuHoaDon
     */
    public ManHinhTraCuuHoaDon() {
        initComponents();

        JTableHeader header = Table_DanhSachHoaDon.getTableHeader();

        header.setBorder(BorderFactory.createEmptyBorder());
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setBorder(BorderFactory.createEmptyBorder());
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        Dimension newSize = new Dimension(163, 50);

        comboBox_Year.setPreferredSize(newSize);
        comboBox_Year.setMinimumSize(newSize);
        comboBox_Year.setMaximumSize(newSize);
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) Table_DanhSachHoaDon.getDefaultRenderer(Object.class);
        renderer.setVerticalAlignment(SwingConstants.TOP);

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) Table_DanhSachHoaDon.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        TableColumnModel columnModel = Table_DanhSachHoaDon.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150); // Cột 0 - Mã hóa đơn
        columnModel.getColumn(1).setPreferredWidth(200); // Cột 1 - Khách hàng

        DefaultTableCellRenderer centerDataRenderer = new DefaultTableCellRenderer();
        centerDataRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerDataRenderer.setVerticalAlignment(SwingConstants.TOP); // Giữ lại căn lề trên

        columnModel.getColumn(2).setCellRenderer(centerDataRenderer); // Cột Tuyến
        columnModel.getColumn(3).setCellRenderer(centerDataRenderer); // Cột Số lượng vé
        columnModel.getColumn(4).setCellRenderer(centerDataRenderer); // Cột Ngày khởi hành
        columnModel.getColumn(5).setCellRenderer(centerDataRenderer); // Cột Tổng tiền

        // Custom cho cột tùy chọn
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
        Table_DanhSachHoaDon.getColumnModel().getColumn(6).setCellRenderer(linkRenderer);

        // THÊM SỰ KIỆN CHO CỘT TÙY CHỌN ĐỂ HIỂN THỊ CHI TIẾT HÓA ĐƠN
        Table_DanhSachHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = Table_DanhSachHoaDon.rowAtPoint(evt.getPoint());
                int col = Table_DanhSachHoaDon.columnAtPoint(evt.getPoint());

                if (col == 6 && row >= 0) {
                    Object maHoaDonObj = Table_DanhSachHoaDon.getValueAt(row, 0);
                    String maHoaDon = (maHoaDonObj != null) ? maHoaDonObj.toString() : "N/A";
                    showChiTietPopup(maHoaDon);
                }
            }
        });
    }
    // --- KẾT THÚC CODE THÊM SỰ KIỆN ---

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
        btnKetQuaTimKiem = new JLabel();
        lblThongBaoKetQua = new JLabel() {
            {
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        jScrollPane1 = new JScrollPane();
        Table_DanhSachHoaDon = new JTable();
        btnFilter = new JButton();
        comboBox_Year = new JComboBox<>();
        btn_Xoa = new JButton();
        btn_LamMoi = new JButton();

        lblTieuDeLocTG.setFont(new Font("Helvetica Neue", Font.PLAIN, 28)); // NOI18N
        lblTieuDeLocTG.setText("Tìm kiếm theo thời gian");

        lblNhapThang.setFont(new Font("Helvetica Neue", Font.BOLD, 16)); // NOI18N
        lblNhapThang.setText("Tháng ");

        ComboBox_DanhMucTimKiem.setBackground(new Color(220, 222, 221));
        ComboBox_DanhMucTimKiem.setFont(new Font("Helvetica Neue", Font.PLAIN, 18)); // NOI18N
        ComboBox_DanhMucTimKiem.setForeground(new Color(102, 102, 102));
        ComboBox_DanhMucTimKiem.setModel(new DefaultComboBoxModel<>(new String[] { "Số điện thoại", "Số CCCD", "Mã hóa đơn" }));
        ComboBox_DanhMucTimKiem.setMinimumSize(new Dimension(124, 100));
        ComboBox_DanhMucTimKiem.setPreferredSize(new Dimension(133, 50));
        ComboBox_DanhMucTimKiem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ComboBox_DanhMucTimKiemActionPerformed(evt);
            }
        });

        lblThongTinTraCuu.setFont(new Font("Helvetica Neue", Font.BOLD, 18)); // NOI18N
        lblThongTinTraCuu.setText("Thông tin tra cứu ");

        txt_NhapThongTin.setBackground(new Color(220, 222, 221));
        txt_NhapThongTin.setFont(new Font("Helvetica Neue", Font.PLAIN, 18)); // NOI18N
        txt_NhapThongTin.setForeground(new Color(102, 102, 102));
        txt_NhapThongTin.setText("VD : 0123456789");
        txt_NhapThongTin.setMinimumSize(new Dimension(124, 40));
        txt_NhapThongTin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                txt_NhapThongTinActionPerformed(evt);
            }
        });

        btn_TraCuu.setBackground(new Color(0, 102, 255));
        btn_TraCuu.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // NOI18N
        btn_TraCuu.setForeground(new Color(255, 255, 255));
        btn_TraCuu.setText("Tra cứu");
        btn_TraCuu.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        btn_TraCuu.setBorderPainted(false);
        btn_TraCuu.setOpaque(true);
        btn_TraCuu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_TraCuuActionPerformed(evt);
            }
        });

        lblNhapThongTin.setFont(new Font("Helvetica Neue", Font.PLAIN, 28)); // NOI18N
        lblNhapThongTin.setText("Nhập thông tin tra cứu hóa đơn");

        lblDanhMucTimKiem.setFont(new Font("Helvetica Neue", Font.BOLD, 18)); // NOI18N
        lblDanhMucTimKiem.setText("Tìm kiếm theo ");

        comboBox_Month.setBackground(new Color(220, 222, 221));
        comboBox_Month.setFont(new Font("Helvetica Neue", Font.PLAIN, 18)); // NOI18N
        comboBox_Month.setModel(new DefaultComboBoxModel<>(new String[] { "<html><br><html>", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", " " }));
        comboBox_Month.setMinimumSize(new Dimension(72, 50));
        comboBox_Month.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                comboBox_MonthActionPerformed(evt);
            }
        });

        lblNhapNam.setFont(new Font("Helvetica Neue", Font.BOLD, 16)); // NOI18N
        lblNhapNam.setText("Năm");

        btnKetQuaTimKiem.setFont(new Font("Apple Braille", Font.PLAIN, 26)); // NOI18N
        btnKetQuaTimKiem.setText("Kết quả tìm kiếm");

        lblThongBaoKetQua.setBackground(new Color(0, 204, 102));
        lblThongBaoKetQua.setFont(new Font("Helvetica Neue", Font.BOLD, 14)); // NOI18N
        lblThongBaoKetQua.setForeground(new Color(255, 255, 255));
        lblThongBaoKetQua.setHorizontalAlignment(SwingConstants.CENTER);
        lblThongBaoKetQua.setText("2 hóa đơn");

        Table_DanhSachHoaDon.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // NOI18N
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

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        Table_DanhSachHoaDon.setRowHeight(60);
        jScrollPane1.setViewportView(Table_DanhSachHoaDon);

        btnFilter.setBackground(new Color(0, 102, 255));
        btnFilter.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // NOI18N
        btnFilter.setForeground(new Color(255, 255, 255));
        btnFilter.setText("Lọc");
        btnFilter.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        btnFilter.setBorderPainted(false);
        btnFilter.setOpaque(true);
        btnFilter.setPreferredSize(new Dimension(33, 50));
        btnFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        comboBox_Year.setBackground(new Color(220, 222, 221));
        comboBox_Year.setFont(new Font("Helvetica Neue", Font.PLAIN, 18)); // NOI18N
        comboBox_Year.setModel(new DefaultComboBoxModel<>(new String[] { "<html><br></html>", "2020", "2021", "2022", "2023", "2024", "2025" }));
        comboBox_Year.setMinimumSize(new Dimension(72, 50));
        comboBox_Year.setPreferredSize(new Dimension(74, 50));
        comboBox_Year.setSize(new Dimension(20, 20));

        btn_Xoa.setBackground(new Color(255, 0, 51));
        btn_Xoa.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // NOI18N
        btn_Xoa.setForeground(new Color(255, 255, 255));
        btn_Xoa.setText("Xóa");
        btn_Xoa.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        btn_Xoa.setBorderPainted(false);
        btn_Xoa.setOpaque(true);
        btn_Xoa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_XoaActionPerformed(evt);
            }
        });

        btn_LamMoi.setBackground(new Color(255, 153, 0));
        btn_LamMoi.setForeground(new Color(255, 255, 255));
        btn_LamMoi.setText("Làm mới");
        btn_LamMoi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_LamMoiActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(63, 63, 63)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addComponent(ComboBox_DanhMucTimKiem, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(comboBox_Month, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(10, 10, 10)
                                                                                .addComponent(lblDanhMucTimKiem)))
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(71, 71, 71)
                                                                                .addComponent(lblThongTinTraCuu))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(58, 58, 58)
                                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(comboBox_Year, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE)
                                                                                                .addGap(18, 18, 18)
                                                                                                .addComponent(btnFilter, GroupLayout.PREFERRED_SIZE, 210, GroupLayout.PREFERRED_SIZE))
                                                                                        .addComponent(txt_NhapThongTin, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE))
                                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(btn_TraCuu, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(18, 18, 18)
                                                                                .addComponent(btn_Xoa, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))))
                                                        .addComponent(lblNhapThongTin)
                                                        .addComponent(lblTieuDeLocTG))
                                                .addContainerGap(74, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnKetQuaTimKiem)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(lblThongBaoKetQua, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btn_LamMoi)
                                                .addGap(17, 17, 17))))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(73, 73, 73)
                                .addComponent(lblNhapThang)
                                .addGap(225, 225, 225)
                                .addComponent(lblNhapNam)
                                .addContainerGap(706, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1)
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
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(txt_NhapThongTin, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btn_TraCuu, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btn_Xoa, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(ComboBox_DanhMucTimKiem, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addGap(22, 22, 22)
                                .addComponent(lblTieuDeLocTG)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblNhapThang)
                                        .addComponent(lblNhapNam))
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnFilter, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comboBox_Year, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Chỉnh lại nếu cần
                                        .addComponent(comboBox_Month, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                                .addGap(72, 72, 72)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnKetQuaTimKiem)
                                        .addComponent(lblThongBaoKetQua, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btn_LamMoi))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
    }// </editor-fold>

    private void ComboBox_DanhMucTimKiemActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btn_TraCuuActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void comboBox_MonthActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void txt_NhapThongTinActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btnFilterActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btn_XoaActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btn_LamMoiActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    /**
     * Hiển thị popup chi tiết hóa đơn với hiệu ứng nền mờ (overlay).
     * @param maHoaDon Mã hóa đơn để hiển thị
     */
    private void showChiTietPopup(String maHoaDon) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) {
            return;
        }

        PopUpChiTietHoaDon chiTietPanel = new PopUpChiTietHoaDon();
        // Maybe: chiTietPanel.loadDataByMaHoaDon(maHoaDon);

        JPanel overlayPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayPanel.setOpaque(false);
        overlayPanel.add(chiTietPanel);

        topFrame.setGlassPane(overlayPanel);
        overlayPanel.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) { // More specific catches
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            ManHinhTraCuuHoaDon panel = new ManHinhTraCuuHoaDon();
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}