//sample - i added some code here to fix the problem

package Data_backend;

import javax.swing.ImageIcon;

/**
 * This is an account
 * It Extends the original account model with display name and logo.
 */
public class BankAccount implements java.io.Serializable {

    public String bankName;
    public double balance;
    public ImageIcon logo;

    public BankAccount(String bankName, double balance, ImageIcon logo) {
        this.bankName = bankName;
        this.balance  = balance;
        this.logo     = logo;
    }

    public String getName()    { return bankName; }
    public double getAmount()  { return balance; }

    public void setName(String name)       { this.bankName = name; }
    public void setAmount(double amount)   { this.balance  = amount; }
}
