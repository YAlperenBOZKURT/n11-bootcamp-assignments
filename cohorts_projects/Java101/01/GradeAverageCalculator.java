import java.util.Scanner;

public class GradeAverageCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Math grade: ");
        double math = scanner.nextDouble();

        System.out.print("Enter Physics grade: ");
        double physics = scanner.nextDouble();

        System.out.print("Enter Chemistry grade: ");
        double chemistry = scanner.nextDouble();

        System.out.print("Enter Turkish grade: ");
        double turkish = scanner.nextDouble();

        System.out.print("Enter History grade: ");
        double history = scanner.nextDouble();

        System.out.print("Enter Music grade: ");
        double music = scanner.nextDouble();

        double average = (math + physics + chemistry + turkish + history + music) / 6;

        String result = average > 60 ? "Passed the class" : "Failed the class";

        System.out.println("Average: " + average);
        System.out.println("Result: " + result);

        scanner.close();
    }
}