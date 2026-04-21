import java.util.Scanner;

public class AtmSystem {
    public static void main(String[] args) {
        String userName, password;
        Scanner input = new Scanner(System.in);
        int right = 3;
        int balance = 1500;
        int select;

        while (right > 0) {
            System.out.print("Enter your username: ");
            userName = input.nextLine();

            System.out.print("Enter your password: ");
            password = input.nextLine();

            if (userName.equals("patika") && password.equals("dev123")) {
                System.out.println("Welcome to Kodluyoruz Bank!");

                do {
                    System.out.println("1-Deposit Money");
                    System.out.println("2-Withdraw Money");
                    System.out.println("3-Check Balance");
                    System.out.println("4-Exit");
                    System.out.print("Please select an operation: ");
                    select = input.nextInt();

                    switch (select) {
                        case 1:
                            System.out.print("Enter deposit amount: ");
                            int deposit = input.nextInt();
                            balance += deposit;
                            System.out.println("Your new balance: " + balance);
                            break;

                        case 2:
                            System.out.print("Enter withdraw amount: ");
                            int withdraw = input.nextInt();
                            if (withdraw > balance) {
                                System.out.println("Insufficient balance.");
                            } else {
                                balance -= withdraw;
                                System.out.println("Your new balance: " + balance);
                            }
                            break;

                        case 3:
                            System.out.println("Your balance: " + balance);
                            break;

                        case 4:
                            System.out.println("See you again.");
                            break;

                        default:
                            System.out.println("Invalid selection. Please try again.");
                    }
                } while (select != 4);

                break;
            } else {
                right--;
                System.out.println("Incorrect username or password. Please try again.");

                if (right == 0) {
                    System.out.println("Your account has been blocked. Please contact the bank.");
                } else {
                    System.out.println("Remaining attempts: " + right);
                }
            }
        }

        input.close();
    }
}