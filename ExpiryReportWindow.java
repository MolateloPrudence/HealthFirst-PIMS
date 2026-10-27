package pims_project.gui;

import pims_project.database.MedicineDAO;
import pims_project.models.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExpiryReportWindow extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JSpinner spinDays;

    public ExpiryReportWindow() {
        initComponents();
        setLocationRelativeTo(null);
        loadData();
    }

    private void initComponents() {
        setTitle("Expiry Report");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

        topBar.add(new JLabel("Show medicines expiring within (days):"));
        spinDays = new JSpinner(new SpinnerNumberModel(30, 1, 3650, 1));
        topBar.add(spinDays);

        JButton btnApply = new JButton("Apply");
        btnApply.addActionListener(e -> loadData());
        topBar.add(btnApply);

        add(topBar, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"ID", "Medicine", "Company", "Expiry Date",
                        "Days Left", "Stock", "Supplier"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        table.setRowHeight(22);
        table.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 13));

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());
        bottom.add(btnRefresh);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {
        model.setRowCount(0);
        int days = (Integer) spinDays.getValue();

        MedicineDAO dao = new MedicineDAO();
        List<Medicine> list = dao.getExpiringMedicines(days);

        java.time.LocalDate today = java.time.LocalDate.now();

        for (Medicine m : list) {
            long daysLeft = 0;
            if (m.getExpiryDate() != null) {
                daysLeft = java.time.temporal.ChronoUnit.DAYS.between(
                        today, m.getExpiryDate().toLocalDate());
            }

            String note = daysLeft < 0 ? "EXPIRED"
                    : daysLeft + " days";

            model.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getName(),
                    m.getCompany(),
                    m.getExpiryDate(),
                    note,
                    m.getQuantityInStock(),
                    m.getSupplierName() != null ? m.getSupplierName() : "—"
            });
        }
    }
}