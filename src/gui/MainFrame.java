package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import entity.Tau;
import service.VeService;

public class MainFrame extends JFrame {
    private VeService veService;
    private JComboBox<Tau> tauComboBox;
    private JTextField khachHangField;
    private JLabel ketQuaLabel;

    public MainFrame() {
        veService = new VeService();
        initUI();
    }

    private void initUI() {
        setTitle("He thong ban ve tau");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Chọn tàu
        panel.add(new JLabel("Chon tau:"));
        tauComboBox = new JComboBox<>();
        for (Tau tau : veService.layDanhSachTau()) {
            tauComboBox.addItem(tau);
        }
        panel.add(tauComboBox);

        // Tên khách hàng
        panel.add(new JLabel("Ten khach hang:"));
        khachHangField = new JTextField();
        panel.add(khachHangField);

        // Nút mua vé
        JButton muaVeButton = new JButton("Mua ve");
        muaVeButton.addActionListener(this::muaVe);
        panel.add(muaVeButton);

        // Kết quả
        ketQuaLabel = new JLabel("Ket qua: ");
        panel.add(ketQuaLabel);

        add(panel);
    }

    private void muaVe(ActionEvent e) {
        Tau selectedTau = (Tau) tauComboBox.getSelectedItem();
        String khachHang = khachHangField.getText().trim();

        if (khachHang.isEmpty()) {
            ketQuaLabel.setText("Ket qua: Vui long nhap ten khach hang");
            return;
        }

        String ketQua = veService.muaVe(selectedTau.getId(), khachHang, 0);
        ketQuaLabel.setText("Ket qua: " + ketQua);
    }
}