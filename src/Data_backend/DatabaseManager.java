package Data_backend;

import java.sql.*;

/**
 * DatabaseManager — handles all SQLite operations for FinancialTracker.
 *
 * HOW IT FITS IN:
 *   - Replaces the binary "financial_data.dat" file used by AccountManager.
 *   - The database file "financial_tracker.db" is created automatically
 *     in the project root the first time the app runs.
 *   - All three tables (bank_accounts, sub_accounts, transactions) are
 *     created automatically if they do not exist yet.
 *
 * SQLITE DRIVER NEEDED:
 *   Download sqlite-jdbc-<version>.jar from https://github.com/xerial/sqlite-jdbc/releases
 *   and add it to your IntelliJ project:
 *   File → Project Structure → Modules → Dependencies → "+" → JARs or directories
 */
public class DatabaseManager {

    // -----------------------------------------------------------------------
    // Connection
    // -----------------------------------------------------------------------

    /** Path to the SQLite database file (created next to the .jar / project root). */
    private static final String DB_URL = "jdbc:sqlite:Z:/amigo/ECPE205_FinancialTracker/libs/finance.db";

    /**
     * Opens (or reuses) a connection to the SQLite database.
     * SQLite is file-based, so no host / port / credentials are needed.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // -----------------------------------------------------------------------
    // Schema creation
    // -----------------------------------------------------------------------

    /**
     * Creates all three tables the first time the app launches.
     * Call this once from AccountManager.loadData() (or Main.java) before
     * any other database operation.
     *
     * Table overview
     * ─────────────
     * bank_accounts  – one row per BankAccount (id, name, balance)
     * sub_accounts   – one row per SubAccount; foreign-keyed to bank_accounts
     * transactions   – one row per Transaction (immutable audit log)
     *
     * Note: logo images are NOT stored in SQLite (binary blobs slow things
     * down and are already bundled as resource files). The logo is reloaded
     * from resources at runtime using the bank_name as a key, exactly as the
     * original code does in AddAccountDialog.
     */
    public static void createTables() {
        String bankAccounts = """
            CREATE TABLE IF NOT EXISTS bank_accounts (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                bank_name TEXT    NOT NULL UNIQUE
            );
            """;

        String subAccounts = """
            CREATE TABLE IF NOT EXISTS sub_accounts (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                bank_account_id INTEGER NOT NULL,
                name            TEXT    NOT NULL,
                balance         REAL    NOT NULL DEFAULT 0,
                FOREIGN KEY (bank_account_id) REFERENCES bank_accounts(id)
                    ON DELETE CASCADE
            );
            """;

        String transactions = """
            CREATE TABLE IF NOT EXISTS transactions (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                bank_name TEXT    NOT NULL,
                type      TEXT    NOT NULL,
                amount    REAL    NOT NULL,
                date      TEXT    NOT NULL
            );
            """;

        try (Connection conn = getConnection();
             Statement  stmt = conn.createStatement()) {

            // Enable foreign-key enforcement (SQLite disables it by default)
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute(bankAccounts);
            stmt.execute(subAccounts);
            stmt.execute(transactions);

            System.out.println("[DB] Tables created / verified.");

        } catch (SQLException e) {
            System.err.println("[DB] createTables error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------
    // BankAccount operations
    // -----------------------------------------------------------------------

    /**
     * Inserts a new bank account row.
     * Called by AccountManager.addAccount().
     *
     * @param bankName  display name (must be unique)
     * @param balance   opening balance
     */
    public static void insertBankAccount(String bankName) {
        String sql = "INSERT INTO bank_accounts (bank_name) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bankName);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DB] insertBankAccount error: " + e.getMessage());
        }
    }

    /**
     * Updates the name and/or balance of an existing bank account.
     * Called by AccountManager.updateAccount().
     *
     * @param oldName   the current name (used to locate the row)
     * @param newName   the new name to set
     * @param newBalance the new balance to set
     */
    public static void updateBankAccount(String oldName, String newName) {
        String sql = "UPDATE bank_accounts SET bank_name = ? WHERE bank_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DB] updateBankAccount error: " + e.getMessage());
        }
    }

    /**
     * Deletes a bank account (and all its sub-accounts via CASCADE).
     * Called by AccountManager.deleteAccount().
     *
     * @param bankName  name of the account to remove
     */
    public static void deleteBankAccount(String bankName) {
        String sql = "DELETE FROM bank_accounts WHERE bank_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bankName);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DB] deleteBankAccount error: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // SubAccount operations
    // -----------------------------------------------------------------------

    /**
     * Returns the primary-key id of a bank_accounts row by name.
     * Used internally when inserting sub-accounts.
     */
    private static int getBankAccountId(Connection conn, String bankName) throws SQLException {
        String sql = "SELECT id FROM bank_accounts WHERE bank_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bankName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        return -1; // not found
    }

    /**
     * Inserts a sub-account linked to the given bank.
     * Called by AccountManager.addSubAccount().
     */
    public static void insertSubAccount(String bankName, String subName, double balance) {
        String sql = "INSERT INTO sub_accounts (bank_account_id, name, balance) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int bankId = getBankAccountId(conn, bankName);
            if (bankId == -1) {
                System.err.println("[DB] insertSubAccount: bank not found → " + bankName);
                return;
            }

            ps.setInt(1, bankId);
            ps.setString(2, subName);
            ps.setDouble(3, balance);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DB] insertSubAccount error: " + e.getMessage());
        }
    }

    /**
     * Updates a sub-account's balance.
     * Called by AccountManager.updateSubAccount().
     */
    public static void updateSubAccount(String bankName, String subName, double newBalance) {
        String sql = """
            UPDATE sub_accounts
               SET balance = ?
             WHERE name = ?
               AND bank_account_id = (SELECT id FROM bank_accounts WHERE bank_name = ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newBalance);
            ps.setString(2, subName);
            ps.setString(3, bankName);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DB] updateSubAccount error: " + e.getMessage());
        }
    }

    /**
     * Deletes a specific sub-account.
     * Called by AccountManager.deleteSubAccount().
     */
    public static void deleteSubAccount(String bankName, String subName) {
        String sql = """
            DELETE FROM sub_accounts
             WHERE name = ?
               AND bank_account_id = (SELECT id FROM bank_accounts WHERE bank_name = ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, subName);
            ps.setString(2, bankName);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DB] deleteSubAccount error: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Transaction operations
    // -----------------------------------------------------------------------

    /**
     * Inserts a transaction record (audit log; rows are never updated/deleted).
     * Called by AccountManager whenever an account or sub-account changes.
     *
     * @param bankName  the bank this transaction belongs to
     * @param type      "Deposit", "Withdraw", "Account updated", "Delete"
     * @param amount    the monetary value (0 for non-monetary events)
     * @param date      formatted date string ("dd MMMM yyyy")
     */
    public static void insertTransaction(String bankName, String type,
                                         double amount, String date) {
        String sql = "INSERT INTO transactions (bank_name, type, amount, date) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bankName);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setString(4, date);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DB] insertTransaction error: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Load all data (replaces ObjectInputStream deserialization)
    // -----------------------------------------------------------------------

    /**
     * Reads every bank account, its sub-accounts, and all transactions from
     * SQLite and populates AccountManager.accounts / AccountManager.transactions.
     *
     * Call this once at startup from AccountManager.loadData().
     *
     * Because logos are resource files (not stored in the DB), we look them up
     * via the helper below using the bank_name stored in each row.
     */
    public static void loadAllData() {
        AccountManager.accounts.clear();
        AccountManager.transactions.clear();

        try (Connection conn = getConnection();
             Statement  stmt = conn.createStatement()) {

            // ── 1. Load bank accounts ────────────────────────────────────────
            ResultSet rsBank = stmt.executeQuery(
                    "SELECT id, bank_name FROM bank_accounts ORDER BY id");

            while (rsBank.next()) {
                int    id       = rsBank.getInt("id");
                String name     = rsBank.getString("bank_name");

                // Reload the logo from bundled resources (not stored in DB)
                javax.swing.ImageIcon logo = loadLogoByName(name);

                BankAccount account = new BankAccount(name, logo);

                // ── 2. Load sub-accounts for this bank ───────────────────────
                String subSql = "SELECT name, balance FROM sub_accounts WHERE bank_account_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(subSql)) {
                    ps.setInt(1, id);
                    ResultSet rsSub = ps.executeQuery();
                    while (rsSub.next()) {
                        account.subAccounts.add(new SubAccount(
                                rsSub.getString("name"),
                                rsSub.getDouble("balance")
                        ));
                    }
                }

                AccountManager.accounts.add(account);
            }

            // ── 3. Load transactions ─────────────────────────────────────────
            ResultSet rsTx = stmt.executeQuery(
                    "SELECT bank_name, type, amount, date FROM transactions ORDER BY id");

            while (rsTx.next()) {
                String bankName = rsTx.getString("bank_name");
                javax.swing.ImageIcon logo = loadLogoByName(bankName);

                AccountManager.transactions.add(new Transaction(
                        bankName,
                        rsTx.getString("type"),
                        rsTx.getDouble("amount"),
                        rsTx.getString("date"),
                        logo
                ));
            }

            System.out.println("[DB] Loaded "
                    + AccountManager.accounts.size()    + " accounts, "
                    + AccountManager.transactions.size()+ " transactions.");

        } catch (SQLException e) {
            System.err.println("[DB] loadAllData error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------
    // Utility: reload logo from resources at runtime
    // -----------------------------------------------------------------------

    /**
     * Maps a bank name back to its resource icon.
     * The original AddAccountDialog already has this mapping — this method
     * mirrors it so logos are re-attached after loading from the DB.
     *
     * If no icon matches, null is returned (the UI handles null gracefully).
     */
    private static javax.swing.ImageIcon loadLogoByName(String bankName) {
        String lower = bankName.toLowerCase();

        // Check for known bank names that are sub-strings of the stored name
        String[] knownBanks = {"bdo", "bpi", "gcash", "gotyme", "maribank", "maya", "unionbank"};

        for (String bank : knownBanks) {
            if (lower.contains(bank)) {
                java.net.URL url = DatabaseManager.class
                        .getResource("/resources/bank_icons/" + bank + ".png");
                if (url != null) return new javax.swing.ImageIcon(url);
            }
        }
        return null; // unknown bank — UI will show a default placeholder
    }
}