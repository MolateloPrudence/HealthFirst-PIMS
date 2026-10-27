================================================================
  HEALTHFIRST PIMS — PHARMACY INVENTORY MANAGEMENT SYSTEM
  Programming 732 — Assignment Submission
================================================================

Student Name:     MOLATELO PRUDENCE MOLELE
Student Number:   402306477
Qualification:    BSc IT
Year of Study:    3rd
Semester:         2
Date Submitted:   21/09/2026

================================================================
  WHAT'S IN THIS SUBMISSION
================================================================

/database/database.sql     — Complete SQL script to create the pims_db database,
                             all 5 tables, and sample data.

/src/                      — All Java source files (package: pims_project).

/screenshots/              — Screenshots of every required screen (login,
                             admin dashboard, manage medicines, cashier POS,
                             bill window, and all 4 reports).

/executable/               — Compiled .jar executable for testing.

README.txt                 — This file.

================================================================
  HOW TO SET UP THE APPLICATION
================================================================

PREREQUISITES:
  - Java JDK 11 or higher installed
  - MySQL Server 8.0 or higher installed and running
  - MySQL Workbench (for running the database script)

STEP 1: Set up the database
  1. Open MySQL Workbench and connect as root.
  2. Open /database/database.sql
  3. Execute the entire script (Ctrl+Shift+Enter) to create the
     database, tables, and sample data.

STEP 2: Configure the database connection (if needed)
  Open the file: src/pims_project/database/DBConnection.java
  Ensure the following match your MySQL setup:
      URL      = "jdbc:mysql://localhost:3306/pims_db"
      USER     = "root"
      PASSWORD = "[YOUR MYSQL ROOT PASSWORD]"

STEP 3: Run the application
  Option A — Using the source code:
     Open the project in NetBeans and press F6.
     OR
     Right-click LoginFrame.java → Run File.

  Option B — Using the compiled executable:
     Double-click /executable/yourname_pims.jar
     (Requires Java to be installed.)

================================================================
  DEFAULT LOGIN CREDENTIALS
================================================================

ADMIN ACCOUNT
  Username: admin
  Password: admin123
  Access:   Full — Manage Medicines, Suppliers, Users, Reports

CASHIER ACCOUNT
  Username: cashier
  Password: cash123
  Access:   Point of Sale, Stock Check, Bill Generation

================================================================
  FEATURES IMPLEMENTED
================================================================

AUTHENTICATION MODULE
  - Secure login screen
  - Role-based redirection (Admin vs Cashier dashboards)
  - Error messages for invalid credentials

CASHIER MODULE
  - Searchable medicine list (live search)
  - Add-to-cart with quantity validation
  - Cart with running total
  - Remove item / clear cart
  - Sale persistence (updates stock in database)
  - Bill window with formatted receipt
  - Save bill to .txt file
  - Stock check (view-only)

ADMIN MODULE
  - Manage Medicines: full CRUD (add, edit, delete, refresh)
  - Manage Suppliers: full CRUD
  - Manage Users: full CRUD (create cashier accounts)
  - Self-deletion protection (can't delete own account)

REPORTS
  - Sales Report (date range filter, grand total, CSV export)
  - Item-Wise Sales Report (ranked by quantity sold)
  - Low Stock Report (medicines at/below reorder level)
  - Expiry Report (configurable days-ahead filter)

================================================================
  TECHNOLOGY STACK
================================================================

  - Java (JDK 25)
  - Swing / AWT (GUI)
  - JDBC (Database connectivity)
  - MySQL Server 8.0 + MySQL Connector/J 26.7.0
  - NetBeans IDE 29

================================================================
  END OF README
================================================================