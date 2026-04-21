import java.util.Scanner;

public class MinMaxFinder {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("How many numbers will you enter: ");
        int count = input.nextInt();

        int number;
        int min = 0;
        int max = 0;

        for (int i = 1; i <= count; i++) {
            System.out.print(i + ". Enter number: ");
            number = input.nextInt();

            if (i == 1) {
                min = number;
                max = number;
            } else {
                if (number < min) {
                    min = number;
                }
                if (number > max) {
                    max = number;
                }
            }
        }

        System.out.println("Maximum number: " + max);
        System.out.println("Minimum number: " + min);

        input.close();
    }
}