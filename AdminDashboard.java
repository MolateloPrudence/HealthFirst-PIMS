package pims_project.gui;

import pims_project.database.MedicineDAO;
import pims_project.database.SupplierDAO;
import pims_project.database.UserDAO;
import pims_project.models.Medicine;
import pims_project.models.Supplier;
import pims_project.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final User currentUser;

    // ---- Medicines tab ----
    private JTable tblMedicines;
    private DefaultTableModel medicineModel;

    // ---- Suppliers tab ----
    private JTable tblSuppliers;
    private DefaultTableModel supplierModel;

    // ---- Users tab ----
    private JTable tblUsers;
    private DefaultTableModel userModel;

    public AdminDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
        refreshMedicines();
        refreshSuppliers();
        refreshUsers();
    }

    private void initComponents() {
        setTitle("HealthFirst PIMS — Admin Dashboard  [" + currentUser.getFullName() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLayout(new BorderLayout());

        // ============ TOP BAR ============
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("Administrator Panel");
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 22));
        topBar.add(lblTitle, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JLabel lblUser = new JLabel("Logged in: " + currentUser.getFullName());
        lblUser.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        userPanel.add(lblUser);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        btnLogout.addActionListener(e -> logout());
        userPanel.add(btnLogout);

        topBar.add(userPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ============ TABS ============
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        tabs.addTab("Manage Medicines", createMedicinesTab());
        tabs.addTab("Manage Suppliers", createSuppliersTab());
        tabs.addTab("Manage Users", createUsersTab());
        tabs.addTab("Reports", createReportsTab());

        add(tabs, BorderLayout.CENTER);
    }

    // ================================================================
    // TAB 1: MEDICINES
    // ================================================================
    private JPanel createMedicinesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        medicineModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Company", "Type", "Price",
                        "Stock", "Reorder", "Expiry", "Supplier"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblMedicines = new JTable(medicineModel);
        tblMedicines.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        tblMedicines.setRowHeight(22);
        tblMedicines.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(tblMedicines);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAdd = new JButton("Add Medicine");
        btnAdd.addActionListener(e -> openMedicineDialog(null));

        JButton btnEdit = new JButton("EditID Selected");
        btnEdit.addActionListener(e -> {
            int row = tblMedicines.getSelectedRow();
           if (row  == -1){
                JOptionPane.showMessageDialog(this, "Select a medicine first.");
                return;
            }
            int id = (int) medicineModel.getValueAt(row, 0);
            Medicine med = new MedicineDAO().getMedicineById(id);
            if (med != null) openMedicineDialog(med);
        });

        JButton btnDelete = new JButton("Delete Selected");
        btnDelete.addActionListener(e -> {
            int row = tblMedicines.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a medicine first.");
                return;
            }
            int id = (int) medicineModel.getValueAt(row, 0);
            String name = (String) medicineModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete \"" + name + "\"? This cannot be undone.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (new MedicineDAO().deleteMedicine(id)) {
                    refreshMedicines();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Delete failed. This medicine may be referenced by existing sales.");
                }
            }
        });

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshMedicines());

        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshMedicines() {
        medicineModel.setRowCount(0);
        List<Medicine> list = new MedicineDAO().getAllMedicines();
        for (Medicine m : list) {
            medicineModel.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getName(),
                    m.getCompany(),
                    m.getMedicineType(),
                    String.format("R%.2f", m.getPrice()),
                    m.getQuantityInStock(),
                    m.getReorderLevel(),
                    m.getExpiryDate(),
                    m.getSupplierName() != null ? m.getSupplierName() : "—"
            });
        }
    }

    // ================================================================
    // TAB 2: SUPPLIERS
    // ================================================================
    private JPanel createSuppliersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        supplierModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Contact" ,"Person", "Phone", "Email", "Address"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblSuppliers = new JTable(supplierModel);
        tblSuppliers.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        tblSuppliers.setRowHeight(22);
        tblSuppliers.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(tblSuppliers);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAdd = new JButton("Add Supplier");
        btnAdd.addActionListener(e -> openSupplierDialog(null));

        JButton btnEdit = new JButton("Edit Selected");
        btnEdit.addActionListener(e -> {
            int row = tblSuppliers.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a supplier first.");
                return;
            }
            Supplier s = new Supplier();
            s.setSupplierId((int) supplierModel.getValueAt(row, 0));
            s.setName((String) supplierModel.getValueAt(row, 1));
            s.setContactPerson((String) supplierModel.getValueAt(row, 2));
            s.setPhone((String) supplierModel.getValueAt(row, 3));
            s.setEmail((String) supplierModel.getValueAt(row, 4));
            s.setAddress((String) supplierModel.getValueAt(row, 5));
            openSupplierDialog(s);
        });

        JButton btnDelete = new JButton("Delete Selected");
        btnDelete.addActionListener(e -> {
            int row = tblSuppliers.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a supplier first.");
                return;
            }
            int id = (int) supplierModel.getValueAt(row, 0);
            String name = (String) supplierModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete supplier \"" + name + "\"?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (new SupplierDAO().deleteSupplier(id)) {
                    refreshSuppliers();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed.");
                }
            }
        });

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshSuppliers());

        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshSuppliers() {
        supplierModel.setRowCount(0);
        List<Supplier> list = new SupplierDAO().getAllSuppliers();
        for (Supplier s : list) {
            supplierModel.addRow(new Object[]{
                    s.getSupplierId(), s.getName(), s.getContactPerson(),
                    s.getPhone(), s.getEmail(), s.getAddress()
            });
        }
    }

    // ================================================================
    // TAB 3: USERS
    // ================================================================
    private JPanel createUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        userModel = new DefaultTableModel(
                new String[]{"ID", "Username", "Full Name", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblUsers = new JTable(userModel);
        tblUsers.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        tblUsers.setRowHeight(22);
        tblUsers.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(tblUsers);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAdd = new JButton("Add User");
        btnAdd.addActionListener(e -> openUserDialog(null));

        JButton btnEdit = new JButton("Edit Selected");
        btnEdit.addActionListener(e -> {
            int row = tblUsers.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a user first.");
                return;
            }
            User u = new User();
            u.setUserId((int) userModel.getValueAt(row, 0));
            u.setUsername((String) userModel.getValueAt(row, 1));
            u.setFullName((String) userModel.getValueAt(row, 2));
            u.setRole((String) userModel.getValueAt(row, 3));
            openUserDialog(u);
        });

        JButton btnDelete = new JButton("Delete Selected");
        btnDelete.addActionListener(e -> {
            int row = tblUsers.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a user first.");
                return;
            }
            int id = (int) userModel.getValueAt(row, 0);
            String name = (String) userModel.getValueAt(row, 1);

            if (id == currentUser.getUserId()) {
                JOptionPane.showMessageDialog(this, "You cannot delete your own account.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete user \"" + name + "\"?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (new UserDAO().deleteUser(id)) {
                    refreshUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed.");
                }
            }
        });

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshUsers());

        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshUsers() {
        userModel.setRowCount(0);
        List<User> list = new UserDAO().getAllUsers();
        for (User u : list) {
            userModel.addRow(new Object[]{
                    u.getUserId(), u.getUsername(), u.getFullName(), u.getRole()
            });
        }
    }

    // ================================================================
    // TAB 4: REPORTS
    // ================================================================
    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JButton btnSales = new JButton("Sales Report");
        btnSales.setFont(new Font("Times New Roman", Font.BOLD, 16));
        btnSales.addActionListener(e -> new SalesReportWindow().setVisible(true));

        JButton btnItemWise = new JButton("Item-Wise Sales Report");
        btnItemWise.setFont(new Font("Times New Roman", Font.BOLD, 16));
        btnItemWise.addActionListener(e -> new ItemWiseReportWindow().setVisible(true));

        JButton btnLowStock = new JButton("Low Stock Report");
        btnLowStock.setFont(new Font("Times New Roman", Font.BOLD, 16));
        btnLowStock.addActionListener(e -> new LowStockReportWindow().setVisible(true));

        JButton btnExpiry = new JButton("Expiry Report (Next 30 Days)");
        btnExpiry.setFont(new Font("Times New Roman", Font.BOLD, 16));
        btnExpiry.addActionListener(e -> new ExpiryReportWindow().setVisible(true));

        panel.add(btnSales);
        panel.add(btnItemWise);
        panel.add(btnLowStock);
        panel.add(btnExpiry);

        return panel;
    }

    // ================================================================
    // DIALOGS
    // ================================================================
    private void openMedicineDialog(Medicine existing) {
        JDialog dlg = new JDialog(this, existing == null ? "Add Medicine" : "Edit Medicine", true);
        dlg.setSize(450, 500);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtName = new JTextField();
        JTextField txtCompany = new JTextField();
        JComboBox<String> cmbType = new JComboBox<>(new String[]{
                "Tablet", "Capsule", "Syrup", "Injection", "Cream"});
        JTextField txtPrice = new JTextField();
        JTextField txtStock = new JTextField();
        JTextField txtReorder = new JTextField();
        JTextField txtExpiry = new JTextField(); // yyyy-mm-dd
        JComboBox<SupplierItem> cmbSupplier = new JComboBox<>();

        // Fill supplier combo
        for (Supplier s : new SupplierDAO().getAllSuppliers()) {
            cmbSupplier.addItem(new SupplierItem(s.getSupplierId(), s.getName()));
        }

        if (existing != null) {
            txtName.setText(existing.getName());
            txtCompany.setText(existing.getCompany());
            cmbType.setSelectedItem(existing.getMedicineType());
            txtPrice.setText(String.valueOf(existing.getPrice()));
            txtStock.setText(String.valueOf(existing.getQuantityInStock()));
            txtReorder.setText(String.valueOf(existing.getReorderLevel()));
            txtExpiry.setText(existing.getExpiryDate() != null ? existing.getExpiryDate().toString() : "");
            for (int i = 0; i < cmbSupplier.getItemCount(); i++) {
                if (cmbSupplier.getItemAt(i).id == existing.getSupplierId()) {
                    cmbSupplier.setSelectedIndex(i);
                    break;
                }
            }
        }

        form.add(new JLabel("Name:")); form.add(txtName);
        form.add(new JLabel("Company:")); form.add(txtCompany);
        form.add(new JLabel("Type:")); form.add(cmbType);
        form.add(new JLabel("Price (R):")); form.add(txtPrice);
        form.add(new JLabel("Quantity in Stock:")); form.add(txtStock);
        form.add(new JLabel("Reorder Level:")); form.add(txtReorder);
        form.add(new JLabel("Expiry (yyyy-mm-dd):")); form.add(txtExpiry);
        form.add(new JLabel("Supplier:")); form.add(cmbSupplier);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dlg.dispose());

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            try {
                Medicine m = existing == null ? new Medicine() : existing;
                m.setName(txtName.getText().trim());
                m.setCompany(txtCompany.getText().trim());
                m.setMedicineType((String) cmbType.getSelectedItem());
                m.setPrice(Double.parseDouble(txtPrice.getText().trim()));
                m.setQuantityInStock(Integer.parseInt(txtStock.getText().trim()));
                m.setReorderLevel(Integer.parseInt(txtReorder.getText().trim()));
                m.setExpiryDate(java.sql.Date.valueOf(txtExpiry.getText().trim()));
                SupplierItem sel = (SupplierItem) cmbSupplier.getSelectedItem();
                if (sel != null) m.setSupplierId(sel.id);

                if (m.getName().isEmpty()) throw new IllegalArgumentException("Name is required");

                MedicineDAO dao = new MedicineDAO();
                boolean ok = (existing == null) ? dao.addMedicine(m) : dao.updateMedicine(m);

                if (ok) {
                    refreshMedicines();
                    dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Save failed.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Price and quantities must be numbers.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid date. Use format yyyy-mm-dd.");
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dlg.add(btnPanel, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void openSupplierDialog(Supplier existing) {
        JDialog dlg = new JDialog(this, existing == null ? "Add Supplier" : "Edit Supplier", true);
        dlg.setSize(400, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtName = new JTextField();
        JTextField txtContact = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextArea txtAddress = new JTextArea(3, 15);

        if (existing != null) {
            txtName.setText(existing.getName());
            txtContact.setText(existing.getContactPerson());
            txtPhone.setText(existing.getPhone());
            txtEmail.setText(existing.getEmail());
            txtAddress.setText(existing.getAddress());
        }

        form.add(new JLabel("Name:")); form.add(txtName);
        form.add(new JLabel("Contact Person:")); form.add(txtContact);
        form.add(new JLabel("Phone:")); form.add(txtPhone);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("Address:")); form.add(new JScrollPane(txtAddress));

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dlg.dispose());

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            Supplier s = existing == null ? new Supplier() : existing;
            s.setName(txtName.getText().trim());
            s.setContactPerson(txtContact.getText().trim());
            s.setPhone(txtPhone.getText().trim());
            s.setEmail(txtEmail.getText().trim());
            s.setAddress(txtAddress.getText().trim());

            if (s.getName().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Name is required.");
                return;
            }

            SupplierDAO dao = new SupplierDAO();
            boolean ok = (existing == null) ? dao.addSupplier(s) : dao.updateSupplier(s);

            if (ok) {
                refreshSuppliers();
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg, "Save failed.");
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void openUserDialog(User existing) {
        JDialog dlg = new JDialog(this, existing == null ? "Add User" : "Edit User", true);
        dlg.setSize(400, 350);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtFullName = new JTextField();
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"Admin", "Cashier"});

        if (existing != null) {
            txtUsername.setText(existing.getUsername());
            txtFullName.setText(existing.getFullName());
            cmbRole.setSelectedItem(existing.getRole());
            txtPassword.setText(existing.getPassword());
        }

        form.add(new JLabel("Username:")); form.add(txtUsername);
        form.add(new JLabel("Password:")); form.add(txtPassword);
        form.add(new JLabel("Full Name:")); form.add(txtFullName);
        form.add(new JLabel("Role:")); form.add(cmbRole);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dlg.dispose());

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            User u = existing == null ? new User() : existing;
            u.setUsername(txtUsername.getText().trim());
            u.setPassword(new String(txtPassword.getPassword()));
            u.setFullName(txtFullName.getText().trim());
            u.setRole((String) cmbRole.getSelectedItem());

            if (u.getUsername().isEmpty() || u.getPassword().isEmpty() || u.getFullName().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "All fields are required.");
                return;
            }

            UserDAO dao = new UserDAO();
            boolean ok = (existing == null) ? dao.addUser(u) : dao.updateUser(u);

            if (ok) {
                refreshUsers();
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg, "Save failed. Username might already exist.");
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ================================================================
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Log out now?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    // Inner helper class for supplier combo box
    private static class SupplierItem {
        int id;
        String name;
        SupplierItem(int id, String name) { this.id = id; this.name = name; }
        @Override
        public String toString() { return name; }
    }
}