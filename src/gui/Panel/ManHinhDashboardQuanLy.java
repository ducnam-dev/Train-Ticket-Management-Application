/*
 * @ (#) ManHinhDashboardQuanLy.java    1.0 10/20/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
package gui.Panel;

import dao.ChuyenTauDao;
import dao.HoaDonDAO;
import entity.ChuyenTau;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Random;

// JFreeChart imports (phải có jfreechart & jcommon trong classpath)
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Lớp này tạo giao diện Dashboard (Trang chủ) dưới dạng MỘT PANEL.
 * [ĐÃ NÂNG CẤP] Thay thế bảng chuyến tàu bằng biểu đồ trung tâm.
 */
public class ManHinhDashboardQuanLy extends JPanel {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT (Giữ nguyên)
    // =================================================================================
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color COLOR_GREEN = new Color(46, 204, 113);
    private static final Color COLOR_YELLOW = new Color(241, 196, 15);
    private static final Color COLOR_ORANGE = new Color(230, 126, 34);
    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);

    // [MỚI] Khai báo DAO (Giữ nguyên)
    private ChuyenTauDao chuyenTauDao;
    private HoaDonDAO hoaDonDAO;
    // private VeDAO veDAO;

    // [MỚI] Khai báo các UI component cần cập nhật (Giữ nguyên)
    private JLabel lblDoanhThuValue;
    private JLabel lblVeDaBanValue;
    private JLabel lblChuyenTauValue;
    private JLabel lblNVOnlineValue;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public ManHinhDashboardQuanLy() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);

        // Khởi tạo DAO (Giữ nguyên)
        try {
            chuyenTauDao = new ChuyenTauDao();
            hoaDonDAO = new HoaDonDAO();
        } catch (Exception e) {
            // Nếu DAO không khởi tạo được thì vẫn hiển thị giao diện với dữ liệu mock
            e.printStackTrace();
        }

        // Tạo nội dung dashboard
        JPanel dashboardContent = createDashboardContent();
        add(dashboardContent, BorderLayout.CENTER);

        // Tải dữ liệu KPI (Giữ nguyên)
        SwingUtilities.invokeLater(() -> {
            loadKpiData();
        });
    }

    // =================================================================================
    // KHU VỰC NỘI DUNG (DASHBOARD)
    // =================================================================================

    private JPanel createDashboardContent() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel lblTitle = new JLabel("Trang chủ / Dashboard");
        lblTitle.setFont(FONT_TITLE);
        panel.add(lblTitle, BorderLayout.NORTH);
        JPanel mainArea = new JPanel(new BorderLayout(15, 15));
        mainArea.setOpaque(false);
        mainArea.add(createKpiPanel(), BorderLayout.NORTH);
        mainArea.add(createMainWidgetsPanel(), BorderLayout.CENTER);
        panel.add(mainArea, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createKpiPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 15));
        panel.setOpaque(false);
        lblDoanhThuValue = new JLabel("0 VNĐ");
        lblVeDaBanValue = new JLabel("0");
        lblChuyenTauValue = new JLabel("0");
        lblNVOnlineValue = new JLabel("0");
        panel.add(createSummaryBox("Doanh thu hôm nay", lblDoanhThuValue, COLOR_GREEN));
        panel.add(createSummaryBox("Vé đã bán", lblVeDaBanValue, COLOR_BLUE_LIGHT));
        panel.add(createSummaryBox("Chuyến tàu hôm nay", lblChuyenTauValue, COLOR_ORANGE));
        panel.add(createSummaryBox("Nhân viên online", lblNVOnlineValue, COLOR_YELLOW));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panel.setPreferredSize(new Dimension(0, 120));
        return panel;
    }

    private JPanel createSummaryBox(String title, JLabel lblValue, Color bgColor) {
        JPanel box = new JPanel(new BorderLayout(0, 5));
        box.setBackground(bgColor);
        box.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_BOLD_14);
        lblTitle.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(Color.WHITE);
        box.add(lblTitle, BorderLayout.NORTH);
        box.add(lblValue, BorderLayout.CENTER);
        return box;
    }

    /**
     * [ĐÃ SỬA] Tạo panel chứa các widget chính.
     * Dùng BorderLayout: Biểu đồ doanh thu (WEST), Biểu đồ mới (CENTER).
     */
    private JPanel createMainWidgetsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15)); // Dùng BorderLayout
        panel.setOpaque(false);

        // 1. Biểu đồ doanh thu (Bên trái)
        JPanel revenueChartPanel = createRevenueChartPanel();
        revenueChartPanel.setPreferredSize(new Dimension(480, 0)); // Đặt chiều rộng ưu tiên
        panel.add(revenueChartPanel, BorderLayout.WEST);

        // 2. Biểu đồ mới (Ở giữa)
        JPanel centerChartPanel = createCenterChartPanel(); // Hàm mới
        panel.add(centerChartPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel cho biểu đồ doanh thu 7 ngày (dùng JFreeChart)
     */
    private JPanel createRevenueChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Doanh thu 7 ngày qua",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, Color.BLACK
        ));

        // Lấy dữ liệu (mock nếu DAO không có)
        Map<String, Double> revenueMap = getLast7DaysRevenue();

        // Tạo dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        revenueMap.forEach((dayLabel, value) -> dataset.addValue(value, "Doanh thu", dayLabel));

        // Tạo biểu đồ cột
        JFreeChart barChart = ChartFactory.createBarChart(
                null,      // title (null để dùng tiêu đề của panel)
                "Ngày",    // domain axis label
                "VNĐ",     // range axis label
                dataset
        );

        // Tùy chỉnh hiển thị
        CategoryPlot plot = barChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", java.text.NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setMaximumBarWidth(0.12);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(460, 320));
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel cho biểu đồ tròn trung tâm (tỷ lệ lấp đầy chuyến)
     */
    private JPanel createCenterChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Tỷ lệ lấp đầy các chuyến hôm nay",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, Color.BLACK
        ));

        // Lấy dữ liệu (mock nếu DAO không có)
        Map<String, Integer> occupancy = getTodayOccupancyDistribution();

        DefaultPieDataset pieDataset = new DefaultPieDataset();
        occupancy.forEach(pieDataset::setValue);

        JFreeChart pieChart = ChartFactory.createPieChart(
                null, // title null để tiêu đề nằm ở panel
                pieDataset,
                true,  // legend
                true,
                false
        );

        // Tùy chỉnh PiePlot (hiển thị %)
        PiePlot piePlot = (PiePlot) pieChart.getPlot();
        piePlot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator("{0}: {1} ({2})",
                java.text.NumberFormat.getNumberInstance(), java.text.NumberFormat.getPercentInstance()));

        ChartPanel piePanel = new ChartPanel(pieChart);
        piePanel.setPreferredSize(new Dimension(700, 420));
        panel.add(piePanel, BorderLayout.CENTER);

        return panel;
    }

    // =================================================================================
    // HỖ TRỢ DỮ LIỆU (Mock / lấy từ DAO nếu có)
    // =================================================================================

    /**
     * Trả về doanh thu 7 ngày gần nhất theo Map<"dd/MM", value>.
     * Thực tế bạn nên lấy từ hoaDonDAO (theo ngày), ở đây dùng mock nếu DAO không cung cấp.
     */
    private Map<String, Double> getLast7DaysRevenue() {
        Map<String, Double> map = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        // Nếu hoaDonDAO có method lấy doanh thu theo ngày => sử dụng ở đây (ví dụ)
        // try {
        //    for (int i = 6; i >= 0; i--) {
        //        LocalDate d = LocalDate.now().minusDays(i);
        //        double rev = hoaDonDAO.getRevenueByDate(d); // ví dụ
        //        map.put(d.format(fmt), rev);
        //    }
        //    return map;
        // } catch (Exception e) { e.printStackTrace(); /* fallback to mock */ }

        // MOCK: tạo số ngẫu nhiên hợp lý (đơn vị VNĐ)
        Random rnd = new Random();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            double base = 3_000_000 + rnd.nextInt(2_500_000); // 3M - 5.5M
            map.put(d.format(fmt), base);
        }
        return map;
    }

    /**
     * Trả về phân bố lấp đầy chuyến hôm nay (ví dụ 4 nhóm).
     * Thực tế bạn nên tính từ chuyenTauDao (lấy số ghế, ghế đã bán).
     */
    private Map<String, Integer> getTodayOccupancyDistribution() {
        Map<String, Integer> map = new LinkedHashMap<>();
        // Nếu có chuyenTauDao với dữ liệu ghế => tính toán thực tế ở đây.
        // try {
        //     List<ChuyenTau> list = chuyenTauDao.getByDate(LocalDate.now());
        //     // tính % lấp đầy mỗi chuyến => gộp vào các nhóm
        // } catch (Exception e) { e.printStackTrace(); }

        // MOCK
        map.put("100% (Đầy)", 45);
        map.put("70-99%", 30);
        map.put("50-69%", 15);
        map.put("<50%", 10);
        return map;
    }

    // =================================================================================
    // CÁC HÀM TẢI DỮ LIỆU TỪ DAO
    // =================================================================================

    private void loadKpiData() {
        try {
            // --- Dữ liệu MOCK (Tạm thời) ---
            double doanhThu = 2420000;
            int veBan = 3;
            int chuyenTau = 5;
            int nvOnline = 1;
            // --- Kết thúc dữ liệu MOCK ---

            // Nếu bạn có hoaDonDAO, chuyenTauDao thì ở đây có thể gán giá trị thực
            // ex:
            // doanhThu = hoaDonDAO.getRevenueByDate(LocalDate.now());
            // veBan = hoaDonDAO.getTicketsSoldByDate(LocalDate.now());
            // chuyenTau = chuyenTauDao.countByDate(LocalDate.now());
            // nvOnline = userDao.countOnlineUsers();

            lblDoanhThuValue.setText(currencyFormat.format(doanhThu));
            lblVeDaBanValue.setText(String.valueOf(veBan));
            lblChuyenTauValue.setText(String.valueOf(chuyenTau));
            lblNVOnlineValue.setText(String.valueOf(nvOnline));

        } catch (Exception e) {
            e.printStackTrace();
            lblDoanhThuValue.setText("Lỗi");
            lblVeDaBanValue.setText("Lỗi");
            lblChuyenTauValue.setText("Lỗi");
            lblNVOnlineValue.setText("Lỗi");
        }
    }

    /**
     * Phương thức main để chạy ứng dụng (kiểm thử)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Dùng giao diện mặc định
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Kết nối DB nếu cần (giữ nguyên)
                // ConnectDB.getInstance().connect();
                System.out.println("Khởi tạo giao diện Dashboard (bỏ qua kết nối CSDL ở demo).");
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Kiểm thử Dashboard (JPanel)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ManHinhDashboardQuanLy dashboardPanel = new ManHinhDashboardQuanLy();
            frame.add(dashboardPanel);
            frame.setSize(1400, 900);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
