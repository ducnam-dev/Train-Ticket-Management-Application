//package test;
//
//import dao.ChuyenTauDao;
//import dao.GaDao;
//import dao.NhanVienDao;
//import dao.TauDAO;
//import entity.ChuyenTau;
//import entity.Ga;
//import entity.NhanVien;
//import entity.Tau;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//
//public class TestChuyenTauDao {
//    public static void main(String[] args) {
//        ChuyenTauDao dao = new ChuyenTauDao();
//
//        // 1. Lấy tất cả
//        System.out.println("=== getAllTBChuyenTau ===");
//        List<ChuyenTau> all = dao.getAllTBChuyenTau();
//        System.out.println("Total: " + all.size());
//        all.forEach(ct -> System.out.println(ct.getMaChuyenTau() + " | " + ct.getTenChuyenTau()));
//
//        // 2. Tìm theo ga
//        System.out.println("\n=== timChuyenTauByGa(G1, G2) ===");
//        List<ChuyenTau> byGa = dao.timChuyenTauByGa("G1", "G2");
//        System.out.println("Found by ga: " + byGa.size());
//        byGa.forEach(ct -> System.out.println(ct.getMaChuyenTau() + " - " + ct.getTenChuyenTau()));
//
//        // 3. Tìm theo ngày
//        LocalDate ngayTest = LocalDate.now(); // hoặc thay bằng ngày cụ thể
//        System.out.println("\n=== timChuyenTauByNgay(" + ngayTest + ") ===");
//        List<ChuyenTau> byNgay = dao.timChuyenTauByNgay(ngayTest);
//        System.out.println("Found by ngay: " + byNgay.size());
//        byNgay.forEach(ct -> System.out.println(ct.getMaChuyenTau() + " - " + ct.getTenChuyenTau()));
//
//        // 4. Thử thêm chuyến (yêu cầu Ga/Tau/NhanVien tồn tại)
//        System.out.println("\n=== themChuyenTau ===");
//        Ga gaDi = GaDao.getGaById("G1");
//        Ga gaDen = GaDao.getGaById("G2");
//        Tau tau = TauDAO.getTauById("T1");
//        NhanVien nv = NhanVienDao.getNhanVienById("NV1");
//
//        if (gaDi == null || gaDen == null || tau == null || nv == null) {
//            System.out.println("Không đủ dữ liệu tham chiếu (Ga/Tau/NhanVien). Hãy chèn dữ liệu mẫu trước khi test insert/update.");
//        } else {
//            // tạo mã chuyến ngẫu nhiên/đảm bảo chưa tồn tại
//            String maCT = "CT_TEST_" + System.currentTimeMillis();
//            ChuyenTau ct = new ChuyenTau(
//                    maCT,
//                    "Chuyen test",
//                    ngayTest,
//                    LocalTime.of(9, 0),
//                    gaDi,
//                    gaDen,
//                    tau,
//                    ngayTest,
//                    LocalTime.of(12, 0),
//                    nv
//            );
//
//            boolean added = dao.themChuyenTau(ct);
//            System.out.println("themChuyenTau result: " + added);
//
//            // 5. Cập nhật chuyến vừa thêm (nếu thêm thành công)
//            if (added) {
//                ct.setTenChuyenTau("Chuyen test - updated");
//                boolean updated = dao.capNhatChuyenTau(ct);
//                System.out.println("capNhatChuyenTau result: " + updated);
//
//                // 6. Chuyển trạng thái
//                boolean statusChanged = dao.chuyenTrangThaiChuyenTau(maCT, "1");
//                System.out.println("chuyenTrangThaiChuyenTau result: " + statusChanged);
//
//                // 7. Kiểm tra lại bằng getAll hoặc tìm theo mã (nếu có method tìm theo mã)
//                System.out.println("Kiểm tra sau khi thêm/cập nhật:");
//                dao.getAllTBChuyenTau().stream()
//                        .filter(x -> x.getMaChuyenTau().equals(maCT))
//                        .forEach(x -> System.out.println(x.getMaChuyenTau() + " | " + x.getTenChuyenTau() + " | trangThai=" + x.getThct()));
//            }
//        }
//
//        System.out.println("\n=== Test finished ===");
//    }
//}