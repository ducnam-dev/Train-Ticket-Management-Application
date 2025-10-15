package service;

import dao.TauDAO;
import dao.VeDAO;
import dao.impl.TauDAOImpl;
import dao.impl.VeDAOImpl;
import entity.Tau;
import entity.Ve;
import java.util.List;
import java.util.List;

public class VeService {
    private TauDAOImpl tauDAO = new TauDAOImpl();
    private VeDAOImpl veDAO = new VeDAOImpl();

    public List<Tau> layDanhSachTau() {
        return tauDAO.layTatCa();
    }

    public synchronized String muaVe(int idTau, String khachHang, double gia) {
        Tau tau = tauDAO.timTheoId(idTau);
        if (tau == null) return "Tau khong ton tai";
        if (tau.getSoGhe() <= 0) return "Het cho";

        // Giả sử giá vé cố định: 100000 VNĐ cho HCM-HN, 80000 VNĐ cho HCM-DN, 60000 VNĐ cho HCM-CT
        double giaVe = switch (tau.getMaTau()) {
            case "T001" -> 100000;
            case "T002" -> 80000;
            case "T003" -> 60000;
            default -> gia;
        };

        Ve ve = new Ve();
        ve.setIdTau(idTau);
        ve.setKhachHang(khachHang);
        ve.setSoGhe(tau.getSoGhe()); // Gán số ghế hiện tại
        ve.setGia(giaVe);

        Ve daTao = veDAO.taoVe(ve);
        if (daTao == null) return "Loi tao ve";

        tau.setSoGhe(tau.getSoGhe() - 1);
        tauDAO.capNhat(tau);

        return "Mua ve thanh cong. Ma ve: " + daTao.getId() + ", ghe: " + daTao.getSoGhe() + ", gia: " + daTao.getGia() + " VND";
    }

    public List<Ve> layVeTheoTau(int idTau) {
        return veDAO.layTheoTau(idTau);
    }
}