import java.util.Scanner;

public class PowersOfFourAndFive {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter a number: ");
        int number = input.nextInt();

        System.out.println("Powers of 4 up to " + number + ":");
        for (int i = 1; i <= number; i *= 4) {
            System.out.println(i);
        }

        System.out.println("Powers of 5 up to " + number + ":");
        for (int i = 1; i <= number; i *= 5) {
            System.out.println(i);
        }

        input.close();
    }
}