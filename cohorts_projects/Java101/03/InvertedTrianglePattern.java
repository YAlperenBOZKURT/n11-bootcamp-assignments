import java.util.Scanner;

public class InvertedTrianglePattern {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter number of steps: ");
        int n = input.nextInt();

        for (int i = n; i >= 1; i--) {
            for (int j = 1; j <= (2 * i - 1); j++) {
                System.out.print("*");
            }
            System.out.println();
        }

        input.close();
    }
}