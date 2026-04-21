import java.util.Scanner;

public class UserLoginSystem {
    public static void main(String[] args) {
        String userName, password, newPassword, resetChoice;
        String correctUserName = "patika";
        String correctPassword = "java123";

        Scanner input = new Scanner(System.in);

        System.out.print("Enter your username: ");
        userName = input.nextLine();

        System.out.print("Enter your password: ");
        password = input.nextLine();

        if (userName.equals(correctUserName) && password.equals(correctPassword)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Incorrect username or password.");
            System.out.print("Do you want to reset your password? (yes/no): ");
            resetChoice = input.nextLine();

            if (resetChoice.equalsIgnoreCase("yes")) {
                System.out.print("Enter your new password: ");
                newPassword = input.nextLine();

                if (newPassword.equals(correctPassword)) {
                    System.out.println("Password could not be created, please enter another password.");
                } else {
                    System.out.println("Password created successfully.");
                }
            } else {
                System.out.println("Password reset cancelled.");
            }
        }

        input.close();
    }
}