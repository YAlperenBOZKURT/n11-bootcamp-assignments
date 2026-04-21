import java.util.Scanner;

public class DigitSumCalculator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter a number: ");
        int number = input.nextInt();

        int tempNumber = Math.abs(number);
        int digitSum = 0;

        while (tempNumber != 0) {
            digitSum += tempNumber % 10;
            tempNumber /= 10;
        }

        System.out.println("Sum of digits: " + digitSum);

        input.close();
    }
}