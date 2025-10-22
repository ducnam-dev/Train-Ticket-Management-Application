/*
 * @ (#) QuanLyDashBoard.java    1.0 10/15/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * @description
 *@author: Viet Hung
 *@date: 10/15/2025
 *@version:  1.0
 */
public class QuanLyDashBoard extends JFrame implements ActionListener {
        private CardLayout cardLayout;
        private JPanel contentPanel;
        private JButton btnBanVe, btnDoiVe, btnTraCuu, btnDashBoard;
        private final Color ACTIVE_COLOR = Color.WHITE;
        private final Color INACTIVE_COLOR = new Color(30, 144, 255); // Xanh dương
    private JButton btnQLTK;

    public QuanLyDashBoard() {
            setTitle("Hệ thống Quản lý Bán vé Tàu");
            setSize(1200, 800);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Sử dụng BorderLayout cho JFrame
            setLayout(new BorderLayout());

            initMenuPanel();
            initContentPanel();
            initEventHandlers();

            // Hiển thị trang ManHinhBanVe ngay khi khởi động (theo yêu cầu màn hình)
            cardLayout.show(contentPanel, "banVe");

            //full screen
//        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize window
//        setUndecorated(true);

            setVisible(true);
        }

        // --- 1. Panel Menu bên trái (Cố định) ---
        private void initMenuPanel() {
            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
            menuPanel.setPreferredSize(new Dimension(180, getHeight()));
            menuPanel.setBackground(new Color(30, 144, 255)); // Màu nền menu

            menuPanel.add(createLogoPanel());
            menuPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Khoảng cách

            // Tạo các nút điều hướng
            btnDashBoard = createMenuItem("Dash board", false);
            btnBanVe = createMenuItem("Bán vé", true);
            btnDoiVe = createMenuItem("Đổi vé", false);
            btnTraCuu = createMenuItem("Tra cứu vé", false);
            btnQLTK = createMenuItem("Quản Lý Tài Khoản", false);

            menuPanel.add(btnDashBoard);
            menuPanel.add(btnBanVe);
            menuPanel.add(btnDoiVe);
            menuPanel.add(btnTraCuu);
            menuPanel.add(btnQLTK);

            menuPanel.add(Box.createVerticalGlue()); // Đẩy các nút lên trên

            // Nút Đăng xuất
            JButton btnLogout = createMenuItem("Đăng xuất", false);
            menuPanel.add(btnLogout);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            add(menuPanel, BorderLayout.WEST);
        }

        // Tạo Panel cho Logo
        private JPanel createLogoPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.setBackground(new Color(30, 144, 255));
            JLabel lblLogo = new JLabel("GA XE", SwingConstants.CENTER);
            lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
            lblLogo.setForeground(Color.WHITE);
            panel.add(lblLogo);
            return panel;
        }

        // Hàm tạo nút menu (tùy chỉnh màu khi active/inactive)
        private JButton createMenuItem(String text, boolean isActive) {
            JButton button = new JButton(text);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            button.setBackground(isActive ? ACTIVE_COLOR : INACTIVE_COLOR);
            button.setForeground(isActive ? Color.BLACK : Color.WHITE);
            button.setBorder(new EmptyBorder(10, 10, 10, 10));
            button.setFocusPainted(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            return button;
        }

        // --- 2. Panel Nội dung Chính (Có thể thay đổi bằng CardLayout) ---
        private void initContentPanel() {
            cardLayout = new CardLayout();
            contentPanel = new JPanel(cardLayout);

            // Thêm các "trang" nội dung vào CardLayout
            contentPanel.add(new ManHinhBanVe());
            // Đây là nơi bạn sẽ đặt lớp BanVeUI (đã vẽ ở câu hỏi trước)
            contentPanel.add(new ManHinhBanVe(), "banVe");
            contentPanel.add(new ManHinhDoiVe(), "doiVe");
            contentPanel.add(new TraCuuPanel(), "traCuu");

            add(contentPanel, BorderLayout.CENTER);
        }

        private void initEventHandlers() {
            btnBanVe.addActionListener(this);
            btnDoiVe.addActionListener(this);
            btnTraCuu.addActionListener(this);
            btnDashBoard.addActionListener(this);
            btnQLTK.addActionListener(this);
        }

        // --- 3. Xử lý sự kiện (Chuyển đổi trang) ---
        @Override
        public void actionPerformed(ActionEvent e) {
            // Thiết lập lại màu cho tất cả các nút về INACTIVE
            btnBanVe.setBackground(INACTIVE_COLOR);
            btnBanVe.setForeground(Color.WHITE);
            btnDoiVe.setBackground(INACTIVE_COLOR);
            btnDoiVe.setForeground(Color.WHITE);
            btnTraCuu.setBackground(INACTIVE_COLOR);
            btnTraCuu.setForeground(Color.WHITE);

            Object src = e.getSource();

            // Chuyển đổi trang và đặt màu nút ACTIVE
            if (src == btnDashBoard) {
                // Show dashboard panel (add to contentPanel if needed)
                ((JButton) src).setBackground(ACTIVE_COLOR);
                ((JButton) src).setForeground(Color.BLACK);
            }
            else if (src == btnBanVe) {
                cardLayout.show(contentPanel, "banVe");
                ((JButton) src).setBackground(ACTIVE_COLOR);
                ((JButton) src).setForeground(Color.BLACK);
            } else if (src == btnDoiVe) {
                cardLayout.show(contentPanel, "doiVe");
                ((JButton) src).setBackground(ACTIVE_COLOR);
                ((JButton) src).setForeground(Color.BLACK);
            } else if (src == btnTraCuu) {
                cardLayout.show(contentPanel, "traCuu");
                ((JButton) src).setBackground(ACTIVE_COLOR);
                ((JButton) src).setForeground(Color.BLACK);
            }
            else if (src == btnQLTK) {
                // Show account management panel (add to contentPanel if needed)
                ((JButton) src).setBackground(ACTIVE_COLOR);
                ((JButton) src).setForeground(Color.BLACK);
            }
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new gui.QuanLyDashBoard());
        }
    }
