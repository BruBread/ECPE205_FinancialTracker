package Data_backend;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This is the data manager, it handles everything
 * I hate u harley
 */
public class AccountManager {

    public static ArrayList<BankAccount>  accounts     = new ArrayList<>();
    public static ArrayList<Transaction>  transactions = new ArrayList<>();

    // Stores all data to bank
    private static void log(String bank, String type, double amount, javax.swing.ImageIcon logo) {
        transactions.add(new Transaction(bank, type, amount, today(), logo));
    }

    // Add, Update, Delete Logics here
    public static void addAccount(BankAccount acc) {
        accounts.add(acc);
        log(acc.bankName, "Deposit", acc.balance, acc.logo);
    }
    public static boolean accountExists(String name){

        for(BankAccount a : accounts)

            if(a.bankName.equalsIgnoreCase(name))

                return true;

        return false;
    }

    public static void updateAccount(BankAccount acc, double oldBalance) {

        if(acc.balance == oldBalance){
            log(acc.bankName, "Edit", 0, acc.logo);
            return;
        }

        double diff = acc.balance - oldBalance;

        log(
                acc.bankName,
                diff >= 0 ? "Deposit" : "Withdraw",
                Math.abs(diff),
                acc.logo
        );
    }
    public static void deleteAccount(BankAccount acc) {
        accounts.remove(acc);
        log(acc.bankName, "Delete", acc.balance, acc.logo);
    }

    public static double totalAssets() {
        double total = 0;
        for (BankAccount a : accounts) total += a.balance;
        return total;
    } // Calculate all assets from every account from every bank

    // Returns current date lol
    private static String today() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
    }
}
