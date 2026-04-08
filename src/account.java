public class account {
    private double amount;
    private String accountname, history;

    public account(String accountname, double amount,String history) {
        this.amount = amount;
        this.accountname = accountname;
        this.history = history;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return accountname;
    }

    public void setName(String accountname) {
        this.accountname = accountname;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
