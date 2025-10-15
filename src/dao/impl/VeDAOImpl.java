package dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dao.VeDAO;
import entity.Ve;

public class VeDAOImpl implements VeDAO {
    private List<Ve> danhSachVe;
    private int nextId;

    public VeDAOImpl() {
        danhSachVe = new ArrayList<>();
        nextId = 1;
    }

    @Override
    public Ve taoVe(Ve ve) {
        ve.setId(nextId++);
        danhSachVe.add(ve);
        return ve;
    }

    @Override
    public List<Ve> layTheoTau(int idTau) {
        return danhSachVe.stream()
                .filter(ve -> ve.getIdTau() == idTau)
                .collect(Collectors.toList());
    }
}