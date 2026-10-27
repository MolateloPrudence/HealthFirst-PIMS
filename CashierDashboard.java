package pims_project.gui;

import pims_project.database.MedicineDAO;
import pims_project.database.SaleDAO;
import pims_project.models.Medicine;
import pims_project.models.Sale;
import pims_project.models.SaleItem;
import pims_project.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CashierDashboard extends JFrame {

    private final User currentUser;

    // Search components
    private JTextField txtSearch;
    private JTable tblMedicines;
    private DefaultTableModel medicineModel;

    // Cart components
    private JTable tblCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;

    // Cart data (parallel to cart table rows)
    private final List<SaleItem> cartItems = new ArrayList<>();
    private final List<Medicine> cartMedicines = new ArrayList<>();

    public CashierDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
        loadMedicines("");
    }

    private void initComponents() {
        setTitle("HealthFirst PIMS — Cashier POS  [" + currentUser.getFullName() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLayout(new BorderLayout(10, 10));

        // ============ TOP BAR ============
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JLabel lblTitle = new JLabel("Point of Sale");
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 22));
        topBar.add(lblTitle, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        btnLogout.addActionListener(e -> logout());
        topBar.add(btnLogout, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ============ CENTER, Split between search and cart ============
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        // ---- Left side: search + medicine results ----
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Search Medicines"));

        JPanel searchBar = new JPanel(new BorderLayout(5, 5));
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        txtSearch.addActionListener(e -> loadMedicines(txtSearch.getText().trim()));

        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        btnSearch.addActionListener(e -> loadMedicines(txtSearch.getText().trim()));

        JButton btnClearSearch = new JButton("Show All");
        btnClearSearch.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        btnClearSearch.addActionListener(e -> {
            txtSearch.setText("");
            loadMedicines("");
        });

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchButtons.add(btnSearch);
        searchButtons.add(btnClearSearch);

        searchBar.add(new JLabel("Search: "), BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        searchBar.add(searchButtons, BorderLayout.EAST);
        leftPanel.add(searchBar, BorderLayout.NORTH);

        // Medicines table
        medicineModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Company", "Type", "Price", "Stock", "Expiry"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMedicines = new JTable(medicineModel);
        tblMedicines.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        tblMedicines.setRowHeight(22);
        tblMedicines.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 13));

        JScrollPane medScroll = new JScrollPane(tblMedicines);
        leftPanel.add(medScroll, BorderLayout.CENTER);

        JButton btnAddToCart = new JButton("Add Selected to Cart →");
        btnAddToCart.setFont(new Font("Times New Roman", Font.BOLD, 13));
        btnAddToCart.addActionListener(e -> addSelectedToCart());
        leftPanel.add(btnAddToCart, BorderLayout.SOUTH);

        centerPanel.add(leftPanel);

        // ---- Right side: cart ----
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        cartModel = new DefaultTableModel(
                new String[]{"Medicine", "Qty", "Price", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCart = new JTable(cartModel);
        tblCart.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        tblCart.setRowHeight(22);
        tblCart.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 13));

        JScrollPane cartScroll = new JScrollPane(tblCart);
        rightPanel.add(cartScroll, BorderLayout.CENTER);

        JPanel cartBottom = new JPanel(new BorderLayout(5, 5));

        // Total label
        lblTotal = new JLabel("Total: R0.00");
        lblTotal.setFont(new Font("Times New Roman", Font.BOLD, 20));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        cartBottom.add(lblTotal, BorderLayout.NORTH);

        // Cart buttons
        JPanel cartButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JButton btnRemoveItem = new JButton("Remove Item");
        btnRemoveItem.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        btnRemoveItem.addActionListener(e -> removeSelectedCartItem());

        JButton btnClearCart = new JButton("Clear Cart");
        btnClearCart.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        btnClearCart.addActionListener(e -> clearCart());

        JButton btnCheckout = new JButton("Generate Bill");
        btnCheckout.setFont(new Font("Times New Roman", Font.BOLD, 14));
        btnCheckout.addActionListener(e -> checkout());

        cartButtons.add(btnRemoveItem);
        cartButtons.add(btnClearCart);
        cartButtons.add(btnCheckout);

        cartBottom.add(cartButtons, BorderLayout.CENTER);
        rightPanel.add(cartBottom, BorderLayout.SOUTH);

        centerPanel.add(rightPanel);

        add(centerPanel, BorderLayout.CENTER);

        // ============ BOTTOM STATUS BAR ============
        JLabel lblStatus = new JLabel(" Ready  |  Cashier: " + currentUser.getFullName());
        lblStatus.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(lblStatus, BorderLayout.SOUTH);
    }

    // =========================================
    // Load medicines matching keyword (or all if empty)
    // =========================================
    private void loadMedicines(String keyword) {
        medicineModel.setRowCount(0);
        MedicineDAO dao = new MedicineDAO();

        List<Medicine> meds = keyword.isEmpty()
                ? dao.getAllMedicines()
                : dao.searchByName(keyword);

        for (Medicine m : meds) {
            medicineModel.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getName(),
                    m.getCompany(),
                    m.getMedicineType(),
                    String.format("R%.2f", m.getPrice()),
                    m.getQuantityInStock(),
                    m.getExpiryDate()
            });
        }
    }

    // =========================================
    // Add the highlighted medicine to the cart
    // =========================================
    private void addSelectedToCart() {
        int row = tblMedicines.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a medicine from the list first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int medicineId = (int) medicineModel.getValueAt(row, 0);

        MedicineDAO dao = new MedicineDAO();
        Medicine med = dao.getMedicineById(medicineId);

        if (med == null) {
            JOptionPane.showMessageDialog(this, "Medicine not found in database.");
            return;
        }

        if (med.getQuantityInStock() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "This medicine is out of stock.",
                    "Out of Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "Enter quantity to sell (available: " + med.getQuantityInStock() + "):",
                "1");

        if (input == null) return; // user cancelled

        int qty;
        try {
            qty = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (qty <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
            return;
        }

        if (qty > med.getQuantityInStock()) {
            JOptionPane.showMessageDialog(this,
                    "Not enough stock. Only " + med.getQuantityInStock() + " available.",
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create a SaleItem object
        SaleItem item = new SaleItem();
        item.setMedicineId(med.getMedicineId());
        item.setQuantitySold(qty);
        item.setPriceAtSale(med.getPrice());
        item.setMedicineName(med.getName());

        cartItems.add(item);
        cartMedicines.add(med);

        cartModel.addRow(new Object[]{
                med.getName(),
                qty,
                String.format("R%.2f", med.getPrice()),
                String.format("R%.2f", item.getSubtotal())
        });

        updateTotal();
    }

    // =========================================
    // Remove selected row from the cart
    // =========================================
    private void removeSelectedCartItem() {
        int row = tblCart.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item in the cart to remove.");
            return;
        }

        cartItems.remove(row);
        cartMedicines.remove(row);
        cartModel.removeRow(row);
        updateTotal();
    }

    // =========================================
    // Clear the entire cart
    // =========================================
    private void clearCart() {
        if (cartItems.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear the cart?",
                "Confirm Clear", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            cartMedicines.clear();
            cartModel.setRowCount(0);
            updateTotal();
        }
    }

    // =========================================
    // Update the total label
    // =========================================
    private void updateTotal() {
        double total = 0;
        for (SaleItem item : cartItems) {
            total += item.getSubtotal();
        }
        lblTotal.setText(String.format("Total: R%.2f", total));
    }

    // =========================================
    // Checkout, save sale and show the bill
    // =========================================
    private void checkout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Cart is empty. Add items first.",
                    "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = 0;
        for (SaleItem item : cartItems) total += item.getSubtotal();

        // Confirm
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Complete sale for R%.2f?", total),
                "Confirm Checkout", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Build Sale and items, save to DB
        Sale sale = new Sale(total, currentUser.getUserId());
        SaleDAO dao = new SaleDAO();

        int saleId = dao.saveSale(sale, cartItems);

        if (saleId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Failed to save sale. Check the console for details.",
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show the bill
        new BillWindow(currentUser.getFullName(), cartItems, total).setVisible(true);

        // Clear cart and refresh medicine list (stock changed)
        cartItems.clear();
        cartMedicines.clear();
        cartModel.setRowCount(0);
        updateTotal();
        loadMedicines(txtSearch.getText().trim());

        JOptionPane.showMessageDialog(this,
                "Sale recorded successfully! Sale ID: " + saleId);
    }

    // =========================================
    // Logout, back to login screen
    // =========================================
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Log out now?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}