import java.util.Scanner;

public class DivisibleNumbersAverage {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter a number: ");
        int number = input.nextInt();

        int sum = 0;
        int count = 0;

        for (int i = 0; i <= number; i++) {
            if (i % 3 == 0 && i % 4 == 0) {
                sum += i;
                count++;
            }
        }

        if (count > 0) {
            double average = (double) sum / count;
            System.out.println("Average: " + average);
        } else {
            System.out.println("There is no number divisible by both 3 and 4.");
        }

        input.close();
    }
}