import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int n1, n2, select;

        System.out.print("Enter first number: ");
        n1 = input.nextInt();

        System.out.print("Enter second number: ");
        n2 = input.nextInt();

        System.out.println("1-Addition\n2-Subtraction\n3-Multiplication\n4-Division");
        System.out.print("Your choice: ");
        select = input.nextInt();

        switch (select) {
            case 1:
                System.out.println("Result: " + (n1 + n2));
                break;

            case 2:
                System.out.println("Result: " + (n1 - n2));
                break;

            case 3:
                System.out.println("Result: " + (n1 * n2));
                break;

            case 4:
                if (n2 != 0) {
                    System.out.println("Result: " + (n1 / n2));
                } else {
                    System.out.println("A number cannot be divided by zero!");
                }
                break;

            default:
                System.out.println("Invalid choice. Please try again.");
        }

        input.close();
    }
}