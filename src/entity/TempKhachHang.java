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
}