package pims_project.gui;

import pims_project.database.UserDAO;
import pims_project.models.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    // UI components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;

    public LoginFrame() {
        initComponents();
        setLocationRelativeTo(null); // center on screen
    }

    private void initComponents() {
        setTitle("HealthFirst PIMS — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setResizable(false);
        setLayout(null);

        // ---- Title label ----
        JLabel lblTitle = new JLabel("HealthFirst Pharmacy");
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 22));
        lblTitle.setBounds(90, 20, 260, 30);
        add(lblTitle);

        JLabel lblSubtitle = new JLabel("Inventory Management System");
        lblSubtitle.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        lblSubtitle.setBounds(120, 50, 220, 20);
        add(lblSubtitle);

        // ---- Username ----
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        lblUser.setBounds(50, 110, 100, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        txtUsername.setBounds(160, 110, 200, 25);
        add(txtUsername);

        // ---- Password ----
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        lblPass.setBounds(50, 150, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        txtPassword.setBounds(160, 150, 200, 25);
        add(txtPassword);

        // ---- Login button ----
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Times New Roman", Font.BOLD, 14));
        btnLogin.setBounds(90, 210, 100, 30);
        btnLogin.addActionListener(e -> doLogin());
        add(btnLogin);

        // ---- Cancel button ----
        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        btnCancel.setBounds(220, 210, 100, 30);
        btnCancel.addActionListener(e -> System.exit(0));
        add(btnCancel);

        // Allow pressing Enter in the password field to trigger login
        txtPassword.addActionListener(e -> doLogin());

        // Focus on username field when window opens
        SwingUtilities.invokeLater(() -> txtUsername.requestFocusInWindow());
    }

    /**
     * Handles the login action, validates credentials and opens the correct dashboard.
     */
    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            return;
        }

        // Login successful,redirect based on role
        JOptionPane.showMessageDialog(this,
                "Welcome, " + user.getFullName() + "!",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);

        dispose(); // close login window

        if ("Admin".equalsIgnoreCase(user.getRole())) {
            new AdminDashboard(user).setVisible(true);
        } else {
            new CashierDashboard(user).setVisible(true);
        }
    }

    public static void main(String[] args) {
        // Set a nicer look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}