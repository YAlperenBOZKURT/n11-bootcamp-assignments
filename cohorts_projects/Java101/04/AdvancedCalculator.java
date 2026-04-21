import java.util.Scanner;

public class AdvancedCalculator {

    static Scanner scan = new Scanner(System.in);

    static void plus() {
        int number, result = 0, i = 1;
        while (true) {
            System.out.print(i++ + ". number: ");
            number = scan.nextInt();
            if (number == 0) {
                break;
            }
            result += number;
        }
        System.out.println("Result: " + result);
    }

    static void minus() {
        System.out.print("How many numbers will you enter: ");
        int counter = scan.nextInt();
        int number, result = 0;

        for (int i = 1; i <= counter; i++) {
            System.out.print(i + ". number: ");
            number = scan.nextInt();
            if (i == 1) {
                result = number;
            } else {
                result -= number;
            }
        }

        System.out.println("Result: " + result);
    }

    static void times() {
        int number, result = 1, i = 1;

        while (true) {
            System.out.print(i++ + ". number: ");
            number = scan.nextInt();

            if (number == 1)
                break;

            if (number == 0) {
                result = 0;
                break;
            }

            result *= number;
        }

        System.out.println("Result: " + result);
    }

    static void divided() {
        System.out.print("How many numbers will you enter: ");
        int counter = scan.nextInt();
        double number, result = 0;

        for (int i = 1; i <= counter; i++) {
            System.out.print(i + ". number: ");
            number = scan.nextDouble();

            if (i == 1) {
                result = number;
            } else {
                if (number == 0) {
                    System.out.println("Divisor cannot be zero.");
                    return;
                }
                result /= number;
            }
        }

        System.out.println("Result: " + result);
    }

    static void power() {
        System.out.print("Enter base value: ");
        int base = scan.nextInt();
        System.out.print("Enter exponent value: ");
        int exponent = scan.nextInt();
        int result = 1;

        for (int i = 1; i <= exponent; i++) {
            result *= base;
        }

        System.out.println("Result: " + result);
    }

    static void factorial() {
        System.out.print("Enter a number: ");
        int n = scan.nextInt();
        int result = 1;

        for (int i = 1; i <= n; i++) {
            result *= i;
        }

        System.out.println("Result: " + result);
    }

    static void mod() {
        System.out.print("Enter first number: ");
        int a = scan.nextInt();
        System.out.print("Enter second number: ");
        int b = scan.nextInt();

        if (b == 0) {
            System.out.println("Second number cannot be zero.");
            return;
        }

        System.out.println("Result: " + (a % b));
    }

    static void rectangle() {
        System.out.print("Enter short side: ");
        int shortSide = scan.nextInt();
        System.out.print("Enter long side: ");
        int longSide = scan.nextInt();

        int area = shortSide * longSide;
        int perimeter = 2 * (shortSide + longSide);

        System.out.println("Area: " + area);
        System.out.println("Perimeter: " + perimeter);
    }

    public static void main(String[] args) {
        int select;
        String menu = "1- Addition\n"
                + "2- Subtraction\n"
                + "3- Multiplication\n"
                + "4- Division\n"
                + "5- Power Calculation\n"
                + "6- Factorial Calculation\n"
                + "7- Mod Operation\n"
                + "8- Rectangle Area and Perimeter\n"
                + "0- Exit";

        do {
            System.out.println("\n" + menu);
            System.out.print("Please select an operation: ");
            select = scan.nextInt();

            switch (select) {
                case 1:
                    plus();
                    break;
                case 2:
                    minus();
                    break;
                case 3:
                    times();
                    break;
                case 4:
                    divided();
                    break;
                case 5:
                    power();
                    break;
                case 6:
                    factorial();
                    break;
                case 7:
                    mod();
                    break;
                case 8:
                    rectangle();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid value, try again.");
            }
        } while (select != 0);
    }
}