package pims_project.gui;

import pims_project.database.SaleDAO;
import pims_project.models.Sale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class SalesReportWindow extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtFrom;
    private JTextField txtTo;

    public SalesReportWindow() {
        initComponents();
        setLocationRelativeTo(null);
        loadSales();
    }

    private void initComponents() {
        setTitle("Sales Report");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top bar with date pickers
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

        topBar.add(new JLabel("From (yyyy-mm-dd):"));
        txtFrom = new JTextField(10);
        txtFrom.setText("2000-01-01");
        topBar.add(txtFrom);

        topBar.add(new JLabel("To (yyyy-mm-dd):"));
        txtTo = new JTextField(10);
        txtTo.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        topBar.add(txtTo);

        JButton btnFilter = new JButton("Apply Filter");
        btnFilter.addActionListener(e -> loadSales());
        topBar.add(btnFilter);

        JButton btnReset = new JButton("All Time");
        btnReset.addActionListener(e -> {
            txtFrom.setText("2000-01-01");
            txtTo.setText("2099-12-31");
            loadSales();
        });
        topBar.add(btnReset);

        add(topBar, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(
                new String[]{"Sale ID", "Date & Time", "Cashier", "Total (R)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        table.setRowHeight(22);
        table.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 13));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom: summary + buttons
        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel lblSummary = new JLabel(" ");
        lblSummary.setFont(new Font("Times New Roman", Font.BOLD, 14));
        bottom.add(lblSummary, BorderLayout.WEST);
        this.getRootPane().putClientProperty("summaryLabel", lblSummary);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton btnExport = new JButton("Export to CSV");
        btnExport.addActionListener(e -> exportCSV());
        btns.add(btnExport);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadSales());
        btns.add(btnRefresh);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        btns.add(btnClose);

        bottom.add(btns, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadSales() {
        model.setRowCount(0);
        String from = txtFrom.getText().trim();
        String to = txtTo.getText().trim();

        SaleDAO dao = new SaleDAO();
        List<Sale> sales = dao.getSalesInRange(from, to);

        double grandTotal = 0;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Sale s : sales) {
            model.addRow(new Object[]{
                    s.getSaleId(),
                    fmt.format(s.getSaleDate()),
                    s.getCashierName() != null ? s.getCashierName() : "—",
                    String.format("%.2f", s.getTotalAmount())
            });
            grandTotal += s.getTotalAmount();
        }

        JLabel lblSummary = (JLabel) this.getRootPane().getClientProperty("summaryLabel");
        if (lblSummary != null) {
            lblSummary.setText(String.format("Total Sales: %d   |   Grand Total: R%.2f",
                    sales.size(), grandTotal));
        }
    }

    private void exportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("sales_report.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (java.io.PrintWriter pw = new java.io.PrintWriter(chooser.getSelectedFile())) {
            // Header
            for (int c = 0; c < model.getColumnCount(); c++) {
                if (c > 0) pw.print(",");
                pw.print(model.getColumnName(c));
            }
            pw.println();

            // Rows
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    if (c > 0) pw.print(",");
                    pw.print(model.getValueAt(r, c));
                }
                pw.println();
            }

            JOptionPane.showMessageDialog(this, "Exported to CSV!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}