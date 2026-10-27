package pims_project.gui;

import pims_project.models.SaleItem;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BillWindow extends JFrame {

    public BillWindow(String cashierName, List<SaleItem> items, double total) {
        initComponents(cashierName, items, total);
        setLocationRelativeTo(null);
    }

    private void initComponents(String cashierName, List<SaleItem> items, double total) {
        setTitle("HealthFirst — Sales Receipt");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 600);
        setResizable(false);

        // Main panel with monospaced receipt look
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Build the receipt as monospaced text
        StringBuilder receipt = new StringBuilder();

        receipt.append("========================================\n");
        receipt.append("         HEALTHFIRST PHARMACY\n");
        receipt.append("      123 Main Street, Johannesburg\n");
        receipt.append("          Tel: 011-123-4567\n");
        receipt.append("========================================\n\n");

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        receipt.append("Date:   ").append(timestamp).append("\n");
        receipt.append("Cashier: ").append(cashierName).append("\n\n");

        receipt.append("----------------------------------------\n");
        receipt.append(String.format("%-20s %5s %10s%n", "Item", "Qty", "Subtotal"));
        receipt.append("----------------------------------------\n");

        for (SaleItem item : items) {
            String name = item.getMedicineName() != null ? item.getMedicineName() : "Medicine #" + item.getMedicineId();
            if (name.length() > 20) name = name.substring(0, 20);

            receipt.append(String.format("%-20s %5d %10.2f%n",
                    name,
                    item.getQuantitySold(),
                    item.getSubtotal()));
        }

        receipt.append("----------------------------------------\n");
        receipt.append(String.format("%-20s %5s %10.2f%n", "TOTAL", "", total));
        receipt.append("========================================\n\n");
        receipt.append("      Thank you for your business!\n");
        receipt.append("         Get well soon. :)\n");

        JTextArea txtReceipt = new JTextArea(receipt.toString());
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtReceipt.setEditable(false);
        txtReceipt.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(txtReceipt);

        panel.add(scroll, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton btnSave = new JButton("Save Bill");
        btnSave.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        btnSave.addActionListener(e -> saveBill(receipt.toString()));

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnClose);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    /** Save the receipt as a .txt file using a file chooser. */
    private void saveBill(String content) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("receipt_"
                + System.currentTimeMillis() + ".txt"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter writer = new java.io.FileWriter(chooser.getSelectedFile())) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "Bill saved successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}