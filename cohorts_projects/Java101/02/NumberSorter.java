import java.util.Scanner;

public class NumberSorter {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int a, b, c;

        System.out.print("Enter first number: ");
        a = input.nextInt();

        System.out.print("Enter second number: ");
        b = input.nextInt();

        System.out.print("Enter third number: ");
        c = input.nextInt();

        if (a <= b && a <= c) {
            if (b <= c) {
                System.out.println("Ascending order: " + a + " < " + b + " < " + c);
            } else {
                System.out.println("Ascending order: " + a + " < " + c + " < " + b);
            }
        } else if (b <= a && b <= c) {
            if (a <= c) {
                System.out.println("Ascending order: " + b + " < " + a + " < " + c);
            } else {
                System.out.println("Ascending order: " + b + " < " + c + " < " + a);
            }
        } else {
            if (a <= b) {
                System.out.println("Ascending order: " + c + " < " + a + " < " + b);
            } else {
                System.out.println("Ascending order: " + c + " < " + b + " < " + a);
            }
        }

        input.close();
    }
}