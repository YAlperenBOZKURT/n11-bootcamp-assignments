import java.util.Scanner;

public class GcdAndLcmCalculator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter first number: ");
        int n1 = input.nextInt();

        System.out.print("Enter second number: ");
        int n2 = input.nextInt();

        int i = 1;
        int gcd = 1;

        while (i <= n1 && i <= n2) {
            if (n1 % i == 0 && n2 % i == 0) {
                gcd = i;
            }
            i++;
        }

        int lcm = (n1 * n2) / gcd;

        System.out.println("GCD: " + gcd);
        System.out.println("LCM: " + lcm);

        input.close();
    }
}