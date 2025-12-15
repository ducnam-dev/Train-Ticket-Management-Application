
import dao.KhuyenMaiDAO;
import entity.KhuyenMai;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MainTestDAO {

    public static void main(String[] args) {
        KhuyenMaiDAO dao = new KhuyenMaiDAO();

        // --- 1. KIỂM TRA PHƯƠNG THỨC LẤY KM HOẠT ĐỘNG ---
        System.out.println("--- 1. Danh sách Khuyến Mãi HOẠT ĐỘNG ---");
        List<KhuyenMai> activeList = dao.layTatCaKMHoatDong();
        if (activeList.isEmpty()) {
            System.out.println("Không có Khuyến mãi nào đang hoạt động theo thời gian hiện tại.");
        } else {
            activeList.forEach(km -> {
                System.out.printf("  [ACTIVE] %s - %s. Giảm: %s. Trạng thái CSDL: %s%n",
                        km.getMaKM(), km.getTenKM(), km.getGiaTriGiam(), km.getTrangThai());
            });
        }

        // --- 2. KIỂM TRA TÌM KIẾM THEO MÃ ---
        String maKMCanTim = "KM001";
        System.out.printf("%n--- 2. Tìm kiếm Khuyến mãi theo Mã: %s ---%n", maKMCanTim);
        KhuyenMai foundKm = dao.layKhuyenMaiTheoMa(maKMCanTim);
        if (foundKm != null) {
            System.out.printf("  [FOUND] Mã: %s, Tên: %s, DK: %s%n",
                    foundKm.getMaKM(), foundKm.getTenKM(), foundKm.getDkApDung());
        } else {
            System.out.println("  Không tìm thấy Khuyến mãi có mã: " + maKMCanTim);
        }

        // --- 3. KIỂM TRA THÊM MỚI ---
        String newMaKM = "KM999";
        System.out.printf("%n--- 3. Thêm mới Khuyến mãi: %s ---%n", newMaKM);
        KhuyenMai newKm = new KhuyenMai(
                newMaKM,
                "KM Test Thêm Mới 15%",
                "PHAN_TRAM_GIA",
                new BigDecimal("15.00"),
                "MIN_GIA",
                new BigDecimal("500000.00"),
                LocalDateTime.now().plusDays(1), // Ngày bắt đầu: Ngày mai (TrangThai KHONG_HOAT_DONG)
                LocalDateTime.now().plusMonths(1),
                "KHONG_HOAT_DONG" // Trạng thái thủ công
        );

        if (dao.themKhuyenMai(newKm)) {
            System.out.println("  Thêm mới thành công! Tình trạng ban đầu: " + newKm.getTrangThai());
        } else {
            System.out.println("  Thêm mới thất bại.");
        }

        // --- 4. KIỂM TRA CHỈNH SỬA ---
        System.out.printf("%n--- 4. Cập nhật Khuyến mãi: %s ---%n", newMaKM);
        if (foundKm != null) { // Sử dụng KM tìm thấy ở bước 2
            // Đổi tên và thay đổi trạng thái (Ví dụ: Tạm dừng thủ công)
            foundKm.setTenKM(foundKm.getTenKM() + " (Đã Sửa)");
            foundKm.setTrangThai("KHONG_HOAT_DONG");

            if (dao.suaKhuyenMai(foundKm)) {
                System.out.println("  Cập nhật thành công! Trạng thái mới: " + foundKm.getTrangThai());
            } else {
                System.out.println("  Cập nhật thất bại.");
            }
        }
    }
}