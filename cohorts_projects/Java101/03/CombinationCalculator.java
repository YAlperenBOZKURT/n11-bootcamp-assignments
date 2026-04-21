import java.util.Scanner;

public class CombinationCalculator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int n, r;
        int nFactorial = 1;
        int rFactorial = 1;
        int nrFactorial = 1;

        System.out.print("Enter n: ");
        n = input.nextInt();

        System.out.print("Enter r: ");
        r = input.nextInt();

        if (n >= 0 && r >= 0 && n >= r) {
            for (int i = 1; i <= n; i++) {
                nFactorial *= i;
            }

            for (int i = 1; i <= r; i++) {
                rFactorial *= i;
            }

            for (int i = 1; i <= (n - r); i++) {
                nrFactorial *= i;
            }

            int combination = nFactorial / (rFactorial * nrFactorial);

            System.out.println("C(" + n + "," + r + ") = " + combination);
        } else {
            System.out.println("Invalid input. Please make sure that n >= r and both are non-negative.");
        }

        input.close();
    }
}