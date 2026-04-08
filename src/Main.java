import java.util.ArrayList;
import java.util.Scanner;

public class Main{
    public static void main() {
        Scanner sc = new Scanner(System.in);
        ArrayList<account> accounts = new ArrayList<>();

        System.out.println("would you like to add a bank? y or n");
        String x = sc.nextLine();
        if (x.equalsIgnoreCase("y")){
            System.out.println("Enter the name of the bank:");
            String bankName = sc.nextLine();

            System.out.println("would you like to add an account? y or n");
            String y = sc.nextLine();
            while (y.equalsIgnoreCase("y")){
                System.out.println("Enter the name of the account: ");
                String acctName = sc.nextLine();

                System.out.println("Enter the balance of the account: ");
                double acctBalance = sc.nextDouble();
                sc.nextLine();

                System.out.println("Enter the history: ");
                String history = sc.nextLine();

                accounts.add(new account(acctName,acctBalance,history));

                System.out.println("would you like to add an account? y or n");
                y = sc.nextLine();

            }

        }
        System.out.println("Total Assets: " + String.format("%.2f", totalAssets(accounts)));
    }
    public static double totalAssets(ArrayList<account> accounts){
        double total = 0;
        if (!accounts.isEmpty()){
            for(account a : accounts){
                total+= a.getAmount();
            }
        }
        return total;
    }

}
