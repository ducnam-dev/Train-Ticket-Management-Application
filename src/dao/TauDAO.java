package dao;

import java.util.List;

import entity.Tau;


public interface TauDAO {
	List<Tau> layTatCa();
	Tau timTheoId(int id);
	Tau timTheoMa(String maTau);
	void capNhat(Tau tau);
	static Tau getTauById(String maTau) {
		// TODO Auto-generated method stub
		return null;
	}
}