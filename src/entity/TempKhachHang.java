// Trong file entity/TempKhachHang.java (Giả định)
package entity;

public class TempKhachHang {
    public String maChoDat;
    public String hoTen = "";
    public String cccd = "";
    public int tuoi = 0;
    public String sdt = "";
    public String maLoaiVe = "VT01"; // Mã loại vé được gán (VT01: Người lớn)
    public ChoDat choDat; // Lưu chi tiết ghế

    public TempKhachHang(ChoDat choDat) {
        this.maChoDat = choDat.getMaCho();
        this.choDat = choDat;
        // Bắt đầu với thông tin ghế để dễ dàng truy vấn
    }

    /**
     * Phương thức này chuyển MaLoaiVe thành tên hiển thị (tương tự như trong ManHinhBanVe).
     * @return Tên loại vé dễ đọc.
     */
    public String tenLoaiVeHienThi() {
        if (maLoaiVe == null) return "Chưa xác định";
        return switch (maLoaiVe) {
            case "VT01" -> "Người lớn";
            case "VT02" -> "Trẻ em";
            case "VT03" -> "Người cao tuổi";
            case "VT04" -> "Sinh viên";
            default -> "Khác";
        };
    }

}