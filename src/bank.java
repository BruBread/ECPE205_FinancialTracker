import java.util.ArrayList;

public class bank {
    private ArrayList<account> accounts = new ArrayList<>();

    public bank(ArrayList<account> accounts) {
        this.accounts = accounts;
    }

    public ArrayList<account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<account> accounts) {
        this.accounts = accounts;
    }
}
