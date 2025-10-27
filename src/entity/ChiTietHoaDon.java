package entity;

// Class này đóng gói thông tin chi tiết cần thiết cho UI (Tra cứu/Trả vé)
//Sai quá sai rồi sửa lại đi nha

public class ChiTietHoaDon {
    private String maHD; // Mã hóa đơn
    private String maVe; // Mã vé
    private  int soLuong;
    //3 tham số

    public ChiTietHoaDon(String maHD, String maVe, int soLuong) {
        this.maHD = maHD;
        this.maVe = maVe;
        this.soLuong = soLuong;
    }

    public ChiTietHoaDon() {
    }



    // *LƯU Ý: Bạn cần cập nhật VeDAOImpl để trả về đối tượng này thay vì Ve.
}