package dao;

import entity.Ve;

/**
 * DAO cho Vé. TODO: hiện tại là stub, cần implement phần lưu vào DB.
 */
public class VeDAO {
    public VeDAO() {}

    public boolean save(Ve ve) {
        // TODO: Lưu vé vào DB (bảng Ve), trường giaVe = ve.getGiaVe()
        System.out.println("[VeDAO] save() gọi: " + ve);
        return true;
    }
}