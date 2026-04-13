package Data_backend;

import javax.swing.ImageIcon;

/**
 * Transactions have different types: "Deposit", "Edit", "Withdraw", "Delete"
 */
public class Transaction implements java.io.Serializable {

    public String    bank;
    public String    type;
    public double    amount;
    public String    date;
    public ImageIcon logo;

    public Transaction(String bank, String type, double amount, String date, ImageIcon logo) {
        this.bank   = bank;
        this.type   = type;
        this.amount = amount;
        this.date   = date;
        this.logo   = logo;
    }
}
