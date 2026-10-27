package pims_project.models;

import java.sql.Timestamp;

public class Sale {
    private int saleId;
    private Timestamp saleDate;
    private double totalAmount;
    private int userId;
    private String cashierName;

    public Sale() {
    }

    // For creating a new sale (no ID yet)
    public Sale(double totalAmount, int userId) {
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    // Full constructor with ID (used when reading from DB)
    public Sale(int saleId, Timestamp saleDate, double totalAmount, int userId) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    // Getters and Setters
    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public Timestamp getSaleDate() { return saleDate; }
    public void setSaleDate(Timestamp saleDate) { this.saleDate = saleDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }
}