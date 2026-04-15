package Data_backend;

public class SubAccount implements java.io.Serializable {

    public String name;    // e.g. "Savings", "Expenses", "Insurance"
    public double balance;

    public SubAccount(String name, double balance) {
        this.name    = name;
        this.balance = balance;
    }

    public String getName()           { return name; }
    public double getBalance()        { return balance; }
    public void setBalance(double b)  { this.balance = b; }
}