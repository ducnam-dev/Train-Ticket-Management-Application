package dao;

import java.util.List;

import entity.Tau;


public interface TauDAO {
List<Tau> layTatCa();
Tau timTheoId(int id);
Tau timTheoMa(String maTau);
void capNhat(Tau tau);
}