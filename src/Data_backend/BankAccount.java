package Data_backend;

import javax.swing.ImageIcon;
import java.util.ArrayList;

public class BankAccount implements java.io.Serializable {

    public String    bankName;
    public ImageIcon logo;
    public ArrayList<SubAccount> subAccounts = new ArrayList<>();

    public BankAccount(String bankName, ImageIcon logo) {
        this.bankName = bankName;
        this.logo     = logo;
    }

    /** Total balance is always the sum of all sub-accounts. */
    public double getTotalBalance() {
        double total = 0;
        for (SubAccount s : subAccounts) total += s.balance;
        return total;
    }

    public String getName()   { return bankName; }
    public double getAmount() { return getTotalBalance(); }

    public void setName(String name) { this.bankName = name; }
}