package pims_project.gui;

import pims_project.database.SaleDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ItemWiseReportWindow extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public ItemWiseReportWindow() {
        initComponents();
        setLocationRelativeTo(null);
        loadData();
    }

    private void initComponents() {
        setTitle("Item-Wise Sales Report");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Sales by Medicine — Ranked by Quantity Sold",
                SwingConstants.CENTER);
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(lblTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Medicine", "Total Quantity Sold", "Total Revenue (R)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        table.setRowHeight(24);
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
        SaleDAO dao = new SaleDAO();
        List<Object[]> rows = dao.getItemWiseSales();
        for (Object[] row : rows) {
            model.addRow(new Object[]{
                    row[0],
                    row[1],
                    String.format("%.2f", (Double) row[2])
            });
        }
    }
}