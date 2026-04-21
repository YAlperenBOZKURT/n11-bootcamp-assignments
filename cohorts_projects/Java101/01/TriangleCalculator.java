import java.util.Scanner;

public class TriangleCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first perpendicular side: ");
        double side1 = scanner.nextDouble();

        System.out.print("Enter second perpendicular side: ");
        double side2 = scanner.nextDouble();

        double hypotenuse = Math.sqrt(side1 * side1 + side2 * side2);
        System.out.println("Hypotenuse: " + hypotenuse);

        System.out.print("Enter side a: ");
        double a = scanner.nextDouble();

        System.out.print("Enter side b: ");
        double b = scanner.nextDouble();

        System.out.print("Enter side c: ");
        double c = scanner.nextDouble();

        if (a + b > c && a + c > b && b + c > a) {
            double semiPerimeter = (a + b + c) / 2.0;
            double area = Math.sqrt(semiPerimeter * (semiPerimeter - a) * (semiPerimeter - b) * (semiPerimeter - c));

            System.out.println("Semi-perimeter: " + semiPerimeter);
            System.out.println("Triangle area: " + area);
        } else {
            System.out.println("These sides do not form a valid triangle.");
        }

        scanner.close();
    }
}