package Data_backend;

import javax.swing.ImageIcon;
import java.util.ArrayList;

public class BankAccount implements java.io.Serializable {

    public String    bankName;
    public double    balance;
    public ImageIcon logo;
    public ArrayList<SubAccount> subAccounts = new ArrayList<>(); // NEW

    public BankAccount(String bankName, double balance, ImageIcon logo) {
        this.bankName = bankName;
        this.balance  = balance;
        this.logo     = logo;
    }

    // NEW: total balance = sum of all sub-accounts (or just balance if none)
    public double getTotalBalance() {
        if (subAccounts.isEmpty()) return balance;
        double total = 0;
        for (SubAccount s : subAccounts) total += s.balance;
        return total;
    }

    public String getName()   { return bankName; }
    public double getAmount() { return getTotalBalance(); } // updated

    public void setName(String name)     { this.bankName = name; }
    public void setAmount(double amount) { this.balance  = amount; }
}