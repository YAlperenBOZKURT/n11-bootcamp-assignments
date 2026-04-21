import java.util.Scanner;

public class RecursivePrime {

    static boolean isPrime(int number, int divisor) {
        if (number < 2) {
            return false;
        }
        if (divisor == 1) {
            return true;
        }
        if (number % divisor == 0) {
            return false;
        }
        return isPrime(number, divisor - 1);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a number: ");
        int number = scanner.nextInt();

        if (isPrime(number, number / 2)) {
            System.out.println(number + " is PRIME!");
        } else {
            System.out.println(number + " is not prime!");
        }

        scanner.close();
    }
}