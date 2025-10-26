package gui.Panel;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.*;

/**
 *
 * @author laptoptt
 */
public class PopUpChiTietHoaDon extends JPanel {

    /**
     * Creates new form PopUpChiTietHoaDon
     */

    // Variables declaration - do not modify
    private JTable BangChiTietHanhKhach;
    private JButton btnDongChiTietHoaDon, btnInHoaDon, btnSoLuongVe;
    private JLabel jLabel10, jLabel12, jLabel15, jLabel18, jLabel19, jLabel20, jLabel21, jLabel22, jLabel25, jLabel26, jLabel27, jLabel29, jLabel3, jLabel30, jLabel31, jLabel32, jLabel33, jLabel34, jLabel35, jLabel4, jLabel5;
    private JLabel lblEmail, lblGaDen, lblGaDi, lblGioDuKien, lblGioKhoiHanh, lblKMchoHoaDon, lblMaChuyen, lblNgayDuKien, lblNgayKhoiHanh, lblNgayLapHD, lblNguoiThanhToan, lblNoiDungKhuyenMai, lblPhuongThucThanhToan, lblSoDienThoai, lblSoHieuTau, lblSoTienPhaiThanhToan, lblTenHanhKhach, lblTenNguoiLap, lblTieuDeHanhKhach, lblTieuDeMaHoaDon, lblTongTien;
    private JPanel jPanel1, jPanel2, jPanel3, jPanel4;
    private JScrollPane jScrollPane1;
    // End of variables declaration

    public PopUpChiTietHoaDon() {
        initComponents();

        // --- BẮT ĐẦU CODE CUSTOM CỦA BẠN ---

        // 1. Đường dẫn đến ảnh của bạn
        String imagePath_Train = "/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/train.png";
        String imagePath_Passenger = "/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/hanhkhach.png";
        String imagePath_Telephone = "/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/telephone.png";
        String imagePath_Email = "/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/email.png";
        String imagePath_Passengers = "/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/nhieuhanhkhach.png";
        String imagePath_Transaction = "/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/payment-success.png";
        String imagePath_PaymentMethod = "/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/payment-method.png";
        String imagePath_Calendar = "/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/schedule.png";


        // 2. Đặt kích thước mới bạn muốn (ví dụ: 24x24)
        // 3. Gọi hàm resize
        ImageIcon resizedTrainIcon = resizeIcon(imagePath_Train, 28, 28);
        ImageIcon resizedPassengerIcon = resizeIcon(imagePath_Passenger,20, 20 );
        ImageIcon resizedTelephoneIcon = resizeIcon(imagePath_Telephone,20, 20 );
        ImageIcon resizedEmailIcon = resizeIcon(imagePath_Email,20, 20 );
        ImageIcon resizedPassengersIcon = resizeIcon(imagePath_Passengers,25, 25 );
        ImageIcon resizedTransactionIcon = resizeIcon(imagePath_Transaction,30, 30 );
        ImageIcon resizedPaymentMethodIcon = resizeIcon(imagePath_PaymentMethod,20, 20 );
        ImageIcon resizedCalendarIcon = resizeIcon(imagePath_Calendar, 20, 20);


        // 4. Đặt lại icon cho jLabel1
        // Dòng này sẽ GHI ĐÈ lên icon to mà NetBeans đã set
        lblTieuDeMaHoaDon.setIcon(resizedTrainIcon);
        jLabel25.setIcon(resizedPassengerIcon);
        lblSoDienThoai.setIcon(resizedTelephoneIcon);
        lblEmail.setIcon(resizedEmailIcon);
        jLabel18.setIcon(resizedPassengersIcon);
        lblSoTienPhaiThanhToan.setIcon(resizedTransactionIcon);
        jLabel27.setIcon(resizedPaymentMethodIcon);
        lblNgayLapHD.setIcon(resizedCalendarIcon);

        // Giả sử nút của bạn tên là btnInHoaDon (thay tên cho đúng)
        btnInHoaDon.setOpaque(true);
        btnInHoaDon.setBorderPainted(false);

        // Thay 'btnDong' bằng tên biến của nút "Đóng"
        btnDongChiTietHoaDon.setOpaque(true);
        btnDongChiTietHoaDon.setBorderPainted(false);

        // --- BẮT ĐẦU CODE CUSTOM BẢNG (PHIÊN BẢN 7 CỘT) ---

        // 1. TÙY CHỈNH TIÊU ĐỀ (HEADER)
        JTableHeader header = BangChiTietHanhKhach.getTableHeader();

        // 1a. Đặt màu nền xám nhẹ cho header
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.BLACK); // Đặt màu chữ là màu đen

        // 1b. Đặt font chữ in đậm, cỡ 15
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 15));

        // 1c. Căn giữa TẤT CẢ tiêu đề cột
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // 2. TÙY CHỈNH CÁC Ô DỮ LIỆU (DATA CELLS)

        // 2a. Tạo một renderer để CĂN GIỮA DỮ LIỆU
        DefaultTableCellRenderer centerDataRenderer = new DefaultTableCellRenderer();
        centerDataRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Lấy model cột để chỉnh
        TableColumnModel columnModel = BangChiTietHanhKhach.getColumnModel();

        // 2b. Áp dụng renderer CĂN GIỮA cho các cột bạn muốn
        // Cột 0, 3, 4, 5, 6
        columnModel.getColumn(0).setCellRenderer(centerDataRenderer); // STT
        columnModel.getColumn(2).setCellRenderer(centerDataRenderer);
        columnModel.getColumn(4).setCellRenderer(centerDataRenderer); // Giá vé
        columnModel.getColumn(5).setCellRenderer(centerDataRenderer); // Khuyến mãi (%)
        columnModel.getColumn(6).setCellRenderer(centerDataRenderer); // Thành tiền

        // 3. TÙY CHỈNH ĐỘ RỘNG CỘT

        // 3a. Cho 2 cột chính RỘNG RA
        columnModel.getColumn(1).setPreferredWidth(155); // Họ và tên
        columnModel.getColumn(2).setPreferredWidth(150); // Mã định danh/CCCD

        // 3b. THU GỌN các cột còn lại cho vừa đủ
        columnModel.getColumn(0).setPreferredWidth(40);  // STT
        columnModel.getColumn(0).setMaxWidth(50);       // STT (đặt chiều rộng tối đa)
        columnModel.getColumn(3).setPreferredWidth(80); // Loại vé
        columnModel.getColumn(4).setPreferredWidth(70); // Giá vé
        columnModel.getColumn(5).setPreferredWidth(110); // Khuyến mãi (%)
        //columnModel.getColumn(5).setPreferredWidth(70);//Thành tiền

        // 4. BỎ VIỀN Ô VÀ TẠO KHOẢNG CÁCH
        // 4a. Tắt các đường kẻ viền (grid)
        BangChiTietHanhKhach.setShowGrid(false);

        // 4b. Tạo khoảng cách 8px ngang giữa các cột
        BangChiTietHanhKhach.setIntercellSpacing(new Dimension(8, 0));

        // --- KẾT THÚC CODE CUSTOM BẢNG ---

        // --- KẾT THÚC CODE CUSTOM ---
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        // 1. Tải ảnh gốc
        ImageIcon originalIcon = new ImageIcon(path);

        // 2. Lấy Image từ icon
        Image originalImage = originalIcon.getImage();

        // 3. Thay đổi kích thước (chọn SCALE_SMOOTH là cân bằng nhất)
        //    Nếu bạn muốn nó "sắc" và "vỡ" hơn, dùng: Image.SCALE_REPLICATE
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // 4. Trả về icon mới
        return new ImageIcon(resizedImage);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new JPanel();
        lblTieuDeMaHoaDon = new javax.swing.JLabel();
        jPanel2 = new JPanel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        lblMaChuyen = new JLabel();
        lblNgayKhoiHanh = new JLabel();
        lblGaDi = new JLabel();
        lblGioKhoiHanh = new JLabel();
        jLabel10 = new JLabel();
        lblGaDen = new JLabel();
        jLabel12 = new JLabel();
        lblNgayDuKien = new JLabel();
        lblGioDuKien = new JLabel();
        jLabel15 = new JLabel();
        lblSoHieuTau = new JLabel();
        jPanel3 = new JPanel();
        jLabel18 = new JLabel() {
            {
                setOpaque(false);
            }

            @Override
            protected void paintComponent(java.awt.Graphics g) {
                Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. VẼ NỀN BO TRÒN:
                // Lấy màu nền (background) mà bạn set trong Properties
                g2.setColor(getBackground());

                // Vẽ nền bo tròn (số 5 là độ bo bạn yêu cầu)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.dispose();

                super.paintComponent(g);
            }
        };
        jLabel19 = new JLabel();
        jLabel20 = new JLabel();
        jLabel21 = new JLabel();
        lblTenHanhKhach = new JLabel();
        lblEmail = new JLabel();
        lblSoDienThoai = new JLabel();
        jLabel25 = new JLabel() {
            {
                setOpaque(false);
            }

            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. VẼ NỀN BO TRÒN:
                // Lấy màu nền (background) mà bạn set trong Properties
                g2.setColor(getBackground());

                // Vẽ nền bo tròn (số 5 là độ bo bạn yêu cầu)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.dispose();

                super.paintComponent(g);
            }
        };
        jScrollPane1 = new javax.swing.JScrollPane();
        BangChiTietHanhKhach = new javax.swing.JTable();
        btnSoLuongVe = new javax.swing.JButton();
        lblTieuDeHanhKhach = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblNgayLapHD = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        lblSoTienPhaiThanhToan = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        lblPhuongThucThanhToan = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        lblTenNguoiLap = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        lblKMchoHoaDon = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        lblTongTien = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lblNguoiThanhToan = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        lblNoiDungKhuyenMai = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        btnDongChiTietHoaDon = new javax.swing.JButton();
        btnInHoaDon = new javax.swing.JButton();

        setBackground(new java.awt.Color(218, 218, 218));
        setForeground(new java.awt.Color(218, 218, 218));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        lblTieuDeMaHoaDon.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 24)); // NOI18N
        lblTieuDeMaHoaDon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTieuDeMaHoaDon.setIcon(new javax.swing.ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/train.png")); // NOI18N
        lblTieuDeMaHoaDon.setText("Chi tiết hóa đơn HD2109202400010005 ");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(158, 158, 158));
        jLabel3.setText("Mã chuyến :");

        jLabel4.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(158, 158, 158));
        jLabel4.setText("Ga đi :");

        jLabel5.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(158, 158, 158));
        jLabel5.setText("Ngày đi :");

        lblMaChuyen.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        lblMaChuyen.setText("C78324");

        lblNgayKhoiHanh.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        lblNgayKhoiHanh.setText("21/08/2024");

        lblGaDi.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        lblGaDi.setText("Sài Gòn");

        lblGioKhoiHanh.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        lblGioKhoiHanh.setText("08:30");

        jLabel10.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(158, 158, 158));
        jLabel10.setText("Ga đến :");

        lblGaDen.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        lblGaDen.setText("Hà Nội");

        jLabel12.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(158, 158, 158));
        jLabel12.setText("Dự kiến :");

        lblNgayDuKien.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        lblNgayDuKien.setText("23/09/2024");

        lblGioDuKien.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        lblGioDuKien.setText("06 : 45");

        jLabel15.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(158, 158, 158));
        jLabel15.setText("Số hiệu tàu :");

        lblSoHieuTau.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        lblSoHieuTau.setText("SE1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(70, 70, 70)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblMaChuyen)
                                        .addComponent(lblGaDi)
                                        .addComponent(lblNgayKhoiHanh)
                                        .addComponent(lblGioKhoiHanh))
                                .addGap(188, 188, 188)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel12)
                                                .addGap(94, 94, 94)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblGioDuKien)
                                                        .addComponent(lblNgayDuKien)))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(84, 84, 84))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jLabel15)
                                                                .addGap(68, 68, 68)))
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblSoHieuTau)
                                                        .addComponent(lblGaDen))
                                                .addGap(28, 28, 28)))
                                .addContainerGap(80, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(14, 14, 14)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel3)
                                                        .addComponent(lblMaChuyen)))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel15)
                                                        .addComponent(lblSoHieuTau))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(lblGaDi)
                                                        .addComponent(jLabel4))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblNgayKhoiHanh)
                                                        .addComponent(jLabel5))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblGioKhoiHanh))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel10)
                                                        .addComponent(lblGaDen))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(lblNgayDuKien)
                                                        .addComponent(jLabel12))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblGioDuKien)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel18.setBackground(new java.awt.Color(100, 100, 100));
        jLabel18.setFont(new java.awt.Font("Oriya Sangam MN", 0, 16)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setIcon(new javax.swing.ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/nhieuhanhkhach.png")); // NOI18N
        jLabel18.setText("Hành khách ");
        jLabel18.setSize(new java.awt.Dimension(17, 60));

        jLabel19.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(158, 158, 158));
        jLabel19.setText("Họ tên :");

        jLabel20.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(158, 158, 158));
        jLabel20.setText("Email :");

        jLabel21.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(158, 158, 158));
        jLabel21.setText("Số điện thoại :");

        lblTenHanhKhach.setFont(new java.awt.Font("Kailasa", 0, 17)); // NOI18N
        lblTenHanhKhach.setText("Nguyễn Văn Nam");

        lblEmail.setFont(new java.awt.Font("Kailasa", 0, 17)); // NOI18N
        lblEmail.setIcon(new javax.swing.ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/email.png")); // NOI18N
        lblEmail.setText("abcxyz@gmail.com");

        lblSoDienThoai.setFont(new java.awt.Font("Kailasa", 0, 17)); // NOI18N
        lblSoDienThoai.setIcon(new javax.swing.ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/telephone.png")); // NOI18N
        lblSoDienThoai.setText("0123456789");

        jLabel25.setBackground(new java.awt.Color(100, 100, 100));
        jLabel25.setFont(new java.awt.Font("Oriya Sangam MN", 0, 16)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/hanhkhach.png")); // NOI18N
        jLabel25.setText(" Người đặt vé  ");

        BangChiTietHanhKhach.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        BangChiTietHanhKhach.setFont(new java.awt.Font("Helvetica Neue", 0, 15)); // NOI18N
        BangChiTietHanhKhach.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        { new Integer(1), "Nguyễn Văn Nam", "044180000960", "Người lớn", "900.000đ", "0", "900.000đ"},
                        { new Integer(2), "Nguyễn Trúc Thùy Tiên", "077202000870", "Sinh Viên", "900.000đ", "10", "810.000đ"},
                        { new Integer(3), "Trần Đức Nam", "044210000102", "Trẻ em", "871.000đ", "15", "740.000đ"},
                        {null, null, null, null, null, null, null}
                },
                new String [] {
                        "STT", "Họ và tên", "Định danh/CCCD", "Loại vé", "Giá vé", "Khuyến mãi (%)", "Thành tiền"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        BangChiTietHanhKhach.setRowHeight(30);
        jScrollPane1.setViewportView(BangChiTietHanhKhach);

        btnSoLuongVe.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        btnSoLuongVe.setForeground(new java.awt.Color(204, 204, 204));
        btnSoLuongVe.setText("Số lượng: 3");
        btnSoLuongVe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSoLuongVeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel25)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel18)
                                                .addGap(39, 39, 39)
                                                .addComponent(btnSoLuongVe, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGap(41, 41, 41)
                                                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(134, 134, 134)
                                                                .addComponent(jLabel21))
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGap(56, 56, 56)
                                                                .addComponent(lblTenHanhKhach)
                                                                .addGap(78, 78, 78)
                                                                .addComponent(lblSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGap(67, 67, 67)
                                                                .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGap(43, 43, 43)
                                                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 823, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel20)
                                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblTenHanhKhach)
                                        .addComponent(lblSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnSoLuongVe))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lblTieuDeHanhKhach.setFont(new java.awt.Font("Noto Sans Tagalog", 0, 16)); // NOI18N
        lblTieuDeHanhKhach.setText("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        lblNgayLapHD.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        lblNgayLapHD.setIcon(new javax.swing.ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/schedule.png")); // NOI18N
        lblNgayLapHD.setText("19/08/2024");

        jLabel29.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(158, 158, 158));
        jLabel29.setText("Ngày lập :");

        jLabel31.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(158, 158, 158));
        jLabel31.setText("Số tiền phải thanh toán :");

        lblSoTienPhaiThanhToan.setFont(new java.awt.Font("Helvetica Neue", 0, 20)); // NOI18N
        lblSoTienPhaiThanhToan.setIcon(new javax.swing.ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/payment-success.png")); // NOI18N
        lblSoTienPhaiThanhToan.setText("2.240.000 ₫");

        jLabel33.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(158, 158, 158));
        jLabel33.setText("Khuyến mãi :");

        lblPhuongThucThanhToan.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        lblPhuongThucThanhToan.setText("Tiền mặt");

        jLabel30.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(158, 158, 158));
        jLabel30.setText("Người lập hóa đơn:");

        lblTenNguoiLap.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        lblTenNguoiLap.setText("Phạm Lan Thy");

        jLabel27.setFont(new java.awt.Font("Helvetica Neue", 0, 19)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(158, 158, 158));
        jLabel27.setIcon(new javax.swing.ImageIcon("/Users/laptoptt/Project_BanVeTau/Train-Ticket-Management-Application/src/images/payment-method.png")); // NOI18N
        jLabel27.setText(":");

        jLabel32.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(158, 158, 158));
        jLabel32.setText("Tổng tiền :");

        lblKMchoHoaDon.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        lblKMchoHoaDon.setText("5%");

        jLabel34.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(158, 158, 158));
        jLabel34.setText("Nội dung :");

        lblTongTien.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        lblTongTien.setText("2.240.000 đ");

        jLabel22.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(158, 158, 158));
        jLabel22.setText("Người thanh toán :");

        lblNguoiThanhToan.setFont(new java.awt.Font("Kailasa", 0, 16)); // NOI18N
        lblNguoiThanhToan.setText("Nguyễn Văn Nam");

        jLabel35.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(158, 158, 158));
        jLabel35.setText("Phương thức");

        lblNoiDungKhuyenMai.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        lblNoiDungKhuyenMai.setText("Giảm trực tiếp cho hóa đơn");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addComponent(jLabel30)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(lblTenNguoiLap))
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                        .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                                .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                                .addGap(11, 11, 11)
                                                                                .addComponent(lblTongTien))
                                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(lblPhuongThucThanhToan)
                                                                                        .addComponent(lblKMchoHoaDon)
                                                                                        .addComponent(lblNguoiThanhToan))))))
                                                .addGap(59, 59, 59)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(lblNoiDungKhuyenMai))
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(lblNgayLapHD, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(110, 110, Short.MAX_VALUE))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblSoTienPhaiThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblTenNguoiLap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblNgayLapHD, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblKMchoHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblNoiDungKhuyenMai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblPhuongThucThanhToan))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblNguoiThanhToan))
                                .addGap(29, 29, 29)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblSoTienPhaiThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(40, Short.MAX_VALUE))
        );

        jLabel26.setFont(new java.awt.Font("Noto Sans Tagalog", 0, 16)); // NOI18N
        jLabel26.setText("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

        btnDongChiTietHoaDon.setBackground(new java.awt.Color(255, 0, 0));
        btnDongChiTietHoaDon.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        btnDongChiTietHoaDon.setForeground(new java.awt.Color(255, 255, 255));
        btnDongChiTietHoaDon.setText("X");
        btnDongChiTietHoaDon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51)));
        btnDongChiTietHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDongChiTietHoaDonActionPerformed(evt);
            }
        });

        btnInHoaDon.setBackground(new java.awt.Color(0, 222, 40));
        btnInHoaDon.setFont(new java.awt.Font("Helvetica Neue", 1, 16)); // NOI18N
        btnInHoaDon.setForeground(new java.awt.Color(255, 255, 255));
        btnInHoaDon.setText("In hóa đơn");
        btnInHoaDon.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(29, 29, 29)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(lblTieuDeMaHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 814, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(btnDongChiTietHoaDon))
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(lblTieuDeHanhKhach, javax.swing.GroupLayout.DEFAULT_SIZE, 1073, Short.MAX_VALUE)
                                                                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(217, 217, 217)
                                                .addComponent(btnInHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(lblTieuDeMaHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btnDongChiTietHoaDon))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTieuDeHanhKhach, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnInHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 886, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>

    private void btnDongChiTietHoaDonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        // Lấy cửa sổ JFrame cha
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (topFrame != null) {
            // Lấy cái Glass Pane (chính là cái nền mờ đang chứa popup này)
            java.awt.Component glassPane = topFrame.getGlassPane();

            // Và ẩn nó đi
            glassPane.setVisible(false);
        }
    }

    private void btnSoLuongVeActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }




    public static void main(String[] args) {
        JFrame frame = new javax.swing.JFrame("Test Màn Hình Tra Cứu");

        // 2. Tạo một bản sao của JPanel (chính là class này)
        PopUpChiTietHoaDon panel = new PopUpChiTietHoaDon();

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
