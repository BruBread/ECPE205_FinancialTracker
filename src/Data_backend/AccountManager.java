package Data_backend;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.*;

/**
 * This is the data manager, it handles everything
 * I hate u harley
 */
public class AccountManager {

    private static final String SAVE_FILE = "financial_data.dat";

    public static void saveData(){

        try{

            ObjectOutputStream out =
                    new ObjectOutputStream(

                            new FileOutputStream(SAVE_FILE)

                    );

            out.writeObject(accounts);

            out.writeObject(transactions);

            out.close();

        }
        catch(Exception e){

            e.printStackTrace();

        }

    }

    public static void loadData(){

        try{

            ObjectInputStream in =
                    new ObjectInputStream(

                            new FileInputStream(SAVE_FILE)

                    );

            accounts =
                    (ArrayList<BankAccount>) in.readObject();

            transactions =
                    (ArrayList<Transaction>) in.readObject();

            in.close();

        }
        catch(Exception e){

            System.out.println("No previous save found");

        }

    }

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

        notifyListeners();
        saveData();
    }
    public static boolean accountExists(String name){

        for(BankAccount a : accounts)

            if(a.bankName.equalsIgnoreCase(name))

                return true;

        return false;
    }

    public static void updateAccount(

            BankAccount acc,

            double oldBalance,

            String oldName,

            javax.swing.ImageIcon oldLogo

    ) {

        boolean nameChanged =
                !oldName.equals(acc.bankName);

        boolean logoChanged =
                !java.util.Objects.equals(oldLogo, acc.logo);

        boolean balanceChanged =
                acc.balance != oldBalance;


        // group name + logo change into one event
        if(nameChanged || logoChanged)

            log(acc.bankName,
                    "Account updated",
                    0,
                    acc.logo);


        if(balanceChanged){

            double diff =
                    acc.balance - oldBalance;

            log(

                    acc.bankName,

                    diff >= 0
                            ? "Deposit"
                            : "Withdraw",

                    Math.abs(diff),

                    acc.logo

            );

        }

        notifyListeners();
        saveData();
    }

    public static void deleteAccount(BankAccount acc) {

        accounts.remove(acc);

        log(acc.bankName, "Delete", acc.balance, acc.logo);

        notifyListeners();
        saveData();
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

    private static final java.util.List<Runnable> listeners = new java.util.ArrayList<>();

    public static void addListener(Runnable r){
        listeners.add(r);
    }

    private static void notifyListeners(){

        for(Runnable r : listeners)

            r.run();
    }
}
