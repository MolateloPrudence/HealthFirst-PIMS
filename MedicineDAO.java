package pims_project.database;

import pims_project.models.Medicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    // ---- Get all medicines (with supplier name joined) ----
    public List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS supplier_name " +
                     "FROM medicines m " +
                     "LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                     "ORDER BY m.medicine_id";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("getAllMedicines error: " + e.getMessage());
        }
        return list;
    }

    // ---- Search medicine by name (used in POS stock check) ----
    public List<Medicine> searchByName(String keyword) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS supplier_name FROM medicines m " +
                     "LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                     "WHERE m.name LIKE ? ORDER BY m.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("searchByName error: " + e.getMessage());
        }
        return list;
    }

    // ---- Get single medicine by ID ----
    public Medicine getMedicineById(int medicineId) {
        String sql = "SELECT m.*, s.name AS supplier_name FROM medicines m " +
                     "LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                     "WHERE m.medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, medicineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("getMedicineById error: " + e.getMessage());
        }
        return null;
    }

    // ---- Add medicine ----
    public boolean addMedicine(Medicine m) {
        String sql = "INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getName());
            ps.setString(2, m.getCompany());
            ps.setString(3, m.getMedicineType());
            ps.setDouble(4, m.getPrice());
            ps.setInt(5, m.getQuantityInStock());
            ps.setInt(6, m.getReorderLevel());
            ps.setDate(7, m.getExpiryDate());
            ps.setInt(8, m.getSupplierId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("addMedicine error: " + e.getMessage());
            return false;
        }
    }

    // ---- Update medicine ----
    public boolean updateMedicine(Medicine m) {
        String sql = "UPDATE medicines SET name = ?, company = ?, medicine_type = ?, price = ?, quantity_in_stock = ?, reorder_level = ?, expiry_date = ?, supplier_id = ? WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getName());
            ps.setString(2, m.getCompany());
            ps.setString(3, m.getMedicineType());
            ps.setDouble(4, m.getPrice());
            ps.setInt(5, m.getQuantityInStock());
            ps.setInt(6, m.getReorderLevel());
            ps.setDate(7, m.getExpiryDate());
            ps.setInt(8, m.getSupplierId());
            ps.setInt(9, m.getMedicineId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("updateMedicine error: " + e.getMessage());
            return false;
        }
    }

    // ---- Delete medicine ----
    public boolean deleteMedicine(int medicineId) {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, medicineId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("deleteMedicine error: " + e.getMessage());
            return false;
        }
    }

    // ---- Reduce stock (called after a sale) ----
    public boolean reduceStock(int medicineId, int quantitySold) {
        String sql = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantitySold);
            ps.setInt(2, medicineId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("reduceStock error: " + e.getMessage());
            return false;
        }
    }

    // ---- REPORT: Low stock medicines ----
    public List<Medicine> getLowStockMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS supplier_name FROM medicines m " +
                     "LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                     "WHERE m.quantity_in_stock <= m.reorder_level ORDER BY m.quantity_in_stock ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("getLowStockMedicines error: " + e.getMessage());
        }
        return list;
    }

    // ---- REPORT: Medicines expiring within X days ----
    public List<Medicine> getExpiringMedicines(int daysAhead) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS supplier_name FROM medicines m " +
                     "LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                     "WHERE m.expiry_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                     "ORDER BY m.expiry_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, daysAhead);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("getExpiringMedicines error: " + e.getMessage());
        }
        return list;
    }

    private Medicine mapRow(ResultSet rs) throws SQLException {
        Medicine m = new Medicine(
                rs.getInt("medicine_id"),
                rs.getString("name"),
                rs.getString("company"),
                rs.getString("medicine_type"),
                rs.getDouble("price"),
                rs.getInt("quantity_in_stock"),
                rs.getInt("reorder_level"),
                rs.getDate("expiry_date"),
                rs.getInt("supplier_id")
        );
        // supplier_name may be null if supplier was deleted
        try { m.setSupplierName(rs.getString("supplier_name")); } catch (SQLException ignored) {}
        return m;
    }

    // ---- Quick test ----
    public static void main(String[] args) {
        MedicineDAO dao = new MedicineDAO();

        System.out.println("=== All medicines ===");
        for (Medicine m : dao.getAllMedicines()) {
            System.out.printf("   %-25s | stock=%3d | price=R%.2f | expires=%s%n",
                    m.getName(), m.getQuantityInStock(), m.getPrice(), m.getExpiryDate());
        }

        System.out.println("\n=== Low stock (≤ reorder level) ===");
        for (Medicine m : dao.getLowStockMedicines()) {
            System.out.println("   " + m.getName() + " — stock: " + m.getQuantityInStock());
        }

        System.out.println("\n=== Expiring within 30 days ===");
        for (Medicine m : dao.getExpiringMedicines(30)) {
            System.out.println("   " + m.getName() + " — expires: " + m.getExpiryDate());
        }
    }
}