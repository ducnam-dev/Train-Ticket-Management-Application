package dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import dao.TauDAO;
import entity.Tau;
import java.util.ArrayList;
import java.util.List;

public class TauDAOImpl implements TauDAO {
    private List<Tau> danhSachTau;

    public TauDAOImpl() {
        // Khởi tạo dữ liệu mẫu
        danhSachTau = new ArrayList<>();
        danhSachTau.add(new Tau(1, "T001", "HCM", "HN", 120));
        danhSachTau.add(new Tau(2, "T002", "HCM", "DN", 90));
        danhSachTau.add(new Tau(3, "T003", "HCM", "CT", 60));
    }

    @Override
    public List<Tau> layTatCa() {
        return new ArrayList<>(danhSachTau);
    }

    @Override
    public Tau timTheoId(int id) {
        return danhSachTau.stream()
                .filter(tau -> tau.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Tau timTheoMa(String maTau) {
        return danhSachTau.stream()
                .filter(tau -> tau.getMaTau().equals(maTau))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void capNhat(Tau tau) {
        for (int i = 0; i < danhSachTau.size(); i++) {
            if (danhSachTau.get(i).getId() == tau.getId()) {
                danhSachTau.set(i, tau);
                break;
            }
        }
    }
}