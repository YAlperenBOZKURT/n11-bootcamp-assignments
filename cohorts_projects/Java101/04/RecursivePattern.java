import java.util.Scanner;

public class RecursivePattern {

    static void pattern(int current, int original, boolean isDecreasing) {
        System.out.print(current + " ");

        if (current <= 0) {
            isDecreasing = false;
        }

        if (current == original && !isDecreasing) {
            return;
        }

        if (isDecreasing) {
            pattern(current - 5, original, true);
        } else {
            pattern(current + 5, original, false);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter N: ");
        int n = scanner.nextInt();

        pattern(n, n, true);

        scanner.close();
    }
}