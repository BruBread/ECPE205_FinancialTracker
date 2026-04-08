public class asset {
    private bank bank;
    private double totalassets = 0;
    public asset(bank bank) {
        this.bank = bank;
    }

    public bank getBank() {
        return bank;
    }

    public void setBank(bank bank) {
        this.bank = bank;
    }

    public String toString(){
        return totalassets + "";
    }
}
