package pims_project.database;

import pims_project.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ==========================================
    // 1. LOGIN, find user by username & password
    // ==========================================
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null; // login failed
    }

    // ==========================================
    // 2. GET ALL USERS (for Manage Users tab)
    // ==========================================
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("getAllUsers error: " + e.getMessage());
        }
        return users;
    }

    // ==========================================
    // 3. ADD USER (Admin creates Cashier accounts)
    // ==========================================
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getFullName());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("addUser error: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // 4. UPDATE USER (change password, role, etc.)
    // ==========================================
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, full_name = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getFullName());
            ps.setInt(5, user.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("updateUser error: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // 5. DELETE USER
    // ==========================================
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("deleteUser error: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // Helper that converts a ResultSet row into a User object
    // ==========================================
    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setFullName(rs.getString("full_name"));
        return user;
    }

    // ==========================================
    // Quick test
    // ==========================================
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();

        System.out.println("=== Test 1: Valid login ===");
        User admin = dao.login("admin", "admin123");
        System.out.println(admin != null ? "Logged in: " + admin : "Login failed");

        System.out.println("\n=== Test 2: Invalid login ===");
        User wrong = dao.login("admin", "wrongpass");
        System.out.println(wrong == null ? "Correctly rejected" : "Should have failed!");

        System.out.println("\n=== Test 3: Get all users ===");
        for (User u : dao.getAllUsers()) {
            System.out.println("   " + u);
        }
    }
}