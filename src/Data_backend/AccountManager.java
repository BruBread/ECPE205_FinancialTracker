package Data_backend;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * AccountManager — updated to use SQLite via DatabaseManager.
 *
 * CHANGES from original:
 *   • saveData()   → removed (DB writes happen immediately on each operation)
 *   • loadData()   → now calls DatabaseManager.loadAllData()
 *   • resetData()  → clears in-memory lists; DB rows are deleted via raw SQL
 *   • addAccount / updateAccount / deleteAccount → also call DatabaseManager
 *   • addSubAccount / updateSubAccount / deleteSubAccount → same
 *   • log()        → also calls DatabaseManager.insertTransaction()
 *
 * Everything else (listeners, totalAssets, etc.) is unchanged.
 */
public class AccountManager {

    public static ArrayList<BankAccount> accounts     = new ArrayList<>();
    public static ArrayList<Transaction> transactions = new ArrayList<>();

    // -----------------------------------------------------------------------
    // Startup / shutdown
    // -----------------------------------------------------------------------

    /**
     * Called once at application start (from Main.java).
     * Ensures the DB schema exists, then reads all persisted data.
     */
    public static void loadData() {
        DatabaseManager.createTables();   // no-op if tables already exist
        DatabaseManager.loadAllData();    // populates accounts + transactions
    }

    /**
     * Clears all data from memory AND from the database.
     * Equivalent to the old resetData() which deleted the .dat file.
     */
    public static void resetData() {
        accounts.clear();
        transactions.clear();

        // Wipe every row (foreign keys cascade to sub_accounts)
        try (java.sql.Connection conn = DatabaseManager.getConnection();
             java.sql.Statement  stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("DELETE FROM transactions;");
            stmt.execute("DELETE FROM bank_accounts;");  // cascades to sub_accounts

        } catch (java.sql.SQLException e) {
            System.err.println("[DB] resetData error: " + e.getMessage());
        }

        notifyListeners();
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /** Logs a transaction in memory AND persists it to the DB. */
    private static void log(String bank, String type, double amount,
                            javax.swing.ImageIcon logo) {
        String date = today();
        transactions.add(new Transaction(bank, type, amount, date, logo));
        DatabaseManager.insertTransaction(bank, type, amount, date);
    }

    private static String today() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
    }

    // -----------------------------------------------------------------------
    // BankAccount CRUD
    // -----------------------------------------------------------------------

    public static void addAccount(BankAccount acc, double initialBalance) {
        accounts.add(acc);
        DatabaseManager.insertBankAccount(acc.bankName);

        // Every new bank starts with a default "Wallet" sub-account
        SubAccount wallet = new SubAccount("Wallet", initialBalance);
        acc.subAccounts.add(wallet);
        DatabaseManager.insertSubAccount(acc.bankName, wallet.name, wallet.balance);

        log(acc.bankName, "Deposit", initialBalance, acc.logo);
        notifyListeners();
    }

    public static boolean accountExists(String name) {
        for (BankAccount a : accounts)
            if (a.bankName.equalsIgnoreCase(name)) return true;
        return false;
    }

    public static void updateAccount(BankAccount acc,
                                     String oldName, javax.swing.ImageIcon oldLogo) {

        boolean nameChanged = !oldName.equals(acc.bankName);
        boolean logoChanged = !java.util.Objects.equals(oldLogo, acc.logo);

        // Persist the updated name to the DB
        DatabaseManager.updateBankAccount(oldName, acc.bankName);

        if (nameChanged || logoChanged)
            log(acc.bankName, "Account updated", 0, acc.logo);

        notifyListeners();
    }

    public static void deleteAccount(BankAccount acc) {
        accounts.remove(acc);
        log(acc.bankName, "Delete", acc.getTotalBalance(), acc.logo);
        DatabaseManager.deleteBankAccount(acc.bankName); // cascades sub_accounts
        notifyListeners();
    }

    public static double totalAssets() {
        double total = 0;
        for (BankAccount a : accounts) total += a.getTotalBalance();
        return total;
    }

    // -----------------------------------------------------------------------
    // SubAccount CRUD
    // -----------------------------------------------------------------------

    public static void addSubAccount(BankAccount bank, SubAccount sub) {
        bank.subAccounts.add(sub);
        DatabaseManager.insertSubAccount(bank.bankName, sub.name, sub.balance);
        log(bank.bankName, "Deposit", sub.balance, bank.logo);
        notifyListeners();
    }

    public static void updateSubAccount(BankAccount bank, SubAccount sub,
                                        double oldBalance) {
        DatabaseManager.updateSubAccount(bank.bankName, sub.name, sub.balance);
        double diff = sub.balance - oldBalance;
        if (diff != 0) {
            log(bank.bankName,
                    diff >= 0 ? "Deposit" : "Withdraw",
                    Math.abs(diff), bank.logo);
        }
        notifyListeners();
    }

    public static void deleteSubAccount(BankAccount bank, SubAccount sub) {
        bank.subAccounts.remove(sub);
        DatabaseManager.deleteSubAccount(bank.bankName, sub.name);
        log(bank.bankName, "Delete", sub.balance, bank.logo);
        notifyListeners();
    }

    // -----------------------------------------------------------------------
    // Listener pattern (unchanged)
    // -----------------------------------------------------------------------

    private static final java.util.List<Runnable> listeners = new java.util.ArrayList<>();

    public static void addListener(Runnable r) { listeners.add(r); }

    private static void notifyListeners() {
        for (Runnable r : listeners) r.run();
    }
}