import java.util.Scanner;

public class PowerCalculator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int base, exponent;
        int result = 1;

        System.out.print("Enter the base value: ");
        base = input.nextInt();

        System.out.print("Enter the exponent value: ");
        exponent = input.nextInt();

        for (int i = 1; i <= exponent; i++) {
            result *= base;
        }

        System.out.println("Result: " + result);

        input.close();
    }
}