import java.util.Scanner;

public class CircleCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        double pi = 3.14;

        System.out.print("Enter radius: ");
        double r = scanner.nextDouble();

        System.out.print("Enter central angle: ");
        double a = scanner.nextDouble();

        double area = pi * r * r;
        double circumference = 2 * pi * r;
        double sectorArea = (pi * r * r * a) / 360;

        System.out.println("Circle area: " + area);
        System.out.println("Circle circumference: " + circumference);
        System.out.println("Sector area: " + sectorArea);

        scanner.close();
    }
}