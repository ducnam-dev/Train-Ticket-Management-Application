package dao;

import entity.Ve;
import java.util.List;

/**
 * Interface VeDAO: Định nghĩa các hành vi truy cập dữ liệu cho đối tượng Vé (Ticket).
 * Dùng để đảm bảo tính nhất quán và triển khai theo mô hình DAO.
 */
public interface VeDAO {

    /**
     * Thêm một vé mới vào hệ thống (tương đương với INSERT vào CSDL).
     * @param ve Đối tượng Ve cần thêm.
     * @return Đối tượng Ve sau khi thêm (có thể đã cập nhật ID).
     */
    Ve taoVe(Ve ve);

    /**
     * Tìm kiếm chi tiết vé theo Mã vé hoặc SĐT khách hàng (cho màn hình Trả vé).
     * @param maVe Mã vé.
     * @param sdt Số điện thoại.
     * @return Đối tượng Ve chứa đầy đủ thông tin hoặc null nếu không tìm thấy.
     */
    Ve getChiTietVeChoTraVe(String maVe, String sdt);

    /**
     * Cập nhật trạng thái vé thành "Đã hủy" (Trả vé).
     * @param maVe Mã vé cần hủy.
     * @return true nếu hủy thành công, ngược lại là false.
     */
    boolean huyVe(String maVe);

    /**
     * Lấy danh sách vé dựa trên Mã tàu.
     * @param idTau ID tàu.
     * @return Danh sách các đối tượng Ve.
     */
    List<Ve> layTheoTau(int idTau);
}