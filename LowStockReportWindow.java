package pims_project.gui;

import pims_project.database.MedicineDAO;
import pims_project.models.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LowStockReportWindow extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public LowStockReportWindow() {
        initComponents();
        setLocationRelativeTo(null);
        loadData();
    }

    private void initComponents() {
        setTitle("Low Stock Report");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Medicines at or Below Reorder Level",
                SwingConstants.CENTER);
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(lblTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"ID", "Medicine", "Company", "In Stock", "Reorder Level",
                        "Shortfall", "Supplier"}, 0) {
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
        MedicineDAO dao = new MedicineDAO();
        List<Medicine> list = dao.getLowStockMedicines();

        for (Medicine m : list) {
            int shortfall = m.getReorderLevel() - m.getQuantityInStock();
            model.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getName(),
                    m.getCompany(),
                    m.getQuantityInStock(),
                    m.getReorderLevel(),
                    shortfall > 0 ? shortfall : 0,
                    m.getSupplierName() != null ? m.getSupplierName() : "—"
            });
        }
    }
}