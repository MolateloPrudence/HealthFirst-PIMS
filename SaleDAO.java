package pims_project.database;

import pims_project.models.Sale;
import pims_project.models.SaleItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    /**
     * Saves a complete sale (header + all line items) and reduces stock.
     * Returns the generated sale_id, or -1 if the transaction failed.
     */
    public int saveSale(Sale sale, List<SaleItem> items) {
        String insertSale = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";
        String insertItem = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?, ?, ?, ?)";
        String updateStock = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // begin transaction

            // 1. Insert the sale header
            int saleId;
            try (PreparedStatement ps = conn.prepareStatement(insertSale, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDouble(1, sale.getTotalAmount());
                ps.setInt(2, sale.getUserId());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No sale_id returned");
                    saleId = keys.getInt(1);
                }
            }

            // 2. Insert each line item + reduce stock
            for (SaleItem item : items) {
                try (PreparedStatement ps = conn.prepareStatement(insertItem)) {
                    ps.setInt(1, saleId);
                    ps.setInt(2, item.getMedicineId());
                    ps.setInt(3, item.getQuantitySold());
                    ps.setDouble(4, item.getPriceAtSale());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(updateStock)) {
                    ps.setInt(1, item.getQuantitySold());
                    ps.setInt(2, item.getMedicineId());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return saleId;

        } catch (SQLException e) {
            System.out.println("saveSale error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return -1;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    // ---- REPORT: Sales in a date range ----
    public List<Sale> getSalesInRange(String fromDate, String toDate) {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name AS cashier_name " +
                     "FROM sales s LEFT JOIN users u ON s.user_id = u.user_id " +
                     "WHERE DATE(s.sale_date) BETWEEN ? AND ? ORDER BY s.sale_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sale s = new Sale(
                            rs.getInt("sale_id"),
                            rs.getTimestamp("sale_date"),
                            rs.getDouble("total_amount"),
                            rs.getInt("user_id")
                    );
                    s.setCashierName(rs.getString("cashier_name"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            System.out.println("getSalesInRange error: " + e.getMessage());
        }
        return list;
    }

    // ---- REPORT: Item-wise sales (which medicine sold how many) ----
    public List<Object[]> getItemWiseSales() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT m.name, SUM(si.quantity_sold) AS total_qty, SUM(si.quantity_sold * si.price_at_sale) AS revenue " +
                     "FROM sale_items si JOIN medicines m ON si.medicine_id = m.medicine_id " +
                     "GROUP BY m.medicine_id, m.name ORDER BY total_qty DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getString("name"),
                        rs.getInt("total_qty"),
                        rs.getDouble("revenue")
                });
            }
        } catch (SQLException e) {
            System.out.println("getItemWiseSales error: " + e.getMessage());
        }
        return rows;
    }

    // ---- Quick test ----
    public static void main(String[] args) {
        SaleDAO dao = new SaleDAO();

        System.out.println("=== Sales report (all time) ===");
        for (Sale s : dao.getSalesInRange("2000-01-01", "2099-12-31")) {
            System.out.println("   Sale #" + s.getSaleId() + " — R" + s.getTotalAmount()
                    + " by " + s.getCashierName() + " on " + s.getSaleDate());
        }

        System.out.println("\n=== Item-wise sales ===");
        for (Object[] row : dao.getItemWiseSales()) {
            System.out.printf("   %-25s | qty=%d | revenue=R%.2f%n",
                    row[0], row[1], row[2]);
        }
    }
}